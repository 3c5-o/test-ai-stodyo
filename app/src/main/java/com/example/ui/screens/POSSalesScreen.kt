package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceCondition
import com.example.data.model.SaleInvoice
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.CartItem
import com.example.ui.viewmodel.StoreViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun POSSalesScreen(
    viewModel: StoreViewModel
) {
    val inStockPhones by viewModel.inStockPhones.collectAsState()
    val allAccessories by viewModel.allAccessories.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val posDiscount by viewModel.posDiscount.collectAsState()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val allInvoices by viewModel.allInvoices.collectAsState()
    val storeProfile by viewModel.storeProfile.collectAsState()

    var activePosTab by remember { mutableIntStateOf(0) } // 0 = POS Register, 1 = Invoices History
    var catalogTab by remember { mutableIntStateOf(0) } // 0 = Phones, 1 = Accessories
    var catalogSearch by remember { mutableStateOf("") }

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var discountInput by remember { mutableStateOf("") }
    var paidAmountInput by remember { mutableStateOf("") }

    var invoiceToView by remember { mutableStateOf<SaleInvoice?>(null) }
    var invoiceToDelete by remember { mutableStateOf<SaleInvoice?>(null) }

    val subtotal = cartItems.sumOf { it.unitPrice * it.quantity }
    val currentDiscount = discountInput.toDoubleOrNull() ?: 0.0
    val netTotal = (subtotal - currentDiscount).coerceAtLeast(0.0)
    val autoPaid = if (paidAmountInput.isBlank()) netTotal else (paidAmountInput.toDoubleOrNull() ?: netTotal)
    val remainingAmount = (netTotal - autoPaid).coerceAtLeast(0.0)

    val paymentMethods = listOf(
        "CASH" to "نقدي 💵",
        "CARD" to "مدى / بطاقة 💳",
        "TRANSFER" to "تحويل بنكي 🏦",
        "INSTALLMENT" to "تقسيط / آجل ⏱️"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianNavyBg)
            .testTag("pos_sales_screen")
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianCardBg)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "نقطة البيع والفواتير الذكية",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                    Text(
                        text = "إصدار فواتير بيع الأجهزة والإكسسوارات مع الخصم والتقسيط",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                if (cartItems.isNotEmpty() && activePosTab == 0) {
                    LuxuryBadge(
                        text = "${cartItems.sumOf { it.quantity }} عناصر في السلة",
                        color = GoldAccent,
                        textColor = GoldAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Tabs
            TabRow(
                selectedTabIndex = activePosTab,
                containerColor = ObsidianSurfaceBg,
                contentColor = GoldPrimary,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = activePosTab == 0,
                    onClick = { activePosTab = 0 },
                    text = {
                        Text(
                            "كاشير ونقطة البيع (POS)",
                            fontWeight = if (activePosTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (activePosTab == 0) GoldPrimary else TextMuted
                        )
                    }
                )
                Tab(
                    selected = activePosTab == 1,
                    onClick = { activePosTab = 1 },
                    text = {
                        Text(
                            "أرشيف الفواتير (${allInvoices.size})",
                            fontWeight = if (activePosTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (activePosTab == 1) GoldPrimary else TextMuted
                        )
                    }
                )
            }
        }

        if (activePosTab == 0) {
            // POS REGISTER SCREEN
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Choose Catalog (Phones / Accessories)
                item {
                    LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "اختيار الأصناف للسلة",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextWhite
                            )

                            // Catalog switch
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = catalogTab == 0,
                                    onClick = { catalogTab = 0 },
                                    label = { Text("📱 هواتف (${inStockPhones.size})") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldPrimary,
                                        selectedLabelColor = ObsidianNavyBg
                                    )
                                )
                                FilterChip(
                                    selected = catalogTab == 1,
                                    onClick = { catalogTab = 1 },
                                    label = { Text("⚡ إكسسوارات (${allAccessories.size})") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldPrimary,
                                        selectedLabelColor = ObsidianNavyBg
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Search Bar
                        OutlinedTextField(
                            value = catalogSearch,
                            onValueChange = { catalogSearch = it },
                            placeholder = { Text("بحث في الأصناف المتاحة...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ObsidianSurfaceBg,
                                unfocusedContainerColor = ObsidianSurfaceBg,
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (catalogTab == 0) {
                            // Phone Picker List
                            val filteredPhones = inStockPhones.filter {
                                catalogSearch.isBlank() ||
                                        it.modelName.contains(catalogSearch, ignoreCase = true) ||
                                        it.brand.contains(catalogSearch, ignoreCase = true) ||
                                        it.imei1.contains(catalogSearch, ignoreCase = true)
                            }

                            if (filteredPhones.isEmpty()) {
                                Text("لا توجد هواتف مطابقة في المخزن", color = TextMuted, modifier = Modifier.padding(8.dp))
                            } else {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(filteredPhones) { phone ->
                                        val inCart = cartItems.any { it.id == "phone_${phone.id}" }
                                        Surface(
                                            modifier = Modifier
                                                .width(180.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .border(1.dp, if (inCart) GoldPrimary else ObsidianBorder, RoundedCornerShape(12.dp))
                                                .clickable {
                                                    viewModel.addPhoneToCart(phone)
                                                },
                                            color = if (inCart) ObsidianSurfaceBg else ObsidianNavyBg
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text(phone.brand, style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                                                    if (inCart) {
                                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                                Text(phone.modelName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextWhite, maxLines = 1)
                                                Text("${phone.storageCapacity} • ${phone.color}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextMuted, maxLines = 1)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(formatMoney(phone.sellingPrice, storeProfile.currency), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GoldAccent)
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Accessory Picker List
                            val filteredAcc = allAccessories.filter {
                                catalogSearch.isBlank() || it.name.contains(catalogSearch, ignoreCase = true)
                            }
                            if (filteredAcc.isEmpty()) {
                                Text("لا توجد إكسسوارات مطابقة", color = TextMuted, modifier = Modifier.padding(8.dp))
                            } else {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(filteredAcc) { acc ->
                                        val inCartItem = cartItems.find { it.id == "acc_${acc.id}" }
                                        Surface(
                                            modifier = Modifier
                                                .width(170.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .border(1.dp, if (inCartItem != null) GoldPrimary else ObsidianBorder, RoundedCornerShape(12.dp))
                                                .clickable {
                                                    viewModel.addAccessoryToCart(acc)
                                                },
                                            color = if (inCartItem != null) ObsidianSurfaceBg else ObsidianNavyBg
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(acc.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextWhite, maxLines = 2)
                                                Text("المتبقي: ${acc.stockQuantity} قطعة", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextMuted)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text(formatMoney(acc.sellingPrice, storeProfile.currency), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GoldAccent)
                                                    if (inCartItem != null) {
                                                        Text("${inCartItem.quantity}x في السلة", color = EmeraldGreen, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 2: Order Cart Items
                item {
                    LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "محتويات السلة الحالية",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextWhite
                            )

                            if (cartItems.isNotEmpty()) {
                                TextButton(onClick = { viewModel.clearCart() }) {
                                    Text("تفريغ السلة", color = CrimsonRed, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (cartItems.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("السلة فارغة، اختر جهازاً أو إكسسواراً من القائمة أعلاه", color = TextMuted, fontSize = 12.sp)
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                cartItems.forEach { item ->
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)),
                                        color = ObsidianSurfaceBg
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(item.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextWhite)
                                                Text(item.subtitle, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                                Text(formatMoney(item.unitPrice, storeProfile.currency), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                if (!item.isPhone) {
                                                    IconButton(
                                                        onClick = { viewModel.updateCartQuantity(item, item.quantity - 1) },
                                                        modifier = Modifier.size(28.dp).clip(CircleShape).background(ObsidianNavyBg)
                                                    ) {
                                                        Icon(Icons.Default.Remove, contentDescription = "تقليل", tint = TextWhite, modifier = Modifier.size(14.dp))
                                                    }
                                                    Text("${item.quantity}", color = TextWhite, fontWeight = FontWeight.Bold)
                                                    IconButton(
                                                        onClick = { viewModel.updateCartQuantity(item, item.quantity + 1) },
                                                        modifier = Modifier.size(28.dp).clip(CircleShape).background(ObsidianNavyBg)
                                                    ) {
                                                        Icon(Icons.Default.Add, contentDescription = "زيادة", tint = TextWhite, modifier = Modifier.size(14.dp))
                                                    }
                                                }
                                                IconButton(
                                                    onClick = { viewModel.removeCartItem(item) },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = CrimsonRed, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 3: Customer & Payment Details
                item {
                    LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "بيانات العميل والدفع",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = customerName,
                                onValueChange = { customerName = it },
                                label = { Text("اسم العميل") },
                                placeholder = { Text("مثال: فيصل العتيبي") },
                                modifier = Modifier.weight(1f).testTag("pos_customer_name"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = ObsidianBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                )
                            )
                            OutlinedTextField(
                                value = customerPhone,
                                onValueChange = { customerPhone = it },
                                label = { Text("رقم الجوال") },
                                placeholder = { Text("05xxxxxxxx") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.weight(1f).testTag("pos_customer_phone"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = ObsidianBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("طريقة الدفع", style = MaterialTheme.typography.labelMedium, color = TextWhite)
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(paymentMethods) { (key, label) ->
                                val isSelected = selectedPaymentMethod == key
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(1.dp, if (isSelected) GoldPrimary else ObsidianBorder, RoundedCornerShape(10.dp))
                                        .clickable { viewModel.setPaymentMethod(key) },
                                    color = if (isSelected) GoldDark else ObsidianSurfaceBg
                                ) {
                                    Text(
                                        text = label,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        color = if (isSelected) TextWhite else TextMuted,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = discountInput,
                                onValueChange = {
                                    discountInput = it
                                    viewModel.setPosDiscount(it.toDoubleOrNull() ?: 0.0)
                                },
                                label = { Text("الخصم الممنوح") },
                                placeholder = { Text("0.0") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f).testTag("pos_discount_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = ObsidianBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                )
                            )
                            OutlinedTextField(
                                value = paidAmountInput,
                                onValueChange = { paidAmountInput = it },
                                label = { Text("المدفوع نقداً/بطاقة") },
                                placeholder = { Text(if (netTotal > 0) netTotal.toString() else "0.0") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f).testTag("pos_paid_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = ObsidianBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = ObsidianBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Bill Summary Box
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("المجموع الأولي:", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                                Text(formatMoney(subtotal, storeProfile.currency), color = TextWhite, style = MaterialTheme.typography.bodyMedium)
                            }
                            if (currentDiscount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("قيمة الخصم:", color = CrimsonRed, style = MaterialTheme.typography.bodyMedium)
                                    Text("-${formatMoney(currentDiscount, storeProfile.currency)}", color = CrimsonRed, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("الصافي المستحق:", color = GoldPrimary, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text(formatMoney(netTotal, storeProfile.currency), color = GoldAccent, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            }
                            if (remainingAmount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("المتبقي (ذمة / تقسيط):", color = AmberWarning, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                    Text(formatMoney(remainingAmount, storeProfile.currency), color = AmberWarning, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Complete Sale Button
                        LuxuryButton(
                            text = "إتمام البيع وطباعة الفاتورة الفاخرة",
                            icon = Icons.Default.CheckCircle,
                            onClick = {
                                viewModel.completeCheckout(
                                    customerName = customerName,
                                    customerPhone = customerPhone,
                                    paidAmount = autoPaid,
                                    warrantyTerms = storeProfile.defaultWarrantyTerms,
                                    onSuccess = { generatedInvoice ->
                                        customerName = ""
                                        customerPhone = ""
                                        discountInput = ""
                                        paidAmountInput = ""
                                        invoiceToView = generatedInvoice
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = cartItems.isNotEmpty(),
                            testTag = "complete_sale_button"
                        )
                    }
                }
            }
        } else {
            // INVOICES ARCHIVE
            if (allInvoices.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = TextMuted, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("لا توجد فواتير مبيعات مسجلة حتى الآن", color = TextMuted)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allInvoices, key = { it.id }) { inv ->
                        val dateFormatted = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar", "SA")).format(Date(inv.createdAt))
                        LuxuryCard(
                            modifier = Modifier.fillMaxWidth().testTag("invoice_card_${inv.id}"),
                            backgroundColor = ObsidianCardBg,
                            onClick = { invoiceToView = inv }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(inv.invoiceNumber, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        LuxuryBadge(
                                            text = when (inv.paymentMethod) {
                                                "CASH" -> "نقدي"
                                                "CARD" -> "بطاقة"
                                                "TRANSFER" -> "تحويل"
                                                else -> "تقسيط"
                                            },
                                            color = EmeraldGreen,
                                            textColor = EmeraldGreen
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("العميل: ${inv.customerName} (${inv.customerPhone})", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                                    Text(inv.itemsSummary, style = MaterialTheme.typography.bodySmall, color = TextMuted, maxLines = 1)
                                    Text(dateFormatted, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextDim)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(formatMoney(inv.netTotal, storeProfile.currency), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldAccent)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row {
                                        IconButton(onClick = { invoiceToView = inv }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.Visibility, contentDescription = "عرض", tint = GoldLight, modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(onClick = { invoiceToDelete = inv }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = CrimsonRed.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Invoice Preview Dialog
    invoiceToView?.let { inv ->
        InvoiceDetailDialog(
            invoice = inv,
            storeProfile = storeProfile,
            onDismiss = { invoiceToView = null }
        )
    }

    // Delete Invoice Dialog
    invoiceToDelete?.let { inv ->
        AlertDialog(
            onDismissRequest = { invoiceToDelete = null },
            title = { Text("حذف الفاتورة", color = CrimsonRed, fontWeight = FontWeight.Bold) },
            text = { Text("هل تريد حذف الفاتورة ${inv.invoiceNumber} نهائياً من الأرشيف؟", color = TextWhite) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteInvoice(inv)
                    invoiceToDelete = null
                }) {
                    Text("حذف", color = CrimsonRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { invoiceToDelete = null }) {
                    Text("إلغاء", color = TextMuted)
                }
            },
            containerColor = ObsidianCardBg
        )
    }
}
