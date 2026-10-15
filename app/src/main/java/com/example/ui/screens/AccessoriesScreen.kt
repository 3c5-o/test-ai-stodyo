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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AccessoryItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessoriesScreen(
    viewModel: StoreViewModel,
    onNavigateToPOS: () -> Unit
) {
    val allAccessories by viewModel.allAccessories.collectAsState()
    val lowStockList by viewModel.lowStockAccessories.collectAsState()
    val storeProfile by viewModel.storeProfile.collectAsState()

    var selectedCategory by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var accessoryToEdit by remember { mutableStateOf<AccessoryItem?>(null) }
    var accessoryToDelete by remember { mutableStateOf<AccessoryItem?>(null) }

    val categories = listOf(
        "ALL" to "الكل (${allAccessories.size})",
        "CHARGERS" to "شواحن وكوابل 🔌",
        "CASES" to "كفرات وحماية 📱",
        "SCREEN_PROTECTORS" to "استكرات شاشة 🛡️",
        "AUDIO" to "سماعات وصوتيات 🎧",
        "SPARE_PARTS" to "قطع غيار وشاشات 🔬",
        "OTHER" to "أخرى 📦"
    )

    val filteredList = allAccessories.filter { item ->
        val matchesSearch = searchQuery.isBlank() ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.barcode.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "ALL" || item.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(ObsidianNavyBg).testTag("accessories_screen"),
        containerColor = ObsidianNavyBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    accessoryToEdit = null
                    showAddEditDialog = true
                },
                containerColor = GoldPrimary,
                contentColor = ObsidianNavyBg,
                shape = CircleShape,
                modifier = Modifier.testTag("add_accessory_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة صنف")
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
                            text = "الإكسسوارات وقطع الغيار",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary
                        )
                        Text(
                            text = "إدارة الكميات، تنبيهات النواقص، والتسعير",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    if (lowStockList.isNotEmpty()) {
                        LuxuryBadge(
                            text = "${lowStockList.size} قارب على النفاد!",
                            color = AmberWarning,
                            textColor = AmberWarning
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("بحث في المنتجات أو الباركود...") },
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

                // Category Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { (key, label) ->
                        val isSel = selectedCategory == key
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, if (isSel) GoldPrimary else ObsidianBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedCategory = key },
                            color = if (isSel) GoldDark else ObsidianSurfaceBg
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = if (isSel) TextWhite else TextMuted,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                            )
                        }
                    }
                }
            }

            // Accessories List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Category, contentDescription = null, tint = TextMuted, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("لا توجد إكسسوارات أو قطع غيار في هذا القسم", color = TextMuted)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        val isLowStock = item.stockQuantity <= item.minAlertQuantity
                        val profit = item.sellingPrice - item.costPrice

                        LuxuryCard(
                            modifier = Modifier.fillMaxWidth().testTag("accessory_card_${item.id}"),
                            backgroundColor = ObsidianCardBg
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(item.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextWhite)
                                        if (isLowStock) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            LuxuryBadge(text = "مخزون منخفض!", color = AmberWarning, textColor = AmberWarning)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("التكلفة: ${formatMoney(item.costPrice, storeProfile.currency)} • البيع: ${formatMoney(item.sellingPrice, storeProfile.currency)} • الربح: +${formatMoney(profit, storeProfile.currency)}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {
                                        accessoryToEdit = item
                                        showAddEditDialog = true
                                    }, modifier = Modifier.size(32.dp)) {
                                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = GoldLight, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { accessoryToDelete = item }, modifier = Modifier.size(32.dp)) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = CrimsonRed.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = ObsidianBorder, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Quantity Stepper & POS Cart Add
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("الكمية المتوفرة:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                    IconButton(
                                        onClick = {
                                            if (item.stockQuantity > 0) {
                                                viewModel.saveAccessory(item.copy(stockQuantity = item.stockQuantity - 1))
                                            }
                                        },
                                        modifier = Modifier.size(28.dp).clip(CircleShape).background(ObsidianSurfaceBg)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "نقص", tint = TextWhite, modifier = Modifier.size(14.dp))
                                    }
                                    Text("${item.stockQuantity} قطعة", fontWeight = FontWeight.Bold, color = if (isLowStock) AmberWarning else TextWhite)
                                    IconButton(
                                        onClick = {
                                            viewModel.saveAccessory(item.copy(stockQuantity = item.stockQuantity + 1))
                                        },
                                        modifier = Modifier.size(28.dp).clip(CircleShape).background(ObsidianSurfaceBg)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "زيادة", tint = TextWhite, modifier = Modifier.size(14.dp))
                                    }
                                }

                                LuxuryButton(
                                    text = "+ إضافة للسلة",
                                    icon = Icons.Default.AddShoppingCart,
                                    onClick = {
                                        viewModel.addAccessoryToCart(item)
                                        onNavigateToPOS()
                                    },
                                    modifier = Modifier.height(36.dp),
                                    containerColor = GoldPrimary,
                                    contentColor = ObsidianNavyBg
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        var name by remember { mutableStateOf(accessoryToEdit?.name ?: "") }
        var category by remember { mutableStateOf(accessoryToEdit?.category ?: "CHARGERS") }
        var cost by remember { mutableStateOf(accessoryToEdit?.costPrice?.toString() ?: "") }
        var sell by remember { mutableStateOf(accessoryToEdit?.sellingPrice?.toString() ?: "") }
        var qty by remember { mutableStateOf(accessoryToEdit?.stockQuantity?.toString() ?: "10") }
        var minAlert by remember { mutableStateOf(accessoryToEdit?.minAlertQuantity?.toString() ?: "3") }
        var barcode by remember { mutableStateOf(accessoryToEdit?.barcode ?: "") }

        Dialog(onDismissRequest = { showAddEditDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, GoldPrimary, RoundedCornerShape(20.dp)),
                color = ObsidianCardBg
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(if (accessoryToEdit == null) "إضافة صنف إكسسوار / قطع غيار" else "تعديل الصنف", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم الصنف والوصف") },
                        placeholder = { Text("مثال: شاحن أنكر 65W سريع") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cost,
                            onValueChange = { cost = it },
                            label = { Text("سعر التكلفة") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = sell,
                            onValueChange = { sell = it },
                            label = { Text("سعر البيع") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = qty,
                            onValueChange = { qty = it },
                            label = { Text("الكمية المتوفرة") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = minAlert,
                            onValueChange = { minAlert = it },
                            label = { Text("تنبيه النقص عند") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                    }

                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("الباركود (Barcode)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LuxuryOutlinedButton(text = "إلغاء", onClick = { showAddEditDialog = false }, modifier = Modifier.weight(1f))
                        LuxuryButton(
                            text = "حفظ الصنف",
                            onClick = {
                                val c = cost.toDoubleOrNull() ?: 0.0
                                val s = sell.toDoubleOrNull() ?: c
                                val q = qty.toIntOrNull() ?: 1
                                val m = minAlert.toIntOrNull() ?: 3

                                val item = (accessoryToEdit ?: AccessoryItem(
                                    name = name.ifBlank { "إكسسوار جديد" },
                                    category = category,
                                    costPrice = c,
                                    sellingPrice = s,
                                    stockQuantity = q,
                                    minAlertQuantity = m,
                                    barcode = barcode
                                )).copy(
                                    name = name.ifBlank { "إكسسوار جديد" },
                                    category = category,
                                    costPrice = c,
                                    sellingPrice = s,
                                    stockQuantity = q,
                                    minAlertQuantity = m,
                                    barcode = barcode
                                )
                                viewModel.saveAccessory(item)
                                showAddEditDialog = false
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation
    accessoryToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { accessoryToDelete = null },
            title = { Text("حذف الصنف", color = CrimsonRed, fontWeight = FontWeight.Bold) },
            text = { Text("هل تريد حذف ${item.name} نهائياً؟", color = TextWhite) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAccessory(item)
                    accessoryToDelete = null
                }) {
                    Text("حذف", color = CrimsonRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { accessoryToDelete = null }) {
                    Text("إلغاء", color = TextMuted)
                }
            },
            containerColor = ObsidianCardBg
        )
    }
}
