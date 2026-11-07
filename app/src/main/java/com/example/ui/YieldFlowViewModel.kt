package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.YieldFlowDatabase
import com.example.data.YieldFlowRepository
import com.example.data.entity.IncomeStreamEntity
import com.example.data.entity.PayoutRuleEntity
import com.example.data.entity.TransactionEntity
import com.example.data.entity.WalletBalanceEntity
import com.example.data.model.PayoutFrequency
import com.example.data.model.PayoutPlatform
import com.example.data.model.StreamCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
  VAULT("Vault"),
  STREAMS("Income Streams"),
  AUTOMATION("Auto-Pilot"),
  CALCULATOR("Calculator"),
  ACCOUNTS("Accounts & Guides")
}

data class YieldOverview(
  val netWorthUsd: Double = 0.0,
  val dailyYieldUsd: Double = 0.0,
  val monthlyYieldUsd: Double = 0.0,
  val activeStreamsCount: Int = 0,
  val totalEarnedUsd: Double = 0.0
)

class YieldFlowViewModel(application: Application) : AndroidViewModel(application) {

  private val database = YieldFlowDatabase.getDatabase(application, viewModelScope)
  private val repository = YieldFlowRepository(database)

  val allBalances: StateFlow<List<WalletBalanceEntity>> = repository.allBalances
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allStreams: StateFlow<List<IncomeStreamEntity>> = repository.allStreams
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allRules: StateFlow<List<PayoutRuleEntity>> = repository.allRules
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _selectedTab = MutableStateFlow(AppTab.VAULT)
  val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

  private val _isPrivacyMode = MutableStateFlow(false)
  val isPrivacyMode: StateFlow<Boolean> = _isPrivacyMode.asStateFlow()

  private val _isVaultLocked = MutableStateFlow(false)
  val isVaultLocked: StateFlow<Boolean> = _isVaultLocked.asStateFlow()

  private val _isSimulatingBatch = MutableStateFlow(false)
  val isSimulatingBatch: StateFlow<Boolean> = _isSimulatingBatch.asStateFlow()

  private val _statusMessage = MutableStateFlow<String?>(null)
  val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

  // Dynamic calculated overview
  val overview: StateFlow<YieldOverview> = combine(allBalances, allStreams) { balances, streams ->
    val netWorth = balances.sumOf { it.totalValueUsd }
    val activeStreams = streams.filter { it.isActive }
    val dailyYield = activeStreams.sumOf { it.dailyYieldUsd }
    val monthlyYield = activeStreams.sumOf { it.monthlyYieldUsd }
    val totalEarned = streams.sumOf { it.totalEarnedUsd }

    YieldOverview(
      netWorthUsd = netWorth,
      dailyYieldUsd = dailyYield,
      monthlyYieldUsd = monthlyYield,
      activeStreamsCount = activeStreams.size,
      totalEarnedUsd = totalEarned
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), YieldOverview())

  fun setTab(tab: AppTab) {
    _selectedTab.value = tab
  }

  fun togglePrivacyMode() {
    _isPrivacyMode.value = !_isPrivacyMode.value
  }

  fun lockVault() {
    _isVaultLocked.value = true
  }

  fun unlockVault(pin: String): Boolean {
    // Demo PIN: 2090 or any 4 digit matching
    if (pin == "2090" || pin == "0000" || pin.length == 4) {
      _isVaultLocked.value = false
      return true
    }
    return false
  }

  fun clearStatusMessage() {
    _statusMessage.value = null
  }

  fun runAutomatedPayoutSimulation() {
    if (_isSimulatingBatch.value) return
    viewModelScope.launch {
      _isSimulatingBatch.value = true
      _statusMessage.value = "Executing automated smart-routing payout batch..."
      delay(1200) // Realistic processing animation
      val count = repository.executeAutomatedPayoutBatch()
      _isSimulatingBatch.value = false
      _statusMessage.value = "Automated Payout Batch Executed: $count streams harvested and routed into digital wallets!"
    }
  }

  fun harvestSingleStream(stream: IncomeStreamEntity) {
    viewModelScope.launch {
      val success = repository.harvestStream(stream.id)
      if (success) {
        _statusMessage.value = "Harvested yield from ${stream.name}! Funds routed to ${stream.targetPlatform.displayName}."
      }
    }
  }

  fun toggleStreamActive(stream: IncomeStreamEntity) {
    viewModelScope.launch {
      repository.toggleStream(stream)
    }
  }

  fun deleteStream(stream: IncomeStreamEntity) {
    viewModelScope.launch {
      repository.deleteStream(stream)
      _statusMessage.value = "Stream removed."
    }
  }

  fun addNewStream(
    name: String,
    category: StreamCategory,
    platform: PayoutPlatform,
    principal: Double,
    apy: Double,
    frequency: PayoutFrequency,
    note: String
  ) {
    viewModelScope.launch {
      val stream = IncomeStreamEntity(
        name = name,
        category = category,
        targetPlatform = platform,
        principalAmountUsd = principal,
        apyPercentage = apy,
        frequency = frequency,
        isActive = true,
        destinationNote = note
      )
      repository.addStream(stream)
      _statusMessage.value = "New passive stream '${name}' added with automated routing."
    }
  }

  fun toggleRuleEnabled(rule: PayoutRuleEntity) {
    viewModelScope.launch {
      repository.toggleRule(rule)
    }
  }

  fun deleteRule(rule: PayoutRuleEntity) {
    viewModelScope.launch {
      repository.deleteRule(rule)
      _statusMessage.value = "Automation rule deleted."
    }
  }

  fun addNewRule(
    title: String,
    sourceFilter: String,
    targetPlatform: PayoutPlatform,
    destinationAccount: String,
    allocationPercentage: Int,
    thresholdUsd: Double,
    frequencyDays: Int
  ) {
    viewModelScope.launch {
      val rule = PayoutRuleEntity(
        title = title,
        sourceFilter = sourceFilter,
        targetPlatform = targetPlatform,
        destinationAccount = destinationAccount,
        allocationPercentage = allocationPercentage,
        triggerThresholdUsd = thresholdUsd,
        frequencyDays = frequencyDays,
        isEnabled = true
      )
      repository.addRule(rule)
      _statusMessage.value = "New automation rule '${title}' configured."
    }
  }

  fun updateWalletAddress(id: String, address: String) {
    viewModelScope.launch {
      repository.updateWalletAddress(id, address)
      _statusMessage.value = "Payout destination updated successfully."
    }
  }
}
