package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AssetType
import com.example.data.model.PayoutFrequency
import com.example.data.model.PayoutPlatform
import com.example.data.model.StreamCategory
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType

@Entity(tableName = "income_streams")
data class IncomeStreamEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val category: StreamCategory,
  val targetPlatform: PayoutPlatform,
  val principalAmountUsd: Double,
  val apyPercentage: Double,
  val frequency: PayoutFrequency,
  val isActive: Boolean = true,
  val lastPayoutTimestamp: Long = System.currentTimeMillis(),
  val totalEarnedUsd: Double = 0.0,
  val destinationNote: String = ""
) {
  val dailyYieldUsd: Double
    get() = (principalAmountUsd * (apyPercentage / 100.0)) / 365.0

  val monthlyYieldUsd: Double
    get() = (principalAmountUsd * (apyPercentage / 100.0)) / 12.0
}

@Entity(tableName = "wallet_balances")
data class WalletBalanceEntity(
  @PrimaryKey val id: String, // "BTC", "ETH", "SOL", "USDC", "PAYPAL_USD", "CASHAPP_USD"
  val name: String,
  val assetType: AssetType,
  val balanceUnits: Double,
  val unitPriceUsd: Double,
  val addressOrAccount: String,
  val iconSymbol: String,
  val colorHex: Long
) {
  val totalValueUsd: Double
    get() = balanceUnits * unitPriceUsd
}

@Entity(tableName = "payout_rules")
data class PayoutRuleEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val sourceFilter: String = "ALL_STREAMS", // or stream category
  val targetPlatform: PayoutPlatform,
  val destinationAccount: String,
  val allocationPercentage: Int, // e.g., 40%
  val triggerThresholdUsd: Double, // e.g., $50.00
  val frequencyDays: Int, // e.g. 7 for weekly
  val isEnabled: Boolean = true,
  val lastRunTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val streamId: Long? = null,
  val streamName: String,
  val platform: PayoutPlatform,
  val amountUsd: Double,
  val amountUnits: Double,
  val assetSymbol: String,
  val type: TransactionType,
  val status: TransactionStatus = TransactionStatus.CONFIRMED,
  val txHashOrRef: String,
  val timestamp: Long = System.currentTimeMillis(),
  val note: String = ""
)
