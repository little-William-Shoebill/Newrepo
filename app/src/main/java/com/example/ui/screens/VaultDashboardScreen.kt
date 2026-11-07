package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.TransactionEntity
import com.example.data.entity.WalletBalanceEntity
import com.example.data.model.TransactionType
import com.example.ui.AppTab
import com.example.ui.YieldOverview
import com.example.ui.components.PlatformBadge
import com.example.ui.components.StatMetricCard
import com.example.ui.components.YieldGrowthCanvasChart
import com.example.ui.theme.CashAppGreen
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.PayPalBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultCardDark
import com.example.ui.theme.VaultCardElevated
import com.example.ui.theme.VaultDarkNavy
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VaultDashboardScreen(
  overview: YieldOverview,
  balances: List<WalletBalanceEntity>,
  transactions: List<TransactionEntity>,
  isPrivacyMode: Boolean,
  isSimulating: Boolean,
  onTogglePrivacy: () -> Unit,
  onLockVault: () -> Unit,
  onRunSimulation: () -> Unit,
  onNavigateToTab: (AppTab) -> Unit,
  onOpenAddStream: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(VaultDarkNavy),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Vault Hero Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(24.dp))
          .background(
            Brush.linearGradient(
              colors = listOf(
                Color(0xFF0F2027),
                Color(0xFF203A43),
                Color(0xFF2C5364)
              )
            )
          )
          .border(1.dp, VaultBorder, RoundedCornerShape(24.dp))
          .padding(20.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(EmeraldNeon)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "SECURE MULTI-ASSET VAULT",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.2.sp
                ),
                color = EmeraldNeon
              )
            }

            Row {
              IconButton(
                onClick = onTogglePrivacy,
                modifier = Modifier.testTag("toggle_privacy_btn")
              ) {
                Icon(
                  imageVector = if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = "Toggle privacy",
                  tint = TextSecondary
                )
              }
              IconButton(
                onClick = onLockVault,
                modifier = Modifier.testTag("lock_vault_btn")
              ) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = "Lock vault",
                  tint = TextSecondary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Total Portfolio Balance",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (isPrivacyMode) "$••••••••" else currencyFormat.format(overview.netWorthUsd),
            style = MaterialTheme.typography.headlineLarge.copy(
              fontWeight = FontWeight.ExtraBold,
              fontSize = 32.sp
            ),
            color = TextPrimary
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Key metrics row inside hero
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "Daily Passive Rate",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = if (isPrivacyMode) "$•••/day" else "+${currencyFormat.format(overview.dailyYieldUsd)}/day",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = EmeraldNeon
              )
            }

            Column {
              Text(
                text = "Est. Monthly Income",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = if (isPrivacyMode) "$••••/mo" else "+${currencyFormat.format(overview.monthlyYieldUsd)}/mo",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = CyanNeon
              )
            }

            Column {
              Text(
                text = "Active Streams",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${overview.activeStreamsCount} Running",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Simulation Button
          Button(
            onClick = onRunSimulation,
            enabled = !isSimulating,
            colors = ButtonDefaults.buttonColors(
              containerColor = EmeraldNeon,
              contentColor = Color(0xFF022C22)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("run_simulation_btn")
          ) {
            if (isSimulating) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Color(0xFF022C22),
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Processing Automated Payouts...",
                fontWeight = FontWeight.Bold
              )
            } else {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Trigger Auto-Pilot Harvest & Sweep",
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // 2. Wallets & Assets Horizontal Carousel
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Digital Wallet Assets",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = TextPrimary
        )
        Text(
          text = "${balances.size} Assets",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
      }
    }

    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
      ) {
        items(balances, key = { it.id }) { asset ->
          WalletAssetCard(asset = asset, isPrivacyMode = isPrivacyMode)
        }
      }
    }

    // 3. 30-Day Historical Yield Growth Chart
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(VaultCardDark)
          .border(1.dp, VaultBorder, RoundedCornerShape(18.dp))
          .padding(16.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Cumulative Passive Yield Growth",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
              )
              Text(
                text = "All automated inflows from Crypto, PayPal & Cash App",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
            Text(
              text = "+16.8%",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
              color = EmeraldNeon
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Visual sparkline curve
          val samplePoints = listOf(
            220f, 260f, 290f, 350f, 410f, 460f, 540f, 620f, 750f, 890f,
            960f, 1080f, 1200f, 1340f, 1480f, 1620f, 1780f, 1940f, 2080f
          )
          YieldGrowthCanvasChart(
            dataPoints = samplePoints,
            lineColor = EmeraldNeon,
            modifier = Modifier
              .fillMaxWidth()
              .height(100.dp)
          )
        }
      }
    }

    // 4. Quick Action Grid
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatMetricCard(
          title = "Active Streams",
          value = "${overview.activeStreamsCount} Running",
          subtitle = "Tap to manage",
          accentColor = CyanNeon,
          modifier = Modifier
            .weight(1f)
            .clickable { onNavigateToTab(AppTab.STREAMS) }
        )
        StatMetricCard(
          title = "Auto-Pilot Rules",
          value = "3 Configured",
          subtitle = "Auto-routing",
          accentColor = EmeraldNeon,
          modifier = Modifier
            .weight(1f)
            .clickable { onNavigateToTab(AppTab.AUTOMATION) }
        )
      }
    }

    // 5. Recent Transaction Activity Feed
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Recent Automated Inflows & Sweeps",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = TextPrimary
        )
        Text(
          text = "${transactions.size} records",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
      }
    }

    if (transactions.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VaultCardDark)
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No transactions yet. Trigger the Auto-Pilot Harvest above!",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
          )
        }
      }
    } else {
      items(transactions.take(8), key = { it.id }) { tx ->
        TransactionItemCard(tx = tx, isPrivacyMode = isPrivacyMode)
      }
    }
  }
}

@Composable
fun WalletAssetCard(
  asset: WalletBalanceEntity,
  isPrivacyMode: Boolean,
  modifier: Modifier = Modifier
) {
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

  Box(
    modifier = modifier
      .width(160.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(VaultCardDark)
      .border(1.dp, VaultBorder, RoundedCornerShape(16.dp))
      .padding(14.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = Color(asset.colorHex).copy(alpha = 0.2f),
          shape = CircleShape,
          modifier = Modifier.size(32.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = asset.iconSymbol,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color(asset.colorHex),
              fontSize = 11.sp
            )
          }
        }

        Text(
          text = asset.name.take(9),
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = if (isPrivacyMode) "$••••" else currencyFormat.format(asset.totalValueUsd),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = TextPrimary,
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = if (isPrivacyMode) "••••" else String.format(Locale.US, "%.4f %s", asset.balanceUnits, asset.iconSymbol),
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        fontSize = 11.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
fun TransactionItemCard(
  tx: TransactionEntity,
  isPrivacyMode: Boolean,
  modifier: Modifier = Modifier
) {
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
  val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.US)

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = VaultCardDark,
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, VaultBorder, RoundedCornerShape(14.dp))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        PlatformBadge(platform = tx.platform)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = tx.streamName,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${tx.type.label} • ${dateFormat.format(Date(tx.timestamp))}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.height(1.dp))
          Text(
            text = "Ref: ${tx.txHashOrRef.take(14)}...",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 10.sp
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = if (isPrivacyMode) "+$•••" else "+${currencyFormat.format(tx.amountUsd)}",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = EmeraldNeon
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = if (isPrivacyMode) "••••" else String.format(Locale.US, "%.4f %s", tx.amountUnits, tx.assetSymbol),
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary,
          fontSize = 11.sp
        )
      }
    }
  }
}
