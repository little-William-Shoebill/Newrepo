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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.entity.WalletBalanceEntity
import com.example.data.model.PayoutPlatform
import com.example.ui.components.CopyableAddressCard
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedBg
import com.example.ui.theme.CashAppGreen
import com.example.ui.theme.CryptoBtcGold
import com.example.ui.theme.CryptoEthBlue
import com.example.ui.theme.CryptoSolGreen
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.PayPalBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultCardDark
import com.example.ui.theme.VaultCardElevated
import com.example.ui.theme.VaultDarkNavy

@Composable
fun ConnectedAccountsScreen(
  balances: List<WalletBalanceEntity>,
  onUpdateAddress: (id: String, newAddress: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var editingAsset by remember { mutableStateOf<WalletBalanceEntity?>(null) }
  var editedAddressText by remember { mutableStateOf("") }

  var expandedGuideIndex by remember { mutableStateOf<Int?>(0) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(VaultDarkNavy),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Destination Manager Header
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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AccountBalanceWallet,
              contentDescription = null,
              tint = CyanNeon,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "CONNECTED PAYOUT DESTINATIONS",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp
              ),
              color = CyanNeon
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "External Wallet & Account Endpoints",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Manage your verified payout addresses for PayPal, Cash App, and Crypto networks. The YieldFlow engine routes automated distributions into these destination accounts.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            lineHeight = 18.sp
          )
        }
      }
    }

    // 2. Connected Destinations List
    item {
      Text(
        text = "Configured Endpoints",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = TextPrimary
      )
    }

    items(balances, key = { it.id }) { asset ->
      val platform = when (asset.id) {
        "BTC" -> PayoutPlatform.CRYPTO_BTC
        "ETH" -> PayoutPlatform.CRYPTO_ETH
        "SOL" -> PayoutPlatform.CRYPTO_SOL
        "PAYPAL" -> PayoutPlatform.PAYPAL
        "CASHAPP" -> PayoutPlatform.CASH_APP
        else -> PayoutPlatform.CRYPTO_ETH
      }

      Surface(
        shape = RoundedCornerShape(14.dp),
        color = VaultCardDark,
        modifier = Modifier
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
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Color(asset.colorHex))
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = asset.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = asset.addressOrAccount,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary,
              fontSize = 11.sp
            )
          }

          IconButton(
            onClick = {
              editingAsset = asset
              editedAddressText = asset.addressOrAccount
            },
            modifier = Modifier.testTag("edit_endpoint_${asset.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit endpoint address",
              tint = CyanNeon,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }

    // 3. Legitimate Passive Income Blueprints
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Lightbulb,
          contentDescription = null,
          tint = EmeraldNeon,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Real-World Passive Income Blueprints",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = TextPrimary
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "How to legitimately establish and automate these income channels in real life.",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary
      )
    }

    val guides = listOf(
      Pair(
        "1. Ethereum & Solana Staking Mechanics",
        "• How it works: Proof-of-Stake blockchains reward validators for securing the network (typically 4%–7% APY).\n• Liquid Staking: Protocols like Lido (stETH) or Marinade (mSOL) let you earn continuous staking yield while keeping your tokens liquid.\n• Payouts: Automatically compounded directly into your decentralized wallet address every epoch."
      ),
      Pair(
        "2. Automated PayPal Creator Royalties",
        "• How it works: Create once, sell indefinitely. Digital templates, code boilerplates, or educational materials sold via platforms like LemonSqueezy, Gumroad, or Shopify.\n• Automation: Set payout schedules to auto-transfer earnings to your linked business PayPal account every Monday."
      ),
      Pair(
        "3. Cash App Savings & Direct Deposit Yield",
        "• How it works: Cash App offers high-yield interest on savings balances when direct deposit is enabled, plus debit card Round-Up investing.\n• Automation: Every debit purchase rounds up to the nearest dollar and auto-invests into Bitcoin or cash reserves."
      ),
      Pair(
        "4. Developer SaaS & Affiliate Commmissions",
        "• How it works: Recommend cloud hosting, APIs, or developer tooling. Earn 10%–30% recurring monthly revenue share as long as customers stay subscribed.\n• Payouts: Automatic monthly wire/PayPal routing with zero manual work."
      )
    )

    items(guides.indices.toList()) { index ->
      val (title, body) = guides[index]
      val isExpanded = expandedGuideIndex == index

      Surface(
        shape = RoundedCornerShape(14.dp),
        color = VaultCardDark,
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, VaultBorder, RoundedCornerShape(14.dp))
          .clickable { expandedGuideIndex = if (isExpanded) null else index }
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = title,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = TextPrimary,
              modifier = Modifier.weight(1f)
            )
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = null,
              tint = TextSecondary
            )
          }

          AnimatedVisibility(visible = isExpanded) {
            Column {
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 20.sp
              )
            }
          }
        }
      }
    }

    // 4. Financial Wellness & Anti-Scam Shield
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(AlertRedBg)
          .border(1.dp, AlertRed.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
          .padding(16.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = AlertRed,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "CRITICAL FINANCIAL SAFETY WARNING",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = AlertRed
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "• Legitimate passive yields come from real economic activity (staking consensus fees, real customer sales, or bank interest).",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFFCA5A5),
            lineHeight = 18.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "• Beware of scams: Any program promising 'guaranteed 1000% APY', 'free money bots', or 'instant cloud mining' is a fraud.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFFCA5A5),
            lineHeight = 18.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "• Never share your private seed phrase or transfer upfront fees to unverified intermediaries.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFFCA5A5),
            lineHeight = 18.sp
          )
        }
      }
    }
  }

  // Edit endpoint address dialog
  if (editingAsset != null) {
    val asset = editingAsset!!
    AlertDialog(
      onDismissRequest = { editingAsset = null },
      containerColor = VaultCardDark,
      title = {
        Text(
          text = "Update ${asset.name} Endpoint",
          color = TextPrimary,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column {
          Text(
            text = "Enter your new verified address, \$cashtag, or email destination for automated payouts:",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = editedAddressText,
            onValueChange = { editedAddressText = it },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = CyanNeon,
              unfocusedBorderColor = VaultBorder,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("edit_address_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (editedAddressText.isNotBlank()) {
              onUpdateAddress(asset.id, editedAddressText.trim())
              editingAsset = null
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color(0xFF042F2C)),
          modifier = Modifier.testTag("save_address_btn")
        ) {
          Text("Save Destination", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { editingAsset = null }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}
