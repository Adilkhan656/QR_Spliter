package com.qrspliter.adil.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qrspliter.adil.data.local.entity.PaymentPartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentPartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParts(parts: List<PaymentPartEntity>)

    @Query("SELECT * FROM payment_parts WHERE sessionId = :sessionId ORDER BY sequenceNumber ASC")
    suspend fun getPartsForSession(sessionId: String): List<PaymentPartEntity>

    @Query("SELECT * FROM payment_parts WHERE sessionId = :sessionId ORDER BY sequenceNumber ASC")
    fun observePartsForSession(sessionId: String): Flow<List<PaymentPartEntity>>

    @Query("SELECT * FROM payment_parts WHERE partId = :partId")
    suspend fun getPartById(partId: String): PaymentPartEntity?

    @Query("UPDATE payment_parts SET status = :status, paidAt = :paidAt WHERE partId = :partId")
    suspend fun updatePartStatus(partId: String, status: String, paidAt: Long?): Int
}
