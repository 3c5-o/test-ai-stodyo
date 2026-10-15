package com.example.data.local

import com.example.data.model.*
import org.json.JSONArray
import org.json.JSONObject

data class BackupContent(
    val phones: List<PhoneDevice>,
    val invoices: List<SaleInvoice>,
    val repairs: List<RepairTicket>,
    val debts: List<CustomerDebt>,
    val accessories: List<AccessoryItem>
)

object BackupManager {

    fun generateBackupJson(
        phones: List<PhoneDevice>,
        invoices: List<SaleInvoice>,
        repairs: List<RepairTicket>,
        debts: List<CustomerDebt>,
        accessories: List<AccessoryItem>
    ): String {
        val root = JSONObject()
        root.put("app", "MobilyPro")
        root.put("version", 1)
        root.put("createdAt", System.currentTimeMillis())

        // Phones
        val phonesArray = JSONArray()
        for (p in phones) {
            val obj = JSONObject()
            obj.put("brand", p.brand)
            obj.put("modelName", p.modelName)
            obj.put("storageCapacity", p.storageCapacity)
            obj.put("ramSize", p.ramSize)
            obj.put("color", p.color)
            obj.put("condition", p.condition)
            obj.put("batteryHealth", p.batteryHealth)
            obj.put("imei1", p.imei1)
            obj.put("imei2", p.imei2)
            obj.put("purchasePrice", p.purchasePrice)
            obj.put("sellingPrice", p.sellingPrice)
            obj.put("quantity", p.quantity)
            obj.put("supplierName", p.supplierName)
            obj.put("warrantyMonths", p.warrantyMonths)
            obj.put("notes", p.notes)
            obj.put("isSold", p.isSold)
            obj.put("soldDate", p.soldDate ?: 0L)
            obj.put("soldPrice", p.soldPrice ?: 0.0)
            obj.put("customerName", p.customerName ?: "")
            obj.put("customerPhone", p.customerPhone ?: "")
            phonesArray.put(obj)
        }
        root.put("phones", phonesArray)

        // Invoices
        val invoicesArray = JSONArray()
        for (inv in invoices) {
            val obj = JSONObject()
            obj.put("invoiceNumber", inv.invoiceNumber)
            obj.put("customerName", inv.customerName)
            obj.put("customerPhone", inv.customerPhone)
            obj.put("itemsSummary", inv.itemsSummary)
            obj.put("totalAmount", inv.totalAmount)
            obj.put("discountAmount", inv.discountAmount)
            obj.put("taxAmount", inv.taxAmount)
            obj.put("netTotal", inv.netTotal)
            obj.put("paidAmount", inv.paidAmount)
            obj.put("remainingAmount", inv.remainingAmount)
            obj.put("paymentMethod", inv.paymentMethod)
            obj.put("isInstallment", inv.isInstallment)
            obj.put("warrantyTerms", inv.warrantyTerms)
            obj.put("createdAt", inv.createdAt)
            invoicesArray.put(obj)
        }
        root.put("invoices", invoicesArray)

        // Repairs
        val repairsArray = JSONArray()
        for (r in repairs) {
            val obj = JSONObject()
            obj.put("ticketNumber", r.ticketNumber)
            obj.put("customerName", r.customerName)
            obj.put("customerPhone", r.customerPhone)
            obj.put("deviceModel", r.deviceModel)
            obj.put("deviceColor", r.deviceColor)
            obj.put("devicePasscode", r.devicePasscode)
            obj.put("problemDescription", r.problemDescription)
            obj.put("repairType", r.repairType)
            obj.put("costParts", r.costParts)
            obj.put("serviceFee", r.serviceFee)
            obj.put("totalCost", r.totalCost)
            obj.put("paidAmount", r.paidAmount)
            obj.put("status", r.status)
            obj.put("technicianName", r.technicianName)
            obj.put("notes", r.notes)
            obj.put("receivedDate", r.receivedDate)
            obj.put("completedDate", r.completedDate ?: 0L)
            repairsArray.put(obj)
        }
        root.put("repairs", repairsArray)

        // Debts
        val debtsArray = JSONArray()
        for (d in debts) {
            val obj = JSONObject()
            obj.put("customerName", d.customerName)
            obj.put("customerPhone", d.customerPhone)
            obj.put("nationalIdOrRef", d.nationalIdOrRef)
            obj.put("description", d.description)
            obj.put("totalDebtAmount", d.totalDebtAmount)
            obj.put("totalPaidAmount", d.totalPaidAmount)
            obj.put("monthlyInstallment", d.monthlyInstallment)
            obj.put("installmentsCount", d.installmentsCount)
            obj.put("paidInstallmentsCount", d.paidInstallmentsCount)
            obj.put("status", d.status)
            obj.put("createdAt", d.createdAt)
            debtsArray.put(obj)
        }
        root.put("debts", debtsArray)

        // Accessories
        val accArray = JSONArray()
        for (a in accessories) {
            val obj = JSONObject()
            obj.put("name", a.name)
            obj.put("category", a.category)
            obj.put("costPrice", a.costPrice)
            obj.put("sellingPrice", a.sellingPrice)
            obj.put("stockQuantity", a.stockQuantity)
            obj.put("minAlertQuantity", a.minAlertQuantity)
            obj.put("barcode", a.barcode)
            accArray.put(obj)
        }
        root.put("accessories", accArray)

        return root.toString(2)
    }

    fun parseBackupJson(jsonStr: String): BackupContent {
        val root = JSONObject(jsonStr)

        val phones = mutableListOf<PhoneDevice>()
        if (root.has("phones")) {
            val arr = root.getJSONArray("phones")
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                phones.add(
                    PhoneDevice(
                        brand = o.optString("brand", ""),
                        modelName = o.optString("modelName", ""),
                        storageCapacity = o.optString("storageCapacity", "128GB"),
                        ramSize = o.optString("ramSize", "8GB"),
                        color = o.optString("color", ""),
                        condition = o.optString("condition", "NEW_SEALED"),
                        batteryHealth = o.optInt("batteryHealth", 100),
                        imei1 = o.optString("imei1", ""),
                        imei2 = o.optString("imei2", ""),
                        purchasePrice = o.optDouble("purchasePrice", 0.0),
                        sellingPrice = o.optDouble("sellingPrice", 0.0),
                        quantity = o.optInt("quantity", 1),
                        supplierName = o.optString("supplierName", ""),
                        warrantyMonths = o.optInt("warrantyMonths", 12),
                        notes = o.optString("notes", ""),
                        isSold = o.optBoolean("isSold", false),
                        soldDate = if (o.has("soldDate") && o.getLong("soldDate") > 0) o.getLong("soldDate") else null,
                        soldPrice = if (o.has("soldPrice") && o.getDouble("soldPrice") > 0) o.getDouble("soldPrice") else null,
                        customerName = o.optString("customerName", null),
                        customerPhone = o.optString("customerPhone", null)
                    )
                )
            }
        }

        val invoices = mutableListOf<SaleInvoice>()
        if (root.has("invoices")) {
            val arr = root.getJSONArray("invoices")
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                invoices.add(
                    SaleInvoice(
                        invoiceNumber = o.optString("invoiceNumber", "INV-${System.currentTimeMillis()}"),
                        customerName = o.optString("customerName", "عميل"),
                        customerPhone = o.optString("customerPhone", ""),
                        itemsSummary = o.optString("itemsSummary", ""),
                        totalAmount = o.optDouble("totalAmount", 0.0),
                        discountAmount = o.optDouble("discountAmount", 0.0),
                        taxAmount = o.optDouble("taxAmount", 0.0),
                        netTotal = o.optDouble("netTotal", 0.0),
                        paidAmount = o.optDouble("paidAmount", 0.0),
                        remainingAmount = o.optDouble("remainingAmount", 0.0),
                        paymentMethod = o.optString("paymentMethod", "CASH"),
                        isInstallment = o.optBoolean("isInstallment", false),
                        warrantyTerms = o.optString("warrantyTerms", "ضمان معتمد"),
                        createdAt = o.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        }

        val repairs = mutableListOf<RepairTicket>()
        if (root.has("repairs")) {
            val arr = root.getJSONArray("repairs")
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                repairs.add(
                    RepairTicket(
                        ticketNumber = o.optString("ticketNumber", "REP-${System.currentTimeMillis()}"),
                        customerName = o.optString("customerName", "عميل"),
                        customerPhone = o.optString("customerPhone", ""),
                        deviceModel = o.optString("deviceModel", ""),
                        deviceColor = o.optString("deviceColor", ""),
                        devicePasscode = o.optString("devicePasscode", ""),
                        problemDescription = o.optString("problemDescription", ""),
                        repairType = o.optString("repairType", "GENERAL"),
                        costParts = o.optDouble("costParts", 0.0),
                        serviceFee = o.optDouble("serviceFee", 0.0),
                        totalCost = o.optDouble("totalCost", 0.0),
                        paidAmount = o.optDouble("paidAmount", 0.0),
                        status = o.optString("status", "RECEIVED"),
                        technicianName = o.optString("technicianName", ""),
                        notes = o.optString("notes", ""),
                        receivedDate = o.optLong("receivedDate", System.currentTimeMillis()),
                        completedDate = if (o.has("completedDate") && o.getLong("completedDate") > 0) o.getLong("completedDate") else null
                    )
                )
            }
        }

        val debts = mutableListOf<CustomerDebt>()
        if (root.has("debts")) {
            val arr = root.getJSONArray("debts")
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                debts.add(
                    CustomerDebt(
                        customerName = o.optString("customerName", "عميل"),
                        customerPhone = o.optString("customerPhone", ""),
                        nationalIdOrRef = o.optString("nationalIdOrRef", ""),
                        description = o.optString("description", ""),
                        totalDebtAmount = o.optDouble("totalDebtAmount", 0.0),
                        totalPaidAmount = o.optDouble("totalPaidAmount", 0.0),
                        monthlyInstallment = o.optDouble("monthlyInstallment", 0.0),
                        installmentsCount = o.optInt("installmentsCount", 1),
                        paidInstallmentsCount = o.optInt("paidInstallmentsCount", 0),
                        status = o.optString("status", "ACTIVE"),
                        createdAt = o.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        }

        val accessories = mutableListOf<AccessoryItem>()
        if (root.has("accessories")) {
            val arr = root.getJSONArray("accessories")
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                accessories.add(
                    AccessoryItem(
                        name = o.optString("name", ""),
                        category = o.optString("category", "OTHER"),
                        costPrice = o.optDouble("costPrice", 0.0),
                        sellingPrice = o.optDouble("sellingPrice", 0.0),
                        stockQuantity = o.optInt("stockQuantity", 0),
                        minAlertQuantity = o.optInt("minAlertQuantity", 5),
                        barcode = o.optString("barcode", "")
                    )
                )
            }
        }

        return BackupContent(phones, invoices, repairs, debts, accessories)
    }
}
