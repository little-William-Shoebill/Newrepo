package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.IncomeStreamDao
import com.example.data.dao.PayoutRuleDao
import com.example.data.dao.TransactionDao
import com.example.data.dao.WalletBalanceDao
import com.example.data.entity.IncomeStreamEntity
import com.example.data.entity.PayoutRuleEntity
import com.example.data.entity.TransactionEntity
import com.example.data.entity.WalletBalanceEntity
import com.example.data.model.AssetType
import com.example.data.model.PayoutFrequency
import com.example.data.model.PayoutPlatform
import com.example.data.model.StreamCategory
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    IncomeStreamEntity::class,
    WalletBalanceEntity::class,
    PayoutRuleEntity::class,
    TransactionEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class YieldFlowDatabase : RoomDatabase() {

  abstract fun incomeStreamDao(): IncomeStreamDao
  abstract fun walletBalanceDao(): WalletBalanceDao
  abstract fun payoutRuleDao(): PayoutRuleDao
  abstract fun transactionDao(): TransactionDao

  companion object {
    @Volatile
    private var INSTANCE: YieldFlowDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): YieldFlowDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          YieldFlowDatabase::class.java,
          "yieldflow_vault.db"
        )
        .addCallback(DatabaseSeedCallback(scope))
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }

  private class DatabaseSeedCallback(
    private val scope: CoroutineScope
  ) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      INSTANCE?.let { database ->
        scope.launch(Dispatchers.IO) {
          populateInitialData(database)
        }
      }
    }

    private suspend fun populateInitialData(database: YieldFlowDatabase) {
      val balanceDao = database.walletBalanceDao()
      val streamDao = database.incomeStreamDao()
      val ruleDao = database.payoutRuleDao()
      val txDao = database.transactionDao()

      // 1. Initial Wallet Balances
      val initialBalances = listOf(
        WalletBalanceEntity(
          id = "ETH",
          name = "Ethereum",
          assetType = AssetType.CRYPTO,
          balanceUnits = 2.45,
          unitPriceUsd = 3450.0,
          addressOrAccount = "0x71C839...E29F",
          iconSymbol = "ETH",
          colorHex = 0xFF627EEA
        ),
        WalletBalanceEntity(
          id = "BTC",
          name = "Bitcoin",
          assetType = AssetType.CRYPTO,
          balanceUnits = 0.125,
          unitPriceUsd = 64200.0,
          addressOrAccount = "bc1qxy2kgdy...3p83wlh",
          iconSymbol = "BTC",
          colorHex = 0xFFF7931A
        ),
        WalletBalanceEntity(
          id = "SOL",
          name = "Solana",
          assetType = AssetType.CRYPTO,
          balanceUnits = 34.50,
          unitPriceUsd = 158.0,
          addressOrAccount = "So111x982...mK41Q",
          iconSymbol = "SOL",
          colorHex = 0xFF14F195
        ),
        WalletBalanceEntity(
          id = "USDC",
          name = "USD Coin",
          assetType = AssetType.CRYPTO,
          balanceUnits = 2840.0,
          unitPriceUsd = 1.0,
          addressOrAccount = "0x71C839...USDC",
          iconSymbol = "USDC",
          colorHex = 0xFF2775CA
        ),
        WalletBalanceEntity(
          id = "PAYPAL",
          name = "PayPal Balance",
          assetType = AssetType.FIAT,
          balanceUnits = 1425.80,
          unitPriceUsd = 1.0,
          addressOrAccount = "jay.passive@gmail.com",
          iconSymbol = "PP",
          colorHex = 0xFF0070BA
        ),
        WalletBalanceEntity(
          id = "CASHAPP",
          name = "Cash App Balance",
          assetType = AssetType.FIAT,
          balanceUnits = 890.50,
          unitPriceUsd = 1.0,
          addressOrAccount = "\$YieldMaster209",
          iconSymbol = "CA",
          colorHex = 0xFF00D632
        )
      )
      balanceDao.insertAll(initialBalances)

      // 2. Initial Passive Streams
      val initialStreams = listOf(
        IncomeStreamEntity(
          id = 1,
          name = "Ethereum 2.0 Validator Staking",
          category = StreamCategory.CRYPTO_STAKING,
          targetPlatform = PayoutPlatform.CRYPTO_ETH,
          principalAmountUsd = 6900.0, // 2.0 ETH
          apyPercentage = 4.8,
          frequency = PayoutFrequency.DAILY,
          isActive = true,
          totalEarnedUsd = 482.30,
          destinationNote = "Auto-compounding directly to Cold Staking Contract"
        ),
        IncomeStreamEntity(
          id = 2,
          name = "Solana Liquid Staking (Marinade)",
          category = StreamCategory.CRYPTO_STAKING,
          targetPlatform = PayoutPlatform.CRYPTO_SOL,
          principalAmountUsd = 3160.0, // 20 SOL
          apyPercentage = 6.9,
          frequency = PayoutFrequency.DAILY,
          isActive = true,
          totalEarnedUsd = 286.40,
          destinationNote = "Epoch rewards routed to Solana Phantom wallet"
        ),
        IncomeStreamEntity(
          id = 3,
          name = "USDC Stablecoin DeFi Yield Pool",
          category = StreamCategory.DEFI_YIELD,
          targetPlatform = PayoutPlatform.CRYPTO_ETH,
          principalAmountUsd = 2500.0,
          apyPercentage = 8.5,
          frequency = PayoutFrequency.HOURLY,
          isActive = true,
          totalEarnedUsd = 192.10,
          destinationNote = "Aave V3 Liquidity Pool yield stream"
        ),
        IncomeStreamEntity(
          id = 4,
          name = "Digital Product Royalties (Templates)",
          category = StreamCategory.DIGITAL_ROYALTIES,
          targetPlatform = PayoutPlatform.PAYPAL,
          principalAmountUsd = 1800.0,
          apyPercentage = 18.2,
          frequency = PayoutFrequency.WEEKLY,
          isActive = true,
          totalEarnedUsd = 645.00,
          destinationNote = "Automated sales payouts to verified PayPal"
        ),
        IncomeStreamEntity(
          id = 5,
          name = "Cloud Dev Tools Affiliate Payouts",
          category = StreamCategory.AFFILIATE_REVENUE,
          targetPlatform = PayoutPlatform.PAYPAL,
          principalAmountUsd = 1200.0,
          apyPercentage = 14.5,
          frequency = PayoutFrequency.MONTHLY,
          isActive = true,
          totalEarnedUsd = 380.00,
          destinationNote = "Monthly recurring SaaS revenue share"
        ),
        IncomeStreamEntity(
          id = 6,
          name = "Cash App Boosts & Round-Up Savings",
          category = StreamCategory.AUTOMATED_SAVINGS,
          targetPlatform = PayoutPlatform.CASH_APP,
          principalAmountUsd = 800.0,
          apyPercentage = 4.5,
          frequency = PayoutFrequency.DAILY,
          isActive = true,
          totalEarnedUsd = 64.80,
          destinationNote = "Direct deposit interest & debit purchase cash-back"
        )
      )
      streamDao.insertAll(initialStreams)

      // 3. Initial Automation Rules
      val initialRules = listOf(
        PayoutRuleEntity(
          id = 1,
          title = "Auto-Sweep 40% Staking Profit to Cold Storage Bitcoin",
          sourceFilter = "CRYPTO_STAKING",
          targetPlatform = PayoutPlatform.CRYPTO_BTC,
          destinationAccount = "bc1qxy2kgdy...3p83wlh",
          allocationPercentage = 40,
          triggerThresholdUsd = 50.0,
          frequencyDays = 7,
          isEnabled = true
        ),
        PayoutRuleEntity(
          id = 2,
          title = "Auto-Route 30% Digital Royalties to PayPal for Expenses",
          sourceFilter = "DIGITAL_ROYALTIES",
          targetPlatform = PayoutPlatform.PAYPAL,
          destinationAccount = "jay.passive@gmail.com",
          allocationPercentage = 30,
          triggerThresholdUsd = 25.0,
          frequencyDays = 14,
          isEnabled = true
        ),
        PayoutRuleEntity(
          id = 3,
          title = "Emergency Liquid Buffer Auto-Deposit to Cash App",
          sourceFilter = "ALL_STREAMS",
          targetPlatform = PayoutPlatform.CASH_APP,
          destinationAccount = "\$YieldMaster209",
          allocationPercentage = 20,
          triggerThresholdUsd = 75.0,
          frequencyDays = 7,
          isEnabled = true
        )
      )
      ruleDao.insertAll(initialRules)

      // 4. Initial Recent Transactions
      val now = System.currentTimeMillis()
      val dayMillis = 86400000L
      val initialTxs = listOf(
        TransactionEntity(
          id = 1,
          streamId = 1,
          streamName = "Ethereum 2.0 Validator Staking",
          platform = PayoutPlatform.CRYPTO_ETH,
          amountUsd = 12.80,
          amountUnits = 0.00371,
          assetSymbol = "ETH",
          type = TransactionType.AUTOMATED_YIELD,
          status = TransactionStatus.CONFIRMED,
          txHashOrRef = "0x89fa12bc78...e49a2",
          timestamp = now - (dayMillis * 0),
          note = "Daily automated staking yield payout"
        ),
        TransactionEntity(
          id = 2,
          streamId = 2,
          streamName = "Solana Liquid Staking (Marinade)",
          platform = PayoutPlatform.CRYPTO_SOL,
          amountUsd = 8.45,
          amountUnits = 0.0535,
          assetSymbol = "SOL",
          type = TransactionType.AUTOMATED_YIELD,
          status = TransactionStatus.CONFIRMED,
          txHashOrRef = "5KNgz4yR8P...a9wX",
          timestamp = now - (dayMillis * 1),
          note = "Epoch reward automated distribution"
        ),
        TransactionEntity(
          id = 3,
          streamId = 4,
          streamName = "Digital Product Royalties",
          platform = PayoutPlatform.PAYPAL,
          amountUsd = 62.50,
          amountUnits = 62.50,
          assetSymbol = "USD",
          type = TransactionType.AUTO_SWEEP_PAYOUT,
          status = TransactionStatus.CONFIRMED,
          txHashOrRef = "PP-8491209341",
          timestamp = now - (dayMillis * 2),
          note = "Gumroad & LemonSqueezy webhook automated payout"
        ),
        TransactionEntity(
          id = 4,
          streamId = null,
          streamName = "Auto-Sweep Automation Rule #1",
          platform = PayoutPlatform.CRYPTO_BTC,
          amountUsd = 50.00,
          amountUnits = 0.00078,
          assetSymbol = "BTC",
          type = TransactionType.AUTO_SWEEP_PAYOUT,
          status = TransactionStatus.CONFIRMED,
          txHashOrRef = "bc1tx9824...77fa",
          timestamp = now - (dayMillis * 3),
          note = "Automated DCA allocation to Cold Storage"
        ),
        TransactionEntity(
          id = 5,
          streamId = 6,
          streamName = "Cash App Boosts & Round-Up Savings",
          platform = PayoutPlatform.CASH_APP,
          amountUsd = 14.20,
          amountUnits = 14.20,
          assetSymbol = "USD",
          type = TransactionType.AUTOMATED_YIELD,
          status = TransactionStatus.CONFIRMED,
          txHashOrRef = "CA-REF-90214",
          timestamp = now - (dayMillis * 4),
          note = "Direct debit round-up bonus auto-credited"
        )
      )
      txDao.insertAll(initialTxs)
    }
  }
}
