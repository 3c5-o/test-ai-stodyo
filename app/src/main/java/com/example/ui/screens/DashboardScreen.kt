package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DeviceCondition
import com.example.data.model.PhoneDevice
import com.example.data.model.RepairStatus
import com.example.data.model.RepairTicket
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun DashboardScreen(
    viewModel: StoreViewModel,
    onNavigateToInventory: () -> Unit,
    onNavigateToPOS: () -> Unit,
    onNavigateToRepairs: () -> Unit,
    onNavigateToValuation: () -> Unit,
    onNavigateToInstallments: () -> Unit,
    onNavigateToAccessories: () -> Unit,
    onAddPhoneClick: () -> Unit,
    onAddRepairClick: () -> Unit
) {
    val stats by viewModel.storeStats.collectAsState()
    val inStockPhones by viewModel.inStockPhones.collectAsState()
    val activeRepairs by viewModel.activeRepairs.collectAsState()
    val lowStockAccessories by viewModel.lowStockAccessories.collectAsState()
    val storeProfile by viewModel.storeProfile.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianNavyBg)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Hero Store Showcase Header
        item {
            StoreHeroHeader(
                storeName = storeProfile.storeName,
                ownerName = storeProfile.ownerName,
                currency = storeProfile.currency,
                onAddPhoneClick = onAddPhoneClick,
                onPOSClick = onNavigateToPOS
            )
        }

        // Low Stock Warning Alert if any
        if (lowStockAccessories.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AmberWarning.copy(alpha = 0.15f))
                        .border(1.dp, AmberWarning.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable { onNavigateToAccessories() }
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = AmberWarning,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "تنبيه المخزون المنخفض!",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = AmberWarning
                                )
                                Text(
                                    text = "يوجد ${lowStockAccessories.size} منتجات/قطع غيار قاربت على النفاد",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextWhite
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "عرض",
                            tint = AmberWarning
                        )
                    }
                }
            }
        }

        // 4 Key Stat Metrics
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader(
                    title = "المؤشرات المالية والمخزون",
                    subtitle = "نظرة مباشرة وفورية على أداء المحل"
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LuxuryStatCard(
                        modifier = Modifier.weight(1f),
                        title = "قيمة مخزون الأجهزة",
                        value = formatMoney(stats.totalInventoryValue, storeProfile.currency),
                        subtitle = "${stats.inStockPhonesCount} جهاز متوفر",
                        icon = Icons.Default.Smartphone,
                        accentColor = GoldPrimary,
                        testTag = "stat_inventory"
                    )
                    LuxuryStatCard(
                        modifier = Modifier.weight(1f),
                        title = "الأرباح المتوقعة",
                        value = formatMoney(stats.totalExpectedProfit, storeProfile.currency),
                        subtitle = "هامش ربح ممتاز",
                        icon = Icons.Default.TrendingUp,
                        accentColor = EmeraldGreen,
                        testTag = "stat_profit"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LuxuryStatCard(
                        modifier = Modifier.weight(1f),
                        title = "إجمالي المبيعات",
                        value = formatMoney(stats.totalSalesRevenue, storeProfile.currency),
                        subtitle = "${stats.soldPhonesCount} هاتف مباع",
                        icon = Icons.Default.PointOfSale,
                        accentColor = ElectricBlue,
                        testTag = "stat_sales"
                    )
                    LuxuryStatCard(
                        modifier = Modifier.weight(1f),
                        title = "الصيانة الجارية",
                        value = formatMoney(stats.totalActiveRepairsValue, storeProfile.currency),
                        subtitle = "${stats.activeRepairsCount} جهاز قيد الصيانة",
                        icon = Icons.Default.Build,
                        accentColor = AmberWarning,
                        testTag = "stat_repairs"
                    )
                }
            }
        }

        // Quick Actions Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                SectionHeader(
                    title = "الخدمات والعمليات السريعة",
                    subtitle = "وصول مباشر لجميع أقسام إدارة المتجر"
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        modifier = Modifier.weight(1f),
                        title = "نقطة البيع\nوالفواتير",
                        icon = Icons.Default.ReceiptLong,
                        accentColor = EmeraldGreen,
                        onClick = onNavigateToPOS
                    )
                    QuickActionTile(
                        modifier = Modifier.weight(1f),
                        title = "إضافة هاتف\nجديد للمخزن",
                        icon = Icons.Default.AddCircleOutline,
                        accentColor = GoldPrimary,
                        onClick = onAddPhoneClick
                    )
                    QuickActionTile(
                        modifier = Modifier.weight(1f),
                        title = "طلب صيانة\nوإصلاح جديد",
                        icon = Icons.Default.Handyman,
                        accentColor = ElectricBlue,
                        onClick = onAddRepairClick
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        modifier = Modifier.weight(1f),
                        title = "فاحص وتقييم\nالهواتف المستعملة",
                        icon = Icons.Default.FactCheck,
                        accentColor = PurpleTech,
                        onClick = onNavigateToValuation
                    )
                    QuickActionTile(
                        modifier = Modifier.weight(1f),
                        title = "التقسيط\nوالديون والذمم",
                        icon = Icons.Default.AccountBalanceWallet,
                        accentColor = GoldAccent,
                        onClick = onNavigateToInstallments
                    )
                    QuickActionTile(
                        modifier = Modifier.weight(1f),
                        title = "الإكسسوارات\nوقطع الغيار",
                        icon = Icons.Default.Category,
                        accentColor = AmberWarning,
                        onClick = onNavigateToAccessories
                    )
                }
            }
        }

        // Live Performance Visualization
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                LuxuryCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = ObsidianCardBg
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "معدل مبيعات الأسبوع",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextWhite
                            )
                            Text(
                                text = "توزيع الإيرادات والأرباح اليومية",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        LuxuryBadge(text = "+24.8% نمو", color = EmeraldGreen, textColor = EmeraldGreen)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Visual Animated Chart Bars
                    WeeklyBarChart()
                }
            }
        }

        // Featured In-Stock Phones Horizontal List
        item {
            Column(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)) {
                PaddingWrapper(horizontal = 16.dp) {
                    SectionHeader(
                        title = "أحدث الهواتف في المخزن",
                        subtitle = "الأجهزة المتاحة للبيع الفوري",
                        actionText = "عرض الكل (${inStockPhones.size})",
                        onActionClick = onNavigateToInventory
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (inStockPhones.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("لا توجد أجهزة متوفرة حالياً", color = TextMuted)
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(inStockPhones.take(6)) { phone ->
                            CompactPhoneCard(
                                phone = phone,
                                currency = storeProfile.currency,
                                onQuickSale = {
                                    viewModel.addPhoneToCart(phone)
                                    onNavigateToPOS()
                                }
                            )
                        }
                    }
                }
            }
        }

        // Active Repair Tickets Summary
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                SectionHeader(
                    title = "الصيانات الجارية في الورشة",
                    subtitle = "متابعة تسليم الأجهزة للعملاء",
                    actionText = "إدارة الصيانة (${activeRepairs.size})",
                    onActionClick = onNavigateToRepairs
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (activeRepairs.isEmpty()) {
                    LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth().padding(12.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("جميع طلبات الصيانة مكتملة وتم تسليمها", color = TextMuted)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        activeRepairs.take(3).forEach { ticket ->
                            CompactRepairCard(
                                ticket = ticket,
                                currency = storeProfile.currency,
                                onStatusAdvance = {
                                    val nextStatus = when (ticket.status) {
                                        "RECEIVED" -> "IN_DIAGNOSIS"
                                        "IN_DIAGNOSIS" -> "IN_REPAIR"
                                        "IN_REPAIR" -> "READY"
                                        "READY" -> "DELIVERED"
                                        else -> "READY"
                                    }
                                    viewModel.updateRepairStatus(ticket.id, nextStatus)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoreHeroHeader(
    storeName: String,
    ownerName: String,
    currency: String,
    onAddPhoneClick: () -> Unit,
    onPOSClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
    ) {
        // Background Hero Banner Image
        Image(
            painter = painterResource(id = R.drawable.store_hero_banner),
            contentDescription = "Store Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient Dark Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            ObsidianNavyBg.copy(alpha = 0.6f),
                            ObsidianNavyBg.copy(alpha = 0.88f),
                            ObsidianNavyBg
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(GoldPrimary, GoldDark)))
                            .border(1.5.dp, GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneIphone,
                            contentDescription = null,
                            tint = ObsidianNavyBg,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = storeName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "المتجر مفتوح • $ownerName",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }

                LuxuryBadge(
                    text = "نسخة VIP الفاخرة",
                    color = GoldPrimary,
                    textColor = GoldAccent
                )
            }

            // Quick Callout / Fast POS Launcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LuxuryButton(
                    text = "نقطة بيع سريعة (POS)",
                    icon = Icons.Default.PointOfSale,
                    onClick = onPOSClick,
                    modifier = Modifier.weight(1.3f),
                    containerColor = GoldPrimary,
                    contentColor = ObsidianNavyBg,
                    testTag = "hero_pos_button"
                )

                LuxuryOutlinedButton(
                    text = "+ إضافة جهاز",
                    icon = Icons.Default.Add,
                    onClick = onAddPhoneClick,
                    modifier = Modifier.weight(1f),
                    borderColor = GoldLight,
                    textColor = GoldLight,
                    testTag = "hero_add_phone_button"
                )
            }
        }
    }
}

@Composable
fun QuickActionTile(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(105.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                Brush.verticalGradient(listOf(accentColor.copy(alpha = 0.4f), ObsidianBorder)),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        color = ObsidianCardBg,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                ),
                color = TextWhite,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun WeeklyBarChart() {
    val days = listOf("السبت", "الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة")
    val sales = listOf(4200f, 6800f, 5400f, 8900f, 7300f, 11500f, 9600f)
    val maxSale = 12000f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEachIndexed { index, day ->
            val heightRatio = (sales[index] / maxSale).coerceIn(0.15f, 1f)
            val isPeak = index == 5 // Thursday

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${(sales[index] / 1000).toInt()}k",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = if (isPeak) GoldAccent else TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .fillMaxHeight(heightRatio)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(
                            if (isPeak) {
                                Brush.verticalGradient(listOf(GoldAccent, GoldDark))
                            } else {
                                Brush.verticalGradient(listOf(ElectricBlue.copy(alpha = 0.8f), ObsidianSurfaceBg))
                            }
                        )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = day.take(3),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (isPeak) GoldPrimary else TextMuted
                )
            }
        }
    }
}

@Composable
fun CompactPhoneCard(
    phone: PhoneDevice,
    currency: String,
    onQuickSale: () -> Unit
) {
    LuxuryCard(
        modifier = Modifier
            .width(190.dp)
            .height(200.dp),
        backgroundColor = ObsidianCardBg,
        shapeRadius = 16.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LuxuryBadge(
                    text = phone.brand,
                    color = GoldPrimary,
                    textColor = GoldPrimary
                )
                LuxuryConditionBadge(condition = phone.condition)
            }

            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = phone.modelName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${phone.storageCapacity} • ${phone.color}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (phone.batteryHealth in 1..99) {
                    Text(
                        text = "البطارية: ${phone.batteryHealth}%",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = EmeraldGreen
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "سعر البيع",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = TextMuted
                    )
                    Text(
                        text = formatMoney(phone.sellingPrice, currency),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = GoldAccent
                    )
                }

                IconButton(
                    onClick = onQuickSale,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                        .testTag("quick_sell_phone_${phone.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCartCheckout,
                        contentDescription = "بيع سريع",
                        tint = ObsidianNavyBg,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CompactRepairCard(
    ticket: RepairTicket,
    currency: String,
    onStatusAdvance: () -> Unit
) {
    val statusEnum = RepairStatus.fromKey(ticket.status)
    LuxuryCard(
        modifier = Modifier.fillMaxWidth(),
        borderBrush = Brush.horizontalGradient(
            listOf(Color(statusEnum.colorHex).copy(alpha = 0.5f), ObsidianBorder)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ticket.ticketNumber,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LuxuryBadge(
                        text = statusEnum.titleAr,
                        color = Color(statusEnum.colorHex),
                        textColor = Color(statusEnum.colorHex)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${ticket.deviceModel} • العميل: ${ticket.customerName}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextWhite
                )
                Text(
                    text = "العطل: ${ticket.problemDescription}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatMoney(ticket.totalCost, currency),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = GoldAccent
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onStatusAdvance,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(statusEnum.colorHex)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "ترقية الحالة",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                }
            }
        }
    }
}

@Composable
fun PaddingWrapper(horizontal: androidx.compose.ui.unit.Dp, content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = horizontal)) {
        content()
    }
}
