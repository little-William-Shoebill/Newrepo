package com.example

import com.example.data.entity.IncomeStreamEntity
import com.example.data.entity.WalletBalanceEntity
import com.example.data.model.AssetType
import com.example.data.model.PayoutFrequency
import com.example.data.model.PayoutPlatform
import com.example.data.model.StreamCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testIncomeStreamYieldCalculations() {
    val stream = IncomeStreamEntity(
      name = "Ethereum Validator",
      category = StreamCategory.CRYPTO_STAKING,
      targetPlatform = PayoutPlatform.CRYPTO_ETH,
      principalAmountUsd = 12000.0,
      apyPercentage = 6.0,
      frequency = PayoutFrequency.DAILY
    )

    // Annual = $720
    // Monthly = $60
    // Daily = $720 / 365 = $1.9726
    assertEquals(60.0, stream.monthlyYieldUsd, 0.001)
    assertEquals(720.0 / 365.0, stream.dailyYieldUsd, 0.001)
    assertTrue(stream.isActive)
  }

  @Test
  fun testWalletBalanceTotalValue() {
    val ethWallet = WalletBalanceEntity(
      id = "ETH",
      name = "Ethereum",
      assetType = AssetType.CRYPTO,
      balanceUnits = 2.5,
      unitPriceUsd = 3400.0,
      addressOrAccount = "0x71C839...E29F",
      iconSymbol = "ETH",
      colorHex = 0xFF627EEA
    )

    assertEquals(8500.0, ethWallet.totalValueUsd, 0.001)
  }
}
