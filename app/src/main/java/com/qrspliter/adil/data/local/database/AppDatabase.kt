package com.qrspliter.adil.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.qrspliter.adil.data.local.dao.PaymentPartDao
import com.qrspliter.adil.data.local.dao.PaymentSessionDao
import com.qrspliter.adil.data.local.entity.PaymentPartEntity
import com.qrspliter.adil.data.local.entity.PaymentSessionEntity

@Database(
    entities = [
        PaymentSessionEntity::class,
        PaymentPartEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun paymentSessionDao(): PaymentSessionDao
    abstract fun paymentPartDao(): PaymentPartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "upi_payment_splitter.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
