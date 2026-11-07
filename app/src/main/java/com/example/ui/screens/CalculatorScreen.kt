package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultCardDark
import com.example.ui.theme.VaultCardElevated
import com.example.ui.theme.VaultDarkNavy
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

@Composable
fun CalculatorScreen(
  modifier: Modifier = Modifier
) {
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

  var initialCapital by remember { mutableFloatStateOf(10000f) }
  var monthlyContribution by remember { mutableFloatStateOf(500f) }
  var apyPercentage by remember { mutableFloatStateOf(8.5f) }
  var yearsHorizon by remember { mutableIntStateOf(5) }

  // Compound interest calculation
  val monthlyRate = (apyPercentage / 100.0) / 12.0
  val totalMonths = yearsHorizon * 12

  // Future value of initial capital: P * (1 + r)^n
  val fvInitial = initialCapital * (1.0 + monthlyRate).pow(totalMonths.toDouble())

  // Future value of annuity: PMT * (((1 + r)^n - 1) / r)
  val fvMonthly = if (monthlyRate > 0) {
    monthlyContribution * (((1.0 + monthlyRate).pow(totalMonths.toDouble()) - 1.0) / monthlyRate)
  } else {
    monthlyContribution * totalMonths.toDouble()
  }

  val totalFutureVault = fvInitial + fvMonthly
  val totalPrincipalInvested = initialCapital + (monthlyContribution * totalMonths)
  val totalCompoundInterestEarned = (totalFutureVault - totalPrincipalInvested).coerceAtLeast(0.0)

  // Projected passive monthly income once matured
  val projectedPassiveMonthly = totalFutureVault * ((apyPercentage / 100.0) / 12.0)
  val projectedPassiveDaily = projectedPassiveMonthly / 30.0

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(VaultDarkNavy),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Projection Output Card
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .background(VaultCardDark)
          .border(1.dp, VaultBorder, RoundedCornerShape(22.dp))
          .padding(20.dp)
      ) {
        Column {
          Text(
            text = "PROJECTED MATURE VAULT VALUE (IN $yearsHorizon YEARS)",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.1.sp
            ),
            color = CyanNeon
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = currencyFormat.format(totalFutureVault),
            style = MaterialTheme.typography.headlineLarge.copy(
              fontWeight = FontWeight.ExtraBold,
              fontSize = 32.sp
            ),
            color = TextPrimary
          )

          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(text = "Passive Cash Flow", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "+${currencyFormat.format(projectedPassiveMonthly)}/mo",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = EmeraldNeon
              )
              Text(
                text = "≈ +${currencyFormat.format(projectedPassiveDaily)}/day",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 11.sp
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(text = "Total Yield Earned", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "+${currencyFormat.format(totalCompoundInterestEarned)}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = CyanNeon
              )
              Text(
                text = "From ${currencyFormat.format(totalPrincipalInvested)} saved",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Ratio breakdown progress bar
          val principalRatio = (totalPrincipalInvested / totalFutureVault.coerceAtLeast(1.0)).toFloat().coerceIn(0f, 1f)
          val interestRatio = 1f - principalRatio

          Column {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
            ) {
              Box(
                modifier = Modifier
                  .weight(principalRatio.coerceAtLeast(0.01f))
                  .fillMaxSize()
                  .background(Color(0xFF3B82F6))
              )
              Box(
                modifier = Modifier
                  .weight(interestRatio.coerceAtLeast(0.01f))
                  .fillMaxSize()
                  .background(EmeraldNeon)
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(8.dp).height(8.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF3B82F6)))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Your Contributions (${(principalRatio * 100).toInt()}%)", color = TextSecondary, fontSize = 11.sp)
              }
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(8.dp).height(8.dp).clip(RoundedCornerShape(2.dp)).background(EmeraldNeon))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Passive Yield Growth (${(interestRatio * 100).toInt()}%)", color = EmeraldNeon, fontSize = 11.sp)
              }
            }
          }
        }
      }
    }

    // 2. Preset Strategies
    item {
      Text(
        text = "Quick Yield Strategies",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = TextPrimary
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        PresetButton(
          title = "ETH/SOL Staking",
          apy = "5.5% APY",
          isSelected = apyPercentage == 5.5f,
          onClick = { apyPercentage = 5.5f },
          modifier = Modifier.weight(1f)
        )
        PresetButton(
          title = "DeFi & Savings",
          apy = "8.5% APY",
          isSelected = apyPercentage == 8.5f,
          onClick = { apyPercentage = 8.5f },
          modifier = Modifier.weight(1f)
        )
        PresetButton(
          title = "Creator Royalties",
          apy = "16.0% APY",
          isSelected = apyPercentage == 16.0f,
          onClick = { apyPercentage = 16.0f },
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. Interactive Sliders Card
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(VaultCardDark)
          .border(1.dp, VaultBorder, RoundedCornerShape(20.dp))
          .padding(18.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
          Text(
            text = "Projection Parameters",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
          )

          // Initial Capital
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Starting Principal", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
              Text(text = currencyFormat.format(initialCapital), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            }
            Slider(
              value = initialCapital,
              onValueChange = { initialCapital = it },
              valueRange = 500f..100000f,
              steps = 199,
              colors = SliderDefaults.colors(thumbColor = EmeraldNeon, activeTrackColor = EmeraldNeon)
            )
          }

          // Monthly Savings
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Monthly Re-Investment", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
              Text(text = "+${currencyFormat.format(monthlyContribution)}/mo", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = CyanNeon)
            }
            Slider(
              value = monthlyContribution,
              onValueChange = { monthlyContribution = it },
              valueRange = 0f..5000f,
              steps = 100,
              colors = SliderDefaults.colors(thumbColor = CyanNeon, activeTrackColor = CyanNeon)
            )
          }

          // APY %
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Annual Percentage Yield (APY)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
              Text(text = String.format(Locale.US, "%.1f%% APY", apyPercentage), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = EmeraldNeon)
            }
            Slider(
              value = apyPercentage,
              onValueChange = { apyPercentage = it },
              valueRange = 2f..25f,
              steps = 46,
              colors = SliderDefaults.colors(thumbColor = EmeraldNeon, activeTrackColor = EmeraldNeon)
            )
          }

          // Time Horizon (Years)
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Compounding Horizon", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
              Text(text = "$yearsHorizon Years", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            }
            Slider(
              value = yearsHorizon.toFloat(),
              onValueChange = { yearsHorizon = it.toInt() },
              valueRange = 1f..15f,
              steps = 14,
              colors = SliderDefaults.colors(thumbColor = TextPrimary, activeTrackColor = TextPrimary)
            )
          }
        }
      }
    }
  }
}

@Composable
fun PresetButton(
  title: String,
  apy: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) EmeraldNeon.copy(alpha = 0.2f) else VaultCardDark,
    modifier = modifier
      .border(
        width = 1.dp,
        color = if (isSelected) EmeraldNeon else VaultBorder,
        shape = RoundedCornerShape(12.dp)
      )
  ) {
    Button(
      onClick = onClick,
      colors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = if (isSelected) EmeraldNeon else TextSecondary
      ),
      contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        Text(text = apy, fontSize = 10.sp, color = if (isSelected) EmeraldNeon else TextMuted)
      }
    }
  }
}
