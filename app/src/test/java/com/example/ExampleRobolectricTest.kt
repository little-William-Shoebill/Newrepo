package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.entity.IncomeStreamEntity
import com.example.data.model.PayoutFrequency
import com.example.data.model.PayoutPlatform
import com.example.data.model.StreamCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("YieldFlow", appName)
  }

  @Test
  fun `income stream calculates daily and monthly yield accurately`() {
    val stream = IncomeStreamEntity(
      name = "ETH Staking",
      category = StreamCategory.CRYPTO_STAKING,
      targetPlatform = PayoutPlatform.CRYPTO_ETH,
      principalAmountUsd = 10000.0,
      apyPercentage = 5.0,
      frequency = PayoutFrequency.DAILY
    )

    // Annual = 500 USD, Monthly ≈ 41.67 USD, Daily ≈ 1.37 USD
    assertEquals(500.0 / 12.0, stream.monthlyYieldUsd, 0.01)
    assertEquals(500.0 / 365.0, stream.dailyYieldUsd, 0.01)
    assertTrue(stream.isActive)
  }
}
