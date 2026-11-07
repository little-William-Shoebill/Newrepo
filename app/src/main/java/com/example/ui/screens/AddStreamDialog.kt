package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.data.model.PayoutFrequency
import com.example.data.model.PayoutPlatform
import com.example.data.model.StreamCategory
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultCardDark

@Composable
fun AddStreamDialog(
  onDismiss: () -> Unit,
  onConfirm: (
    name: String,
    category: StreamCategory,
    platform: PayoutPlatform,
    principal: Double,
    apy: Double,
    frequency: PayoutFrequency,
    note: String
  ) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var category by remember { mutableStateOf(StreamCategory.CRYPTO_STAKING) }
  var platform by remember { mutableStateOf(PayoutPlatform.CRYPTO_ETH) }
  var principalText by remember { mutableStateOf("1000") }
  var apyText by remember { mutableStateOf("6.5") }
  var frequency by remember { mutableStateOf(PayoutFrequency.DAILY) }
  var routingNote by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = VaultCardDark,
    title = {
      Text(
        text = "Add Passive Income Stream",
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
        // Stream Name
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Stream Name (e.g. SOL Liquid Staking)") },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EmeraldNeon,
            unfocusedBorderColor = VaultBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("stream_name_input")
        )

        // Category
        Text(text = "Income Category", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(StreamCategory.values()) { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat.displayName.take(14), fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldNeon,
                selectedLabelColor = Color(0xFF022C22),
                containerColor = Color(0xFF1E293B),
                labelColor = TextSecondary
              )
            )
          }
        }

        // Payout Target Platform
        Text(text = "Payout Target Destination", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(PayoutPlatform.values()) { plat ->
            FilterChip(
              selected = platform == plat,
              onClick = { platform = plat },
              label = { Text(plat.displayName, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CyanNeon,
                selectedLabelColor = Color(0xFF042F2C),
                containerColor = Color(0xFF1E293B),
                labelColor = TextSecondary
              )
            )
          }
        }

        // Principal & APY
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = principalText,
            onValueChange = { principalText = it },
            label = { Text("Principal ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = EmeraldNeon,
              unfocusedBorderColor = VaultBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.weight(1f).testTag("stream_principal_input")
          )

          OutlinedTextField(
            value = apyText,
            onValueChange = { apyText = it },
            label = { Text("APY (%)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = EmeraldNeon,
              unfocusedBorderColor = VaultBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.weight(1f).testTag("stream_apy_input")
          )
        }

        // Frequency
        Text(text = "Yield Frequency", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(PayoutFrequency.values()) { freq ->
            FilterChip(
              selected = frequency == freq,
              onClick = { frequency = freq },
              label = { Text(freq.label, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldNeon,
                selectedLabelColor = Color(0xFF022C22),
                containerColor = Color(0xFF1E293B),
                labelColor = TextSecondary
              )
            )
          }
        }

        // Routing Note
        OutlinedTextField(
          value = routingNote,
          onValueChange = { routingNote = it },
          label = { Text("Routing details (e.g. Phantom Wallet / PayPal)") },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EmeraldNeon,
            unfocusedBorderColor = VaultBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth().testTag("stream_note_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val pVal = principalText.toDoubleOrNull() ?: 500.0
          val aVal = apyText.toDoubleOrNull() ?: 5.0
          val sName = if (name.isBlank()) "${category.displayName} Yield" else name.trim()
          onConfirm(sName, category, platform, pVal, aVal, frequency, routingNote)
        },
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon, contentColor = Color(0xFF022C22)),
        modifier = Modifier.testTag("submit_stream_btn")
      ) {
        Text("Create Stream", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    }
  )
}
