package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.ui.AppTab
import com.example.ui.YieldFlowViewModel
import com.example.ui.screens.AddRuleDialog
import com.example.ui.screens.AddStreamDialog
import com.example.ui.screens.AutomationRulesScreen
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.ConnectedAccountsScreen
import com.example.ui.screens.PassiveStreamsScreen
import com.example.ui.screens.SecurityLockScreen
import com.example.ui.screens.VaultDashboardScreen
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultCardDark
import com.example.ui.theme.VaultDarkNavy

class MainActivity : ComponentActivity() {
  private val viewModel: YieldFlowViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        YieldFlowMainApp(viewModel = viewModel)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YieldFlowMainApp(viewModel: YieldFlowViewModel) {
  val selectedTab by viewModel.selectedTab.collectAsState()
  val overview by viewModel.overview.collectAsState()
  val balances by viewModel.allBalances.collectAsState()
  val streams by viewModel.allStreams.collectAsState()
  val rules by viewModel.allRules.collectAsState()
  val transactions by viewModel.allTransactions.collectAsState()
  val isPrivacyMode by viewModel.isPrivacyMode.collectAsState()
  val isVaultLocked by viewModel.isVaultLocked.collectAsState()
  val isSimulating by viewModel.isSimulatingBatch.collectAsState()
  val statusMessage by viewModel.statusMessage.collectAsState()

  var showAddStreamDialog by remember { mutableStateOf(false) }
  var showAddRuleDialog by remember { mutableStateOf(false) }

  // If vault is locked, show PIN lock
  if (isVaultLocked) {
    SecurityLockScreen(
      onUnlock = { pin -> viewModel.unlockVault(pin) }
    )
    return
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(VaultDarkNavy),
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = EmeraldNeon.copy(alpha = 0.2f),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.size(32.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Shield,
                  contentDescription = null,
                  tint = EmeraldNeon,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "YieldFlow",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp
                ),
                color = TextPrimary
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(EmeraldNeon)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Vault Encrypted • Auto-Pilot Active",
                  style = MaterialTheme.typography.labelSmall,
                  color = EmeraldNeon,
                  fontSize = 10.sp
                )
              }
            }
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.togglePrivacyMode() },
            modifier = Modifier.testTag("appbar_privacy_btn")
          ) {
            Icon(
              imageVector = if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
              contentDescription = "Toggle Balance Privacy",
              tint = TextSecondary
            )
          }
          IconButton(
            onClick = { viewModel.lockVault() },
            modifier = Modifier.testTag("appbar_lock_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Lock Vault",
              tint = TextSecondary
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = VaultDarkNavy,
          scrolledContainerColor = VaultDarkNavy
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = VaultCardDark,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("main_bottom_nav")
      ) {
        val navItems = listOf(
          Triple(AppTab.VAULT, Icons.Default.AccountBalance, "Vault"),
          Triple(AppTab.STREAMS, Icons.Default.TrendingUp, "Streams"),
          Triple(AppTab.AUTOMATION, Icons.Default.Autorenew, "Auto-Pilot"),
          Triple(AppTab.CALCULATOR, Icons.Default.Calculate, "Calculator"),
          Triple(AppTab.ACCOUNTS, Icons.Default.AccountBalanceWallet, "Endpoints")
        )

        navItems.forEach { (tab, icon, label) ->
          val isSelected = selectedTab == tab
          NavigationBarItem(
            selected = isSelected,
            onClick = { viewModel.setTab(tab) },
            icon = {
              Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) EmeraldNeon else TextSecondary
              )
            },
            label = {
              Text(
                text = label,
                color = if (isSelected) EmeraldNeon else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = EmeraldNeon.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(VaultDarkNavy)
    ) {
      when (selectedTab) {
        AppTab.VAULT -> {
          VaultDashboardScreen(
            overview = overview,
            balances = balances,
            transactions = transactions,
            isPrivacyMode = isPrivacyMode,
            isSimulating = isSimulating,
            onTogglePrivacy = { viewModel.togglePrivacyMode() },
            onLockVault = { viewModel.lockVault() },
            onRunSimulation = { viewModel.runAutomatedPayoutSimulation() },
            onNavigateToTab = { tab -> viewModel.setTab(tab) },
            onOpenAddStream = { showAddStreamDialog = true }
          )
        }
        AppTab.STREAMS -> {
          PassiveStreamsScreen(
            streams = streams,
            onHarvestStream = { stream -> viewModel.harvestSingleStream(stream) },
            onToggleActive = { stream -> viewModel.toggleStreamActive(stream) },
            onDeleteStream = { stream -> viewModel.deleteStream(stream) },
            onOpenAddStream = { showAddStreamDialog = true }
          )
        }
        AppTab.AUTOMATION -> {
          AutomationRulesScreen(
            rules = rules,
            isSimulating = isSimulating,
            onRunSimulation = { viewModel.runAutomatedPayoutSimulation() },
            onToggleRule = { rule -> viewModel.toggleRuleEnabled(rule) },
            onDeleteRule = { rule -> viewModel.deleteRule(rule) },
            onOpenAddRule = { showAddRuleDialog = true }
          )
        }
        AppTab.CALCULATOR -> {
          CalculatorScreen()
        }
        AppTab.ACCOUNTS -> {
          ConnectedAccountsScreen(
            balances = balances,
            onUpdateAddress = { id, newAddress -> viewModel.updateWalletAddress(id, newAddress) }
          )
        }
      }

      // Floating Notification Banner for Feedback
      AnimatedVisibility(
        visible = statusMessage != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        statusMessage?.let { msg ->
          Surface(
            color = VaultCardDark,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldNeon,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = msg,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary,
                modifier = Modifier.weight(1f)
              )
              IconButton(
                onClick = { viewModel.clearStatusMessage() },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Dismiss",
                  tint = TextSecondary,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }
  }

  // Dialogs
  if (showAddStreamDialog) {
    AddStreamDialog(
      onDismiss = { showAddStreamDialog = false },
      onConfirm = { name, category, platform, principal, apy, frequency, note ->
        viewModel.addNewStream(name, category, platform, principal, apy, frequency, note)
        showAddStreamDialog = false
      }
    )
  }

  if (showAddRuleDialog) {
    AddRuleDialog(
      onDismiss = { showAddRuleDialog = false },
      onConfirm = { title, sourceFilter, targetPlatform, destinationAccount, allocationPercentage, thresholdUsd, frequencyDays ->
        viewModel.addNewRule(title, sourceFilter, targetPlatform, destinationAccount, allocationPercentage, thresholdUsd, frequencyDays)
        showAddRuleDialog = false
      }
    )
  }
}
