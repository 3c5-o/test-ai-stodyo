package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhoneDevice
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PhoneValuationResult
import com.example.ui.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsedPhoneValuationScreen(
    viewModel: StoreViewModel,
    onPhoneAddedToInventory: () -> Unit
) {
    val storeProfile by viewModel.storeProfile.collectAsState()

    var brand by remember { mutableStateOf("Apple") }
    var model by remember { mutableStateOf("iPhone 15 Pro") }
    var storage by remember { mutableStateOf("256GB") }
    var batteryHealth by remember { mutableFloatStateOf(89f) }

    var screenScratches by remember { mutableStateOf(false) }
    var screenReplaced by remember { mutableStateOf(false) }
    var bodyDents by remember { mutableStateOf(false) }
    var faceIdWorks by remember { mutableStateOf(true) }
    var camerasAllGood by remember { mutableStateOf(true) }
    var hasOriginalBox by remember { mutableStateOf(true) }

    var valuationResult by remember { mutableStateOf<PhoneValuationResult?>(null) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    val popularAppleModels = listOf("iPhone 16 Pro Max", "iPhone 16 Pro", "iPhone 16", "iPhone 15 Pro Max", "iPhone 15 Pro", "iPhone 15", "iPhone 14 Pro", "iPhone 14", "iPhone 13")
    val popularSamsungModels = listOf("Galaxy S25 Ultra", "Galaxy S24 Ultra", "Galaxy S23 Ultra", "Galaxy S22 Ultra", "Galaxy Z Fold 6", "Galaxy Z Flip 6")
    val popularOtherModels = listOf("Pixel 9 Pro XL", "Xiaomi 14 Ultra", "Honor Magic 6 Pro", "Huawei Pura 70")

    val currentModels = when (brand) {
        "Apple" -> popularAppleModels
        "Samsung" -> popularSamsungModels
        else -> popularOtherModels
    }

    // Auto calculate upon changes
    LaunchedEffect(brand, model, storage, batteryHealth, screenScratches, screenReplaced, bodyDents, faceIdWorks, camerasAllGood, hasOriginalBox) {
        valuationResult = viewModel.calculateValuation(
            brand = brand,
            model = model,
            storage = storage,
            batteryHealth = batteryHealth.toInt(),
            screenHasScratches = screenScratches,
            screenReplaced = screenReplaced,
            bodyHasDents = bodyDents,
            faceIdWorks = faceIdWorks,
            camerasAllGood = camerasAllGood,
            hasOriginalBox = hasOriginalBox
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(ObsidianNavyBg).testTag("valuation_screen"),
        containerColor = ObsidianNavyBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(PurpleTech.copy(alpha = 0.25f), ObsidianCardBg)))
                    .border(1.dp, PurpleTech.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FactCheck, contentDescription = null, tint = PurpleTech, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "فاحص وتقييم الهواتف المستعملة الذكي",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "فحص دقيق لحالة الجهاز لتقدير سعر الشراء المناسب وهامش الربح المتوقع",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            // Step 1: Device Selection
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Text("١. بيانات الجهاز والموديل", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                Spacer(modifier = Modifier.height(10.dp))

                // Brand Selector
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("Apple", "Samsung", "أخرى / Other")) { b ->
                        BrandChip(
                            brandName = b,
                            isSelected = brand == b,
                            onSelect = {
                                brand = b
                                model = if (b == "Apple") "iPhone 15 Pro" else if (b == "Samsung") "Galaxy S24 Ultra" else "Pixel 9 Pro XL"
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Model Selector
                Text("الموديل:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(currentModels) { m ->
                        val isSel = model == m
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) GoldDark else ObsidianSurfaceBg)
                                .border(1.dp, if (isSel) GoldAccent else ObsidianBorder, RoundedCornerShape(10.dp))
                                .clickable { model = m }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(m, color = if (isSel) TextWhite else TextMuted, style = MaterialTheme.typography.bodySmall.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Storage Selector
                Text("السعة:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("128GB", "256GB", "512GB", "1TB").forEach { s ->
                        val isSel = storage == s
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) GoldPrimary else ObsidianSurfaceBg)
                                .clickable { storage = s }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(s, color = if (isSel) ObsidianNavyBg else TextWhite, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            // Step 2: Inspection Checklist
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Text("٢. فحص الحالة والعيوب الفنية", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                Spacer(modifier = Modifier.height(10.dp))

                // Battery Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("صحة البطارية: ${batteryHealth.toInt()}%", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                        LuxuryBadge(
                            text = if (batteryHealth >= 90) "ممتازة" else if (batteryHealth >= 80) "جيدة" else "تتطلب استبدال",
                            color = if (batteryHealth >= 85) EmeraldGreen else AmberWarning,
                            textColor = if (batteryHealth >= 85) EmeraldGreen else AmberWarning
                        )
                    }
                    Slider(
                        value = batteryHealth,
                        onValueChange = { batteryHealth = it },
                        valueRange = 50f..100f,
                        colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Checklist Toggles
                ChecklistToggleItem(title = "بصمة الوجه FaceID / البصمة تعمل بكفاءة", checked = faceIdWorks, onCheckedChange = { faceIdWorks = it })
                ChecklistToggleItem(title = "الكاميرات والعدسات 1x / 3x / 5x سليمة تماماً", checked = camerasAllGood, onCheckedChange = { camerasAllGood = it })
                ChecklistToggleItem(title = "يتوفر الكرتون الأصلي مع الجهاز", checked = hasOriginalBox, onCheckedChange = { hasOriginalBox = it })
                ChecklistToggleItem(title = "توجد خدوش أو كسور على الشاشة", checked = screenScratches, onCheckedChange = { screenScratches = it })
                ChecklistToggleItem(title = "الشاشة متبدلة أو غير أصلية", checked = screenReplaced, onCheckedChange = { screenReplaced = it })
                ChecklistToggleItem(title = "توجد صدمات أو انحناء في الهيكل المعدني", checked = bodyDents, onCheckedChange = { bodyDents = it })
            }

            // Step 3: Live Valuation Card & Offer
            valuationResult?.let { res ->
                LuxuryCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderBrush = Brush.verticalGradient(listOf(GoldAccent, ObsidianBorder)),
                    backgroundColor = ObsidianSurfaceBg
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("نتيجة التقييم الفني المعتمد", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                            Text("${res.brand} ${res.model} - ${res.storage}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                        LuxuryBadge(text = "درجة النظافة: ${res.grade}", color = GoldAccent, textColor = GoldAccent)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (res.deductedIssues.isNotEmpty()) {
                        Text("الملاحظات والخصومات المطبقة:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        res.deductedIssues.forEach { issue ->
                            Text("• $issue", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = AmberWarning)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    HorizontalDivider(color = ObsidianBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // 3 Price Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Offer Price (Cost to Store)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ObsidianCardBg)
                                .border(1.dp, GoldPrimary, RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("عرض الشراء للعميل", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = GoldPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(formatMoney(res.fairTradeInOffer, storeProfile.currency), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = GoldAccent)
                            }
                        }

                        // Resale Price
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ObsidianCardBg)
                                .border(1.dp, ElectricBlue, RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("سعر البيع المقترح", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = ElectricBlue)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(formatMoney(res.recommendedResalePrice, storeProfile.currency), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextWhite)
                            }
                        }

                        // Profit Expected
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ObsidianCardBg)
                                .border(1.dp, EmeraldGreen, RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("الربح الصافي المتوقع", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = EmeraldGreen)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("+${formatMoney(res.expectedProfitMargin, storeProfile.currency)}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // One-Tap Add Directly to Inventory
                    LuxuryButton(
                        text = "إضافة الجهاز مباشرة إلى المخزن بسعر الشراء هذا",
                        icon = Icons.Default.AddShoppingCart,
                        onClick = {
                            val cond = if (res.grade.contains("A+")) "LIKE_NEW" else if (res.grade.contains("A")) "USED_GOOD" else "REFURBISHED"
                            val newPhone = PhoneDevice(
                                brand = res.brand,
                                modelName = res.model,
                                storageCapacity = res.storage,
                                ramSize = "8GB",
                                color = "مستعمل - تم الفحص",
                                condition = cond,
                                batteryHealth = batteryHealth.toInt(),
                                imei1 = "35${(1000000000000L..9999999999999L).random()}",
                                purchasePrice = res.fairTradeInOffer,
                                sellingPrice = res.recommendedResalePrice,
                                quantity = 1,
                                supplierName = "شراء من عميل بالتقييم الذكي",
                                warrantyMonths = 3,
                                notes = "تقييم: ${res.grade} • العيوب: ${res.deductedIssues.joinToString()}"
                            )
                            viewModel.savePhone(newPhone)
                            showSuccessSnackbar = true
                            onPhoneAddedToInventory()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("add_evaluated_phone_btn")
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ChecklistToggleItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodySmall, color = TextWhite, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GoldPrimary,
                checkedTrackColor = GoldDark,
                uncheckedTrackColor = ObsidianBorder
            )
        )
    }
}
