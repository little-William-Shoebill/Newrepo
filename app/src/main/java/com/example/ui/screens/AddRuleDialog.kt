package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PayoutPlatform
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultCardDark

@Composable
fun AddRuleDialog(
  onDismiss: () -> Unit,
  onConfirm: (
    title: String,
    sourceFilter: String,
    targetPlatform: PayoutPlatform,
    destinationAccount: String,
    allocationPercentage: Int,
    thresholdUsd: Double,
    frequencyDays: Int
  ) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var sourceFilter by remember { mutableStateOf("ALL_STREAMS") }
  var platform by remember { mutableStateOf(PayoutPlatform.PAYPAL) }
  var destinationAccount by remember { mutableStateOf("jay.passive@gmail.com") }
  var allocationText by remember { mutableStateOf("30") }
  var thresholdText by remember { mutableStateOf("50") }
  var frequencyDaysText by remember { mutableStateOf("7") }

  val sourceOptions = listOf(
    Pair("ALL_STREAMS", "All Streams"),
    Pair("CRYPTO_STAKING", "Crypto Staking"),
    Pair("DIGITAL_ROYALTIES", "Creator Royalties"),
    Pair("AFFILIATE_REVENUE", "Affiliate"),
    Pair("AUTOMATED_SAVINGS", "Savings Sweeps")
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = VaultCardDark,
    title = {
      Text(
        text = "Create Automation Rule",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = TextPrimary
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Rule Title (e.g. 50% Profit Auto-Sweep)") },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanNeon,
            unfocusedBorderColor = VaultBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth().testTag("rule_title_input")
        )

        Text(text = "Source Inflow Filter", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(sourceOptions) { (key, label) ->
            FilterChip(
              selected = sourceFilter == key,
              onClick = { sourceFilter = key },
              label = { Text(label, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CyanNeon,
                selectedLabelColor = Color(0xFF042F2C),
                containerColor = Color(0xFF1E293B),
                labelColor = TextSecondary
              )
            )
          }
        }

        Text(text = "Destination Platform", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(PayoutPlatform.values()) { plat ->
            FilterChip(
              selected = platform == plat,
              onClick = {
                platform = plat
                destinationAccount = when (plat) {
                  PayoutPlatform.PAYPAL -> "jay.passive@gmail.com"
                  PayoutPlatform.CASH_APP -> "\$YieldMaster209"
                  PayoutPlatform.CRYPTO_BTC -> "bc1qxy2kgdy...3p83wlh"
                  PayoutPlatform.CRYPTO_ETH -> "0x71C839...E29F"
                  PayoutPlatform.CRYPTO_SOL -> "So111x982...mK41Q"
                }
              },
              label = { Text(plat.displayName, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldNeon,
                selectedLabelColor = Color(0xFF022C22),
                containerColor = Color(0xFF1E293B),
                labelColor = TextSecondary
              )
            )
          }
        }

        OutlinedTextField(
          value = destinationAccount,
          onValueChange = { destinationAccount = it },
          label = { Text("Destination Address / Account") },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanNeon,
            unfocusedBorderColor = VaultBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth().testTag("rule_destination_input")
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = allocationText,
            onValueChange = { allocationText = it },
            label = { Text("Allocation (%)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = CyanNeon,
              unfocusedBorderColor = VaultBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.weight(1f).testTag("rule_allocation_input")
          )

          OutlinedTextField(
            value = thresholdText,
            onValueChange = { thresholdText = it },
            label = { Text("Min Yield ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = CyanNeon,
              unfocusedBorderColor = VaultBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.weight(1f).testTag("rule_threshold_input")
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val allocVal = allocationText.toIntOrNull()?.coerceIn(5, 100) ?: 30
          val threshVal = thresholdText.toDoubleOrNull()?.coerceAtLeast(5.0) ?: 50.0
          val freqDays = frequencyDaysText.toIntOrNull()?.coerceIn(1, 30) ?: 7
          val rTitle = if (title.isBlank()) "Auto-Sweep $allocVal% to ${platform.displayName}" else title.trim()
          onConfirm(rTitle, sourceFilter, platform, destinationAccount.trim(), allocVal, threshVal, freqDays)
        },
        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color(0xFF042F2C)),
        modifier = Modifier.testTag("submit_rule_btn")
      ) {
        Text("Save Rule", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}
