package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.IncomeStreamEntity
import com.example.data.entity.PayoutRuleEntity
import com.example.data.entity.TransactionEntity
import com.example.data.entity.WalletBalanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeStreamDao {
  @Query("SELECT * FROM income_streams ORDER BY id DESC")
  fun getAllStreams(): Flow<List<IncomeStreamEntity>>

  @Query("SELECT * FROM income_streams WHERE isActive = 1")
  suspend fun getActiveStreams(): List<IncomeStreamEntity>

  @Query("SELECT * FROM income_streams WHERE id = :id")
  suspend fun getStreamById(id: Long): IncomeStreamEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStream(stream: IncomeStreamEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(streams: List<IncomeStreamEntity>)

  @Update
  suspend fun updateStream(stream: IncomeStreamEntity)

  @Delete
  suspend fun deleteStream(stream: IncomeStreamEntity)
}

@Dao
interface WalletBalanceDao {
  @Query("SELECT * FROM wallet_balances ORDER BY (balanceUnits * unitPriceUsd) DESC")
  fun getAllBalances(): Flow<List<WalletBalanceEntity>>

  @Query("SELECT * FROM wallet_balances")
  suspend fun getBalancesList(): List<WalletBalanceEntity>

  @Query("SELECT * FROM wallet_balances WHERE id = :id")
  suspend fun getBalanceById(id: String): WalletBalanceEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(balance: WalletBalanceEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(balances: List<WalletBalanceEntity>)
}

@Dao
interface PayoutRuleDao {
  @Query("SELECT * FROM payout_rules ORDER BY id DESC")
  fun getAllRules(): Flow<List<PayoutRuleEntity>>

  @Query("SELECT * FROM payout_rules WHERE isEnabled = 1")
  suspend fun getActiveRules(): List<PayoutRuleEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRule(rule: PayoutRuleEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(rules: List<PayoutRuleEntity>)

  @Update
  suspend fun updateRule(rule: PayoutRuleEntity)

  @Delete
  suspend fun deleteRule(rule: PayoutRuleEntity)
}

@Dao
interface TransactionDao {
  @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
  fun getAllTransactions(): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit")
  fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(transaction: TransactionEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(transactions: List<TransactionEntity>)

  @Query("DELETE FROM transactions")
  suspend fun clearAll()
}
