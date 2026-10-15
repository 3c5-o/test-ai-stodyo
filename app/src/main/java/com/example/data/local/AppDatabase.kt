package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        PhoneDevice::class,
        SaleInvoice::class,
        RepairTicket::class,
        CustomerDebt::class,
        AccessoryItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun phoneDao(): PhoneDao
    abstract fun saleDao(): SaleDao
    abstract fun repairDao(): RepairDao
    abstract fun debtDao(): DebtDao
    abstract fun accessoryDao(): AccessoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mobily_pro_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
