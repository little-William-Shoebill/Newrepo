package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.PayoutRuleEntity
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldNeon
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
fun AutomationRulesScreen(
  rules: List<PayoutRuleEntity>,
  isSimulating: Boolean,
  onRunSimulation: () -> Unit,
  onToggleRule: (PayoutRuleEntity) -> Unit,
  onDeleteRule: (PayoutRuleEntity) -> Unit,
  onOpenAddRule: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
  val enabledRules = rules.filter { it.isEnabled }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(VaultDarkNavy)
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Auto-Pilot Engine Banner
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(VaultCardDark)
            .border(1.dp, VaultBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
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
                  text = "SMART ROUTING AUTO-PILOT",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp
                  ),
                  color = EmeraldNeon
                )
              }

              Surface(
                color = CyanNeon.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "${enabledRules.size} Active Rules",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = CyanNeon,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Automated Payment Routing Rules",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "When passive income streams yield rewards, smart automation automatically splits and routes funds to cold storage crypto, PayPal, or Cash App based on your predefined rules.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary,
              lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = onRunSimulation,
              enabled = !isSimulating,
              colors = ButtonDefaults.buttonColors(
                containerColor = CyanNeon,
                contentColor = Color(0xFF042F2C)
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("run_rules_simulation_btn")
            ) {
              if (isSimulating) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = Color(0xFF042F2C),
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Executing Smart Routing Batch...", fontWeight = FontWeight.Bold)
              } else {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simulate Immediate Auto-Sweep Run", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // 2. Rules List Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Active Automation Rules",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
          )
          Text(
            text = "${rules.size} total",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
      }

      if (rules.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(VaultCardDark)
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "No automation rules configured yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Tap the + button below to create your first rule!",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
              )
            }
          }
        }
      } else {
        items(rules, key = { it.id }) { rule ->
          RuleCardItem(
            rule = rule,
            onToggle = { onToggleRule(rule) },
            onDelete = { onDeleteRule(rule) }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(72.dp))
      }
    }

    // Floating Action Button
    FloatingActionButton(
      onClick = onOpenAddRule,
      containerColor = CyanNeon,
      contentColor = Color(0xFF042F2C),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("add_rule_fab")
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Create Automation Rule"
      )
    }
  }
}

@Composable
fun RuleCardItem(
  rule: PayoutRuleEntity,
  onToggle: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
  val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.US)

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = VaultCardDark,
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, VaultBorder, RoundedCornerShape(18.dp))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = rule.title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = if (rule.isEnabled) TextPrimary else TextMuted,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = Color(0xFF1E293B),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = rule.sourceFilter.replace("_", " "),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.Default.ArrowForward,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            PlatformBadge(platform = rule.targetPlatform)
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Switch(
            checked = rule.isEnabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = CyanNeon,
              checkedTrackColor = Color(0xFF0E7490),
              uncheckedThumbColor = TextMuted,
              uncheckedTrackColor = VaultCardElevated
            ),
            modifier = Modifier.testTag("rule_toggle_${rule.id}")
          )
          IconButton(onClick = onDelete) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Delete rule",
              tint = TextMuted,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Configuration parameters row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(VaultCardElevated)
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(text = "Allocation Split", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${rule.allocationPercentage}%",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = CyanNeon
          )
        }

        Column {
          Text(text = "Trigger Threshold", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "≥ ${currencyFormat.format(rule.triggerThresholdUsd)}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
          )
        }

        Column {
          Text(text = "Interval", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Every ${rule.frequencyDays}d",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Destination: ${rule.destinationAccount}",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Last executed: ${dateFormat.format(Date(rule.lastRunTimestamp))}",
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted,
        fontSize = 11.sp
      )
    }
  }
}
