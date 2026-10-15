package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phones")
data class PhoneDevice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brand: String,                   // Apple, Samsung, Xiaomi, etc.
    val modelName: String,               // iPhone 16 Pro Max, Galaxy S25 Ultra, etc.
    val storageCapacity: String,         // 128GB, 256GB, 512GB, 1TB
    val ramSize: String = "8GB",         // 8GB, 12GB, 16GB
    val color: String,                   // Titanium, Midnight, Silver, etc.
    val condition: String,               // NEW_SEALED, LIKE_NEW, USED_GOOD, REFURBISHED
    val batteryHealth: Int = 100,        // 100%, 95%, 88%
    val imei1: String,
    val imei2: String = "",
    val purchasePrice: Double,           // Cost price
    val sellingPrice: Double,            // Market sell price
    val quantity: Int = 1,
    val supplierName: String = "",
    val warrantyMonths: Int = 12,        // 0, 3, 6, 12, 24
    val notes: String = "",
    val isSold: Boolean = false,
    val soldDate: Long? = null,
    val soldPrice: Double? = null,
    val customerName: String? = null,
    val customerPhone: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sale_invoices")
data class SaleInvoice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,           // INV-2026-001
    val customerName: String,
    val customerPhone: String,
    val itemsSummary: String,            // Descriptions of items
    val totalAmount: Double,
    val discountAmount: Double = 0.0,
    val taxAmount: Double = 0.0,
    val netTotal: Double,
    val paidAmount: Double,
    val remainingAmount: Double = 0.0,
    val paymentMethod: String,           // CASH, CARD, TRANSFER, INSTALLMENT
    val isInstallment: Boolean = false,
    val warrantyTerms: String = "ضمان المحل 6 أشهر شامل العيوب المصنعية",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "repair_tickets")
data class RepairTicket(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticketNumber: String,            // REP-2026-101
    val customerName: String,
    val customerPhone: String,
    val deviceModel: String,             // iPhone 15 Pro, Samsung S24
    val deviceColor: String = "",
    val devicePasscode: String = "",
    val problemDescription: String,      // تبديل شاشة أصلية + فحص منفذ الشحن
    val repairType: String = "SCREEN",   // SCREEN, BATTERY, CAMERA, CHARGING_PORT, MOTHERBOARD, SOFTWARE, OTHER
    val costParts: Double = 0.0,         // تكلفة قطع الغيار
    val serviceFee: Double = 0.0,        // أجر الصيانة
    val totalCost: Double = 0.0,         // التكلفة الإجمالية
    val paidAmount: Double = 0.0,        // المدفوع مقدماً
    val status: String = "RECEIVED",     // RECEIVED, IN_DIAGNOSIS, WAITING_PARTS, IN_REPAIR, READY, DELIVERED, CANCELLED
    val technicianName: String = "المهندس المختص",
    val receivedDate: Long = System.currentTimeMillis(),
    val expectedDeliveryDate: Long = System.currentTimeMillis() + 86400000L,
    val completedDate: Long? = null,
    val notes: String = ""
)

@Entity(tableName = "customer_debts")
data class CustomerDebt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerName: String,
    val customerPhone: String,
    val nationalIdOrRef: String = "",
    val description: String,             // تقسيط ايفون 16 برو ماكس
    val totalDebtAmount: Double,
    val totalPaidAmount: Double = 0.0,
    val monthlyInstallment: Double = 0.0,
    val installmentsCount: Int = 1,
    val paidInstallmentsCount: Int = 0,
    val nextDueDate: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000L),
    val status: String = "ACTIVE",       // ACTIVE, SETTLED, OVERDUE
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "accessories")
data class AccessoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,                    // شاحن ابل أصلي 20 واط تايب سي
    val category: String,                // CHARGERS, CASES, SCREEN_PROTECTORS, AUDIO, SMART_WATCH, SPARE_PARTS, OTHER
    val costPrice: Double,
    val sellingPrice: Double,
    val stockQuantity: Int,
    val minAlertQuantity: Int = 5,
    val barcode: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class StoreProfile(
    val storeName: String = "مبايلي برو للهواتف الذكية",
    val ownerName: String = "أحمد المنصور",
    val phoneNumber: String = "+966 50 123 4567",
    val address: String = "الرياض - شارع العليا العام",
    val taxNumber: String = "30012458900003",
    val currency: String = "ر.س",
    val defaultWarrantyTerms: String = "ضمان تجربة واستبدال لمدة 30 يوماً وضمان مصنعي لمدة عام"
)

// Condition Helper
enum class DeviceCondition(val key: String, val titleAr: String, val titleEn: String) {
    NEW_SEALED("NEW_SEALED", "جديد بالكرتون", "New Sealed"),
    LIKE_NEW("LIKE_NEW", "مستعمل كالجديد", "Like New (Mint)"),
    USED_GOOD("USED_GOOD", "مستعمل بحالة ممتازة", "Used (Good)"),
    REFURBISHED("REFURBISHED", "مجدد بضمان", "Refurbished");

    companion object {
        fun fromKey(key: String): DeviceCondition = entries.find { it.key == key } ?: LIKE_NEW
    }
}

// Repair Status Helper
enum class RepairStatus(val key: String, val titleAr: String, val colorHex: Long) {
    RECEIVED("RECEIVED", "تم الاستلام", 0xFF3B82F6),
    IN_DIAGNOSIS("IN_DIAGNOSIS", "قيد الفحص الدقيق", 0xFF8B5CF6),
    WAITING_PARTS("WAITING_PARTS", "بانتظار قطع الغيار", 0xFFF59E0B),
    IN_REPAIR("IN_REPAIR", "جاري الإصلاح", 0xFF06B6D4),
    READY("READY", "جاهز للتسليم", 0xFF10B981),
    DELIVERED("DELIVERED", "تم التسليم للعميل", 0xFF64748B),
    CANCELLED("CANCELLED", "ملغي / غير قابل للإصلاح", 0xFFEF4444);

    companion object {
        fun fromKey(key: String): RepairStatus = entries.find { it.key == key } ?: RECEIVED
    }
}
