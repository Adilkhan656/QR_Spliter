package com.qrspliter.adil.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qrspliter.adil.data.local.entity.PaymentSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PaymentSessionEntity)

    @Query("SELECT * FROM payment_sessions WHERE sessionId = :sessionId")
    suspend fun getSessionById(sessionId: String): PaymentSessionEntity?

    @Query("SELECT * FROM payment_sessions WHERE sessionId = :sessionId")
    fun observeSessionById(sessionId: String): Flow<PaymentSessionEntity?>

    @Query("SELECT * FROM payment_sessions ORDER BY createdAt DESC")
    fun observeAllSessions(): Flow<List<PaymentSessionEntity>>

    @Query("UPDATE payment_sessions SET status = :status WHERE sessionId = :sessionId")
    suspend fun updateSessionStatus(sessionId: String, status: String): Int

    @Query("DELETE FROM payment_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String): Int

    @Query("DELETE FROM payment_sessions WHERE createdAt < :cutoffTimestamp")
    suspend fun deleteSessionsOlderThan(cutoffTimestamp: Long): Int

    @Query("DELETE FROM payment_sessions")
    suspend fun deleteAllSessions(): Int
}
