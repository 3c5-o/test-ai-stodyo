package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class StoreRepository(
    private val phoneDao: PhoneDao,
    private val saleDao: SaleDao,
    private val repairDao: RepairDao,
    private val debtDao: DebtDao,
    private val accessoryDao: AccessoryDao
) {
    // Phones
    val inStockPhones: Flow<List<PhoneDevice>> = phoneDao.getInStockPhones()
    val soldPhones: Flow<List<PhoneDevice>> = phoneDao.getSoldPhones()
    val allPhones: Flow<List<PhoneDevice>> = phoneDao.getAllPhones()

    suspend fun getPhoneById(id: Long) = phoneDao.getPhoneById(id)
    suspend fun insertPhone(phone: PhoneDevice) = phoneDao.insertPhone(phone)
    suspend fun updatePhone(phone: PhoneDevice) = phoneDao.updatePhone(phone)
    suspend fun deletePhone(phone: PhoneDevice) = phoneDao.deletePhone(phone)
    suspend fun markPhoneSold(id: Long, soldDate: Long, soldPrice: Double, customerName: String, customerPhone: String) =
        phoneDao.markAsSold(id, soldDate, soldPrice, customerName, customerPhone)

    // Sales & Invoices
    val allInvoices: Flow<List<SaleInvoice>> = saleDao.getAllInvoices()
    suspend fun getInvoiceById(id: Long) = saleDao.getInvoiceById(id)
    suspend fun insertInvoice(invoice: SaleInvoice) = saleDao.insertInvoice(invoice)
    suspend fun deleteInvoice(invoice: SaleInvoice) = saleDao.deleteInvoice(invoice)

    // Repairs
    val allRepairTickets: Flow<List<RepairTicket>> = repairDao.getAllRepairTickets()
    val activeRepairTickets: Flow<List<RepairTicket>> = repairDao.getActiveRepairTickets()
    suspend fun getRepairTicketById(id: Long) = repairDao.getRepairTicketById(id)
    suspend fun insertRepairTicket(ticket: RepairTicket) = repairDao.insertRepairTicket(ticket)
    suspend fun updateRepairTicket(ticket: RepairTicket) = repairDao.updateRepairTicket(ticket)
    suspend fun deleteRepairTicket(ticket: RepairTicket) = repairDao.deleteRepairTicket(ticket)
    suspend fun updateRepairStatus(id: Long, newStatus: String, completedDate: Long? = null) =
        repairDao.updateStatus(id, newStatus, completedDate)

    // Debts & Installments
    val allDebts: Flow<List<CustomerDebt>> = debtDao.getAllDebts()
    val activeDebts: Flow<List<CustomerDebt>> = debtDao.getActiveDebts()
    suspend fun insertDebt(debt: CustomerDebt) = debtDao.insertDebt(debt)
    suspend fun updateDebt(debt: CustomerDebt) = debtDao.updateDebt(debt)
    suspend fun deleteDebt(debt: CustomerDebt) = debtDao.deleteDebt(debt)

    // Accessories
    val allAccessories: Flow<List<AccessoryItem>> = accessoryDao.getAllAccessories()
    val lowStockAccessories: Flow<List<AccessoryItem>> = accessoryDao.getLowStockAccessories()
    suspend fun insertAccessory(item: AccessoryItem) = accessoryDao.insertAccessory(item)
    suspend fun updateAccessory(item: AccessoryItem) = accessoryDao.updateAccessory(item)
    suspend fun deleteAccessory(item: AccessoryItem) = accessoryDao.deleteAccessory(item)

    // Restore Backup Data
    suspend fun restoreBackupData(backup: com.example.data.local.BackupContent) {
        backup.phones.forEach { phoneDao.insertPhone(it) }
        backup.invoices.forEach { saleDao.insertInvoice(it) }
        backup.repairs.forEach { repairDao.insertRepairTicket(it) }
        backup.debts.forEach { debtDao.insertDebt(it) }
        backup.accessories.forEach { accessoryDao.insertAccessory(it) }
    }

    // Populate initial rich demo data if empty
    suspend fun checkAndSeedInitialData() {
        val existingPhones = phoneDao.getAllPhones().first()
        if (existingPhones.isEmpty()) {
            val samplePhones = listOf(
                PhoneDevice(
                    brand = "Apple",
                    modelName = "iPhone 16 Pro Max",
                    storageCapacity = "256GB",
                    ramSize = "8GB",
                    color = "Desert Titanium (تيتانيوم صحراوي)",
                    condition = "NEW_SEALED",
                    batteryHealth = 100,
                    imei1 = "359874102345671",
                    imei2 = "359874102345672",
                    purchasePrice = 4550.0,
                    sellingPrice = 5150.0,
                    quantity = 3,
                    supplierName = "الموزع المعتمد أبل",
                    warrantyMonths = 24,
                    notes = "نسخة الشرق الأوسط شريحتين الكترونية وفعلية"
                ),
                PhoneDevice(
                    brand = "Samsung",
                    modelName = "Galaxy S25 Ultra",
                    storageCapacity = "512GB",
                    ramSize = "12GB",
                    color = "Titanium Silver (فضي تيتانيوم)",
                    condition = "NEW_SEALED",
                    batteryHealth = 100,
                    imei1 = "864125091827364",
                    imei2 = "864125091827365",
                    purchasePrice = 4200.0,
                    sellingPrice = 4799.0,
                    quantity = 2,
                    supplierName = "سامسونج الخليج",
                    warrantyMonths = 24,
                    notes = "مع قلم S-Pen مدمج ومعالج Snapdragon 8 Elite"
                ),
                PhoneDevice(
                    brand = "Apple",
                    modelName = "iPhone 15 Pro",
                    storageCapacity = "128GB",
                    ramSize = "8GB",
                    color = "Natural Titanium (تيتانيوم طبيعي)",
                    condition = "LIKE_NEW",
                    batteryHealth = 96,
                    imei1 = "354112984716253",
                    purchasePrice = 2850.0,
                    sellingPrice = 3300.0,
                    quantity = 1,
                    supplierName = "شراء من عميل مع الفحص",
                    warrantyMonths = 6,
                    notes = "خالي من الخدوش مع الكرتون وكيبل الشحن الأصلي"
                ),
                PhoneDevice(
                    brand = "Google",
                    modelName = "Pixel 9 Pro XL",
                    storageCapacity = "256GB",
                    ramSize = "16GB",
                    color = "Hazel (رمادي بندقي)",
                    condition = "NEW_SEALED",
                    batteryHealth = 100,
                    imei1 = "357788990011223",
                    purchasePrice = 3600.0,
                    sellingPrice = 4100.0,
                    quantity = 1,
                    supplierName = "استيراد دولي",
                    warrantyMonths = 12,
                    notes = "ذكاء اصطناعي فائق وكاميرا سينمائية"
                ),
                PhoneDevice(
                    brand = "Xiaomi",
                    modelName = "Xiaomi 14 Ultra",
                    storageCapacity = "512GB",
                    ramSize = "16GB",
                    color = "Black Leather (جلد أسود)",
                    condition = "LIKE_NEW",
                    batteryHealth = 98,
                    imei1 = "869911223344556",
                    purchasePrice = 2900.0,
                    sellingPrice = 3450.0,
                    quantity = 1,
                    supplierName = "شراء محلي",
                    warrantyMonths = 6,
                    notes = "عدسات لايكا الاحترافية + كيت التصوير كامل"
                ),
                PhoneDevice(
                    brand = "Apple",
                    modelName = "iPhone 14",
                    storageCapacity = "128GB",
                    ramSize = "6GB",
                    color = "Midnight (أسود ليلي)",
                    condition = "USED_GOOD",
                    batteryHealth = 89,
                    imei1 = "351122334455667",
                    purchasePrice = 1750.0,
                    sellingPrice = 2150.0,
                    quantity = 1,
                    supplierName = "استبدال من عميل",
                    warrantyMonths = 3,
                    notes = "تم فحص الشاشة والكاميرات وحالة البطارية ممتازة",
                    isSold = true,
                    soldDate = System.currentTimeMillis() - 86400000L * 2,
                    soldPrice = 2150.0,
                    customerName = "خالد العتيبي",
                    customerPhone = "0551239874"
                )
            )
            samplePhones.forEach { phoneDao.insertPhone(it) }

            val sampleInvoices = listOf(
                SaleInvoice(
                    invoiceNumber = "INV-2026-001",
                    customerName = "خالد العتيبي",
                    customerPhone = "0551239874",
                    itemsSummary = "هاتف iPhone 14 128GB مستعمل بحالة ممتازة + استكر حماية شاشة نانو سراميك + كفر ماج سيف أصلي",
                    totalAmount = 2300.0,
                    discountAmount = 50.0,
                    taxAmount = 0.0,
                    netTotal = 2250.0,
                    paidAmount = 2250.0,
                    remainingAmount = 0.0,
                    paymentMethod = "CARD",
                    warrantyTerms = "ضمان تجربة واستبدال 3 أشهر"
                ),
                SaleInvoice(
                    invoiceNumber = "INV-2026-002",
                    customerName = "سارة الشمري",
                    customerPhone = "0509876543",
                    itemsSummary = "شاحن Apple 35W ثنائي + سماعات AirPods Pro 2 Type-C",
                    totalAmount = 1080.0,
                    discountAmount = 30.0,
                    taxAmount = 0.0,
                    netTotal = 1050.0,
                    paidAmount = 1050.0,
                    remainingAmount = 0.0,
                    paymentMethod = "CASH",
                    warrantyTerms = "ضمان سنتين حاسبات العرب"
                )
            )
            sampleInvoices.forEach { saleDao.insertInvoice(it) }

            val sampleRepairs = listOf(
                RepairTicket(
                    ticketNumber = "REP-2026-101",
                    customerName = "فيصل الحربي",
                    customerPhone = "0543322110",
                    deviceModel = "iPhone 15 Pro",
                    deviceColor = "Natural Titanium",
                    devicePasscode = "2468",
                    problemDescription = "كسر في الزجاج الخارجي والشاشة مع فقدان حساسية اللمس في الجزء السفلي",
                    repairType = "SCREEN",
                    costParts = 550.0,
                    serviceFee = 200.0,
                    totalCost = 750.0,
                    paidAmount = 200.0,
                    status = "IN_REPAIR",
                    technicianName = "م. عمر (خبير أبل)",
                    notes = "تغيير شاشة أصلية مجددة مع نقل شريحة الـ True Tone"
                ),
                RepairTicket(
                    ticketNumber = "REP-2026-102",
                    customerName = "عبدالله الدوسري",
                    customerPhone = "0567788990",
                    deviceModel = "Samsung Galaxy S23 Ultra",
                    deviceColor = "Phantom Black",
                    devicePasscode = "نمط L",
                    problemDescription = "تراجع كفاءة البطارية وانتفاخ بسيط وانطفاء مفاجئ عند 20%",
                    repairType = "BATTERY",
                    costParts = 180.0,
                    serviceFee = 120.0,
                    totalCost = 300.0,
                    paidAmount = 300.0,
                    status = "READY",
                    technicianName = "م. سامي (أندرويد)",
                    notes = "تم تركيب بطارية وكالة أصلية وضبط معايرة الشحن، جاهز للتسليم"
                ),
                RepairTicket(
                    ticketNumber = "REP-2026-103",
                    customerName = "محمد القحطاني",
                    customerPhone = "0532211445",
                    deviceModel = "iPhone 14 Pro Max",
                    deviceColor = "Deep Purple",
                    problemDescription = "منفذ الشحن لا يستجيب وعدم شحن الجهاز نهائياً",
                    repairType = "CHARGING_PORT",
                    costParts = 120.0,
                    serviceFee = 130.0,
                    totalCost = 250.0,
                    paidAmount = 0.0,
                    status = "WAITING_PARTS",
                    technicianName = "م. عمر",
                    notes = "طلب فلكس شحن أصلي يصل مساء اليوم"
                )
            )
            sampleRepairs.forEach { repairDao.insertRepairTicket(it) }

            val sampleDebts = listOf(
                CustomerDebt(
                    customerName = "ماجد الغامدي",
                    customerPhone = "0501122334",
                    nationalIdOrRef = "1098765432",
                    description = "تقسيط هاتف iPhone 16 Pro 256GB",
                    totalDebtAmount = 4800.0,
                    totalPaidAmount = 2400.0,
                    monthlyInstallment = 800.0,
                    installmentsCount = 6,
                    paidInstallmentsCount = 3,
                    status = "ACTIVE"
                ),
                CustomerDebt(
                    customerName = "تركي السبيعي",
                    customerPhone = "0554433221",
                    nationalIdOrRef = "1055443322",
                    description = "متبقي فاتورة أجهزة وإكسسوارات",
                    totalDebtAmount = 950.0,
                    totalPaidAmount = 400.0,
                    monthlyInstallment = 550.0,
                    installmentsCount = 1,
                    paidInstallmentsCount = 0,
                    status = "ACTIVE"
                )
            )
            sampleDebts.forEach { debtDao.insertDebt(it) }

            val sampleAccessories = listOf(
                AccessoryItem(name = "شاحن Apple أصلي 20W Type-C", category = "CHARGERS", costPrice = 55.0, sellingPrice = 89.0, stockQuantity = 24, minAlertQuantity = 5, barcode = "194252157015"),
                AccessoryItem(name = "كابل Type-C إلى Type-C مضفر سريع 60W", category = "CHARGERS", costPrice = 25.0, sellingPrice = 55.0, stockQuantity = 35, minAlertQuantity = 8, barcode = "693216938221"),
                AccessoryItem(name = "كفر حماية مغناطيسي Magsafe فاخر شفاف", category = "CASES", costPrice = 20.0, sellingPrice = 65.0, stockQuantity = 18, minAlertQuantity = 4, barcode = "880975664120"),
                AccessoryItem(name = "استكر شاشة زجاج مقوى Privacy ضد اللقافة", category = "SCREEN_PROTECTORS", costPrice = 12.0, sellingPrice = 45.0, stockQuantity = 42, minAlertQuantity = 10, barcode = "793573188921"),
                AccessoryItem(name = "سماعة AirPods Pro 2 الأصلية Type-C", category = "AUDIO", costPrice = 720.0, sellingPrice = 860.0, stockQuantity = 4, minAlertQuantity = 3, barcode = "195949052520"),
                AccessoryItem(name = "شاشة iPhone 15 Pro OLED أصلية مجددة", category = "SPARE_PARTS", costPrice = 550.0, sellingPrice = 750.0, stockQuantity = 3, minAlertQuantity = 2, barcode = "SP-IP15P-OLED"),
                AccessoryItem(name = "بطارية Galaxy S24 Ultra أصلية مع شريط لاصق", category = "SPARE_PARTS", costPrice = 140.0, sellingPrice = 220.0, stockQuantity = 2, minAlertQuantity = 2, barcode = "SP-SAM-S24U-BAT")
            )
            sampleAccessories.forEach { accessoryDao.insertAccessory(it) }
        }
    }
}
