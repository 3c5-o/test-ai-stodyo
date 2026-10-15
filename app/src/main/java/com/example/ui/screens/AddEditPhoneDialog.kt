package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DeviceCondition
import com.example.data.model.PhoneDevice
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPhoneDialog(
    initialPhone: PhoneDevice? = null,
    onDismiss: () -> Unit,
    onSave: (PhoneDevice) -> Unit
) {
    var brand by remember { mutableStateOf(initialPhone?.brand ?: "Apple") }
    var modelName by remember { mutableStateOf(initialPhone?.modelName ?: "") }
    var storageCapacity by remember { mutableStateOf(initialPhone?.storageCapacity ?: "256GB") }
    var ramSize by remember { mutableStateOf(initialPhone?.ramSize ?: "8GB") }
    var color by remember { mutableStateOf(initialPhone?.color ?: "Desert Titanium") }
    var condition by remember { mutableStateOf(initialPhone?.condition ?: "NEW_SEALED") }
    var batteryHealth by remember { mutableStateOf(initialPhone?.batteryHealth?.toString() ?: "100") }
    var imei1 by remember { mutableStateOf(initialPhone?.imei1 ?: "") }
    var imei2 by remember { mutableStateOf(initialPhone?.imei2 ?: "") }
    var purchasePrice by remember { mutableStateOf(initialPhone?.purchasePrice?.toString() ?: "") }
    var sellingPrice by remember { mutableStateOf(initialPhone?.sellingPrice?.toString() ?: "") }
    var quantity by remember { mutableStateOf(initialPhone?.quantity?.toString() ?: "1") }
    var supplierName by remember { mutableStateOf(initialPhone?.supplierName ?: "") }
    var warrantyMonths by remember { mutableStateOf(initialPhone?.warrantyMonths?.toString() ?: "12") }
    var notes by remember { mutableStateOf(initialPhone?.notes ?: "") }

    val brandPresets = listOf("Apple", "Samsung", "Xiaomi", "Google", "Huawei", "Honor", "OnePlus")
    val storagePresets = listOf("64GB", "128GB", "256GB", "512GB", "1TB")
    val popularModelPresets = when (brand) {
        "Apple" -> listOf("iPhone 16 Pro Max", "iPhone 16 Pro", "iPhone 16", "iPhone 15 Pro Max", "iPhone 15", "iPhone 14")
        "Samsung" -> listOf("Galaxy S25 Ultra", "Galaxy S25+", "Galaxy S25", "Galaxy S24 Ultra", "Galaxy Z Fold 6")
        "Xiaomi" -> listOf("Xiaomi 14 Ultra", "Xiaomi 14", "Redmi Note 13 Pro+", "Poco F6 Pro")
        "Google" -> listOf("Pixel 9 Pro XL", "Pixel 9 Pro", "Pixel 9", "Pixel 8 Pro")
        else -> listOf("Flagship Model", "Mid-range Model")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            color = ObsidianCardBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (initialPhone == null) "إضافة جهاز هاتف جديد" else "تعديل بيانات الهاتف",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary
                        )
                        Text(
                            text = "أدخل مواصفات الجهاز والأسعار ورقم الـ IMEI",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Form Fields
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Brand Selector
                    Text("الماركة / الشركة المصنعة", style = MaterialTheme.typography.labelMedium, color = TextWhite)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(brandPresets) { b ->
                            BrandChip(
                                brandName = b,
                                isSelected = brand == b,
                                onSelect = { brand = b }
                            )
                        }
                    }

                    // Popular Model Quick Fill Chips
                    Text("نماذج سريعة لـ $brand", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(popularModelPresets) { presetModel ->
                            AssistChip(
                                onClick = { modelName = presetModel },
                                label = { Text(presetModel, color = if (modelName == presetModel) GoldAccent else TextWhite) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (modelName == presetModel) ObsidianSurfaceBg else ObsidianNavyBg
                                )
                            )
                        }
                    }

                    // Model Name Input
                    OutlinedTextField(
                        value = modelName,
                        onValueChange = { modelName = it },
                        label = { Text("اسم الموديل (Model Name)") },
                        placeholder = { Text("مثال: iPhone 16 Pro Max") },
                        modifier = Modifier.fillMaxWidth().testTag("phone_model_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    // Storage Capacity Selector
                    Text("سعة التخزين (Storage)", style = MaterialTheme.typography.labelMedium, color = TextWhite)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(storagePresets) { s ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (storageCapacity == s) GoldPrimary else ObsidianSurfaceBg)
                                    .clickable { storageCapacity = s }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = s,
                                    color = if (storageCapacity == s) ObsidianNavyBg else TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Color & RAM
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = color,
                            onValueChange = { color = it },
                            label = { Text("اللون") },
                            placeholder = { Text("تيتانيوم طبيعي") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = ramSize,
                            onValueChange = { ramSize = it },
                            label = { Text("الرام (RAM)") },
                            placeholder = { Text("8GB / 12GB") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }

                    // Device Condition
                    Text("حالة الجهاز", style = MaterialTheme.typography.labelMedium, color = TextWhite)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DeviceCondition.entries.forEach { cond ->
                            val isSel = condition == cond.key
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) GoldDark else ObsidianSurfaceBg)
                                    .border(1.dp, if (isSel) GoldAccent else ObsidianBorder, RoundedCornerShape(10.dp))
                                    .clickable { condition = cond.key }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cond.titleAr,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSel) TextWhite else TextMuted
                                )
                            }
                        }
                    }

                    // Battery Health & Warranty
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = batteryHealth,
                            onValueChange = { batteryHealth = it },
                            label = { Text("نسبة البطارية %") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = warrantyMonths,
                            onValueChange = { warrantyMonths = it },
                            label = { Text("مدة الضمان (شهور)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }

                    // IMEI 1 & IMEI 2
                    OutlinedTextField(
                        value = imei1,
                        onValueChange = { imei1 = it },
                        label = { Text("الرقم التسلسلي IMEI 1 (مطلوب)") },
                        placeholder = { Text("359874102345671") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("phone_imei_input"),
                        trailingIcon = {
                            IconButton(onClick = {
                                // Fast generate realistic IMEI if empty
                                imei1 = "35${(1000000000000L..9999999999999L).random()}"
                            }) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = "توليد IMEI", tint = GoldPrimary)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    OutlinedTextField(
                        value = imei2,
                        onValueChange = { imei2 = it },
                        label = { Text("الرقم التسلسلي IMEI 2 (اختياري)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    // Purchase & Selling Price
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = purchasePrice,
                            onValueChange = { purchasePrice = it },
                            label = { Text("سعر التكلفة / الشراء") },
                            placeholder = { Text("3500") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("phone_cost_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = sellingPrice,
                            onValueChange = { sellingPrice = it },
                            label = { Text("سعر البيع المعروض") },
                            placeholder = { Text("4100") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("phone_price_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }

                    // Supplier & Notes
                    OutlinedTextField(
                        value = supplierName,
                        onValueChange = { supplierName = it },
                        label = { Text("اسم المورد / العميل المصدر") },
                        placeholder = { Text("الوكيل / شراء مباشر") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات إضافية (الملحقات، حالة الجهاز...)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LuxuryOutlinedButton(
                        text = "إلغاء",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        borderColor = TextMuted,
                        textColor = TextMuted
                    )

                    LuxuryButton(
                        text = if (initialPhone == null) "حفظ وإضافة للمخزن" else "حفظ التعديلات",
                        icon = Icons.Default.Check,
                        onClick = {
                            val pPrice = purchasePrice.toDoubleOrNull() ?: 0.0
                            val sPrice = sellingPrice.toDoubleOrNull() ?: pPrice
                            val bHealth = batteryHealth.toIntOrNull() ?: 100
                            val wMonths = warrantyMonths.toIntOrNull() ?: 12
                            val qty = quantity.toIntOrNull() ?: 1

                            val finalImei1 = if (imei1.isBlank()) "35${(1000000000000L..9999999999999L).random()}" else imei1

                            val phoneToSave = (initialPhone ?: PhoneDevice(
                                brand = brand,
                                modelName = modelName.ifBlank { "هاتف ذكي $brand" },
                                storageCapacity = storageCapacity,
                                ramSize = ramSize,
                                color = color.ifBlank { "أسود" },
                                condition = condition,
                                batteryHealth = bHealth,
                                imei1 = finalImei1,
                                imei2 = imei2,
                                purchasePrice = pPrice,
                                sellingPrice = sPrice,
                                quantity = qty,
                                supplierName = supplierName,
                                warrantyMonths = wMonths,
                                notes = notes
                            )).copy(
                                brand = brand,
                                modelName = modelName.ifBlank { "هاتف ذكي $brand" },
                                storageCapacity = storageCapacity,
                                ramSize = ramSize,
                                color = color.ifBlank { "أسود" },
                                condition = condition,
                                batteryHealth = bHealth,
                                imei1 = finalImei1,
                                imei2 = imei2,
                                purchasePrice = pPrice,
                                sellingPrice = sPrice,
                                quantity = qty,
                                supplierName = supplierName,
                                warrantyMonths = wMonths,
                                notes = notes
                            )
                            onSave(phoneToSave)
                        },
                        modifier = Modifier.weight(1.5f),
                        testTag = "save_phone_button"
                    )
                }
            }
        }
    }
}
