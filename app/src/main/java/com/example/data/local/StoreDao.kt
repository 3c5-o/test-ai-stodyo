package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PhoneDao {
    @Query("SELECT * FROM phones WHERE isSold = 0 ORDER BY id DESC")
    fun getInStockPhones(): Flow<List<PhoneDevice>>

    @Query("SELECT * FROM phones WHERE isSold = 1 ORDER BY soldDate DESC")
    fun getSoldPhones(): Flow<List<PhoneDevice>>

    @Query("SELECT * FROM phones ORDER BY id DESC")
    fun getAllPhones(): Flow<List<PhoneDevice>>

    @Query("SELECT * FROM phones WHERE id = :id")
    suspend fun getPhoneById(id: Long): PhoneDevice?

    @Query("SELECT * FROM phones WHERE imei1 LIKE '%' || :query || '%' OR imei2 LIKE '%' || :query || '%' OR modelName LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%'")
    fun searchPhones(query: String): Flow<List<PhoneDevice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhone(phone: PhoneDevice): Long

    @Update
    suspend fun updatePhone(phone: PhoneDevice)

    @Delete
    suspend fun deletePhone(phone: PhoneDevice)

    @Query("UPDATE phones SET isSold = 1, soldDate = :soldDate, soldPrice = :soldPrice, customerName = :customerName, customerPhone = :customerPhone WHERE id = :id")
    suspend fun markAsSold(id: Long, soldDate: Long, soldPrice: Double, customerName: String, customerPhone: String)
}

@Dao
interface SaleDao {
    @Query("SELECT * FROM sale_invoices ORDER BY createdAt DESC")
    fun getAllInvoices(): Flow<List<SaleInvoice>>

    @Query("SELECT * FROM sale_invoices WHERE id = :id")
    suspend fun getInvoiceById(id: Long): SaleInvoice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: SaleInvoice): Long

    @Delete
    suspend fun deleteInvoice(invoice: SaleInvoice)
}

@Dao
interface RepairDao {
    @Query("SELECT * FROM repair_tickets ORDER BY receivedDate DESC")
    fun getAllRepairTickets(): Flow<List<RepairTicket>>

    @Query("SELECT * FROM repair_tickets WHERE status != 'DELIVERED' AND status != 'CANCELLED' ORDER BY receivedDate DESC")
    fun getActiveRepairTickets(): Flow<List<RepairTicket>>

    @Query("SELECT * FROM repair_tickets WHERE id = :id")
    suspend fun getRepairTicketById(id: Long): RepairTicket?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepairTicket(ticket: RepairTicket): Long

    @Update
    suspend fun updateRepairTicket(ticket: RepairTicket)

    @Delete
    suspend fun deleteRepairTicket(ticket: RepairTicket)

    @Query("UPDATE repair_tickets SET status = :newStatus, completedDate = :completedDate WHERE id = :id")
    suspend fun updateStatus(id: Long, newStatus: String, completedDate: Long?)
}

@Dao
interface DebtDao {
    @Query("SELECT * FROM customer_debts ORDER BY nextDueDate ASC")
    fun getAllDebts(): Flow<List<CustomerDebt>>

    @Query("SELECT * FROM customer_debts WHERE status = 'ACTIVE' ORDER BY nextDueDate ASC")
    fun getActiveDebts(): Flow<List<CustomerDebt>>

    @Query("SELECT * FROM customer_debts WHERE id = :id")
    suspend fun getDebtById(id: Long): CustomerDebt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: CustomerDebt): Long

    @Update
    suspend fun updateDebt(debt: CustomerDebt)

    @Delete
    suspend fun deleteDebt(debt: CustomerDebt)
}

@Dao
interface AccessoryDao {
    @Query("SELECT * FROM accessories ORDER BY id DESC")
    fun getAllAccessories(): Flow<List<AccessoryItem>>

    @Query("SELECT * FROM accessories WHERE stockQuantity <= minAlertQuantity")
    fun getLowStockAccessories(): Flow<List<AccessoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccessory(item: AccessoryItem): Long

    @Update
    suspend fun updateAccessory(item: AccessoryItem)

    @Delete
    suspend fun deleteAccessory(item: AccessoryItem)
}
