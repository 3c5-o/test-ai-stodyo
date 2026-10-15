package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceCondition
import com.example.data.model.PhoneDevice
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: StoreViewModel,
    onNavigateToPOS: () -> Unit
) {
    val inStockPhones by viewModel.inStockPhones.collectAsState()
    val soldPhones by viewModel.soldPhones.collectAsState()
    val storeProfile by viewModel.storeProfile.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = In Stock, 1 = Sold
    var searchQuery by remember { mutableStateOf("") }
    var selectedBrandFilter by remember { mutableStateOf("الكل") }
    var selectedConditionFilter by remember { mutableStateOf("الكل") }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var phoneToEdit by remember { mutableStateOf<PhoneDevice?>(null) }
    var phoneToDelete by remember { mutableStateOf<PhoneDevice?>(null) }

    val brandList = listOf("الكل", "Apple", "Samsung", "Xiaomi", "Google", "Huawei", "Honor")
    val conditionList = listOf("الكل", "NEW_SEALED", "LIKE_NEW", "USED_GOOD", "REFURBISHED")

    val activeList = if (selectedTab == 0) inStockPhones else soldPhones

    val filteredPhones = activeList.filter { phone ->
        val matchesSearch = searchQuery.isBlank() ||
                phone.modelName.contains(searchQuery, ignoreCase = true) ||
                phone.brand.contains(searchQuery, ignoreCase = true) ||
                phone.imei1.contains(searchQuery, ignoreCase = true) ||
                phone.color.contains(searchQuery, ignoreCase = true) ||
                (phone.customerName ?: "").contains(searchQuery, ignoreCase = true)

        val matchesBrand = selectedBrandFilter == "الكل" || phone.brand.equals(selectedBrandFilter, ignoreCase = true)
        val matchesCondition = selectedConditionFilter == "الكل" || phone.condition == selectedConditionFilter

        matchesSearch && matchesBrand && matchesCondition
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(ObsidianNavyBg).testTag("inventory_screen"),
        containerColor = ObsidianNavyBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    phoneToEdit = null
                    showAddEditDialog = true
                },
                containerColor = GoldPrimary,
                contentColor = ObsidianNavyBg,
                shape = CircleShape,
                modifier = Modifier.testTag("add_phone_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة هاتف جديد")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Bar
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
                            text = "مخزن الهواتف والأجهزة",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary
                        )
                        Text(
                            text = "إدارة المخزون، الأرقام التسلسلية IMEI، وهامش الربح",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    LuxuryBadge(
                        text = "${inStockPhones.size} جهاز متوفر",
                        color = EmeraldGreen,
                        textColor = EmeraldGreen
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("بحث بالموديل، الماركة، أو رقم الـ IMEI...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح", tint = TextMuted)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("inventory_search_field"),
                    shape = RoundedCornerShape(14.dp),
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

                // Tabs: In Stock vs Sold
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ObsidianSurfaceBg,
                    contentColor = GoldPrimary,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "المتوفر في المخزن (${inStockPhones.size})",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) GoldPrimary else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "سجل الأجهزة المباعة (${soldPhones.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) GoldPrimary else TextMuted
                            )
                        }
                    )
                }
            }

            // Brand & Condition Filter Chips
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(brandList) { brand ->
                        BrandChip(
                            brandName = brand,
                            isSelected = selectedBrandFilter == brand,
                            onSelect = { selectedBrandFilter = brand }
                        )
                    }
                }

                val conditionDisplay = listOf(
                    "الكل" to "جميع الحالات",
                    "NEW_SEALED" to "📦 جديد كرتون",
                    "LIKE_NEW" to "✨ كالجديد",
                    "USED_GOOD" to "📱 مستعمل نظيف",
                    "REFURBISHED" to "🛠️ مجدد معتمد"
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(conditionDisplay) { (key, label) ->
                        val isSel = selectedConditionFilter == key
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, if (isSel) GoldPrimary else ObsidianBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedConditionFilter = key },
                            color = if (isSel) GoldPrimary.copy(alpha = 0.2f) else ObsidianSurfaceBg
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                color = if (isSel) GoldAccent else TextMuted,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                            )
                        }
                    }
                }
            }

            // Phones List
            if (filteredPhones.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "لا توجد أجهزة مطابقة لنتائج البحث" else "لا توجد هواتف في هذا القسم حالياً",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (selectedTab == 0) {
                            LuxuryButton(
                                text = "إضافة جهاز للمخزن",
                                icon = Icons.Default.Add,
                                onClick = {
                                    phoneToEdit = null
                                    showAddEditDialog = true
                                },
                                testTag = "empty_add_phone_btn"
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredPhones, key = { it.id }) { phone ->
                        PhoneInventoryCard(
                            phone = phone,
                            currency = storeProfile.currency,
                            isSoldView = selectedTab == 1,
                            onEdit = {
                                phoneToEdit = phone
                                showAddEditDialog = true
                            },
                            onDelete = {
                                phoneToDelete = phone
                            },
                            onSell = {
                                viewModel.addPhoneToCart(phone)
                                onNavigateToPOS()
                            }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        AddEditPhoneDialog(
            initialPhone = phoneToEdit,
            onDismiss = {
                showAddEditDialog = false
                phoneToEdit = null
            },
            onSave = { savedPhone ->
                viewModel.savePhone(savedPhone)
                showAddEditDialog = false
                phoneToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (phoneToDelete != null) {
        AlertDialog(
            onDismissRequest = { phoneToDelete = null },
            title = { Text("تأكيد حذف الجهاز", color = CrimsonRed, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "هل أنت متأكد من حذف ${phoneToDelete?.brand} ${phoneToDelete?.modelName} من المخزن نهائياً؟",
                    color = TextWhite
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        phoneToDelete?.let { viewModel.deletePhone(it) }
                        phoneToDelete = null
                    }
                ) {
                    Text("حذف", color = CrimsonRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { phoneToDelete = null }) {
                    Text("إلغاء", color = TextMuted)
                }
            },
            containerColor = ObsidianCardBg
        )
    }
}

@Composable
fun PhoneInventoryCard(
    phone: PhoneDevice,
    currency: String,
    isSoldView: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSell: () -> Unit
) {
    val profit = phone.sellingPrice - phone.purchasePrice
    val profitPercent = if (phone.purchasePrice > 0) ((profit / phone.purchasePrice) * 100).toInt() else 0

    LuxuryCard(
        modifier = Modifier.fillMaxWidth().testTag("phone_card_${phone.id}"),
        backgroundColor = ObsidianCardBg
    ) {
        // Top Badges & Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                LuxuryBadge(
                    text = phone.brand,
                    color = GoldPrimary,
                    textColor = GoldPrimary
                )
                LuxuryConditionBadge(condition = phone.condition)
                if (phone.batteryHealth in 1..99) {
                    LuxuryBadge(
                        text = "⚡ ${phone.batteryHealth}%",
                        color = EmeraldGreen,
                        textColor = EmeraldGreen
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp).testTag("edit_phone_${phone.id}")) {
                    Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = GoldLight, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).testTag("delete_phone_${phone.id}")) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CrimsonRed.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Device Title & Specifications
        Text(
            text = phone.modelName,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextWhite
        )
        Text(
            text = "السعة: ${phone.storageCapacity} • الرام: ${phone.ramSize} • اللون: ${phone.color}",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        // IMEI & Serial Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianSurfaceBg)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCode, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "IMEI 1: ${phone.imei1}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = TextWhite
                    )
                }
                if (phone.imei2.isNotBlank()) {
                    Text(
                        text = "IMEI 2: ${phone.imei2.take(8)}...",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextMuted
                    )
                }
            }
        }

        if (phone.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "ملاحظات: ${phone.notes}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextDim,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = ObsidianBorder, thickness = 0.8.dp)
        Spacer(modifier = Modifier.height(10.dp))

        // Price Breakdown & Footer
        if (!isSoldView) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Column {
                            Text("سعر التكلفة", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextMuted)
                            Text(formatMoney(phone.purchasePrice, currency), style = MaterialTheme.typography.bodySmall, color = TextWhite)
                        }
                        Column {
                            Text("سعر البيع", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = GoldPrimary)
                            Text(formatMoney(phone.sellingPrice, currency), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = GoldAccent)
                        }
                        Column {
                            Text("الربح المتوقع", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = EmeraldGreen)
                            Text("+${formatMoney(profit, currency)} ($profitPercent%)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = EmeraldGreen)
                        }
                    }
                }

                LuxuryButton(
                    text = "بيع (POS)",
                    icon = Icons.Default.ShoppingCartCheckout,
                    onClick = onSell,
                    modifier = Modifier.height(40.dp),
                    containerColor = GoldPrimary,
                    contentColor = ObsidianNavyBg,
                    testTag = "sell_phone_btn_${phone.id}"
                )
            }
        } else {
            // Sold device info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val dateStr = phone.soldDate?.let {
                        SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(it))
                    } ?: "مؤخراً"
                    Text("تم البيع بتاريخ $dateStr", style = MaterialTheme.typography.bodySmall, color = EmeraldGreen)
                    Text("المشتري: ${phone.customerName ?: "عميل نقدي"} • ${phone.customerPhone ?: ""}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("سعر البيع الفعلي", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextMuted)
                    Text(formatMoney(phone.soldPrice ?: phone.sellingPrice, currency), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = GoldAccent)
                }
            }
        }
    }
}
