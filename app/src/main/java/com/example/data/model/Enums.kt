package com.example.data.model

enum class StreamCategory(val displayName: String, val badgeColorHex: Long) {
  CRYPTO_STAKING("Crypto Staking & Validator", 0xFF8B5CF6),
  DEFI_YIELD("DeFi Yield Vault", 0xFF06B6D4),
  DIGITAL_ROYALTIES("Creator & Digital Royalties", 0xFFF59E0B),
  AFFILIATE_REVENUE("Affiliate & Referral Streams", 0xFF3B82F6),
  AUTOMATED_SAVINGS("Automated Cash-Back & Sweeps", 0xFF10B981)
}

enum class PayoutPlatform(val displayName: String, val symbol: String, val brandColorHex: Long) {
  CRYPTO_BTC("Bitcoin Network", "BTC", 0xFFF7931A),
  CRYPTO_ETH("Ethereum (ERC-20)", "ETH", 0xFF627EEA),
  CRYPTO_SOL("Solana Network", "SOL", 0xFF14F195),
  PAYPAL("PayPal Payouts", "USD", 0xFF0070BA),
  CASH_APP("Cash App Direct", "USD", 0xFF00D632)
}

enum class PayoutFrequency(val label: String, val daysInterval: Int) {
  HOURLY("Hourly Yield", 1),
  DAILY("Daily Automatic", 1),
  WEEKLY("Weekly Batch", 7),
  MONTHLY("Monthly Harvest", 30)
}

enum class AssetType {
  CRYPTO,
  FIAT
}

enum class TransactionType(val label: String) {
  AUTOMATED_YIELD("Automated Yield Inflow"),
  AUTO_SWEEP_PAYOUT("Auto-Sweep Payout"),
  REINVESTMENT("Compounded Reinvestment"),
  MANUAL_HARVEST("Manual Yield Collection"),
  TRANSIT_ROUTING("Vault Transfer")
}

enum class TransactionStatus {
  CONFIRMED,
  PROCESSING,
  SCHEDULED
}
