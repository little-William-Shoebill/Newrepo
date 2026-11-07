package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultCardDark
import com.example.ui.theme.VaultCardElevated
import com.example.ui.theme.VaultDarkNavy

@Composable
fun SecurityLockScreen(
  onUnlock: (pin: String) -> Boolean,
  modifier: Modifier = Modifier
) {
  var enteredPin by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }

  fun handleKey(key: String) {
    if (enteredPin.length < 4) {
      val next = enteredPin + key
      enteredPin = next
      isError = false
      if (next.length == 4) {
        val success = onUnlock(next)
        if (!success) {
          isError = true
          enteredPin = ""
        }
      }
    }
  }

  fun handleDelete() {
    if (enteredPin.isNotEmpty()) {
      enteredPin = enteredPin.dropLast(1)
      isError = false
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(VaultDarkNavy),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp)
    ) {
      // Shield Icon
      Surface(
        color = EmeraldNeon.copy(alpha = 0.15f),
        shape = CircleShape,
        modifier = Modifier.size(72.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = EmeraldNeon,
            modifier = Modifier.size(36.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "YieldFlow Vault Locked",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = TextPrimary
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = if (isError) "Incorrect PIN. Default is 2090" else "Enter 4-digit PIN to decrypt balances (default: 2090)",
        style = MaterialTheme.typography.bodySmall,
        color = if (isError) AlertRed else TextSecondary,
        fontSize = 12.sp
      )

      Spacer(modifier = Modifier.height(28.dp))

      // PIN Indicators
      Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(4) { index ->
          val isFilled = index < enteredPin.length
          Box(
            modifier = Modifier
              .size(16.dp)
              .clip(CircleShape)
              .background(
                if (isError) AlertRed
                else if (isFilled) EmeraldNeon
                else VaultCardElevated
              )
              .border(
                width = 1.dp,
                color = if (isError) AlertRed else VaultBorder,
                shape = CircleShape
              )
          )
        }
      }

      Spacer(modifier = Modifier.height(40.dp))

      // Custom Keypad
      val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("BIO", "0", "DEL")
      )

      Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        keys.forEach { row ->
          Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
          ) {
            row.forEach { key ->
              when (key) {
                "BIO" -> {
                  Surface(
                    shape = CircleShape,
                    color = VaultCardDark,
                    modifier = Modifier
                      .size(68.dp)
                      .border(1.dp, VaultBorder, CircleShape)
                      .clickable { onUnlock("2090") }
                      .testTag("pin_key_bio")
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Quick unlock",
                        tint = CyanNeon,
                        modifier = Modifier.size(28.dp)
                      )
                    }
                  }
                }
                "DEL" -> {
                  Surface(
                    shape = CircleShape,
                    color = VaultCardDark,
                    modifier = Modifier
                      .size(68.dp)
                      .border(1.dp, VaultBorder, CircleShape)
                      .clickable { handleDelete() }
                      .testTag("pin_key_del")
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Icon(
                        imageVector = Icons.Default.Backspace,
                        contentDescription = "Delete",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                      )
                    }
                  }
                }
                else -> {
                  Surface(
                    shape = CircleShape,
                    color = VaultCardDark,
                    modifier = Modifier
                      .size(68.dp)
                      .border(1.dp, VaultBorder, CircleShape)
                      .clickable { handleKey(key) }
                      .testTag("pin_key_$key")
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        text = key,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      TextButton(
        onClick = { onUnlock("2090") },
        modifier = Modifier.testTag("skip_unlock_btn")
      ) {
        Text("Quick Unlock Vault", color = CyanNeon)
      }
    }
  }
}
