package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RepairStatus
import com.example.data.model.RepairTicket
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairsScreen(
    viewModel: StoreViewModel
) {
    val allRepairs by viewModel.allRepairs.collectAsState()
    val storeProfile by viewModel.storeProfile.collectAsState()
    val context = LocalContext.current

    var selectedStatusFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var ticketToEdit by remember { mutableStateOf<RepairTicket?>(null) }
    var ticketToDelete by remember { mutableStateOf<RepairTicket?>(null) }

    val statusTabs = listOf(
        "ALL" to "الكل (${allRepairs.size})",
        "RECEIVED" to "تم الاستلام",
        "IN_DIAGNOSIS" to "قيد الفحص",
        "WAITING_PARTS" to "بانتظار قطع",
        "IN_REPAIR" to "قيد الإصلاح",
        "READY" to "جاهز للتسليم",
        "DELIVERED" to "تم التسليم"
    )

    val filteredTickets = allRepairs.filter { ticket ->
        val matchesSearch = searchQuery.isBlank() ||
                ticket.customerName.contains(searchQuery, ignoreCase = true) ||
                ticket.customerPhone.contains(searchQuery, ignoreCase = true) ||
                ticket.deviceModel.contains(searchQuery, ignoreCase = true) ||
                ticket.ticketNumber.contains(searchQuery, ignoreCase = true)

        val matchesStatus = selectedStatusFilter == "ALL" || ticket.status == selectedStatusFilter
        matchesSearch && matchesStatus
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(ObsidianNavyBg).testTag("repairs_screen"),
        containerColor = ObsidianNavyBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    ticketToEdit = null
                    showAddEditDialog = true
                },
                containerColor = ElectricBlue,
                contentColor = ObsidianNavyBg,
                shape = CircleShape,
                modifier = Modifier.testTag("add_repair_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "تسجيل طلب صيانة جديد")
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
                            text = "مركز الصيانة والورشة",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = ElectricBlue
                        )
                        Text(
                            text = "متابعة مراحل الإصلاح، قطع الغيار، وإشعارات العملاء",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    LuxuryBadge(
                        text = "${allRepairs.count { it.status != "DELIVERED" }} قيد المتابعة",
                        color = AmberWarning,
                        textColor = AmberWarning
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("بحث برقم التذكرة، العميل، أو موديل الجهاز...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ElectricBlue) },
                    modifier = Modifier.fillMaxWidth().testTag("repairs_search_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianSurfaceBg,
                        unfocusedContainerColor = ObsidianSurfaceBg,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Status Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(statusTabs) { (key, label) ->
                        val isSelected = selectedStatusFilter == key
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, if (isSelected) ElectricBlue else ObsidianBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedStatusFilter = key },
                            color = if (isSelected) ElectricBlue else ObsidianSurfaceBg
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = if (isSelected) ObsidianNavyBg else TextMuted,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            )
                        }
                    }
                }
            }

            // Ticket List
            if (filteredTickets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Handyman, contentDescription = null, tint = TextMuted, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "لا توجد طلبات صيانة مطابقة للبحث" else "لا توجد طلبات في هذه الحالة",
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTickets, key = { it.id }) { ticket ->
                        val statusEnum = RepairStatus.fromKey(ticket.status)
                        val statusColor = Color(statusEnum.colorHex)
                        val dateFormatted = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(ticket.receivedDate))

                        LuxuryCard(
                            modifier = Modifier.fillMaxWidth().testTag("repair_card_${ticket.id}"),
                            backgroundColor = ObsidianCardBg
                        ) {
                            // Top Row: Ticket Number, Status Badge & Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = ticket.ticketNumber,
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = GoldPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    LuxuryBadge(
                                        text = statusEnum.titleAr,
                                        color = statusColor,
                                        textColor = statusColor
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Share notification with customer
                                    IconButton(
                                        onClick = {
                                            val remainingCost = ticket.totalCost - ticket.paidAmount
                                            val msg = """
                                                ━━━━━━━━━━━━━━━━━━━━━━
                                                🔧 *مركز صيانة ${storeProfile.storeName}*
                                                ━━━━━━━━━━━━━━━━━━━━━━
                                                مرحباً أستاذ *${ticket.customerName}*،
                                                تحديث حالة جهازك: *${ticket.deviceModel}*
                                                
                                                🔖 *رقم التذكرة:* ${ticket.ticketNumber}
                                                🔍 *العطل:* ${ticket.problemDescription}
                                                ⚙️ *الحالة الحالية:* *${statusEnum.titleAr}*
                                                
                                                💰 *إجمالي التكلفة:* ${formatMoney(ticket.totalCost, storeProfile.currency)}
                                                ✅ *المدفوع مقدماً:* ${formatMoney(ticket.paidAmount, storeProfile.currency)}
                                                ${if (remainingCost > 0) "💵 *المتبقي عند الاستلام:* ${formatMoney(remainingCost, storeProfile.currency)}\n" else ""}
                                                📍 ${storeProfile.address}
                                                📞 للاستفسار: ${storeProfile.phoneNumber}
                                                ━━━━━━━━━━━━━━━━━━━━━━
                                                ✨ نسعد دائماً بخدمتكم!
                                            """.trimIndent()
                                            try {
                                                val cleanPhone = ticket.customerPhone.replace("[^0-9]".toRegex(), "")
                                                val whatsappUri = if (cleanPhone.length >= 9) {
                                                    android.net.Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${android.net.Uri.encode(msg)}")
                                                } else {
                                                    android.net.Uri.parse("https://api.whatsapp.com/send?text=${android.net.Uri.encode(msg)}")
                                                }
                                                val waIntent = Intent(Intent.ACTION_VIEW, whatsappUri)
                                                context.startActivity(waIntent)
                                            } catch (e: Exception) {
                                                val shareIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, msg)
                                                    type = "text/plain"
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "إرسال إشعار للعميل"))
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "إرسال إشعار", tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(onClick = {
                                        ticketToEdit = ticket
                                        showAddEditDialog = true
                                    }, modifier = Modifier.size(32.dp)) {
                                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = GoldLight, modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(onClick = { ticketToDelete = ticket }, modifier = Modifier.size(32.dp)) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = CrimsonRed.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Device & Customer Info
                            Text(
                                text = ticket.deviceModel,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextWhite
                            )
                            Text(
                                text = "العميل: ${ticket.customerName} • هاتف: ${ticket.customerPhone}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )

                            if (ticket.devicePasscode.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ObsidianSurfaceBg)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "رمز الجهاز: ${ticket.devicePasscode}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = GoldAccent
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Problem Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ObsidianSurfaceBg)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "العطل: ${ticket.problemDescription}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextWhite
                                    )
                                    if (ticket.notes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "ملاحظات الفني: ${ticket.notes}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = TextDim
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = ObsidianBorder, thickness = 0.8.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Footer: Cost Breakdown & Status Advance Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text("قطع: ${formatMoney(ticket.costParts, storeProfile.currency)}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextMuted)
                                        Text("أجر: ${formatMoney(ticket.serviceFee, storeProfile.currency)}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextMuted)
                                    }
                                    Text(
                                        text = "الإجمالي: ${formatMoney(ticket.totalCost, storeProfile.currency)}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldAccent
                                    )
                                }

                                // Status Switcher Button
                                val nextStatus = when (ticket.status) {
                                    "RECEIVED" -> "IN_DIAGNOSIS" to "بدء الفحص 🔬"
                                    "IN_DIAGNOSIS" -> "IN_REPAIR" to "بدء الإصلاح ⚙️"
                                    "IN_REPAIR" -> "READY" to "جاهز للتسليم ✅"
                                    "READY" -> "DELIVERED" to "تسليم للعميل 🤝"
                                    else -> null
                                }

                                if (nextStatus != null) {
                                    Button(
                                        onClick = {
                                            viewModel.updateRepairStatus(ticket.id, nextStatus.first)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("advance_repair_${ticket.id}")
                                    ) {
                                        Text(
                                            text = nextStatus.second,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextWhite
                                        )
                                    }
                                } else {
                                    LuxuryBadge(
                                        text = "تم التسليم بنجاح ✨",
                                        color = EmeraldGreen,
                                        textColor = EmeraldGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        AddEditRepairDialog(
            initialTicket = ticketToEdit,
            onDismiss = {
                showAddEditDialog = false
                ticketToEdit = null
            },
            onSave = { savedTicket ->
                viewModel.saveRepairTicket(savedTicket)
                showAddEditDialog = false
                ticketToEdit = null
            }
        )
    }

    // Delete Dialog
    if (ticketToDelete != null) {
        AlertDialog(
            onDismissRequest = { ticketToDelete = null },
            title = { Text("حذف طلب الصيانة", color = CrimsonRed, fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من حذف تذكرة الصيانة ${ticketToDelete?.ticketNumber} نهائياً؟", color = TextWhite) },
            confirmButton = {
                TextButton(onClick = {
                    ticketToDelete?.let { viewModel.deleteRepairTicket(it) }
                    ticketToDelete = null
                }) {
                    Text("حذف", color = CrimsonRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { ticketToDelete = null }) {
                    Text("إلغاء", color = TextMuted)
                }
            },
            containerColor = ObsidianCardBg
        )
    }
}
