package com.example.data

import com.example.data.entity.IncomeStreamEntity
import com.example.data.entity.PayoutRuleEntity
import com.example.data.entity.TransactionEntity
import com.example.data.entity.WalletBalanceEntity
import com.example.data.model.PayoutPlatform
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random

class YieldFlowRepository(private val database: YieldFlowDatabase) {

  val allStreams: Flow<List<IncomeStreamEntity>> = database.incomeStreamDao().getAllStreams()
  val allBalances: Flow<List<WalletBalanceEntity>> = database.walletBalanceDao().getAllBalances()
  val allRules: Flow<List<PayoutRuleEntity>> = database.payoutRuleDao().getAllRules()
  val allTransactions: Flow<List<TransactionEntity>> = database.transactionDao().getAllTransactions()

  suspend fun addStream(stream: IncomeStreamEntity): Long = withContext(Dispatchers.IO) {
    database.incomeStreamDao().insertStream(stream)
  }

  suspend fun toggleStream(stream: IncomeStreamEntity) = withContext(Dispatchers.IO) {
    database.incomeStreamDao().updateStream(stream.copy(isActive = !stream.isActive))
  }

  suspend fun deleteStream(stream: IncomeStreamEntity) = withContext(Dispatchers.IO) {
    database.incomeStreamDao().deleteStream(stream)
  }

  suspend fun addRule(rule: PayoutRuleEntity): Long = withContext(Dispatchers.IO) {
    database.payoutRuleDao().insertRule(rule)
  }

  suspend fun toggleRule(rule: PayoutRuleEntity) = withContext(Dispatchers.IO) {
    database.payoutRuleDao().updateRule(rule.copy(isEnabled = !rule.isEnabled))
  }

  suspend fun deleteRule(rule: PayoutRuleEntity) = withContext(Dispatchers.IO) {
    database.payoutRuleDao().deleteRule(rule)
  }

  suspend fun updateWalletAddress(id: String, newAddress: String) = withContext(Dispatchers.IO) {
    val existing = database.walletBalanceDao().getBalanceById(id) ?: return@withContext
    database.walletBalanceDao().insertOrUpdate(existing.copy(addressOrAccount = newAddress))
  }

  /**
   * Harvests an individual stream manually, credits the wallet and logs a confirmed transaction.
   */
  suspend fun harvestStream(streamId: Long): Boolean = withContext(Dispatchers.IO) {
    val stream = database.incomeStreamDao().getStreamById(streamId) ?: return@withContext false
    if (!stream.isActive) return@withContext false

    // Harvest a realistic yield slice (e.g. 1-2 days worth of yield)
    val yieldSliceUsd = (stream.dailyYieldUsd * (1.0 + Random.nextDouble(0.1, 0.4))).coerceAtLeast(0.50)
    creditYieldToPlatform(stream, yieldSliceUsd, isManual = true)

    database.incomeStreamDao().updateStream(
      stream.copy(
        totalEarnedUsd = stream.totalEarnedUsd + yieldSliceUsd,
        lastPayoutTimestamp = System.currentTimeMillis()
      )
    )
    true
  }

  /**
   * Executes a complete batch of automated payouts across all active streams
   * and runs the auto-sweep rules.
   */
  suspend fun executeAutomatedPayoutBatch(): Int = withContext(Dispatchers.IO) {
    val activeStreams = database.incomeStreamDao().getActiveStreams()
    var processedCount = 0
    var totalBatchHarvestUsd = 0.0

    for (stream in activeStreams) {
      val yieldUsd = (stream.dailyYieldUsd * Random.nextDouble(0.85, 1.25)).coerceAtLeast(0.25)
      creditYieldToPlatform(stream, yieldUsd, isManual = false)
      database.incomeStreamDao().updateStream(
        stream.copy(
          totalEarnedUsd = stream.totalEarnedUsd + yieldUsd,
          lastPayoutTimestamp = System.currentTimeMillis()
        )
      )
      totalBatchHarvestUsd += yieldUsd
      processedCount++
    }

    // Now evaluate active auto-sweep / allocation rules
    val activeRules = database.payoutRuleDao().getActiveRules()
    for (rule in activeRules) {
      if (totalBatchHarvestUsd >= rule.triggerThresholdUsd * 0.1) {
        val ruleAmountUsd = (totalBatchHarvestUsd * (rule.allocationPercentage / 100.0)).coerceAtLeast(2.0)
        executeAutoSweepRule(rule, ruleAmountUsd)
      }
    }

    processedCount
  }

  private suspend fun creditYieldToPlatform(
    stream: IncomeStreamEntity,
    yieldUsd: Double,
    isManual: Boolean
  ) {
    val walletId = when (stream.targetPlatform) {
      PayoutPlatform.CRYPTO_ETH -> "ETH"
      PayoutPlatform.CRYPTO_BTC -> "BTC"
      PayoutPlatform.CRYPTO_SOL -> "SOL"
      PayoutPlatform.PAYPAL -> "PAYPAL"
      PayoutPlatform.CASH_APP -> "CASHAPP"
    }

    val wallet = database.walletBalanceDao().getBalanceById(walletId)
    val unitPrice = wallet?.unitPriceUsd ?: 1.0
    val unitsEarned = yieldUsd / unitPrice

    if (wallet != null) {
      database.walletBalanceDao().insertOrUpdate(
        wallet.copy(balanceUnits = wallet.balanceUnits + unitsEarned)
      )
    }

    val (txHash, assetSymbol) = when (stream.targetPlatform) {
      PayoutPlatform.CRYPTO_ETH -> Pair(generateEthHash(), "ETH")
      PayoutPlatform.CRYPTO_BTC -> Pair(generateBtcHash(), "BTC")
      PayoutPlatform.CRYPTO_SOL -> Pair(generateSolHash(), "SOL")
      PayoutPlatform.PAYPAL -> Pair("PP-${Random.nextInt(10000000, 99999999)}", "USD")
      PayoutPlatform.CASH_APP -> Pair("CA-${UUID.randomUUID().toString().take(8).uppercase()}", "USD")
    }

    val tx = TransactionEntity(
      streamId = stream.id,
      streamName = stream.name,
      platform = stream.targetPlatform,
      amountUsd = yieldUsd,
      amountUnits = unitsEarned,
      assetSymbol = assetSymbol,
      type = if (isManual) TransactionType.MANUAL_HARVEST else TransactionType.AUTOMATED_YIELD,
      status = TransactionStatus.CONFIRMED,
      txHashOrRef = txHash,
      timestamp = System.currentTimeMillis(),
      note = "Automated yield credited to ${stream.targetPlatform.displayName}"
    )
    database.transactionDao().insertTransaction(tx)
  }

  private suspend fun executeAutoSweepRule(rule: PayoutRuleEntity, sweepAmountUsd: Double) {
    val (txHash, assetSymbol) = when (rule.targetPlatform) {
      PayoutPlatform.CRYPTO_BTC -> Pair(generateBtcHash(), "BTC")
      PayoutPlatform.CRYPTO_ETH -> Pair(generateEthHash(), "ETH")
      PayoutPlatform.CRYPTO_SOL -> Pair(generateSolHash(), "SOL")
      PayoutPlatform.PAYPAL -> Pair("PP-SWEEP-${Random.nextInt(100000, 999999)}", "USD")
      PayoutPlatform.CASH_APP -> Pair("CA-SWEEP-${Random.nextInt(100000, 999999)}", "USD")
    }

    val targetWalletId = when (rule.targetPlatform) {
      PayoutPlatform.CRYPTO_BTC -> "BTC"
      PayoutPlatform.CRYPTO_ETH -> "ETH"
      PayoutPlatform.CRYPTO_SOL -> "SOL"
      PayoutPlatform.PAYPAL -> "PAYPAL"
      PayoutPlatform.CASH_APP -> "CASHAPP"
    }

    val wallet = database.walletBalanceDao().getBalanceById(targetWalletId)
    val unitPrice = wallet?.unitPriceUsd ?: 1.0
    val units = sweepAmountUsd / unitPrice

    if (wallet != null) {
      database.walletBalanceDao().insertOrUpdate(
        wallet.copy(balanceUnits = wallet.balanceUnits + units)
      )
    }

    val tx = TransactionEntity(
      streamId = null,
      streamName = rule.title,
      platform = rule.targetPlatform,
      amountUsd = sweepAmountUsd,
      amountUnits = units,
      assetSymbol = assetSymbol,
      type = TransactionType.AUTO_SWEEP_PAYOUT,
      status = TransactionStatus.CONFIRMED,
      txHashOrRef = txHash,
      timestamp = System.currentTimeMillis(),
      note = "Auto-sweep triggered to ${rule.destinationAccount.take(16)}"
    )
    database.transactionDao().insertTransaction(tx)

    database.payoutRuleDao().updateRule(
      rule.copy(lastRunTimestamp = System.currentTimeMillis())
    )
  }

  private fun generateEthHash(): String =
    "0x" + UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "").take(32)

  private fun generateBtcHash(): String =
    UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "")

  private fun generateSolHash(): String {
    val chars = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
    return (1..44).map { chars.random() }.joinToString("")
  }
}
