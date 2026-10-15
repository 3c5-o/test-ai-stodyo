package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.StoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartItem(
    val id: String, // phone_{id} or acc_{id}
    val title: String,
    val subtitle: String,
    val unitPrice: Double,
    val costPrice: Double,
    val quantity: Int = 1,
    val isPhone: Boolean = false,
    val phoneId: Long? = null,
    val accessoryId: Long? = null,
    val imei: String = ""
)

data class StoreStats(
    val totalInventoryValue: Double = 0.0,
    val totalExpectedProfit: Double = 0.0,
    val inStockPhonesCount: Int = 0,
    val soldPhonesCount: Int = 0,
    val totalSalesRevenue: Double = 0.0,
    val totalSalesProfit: Double = 0.0,
    val activeRepairsCount: Int = 0,
    val totalActiveRepairsValue: Double = 0.0,
    val totalPendingDebts: Double = 0.0,
    val lowStockAccessoriesCount: Int = 0
)

data class PhoneValuationResult(
    val brand: String = "",
    val model: String = "",
    val storage: String = "",
    val grade: String = "A", // A+, A, B, C
    val estimatedBaseMarketPrice: Double = 0.0,
    val fairTradeInOffer: Double = 0.0,
    val recommendedResalePrice: Double = 0.0,
    val expectedProfitMargin: Double = 0.0,
    val deductedIssues: List<String> = emptyList()
)

class StoreViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StoreRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = StoreRepository(
            db.phoneDao(),
            db.saleDao(),
            db.repairDao(),
            db.debtDao(),
            db.accessoryDao()
        )
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    // Flows
    val inStockPhones: StateFlow<List<PhoneDevice>> = repository.inStockPhones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val soldPhones: StateFlow<List<PhoneDevice>> = repository.soldPhones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allInvoices: StateFlow<List<SaleInvoice>> = repository.allInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRepairs: StateFlow<List<RepairTicket>> = repository.allRepairTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeRepairs: StateFlow<List<RepairTicket>> = repository.activeRepairTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDebts: StateFlow<List<CustomerDebt>> = repository.allDebts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDebts: StateFlow<List<CustomerDebt>> = repository.activeDebts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAccessories: StateFlow<List<AccessoryItem>> = repository.allAccessories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockAccessories: StateFlow<List<AccessoryItem>> = repository.lowStockAccessories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Store Profile State
    private val _storeProfile = MutableStateFlow(StoreProfile())
    val storeProfile: StateFlow<StoreProfile> = _storeProfile.asStateFlow()

    fun updateStoreProfile(profile: StoreProfile) {
        _storeProfile.value = profile
    }

    // Dynamic Stats
    val storeStats: StateFlow<StoreStats> = combine(
        inStockPhones,
        soldPhones,
        allInvoices,
        activeRepairs,
        combine(activeDebts, lowStockAccessories) { debts, lowStockAcc -> Pair(debts, lowStockAcc) }
    ) { inStock, sold, invoices, repairs, debtsAndAcc ->
        val debts = debtsAndAcc.first
        val lowStockAcc = debtsAndAcc.second
        val totalInvValue = inStock.sumOf { it.purchasePrice * it.quantity }
        val totalExpectedSelling = inStock.sumOf { it.sellingPrice * it.quantity }
        val totalExpProfit = totalExpectedSelling - totalInvValue

        val totalSalesRev = invoices.sumOf { it.netTotal }
        val totalSalesProf = sold.sumOf { (it.soldPrice ?: it.sellingPrice) - it.purchasePrice }

        val activeRepairsVal = repairs.sumOf { it.totalCost }
        val pendingDebtsVal = debts.sumOf { it.totalDebtAmount - it.totalPaidAmount }

        StoreStats(
            totalInventoryValue = totalInvValue,
            totalExpectedProfit = totalExpProfit,
            inStockPhonesCount = inStock.sumOf { it.quantity },
            soldPhonesCount = sold.size,
            totalSalesRevenue = totalSalesRev,
            totalSalesProfit = totalSalesProf,
            activeRepairsCount = repairs.size,
            totalActiveRepairsValue = activeRepairsVal,
            totalPendingDebts = pendingDebtsVal,
            lowStockAccessoriesCount = lowStockAcc.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StoreStats())

    // POS Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _posDiscount = MutableStateFlow(0.0)
    val posDiscount: StateFlow<Double> = _posDiscount.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow("CASH") // CASH, CARD, TRANSFER, INSTALLMENT
    val selectedPaymentMethod: StateFlow<String> = _selectedPaymentMethod.asStateFlow()

    fun setPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }

    fun setPosDiscount(discount: Double) {
        _posDiscount.value = discount
    }

    fun addPhoneToCart(phone: PhoneDevice) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.id == "phone_${phone.id}" }
        if (existingIndex >= 0) {
            // Already in cart (phones are usually serialized per IMEI)
            return
        }
        current.add(
            CartItem(
                id = "phone_${phone.id}",
                title = "${phone.brand} ${phone.modelName}",
                subtitle = "${phone.storageCapacity} - ${phone.color} (${DeviceCondition.fromKey(phone.condition).titleAr})",
                unitPrice = phone.sellingPrice,
                costPrice = phone.purchasePrice,
                quantity = 1,
                isPhone = true,
                phoneId = phone.id,
                imei = phone.imei1
            )
        )
        _cartItems.value = current
    }

    fun addAccessoryToCart(acc: AccessoryItem) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.id == "acc_${acc.id}" }
        if (existingIndex >= 0) {
            val item = current[existingIndex]
            if (item.quantity < acc.stockQuantity) {
                current[existingIndex] = item.copy(quantity = item.quantity + 1)
            }
        } else {
            current.add(
                CartItem(
                    id = "acc_${acc.id}",
                    title = acc.name,
                    subtitle = "إكسسوار / قطع غيار",
                    unitPrice = acc.sellingPrice,
                    costPrice = acc.costPrice,
                    quantity = 1,
                    isPhone = false,
                    accessoryId = acc.id
                )
            )
        }
        _cartItems.value = current
    }

    fun removeCartItem(item: CartItem) {
        val current = _cartItems.value.toMutableList()
        current.removeAll { it.id == item.id }
        _cartItems.value = current
    }

    fun updateCartQuantity(item: CartItem, newQty: Int) {
        if (newQty <= 0) {
            removeCartItem(item)
            return
        }
        val current = _cartItems.value.toMutableList()
        val idx = current.indexOfFirst { it.id == item.id }
        if (idx >= 0) {
            current[idx] = current[idx].copy(quantity = newQty)
            _cartItems.value = current
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _posDiscount.value = 0.0
    }

    fun completeCheckout(
        customerName: String,
        customerPhone: String,
        paidAmount: Double,
        warrantyTerms: String = "ضمان تجربة واستبدال 30 يوماً",
        onSuccess: (SaleInvoice) -> Unit
    ) {
        viewModelScope.launch {
            val items = _cartItems.value
            if (items.isEmpty()) return@launch

            val subtotal = items.sumOf { it.unitPrice * it.quantity }
            val discount = _posDiscount.value
            val netTotal = (subtotal - discount).coerceAtLeast(0.0)
            val remaining = (netTotal - paidAmount).coerceAtLeast(0.0)
            val invNumber = "INV-${System.currentTimeMillis() % 100000}"

            val summaryBuilder = StringBuilder()
            items.forEachIndexed { i, item ->
                if (i > 0) summaryBuilder.append(" + ")
                summaryBuilder.append("${item.title} (${item.quantity}x)")
                if (item.imei.isNotEmpty()) {
                    summaryBuilder.append(" [IMEI: ${item.imei}]")
                }
            }

            val invoice = SaleInvoice(
                invoiceNumber = invNumber,
                customerName = customerName.ifBlank { "عميل نقدي" },
                customerPhone = customerPhone.ifBlank { "-" },
                itemsSummary = summaryBuilder.toString(),
                totalAmount = subtotal,
                discountAmount = discount,
                taxAmount = 0.0,
                netTotal = netTotal,
                paidAmount = paidAmount,
                remainingAmount = remaining,
                paymentMethod = _selectedPaymentMethod.value,
                isInstallment = remaining > 0,
                warrantyTerms = warrantyTerms
            )

            val invoiceId = repository.insertInvoice(invoice)
            val createdInvoice = invoice.copy(id = invoiceId)

            // Update inventory
            items.forEach { item ->
                if (item.isPhone && item.phoneId != null) {
                    repository.markPhoneSold(
                        id = item.phoneId,
                        soldDate = System.currentTimeMillis(),
                        soldPrice = item.unitPrice,
                        customerName = customerName.ifBlank { "عميل نقدي" },
                        customerPhone = customerPhone.ifBlank { "-" }
                    )
                } else if (!item.isPhone && item.accessoryId != null) {
                    val accList = allAccessories.value
                    val acc = accList.find { it.id == item.accessoryId }
                    if (acc != null) {
                        val newQty = (acc.stockQuantity - item.quantity).coerceAtLeast(0)
                        repository.updateAccessory(acc.copy(stockQuantity = newQty))
                    }
                }
            }

            // If remaining amount > 0, create debt record
            if (remaining > 0) {
                repository.insertDebt(
                    CustomerDebt(
                        customerName = customerName.ifBlank { "عميل آجل" },
                        customerPhone = customerPhone.ifBlank { "-" },
                        description = "متبقي فاتورة $invNumber",
                        totalDebtAmount = netTotal,
                        totalPaidAmount = paidAmount,
                        monthlyInstallment = remaining,
                        installmentsCount = 1,
                        paidInstallmentsCount = 0,
                        status = "ACTIVE"
                    )
                )
            }

            clearCart()
            onSuccess(createdInvoice)
        }
    }

    // Phone CRUD
    fun savePhone(phone: PhoneDevice) {
        viewModelScope.launch {
            if (phone.id == 0L) {
                repository.insertPhone(phone)
            } else {
                repository.updatePhone(phone)
            }
        }
    }

    fun deletePhone(phone: PhoneDevice) {
        viewModelScope.launch {
            repository.deletePhone(phone)
        }
    }

    // Repair CRUD
    fun saveRepairTicket(ticket: RepairTicket) {
        viewModelScope.launch {
            if (ticket.id == 0L) {
                repository.insertRepairTicket(ticket)
            } else {
                repository.updateRepairTicket(ticket)
            }
        }
    }

    fun updateRepairStatus(id: Long, status: String) {
        viewModelScope.launch {
            val completedDate = if (status == "READY" || status == "DELIVERED") System.currentTimeMillis() else null
            repository.updateRepairStatus(id, status, completedDate)
        }
    }

    fun deleteRepairTicket(ticket: RepairTicket) {
        viewModelScope.launch {
            repository.deleteRepairTicket(ticket)
        }
    }

    // Debt CRUD
    fun saveDebt(debt: CustomerDebt) {
        viewModelScope.launch {
            if (debt.id == 0L) {
                repository.insertDebt(debt)
            } else {
                repository.updateDebt(debt)
            }
        }
    }

    fun recordDebtPayment(debt: CustomerDebt, paymentAmount: Double) {
        viewModelScope.launch {
            val newPaid = debt.totalPaidAmount + paymentAmount
            val isSettled = newPaid >= debt.totalDebtAmount
            val newStatus = if (isSettled) "SETTLED" else "ACTIVE"
            val newPaidInstallments = debt.paidInstallmentsCount + 1
            repository.updateDebt(
                debt.copy(
                    totalPaidAmount = newPaid,
                    paidInstallmentsCount = newPaidInstallments,
                    status = newStatus
                )
            )
        }
    }

    fun deleteDebt(debt: CustomerDebt) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
        }
    }

    // Accessory CRUD
    fun saveAccessory(item: AccessoryItem) {
        viewModelScope.launch {
            if (item.id == 0L) {
                repository.insertAccessory(item)
            } else {
                repository.updateAccessory(item)
            }
        }
    }

    fun deleteAccessory(item: AccessoryItem) {
        viewModelScope.launch {
            repository.deleteAccessory(item)
        }
    }

    fun deleteInvoice(invoice: SaleInvoice) {
        viewModelScope.launch {
            repository.deleteInvoice(invoice)
        }
    }

    // Used Phone Valuation / Diagnostics Tool Algorithm
    fun calculateValuation(
        brand: String,
        model: String,
        storage: String,
        batteryHealth: Int,
        screenHasScratches: Boolean,
        screenReplaced: Boolean,
        bodyHasDents: Boolean,
        faceIdWorks: Boolean,
        camerasAllGood: Boolean,
        hasOriginalBox: Boolean
    ): PhoneValuationResult {
        // Base market reference estimates
        var baseMarketPrice = when {
            model.contains("16 Pro Max", ignoreCase = true) -> 4600.0
            model.contains("16 Pro", ignoreCase = true) -> 4000.0
            model.contains("15 Pro Max", ignoreCase = true) -> 3700.0
            model.contains("15 Pro", ignoreCase = true) -> 3100.0
            model.contains("15", ignoreCase = true) -> 2400.0
            model.contains("14 Pro Max", ignoreCase = true) -> 2900.0
            model.contains("14 Pro", ignoreCase = true) -> 2500.0
            model.contains("14", ignoreCase = true) -> 1900.0
            model.contains("13 Pro Max", ignoreCase = true) -> 2300.0
            model.contains("13", ignoreCase = true) -> 1600.0
            model.contains("S25 Ultra", ignoreCase = true) -> 4400.0
            model.contains("S24 Ultra", ignoreCase = true) -> 3400.0
            model.contains("S23 Ultra", ignoreCase = true) -> 2400.0
            model.contains("Pixel 9", ignoreCase = true) -> 3300.0
            model.contains("Xiaomi 14", ignoreCase = true) -> 2700.0
            else -> 2000.0
        }

        // Storage multiplier
        if (storage.contains("512")) baseMarketPrice += 400.0
        if (storage.contains("1TB")) baseMarketPrice += 700.0
        if (storage.contains("128") && baseMarketPrice > 2500.0) baseMarketPrice -= 200.0

        val issues = mutableListOf<String>()
        var deductionFactor = 0.0

        if (batteryHealth < 80) {
            deductionFactor += 0.12
            issues.add("البطارية أقل من 80% (تحتاج تبديل -12%)")
        } else if (batteryHealth < 88) {
            deductionFactor += 0.05
            issues.add("البطارية مستهلكة جزئياً (-5%)")
        }

        if (screenReplaced) {
            deductionFactor += 0.20
            issues.add("الشاشة متبدلة غير وكالة (-20%)")
        } else if (screenHasScratches) {
            deductionFactor += 0.06
            issues.add("خدوش سطحية على الشاشة (-6%)")
        }

        if (bodyHasDents) {
            deductionFactor += 0.08
            issues.add("صدمات أو خدوش في الهيكل الخارجي (-8%)")
        }

        if (!faceIdWorks) {
            deductionFactor += 0.22
            issues.add("بصمة الوجه FaceID/بصمة الإصبع معطلة (-22%)")
        }

        if (!camerasAllGood) {
            deductionFactor += 0.15
            issues.add("خلل في إحدى العدسات أو الفوكس (-15%)")
        }

        if (!hasOriginalBox) {
            deductionFactor += 0.05
            issues.add("بدون الكرتون الأصلي والملحقات (-5%)")
        }

        val conditionGrade = when {
            deductionFactor == 0.0 && batteryHealth >= 95 -> "A+ (كالجديد تماماً)"
            deductionFactor <= 0.08 -> "A (ممتاز جداً)"
            deductionFactor <= 0.20 -> "B (جيد جداً مع ملاحظات خفيفة)"
            else -> "C (يحتاج صيانة وتجديد)"
        }

        val estimatedCurrentResale = baseMarketPrice * (1.0 - deductionFactor)
        // Store purchase offer gives healthy margin 18-22%
        val storeBuyOffer = estimatedCurrentResale * 0.80
        val profitExpected = estimatedCurrentResale - storeBuyOffer

        return PhoneValuationResult(
            brand = brand,
            model = model,
            storage = storage,
            grade = conditionGrade,
            estimatedBaseMarketPrice = baseMarketPrice,
            fairTradeInOffer = storeBuyOffer,
            recommendedResalePrice = estimatedCurrentResale,
            expectedProfitMargin = profitExpected,
            deductedIssues = issues
        )
    }
}
