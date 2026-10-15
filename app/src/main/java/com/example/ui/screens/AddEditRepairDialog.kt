package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.RepairStatus
import com.example.data.model.RepairTicket
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRepairDialog(
    initialTicket: RepairTicket? = null,
    onDismiss: () -> Unit,
    onSave: (RepairTicket) -> Unit
) {
    var ticketNumber by remember {
        mutableStateOf(initialTicket?.ticketNumber ?: "REP-${(100..999).random()}")
    }
    var customerName by remember { mutableStateOf(initialTicket?.customerName ?: "") }
    var customerPhone by remember { mutableStateOf(initialTicket?.customerPhone ?: "") }
    var deviceModel by remember { mutableStateOf(initialTicket?.deviceModel ?: "") }
    var deviceColor by remember { mutableStateOf(initialTicket?.deviceColor ?: "") }
    var devicePasscode by remember { mutableStateOf(initialTicket?.devicePasscode ?: "") }
    var problemDescription by remember { mutableStateOf(initialTicket?.problemDescription ?: "") }
    var repairType by remember { mutableStateOf(initialTicket?.repairType ?: "SCREEN") }
    var costParts by remember { mutableStateOf(initialTicket?.costParts?.toString() ?: "0") }
    var serviceFee by remember { mutableStateOf(initialTicket?.serviceFee?.toString() ?: "150") }
    var paidAmount by remember { mutableStateOf(initialTicket?.paidAmount?.toString() ?: "0") }
    var technicianName by remember { mutableStateOf(initialTicket?.technicianName ?: "م. الورشة الرئيسي") }
    var notes by remember { mutableStateOf(initialTicket?.notes ?: "") }
    var status by remember { mutableStateOf(initialTicket?.status ?: "RECEIVED") }

    val repairTypes = listOf(
        "SCREEN" to "شاشة 🖥️",
        "BATTERY" to "بطارية 🔋",
        "CHARGING_PORT" to "منفذ الشحن 🔌",
        "CAMERA" to "كاميرا 📷",
        "MOTHERBOARD" to "ماذربورد / IC 🔬",
        "SOFTWARE" to "سوفتوير وفك قفل 💻",
        "OTHER" to "أعطال أخرى 🛠️"
    )

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
                            text = if (initialTicket == null) "تسجيل طلب صيانة جديد" else "تعديل بيانات الصيانة",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary
                        )
                        Text(
                            text = "استلام جهاز للورشة وتوثيق العطل والرمز وتكلفة القطع",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Ticket Number & Status
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = ticketNumber,
                            onValueChange = { ticketNumber = it },
                            label = { Text("رقم التذكرة") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )

                        OutlinedTextField(
                            value = technicianName,
                            onValueChange = { technicianName = it },
                            label = { Text("الفني المسؤول") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }

                    // Customer Details
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("اسم العميل") },
                            placeholder = { Text("مثال: عبدالمحسن الحربي") },
                            modifier = Modifier.weight(1f).testTag("repair_customer_name"),
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
                            label = { Text("رقم جوال العميل") },
                            placeholder = { Text("05xxxxxxxx") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f).testTag("repair_customer_phone"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }

                    // Device Details
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = deviceModel,
                            onValueChange = { deviceModel = it },
                            label = { Text("نوع وموديل الجهاز") },
                            placeholder = { Text("iPhone 15 Pro Max") },
                            modifier = Modifier.weight(1.2f).testTag("repair_device_model"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = devicePasscode,
                            onValueChange = { devicePasscode = it },
                            label = { Text("رمز القفل") },
                            placeholder = { Text("1234 / نمط") },
                            modifier = Modifier.weight(0.8f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }

                    // Repair Type Selector
                    Text("نوع الصيانة المطلوب", style = MaterialTheme.typography.labelMedium, color = TextWhite)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(repairTypes) { (key, label) ->
                            val isSel = repairType == key
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, if (isSel) GoldPrimary else ObsidianBorder, RoundedCornerShape(10.dp))
                                    .clickable { repairType = key },
                                color = if (isSel) GoldDark else ObsidianSurfaceBg
                            ) {
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = if (isSel) TextWhite else TextMuted,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                )
                            }
                        }
                    }

                    // Problem Description
                    OutlinedTextField(
                        value = problemDescription,
                        onValueChange = { problemDescription = it },
                        label = { Text("وصف العطل والفحص المبدئي") },
                        placeholder = { Text("مثال: تبديل شاشة أصلية + تنظيف منفذ الشحن وفحص الكاميرا") },
                        modifier = Modifier.fillMaxWidth().testTag("repair_problem_desc"),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    // Cost Calculation: Parts + Service = Total
                    Text("التكاليف والحساب المالي", style = MaterialTheme.typography.labelMedium, color = TextWhite)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = costParts,
                            onValueChange = { costParts = it },
                            label = { Text("تكلفة القطع") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = serviceFee,
                            onValueChange = { serviceFee = it },
                            label = { Text("أجر اليد / الخدمة") },
                            placeholder = { Text("150") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = paidAmount,
                            onValueChange = { paidAmount = it },
                            label = { Text("المقدم المدفوع") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }

                    // Status Picker (if editing)
                    if (initialTicket != null) {
                        Text("حالة طلب الصيانة", style = MaterialTheme.typography.labelMedium, color = TextWhite)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(RepairStatus.entries) { st ->
                                val isSel = status == st.key
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { status = st.key },
                                    color = if (isSel) GoldPrimary else ObsidianSurfaceBg
                                ) {
                                    Text(
                                        text = st.titleAr,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        color = if (isSel) ObsidianNavyBg else TextMuted,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    // Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات إضافية للورشة") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
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
                        text = if (initialTicket == null) "حفظ التذكرة" else "حفظ التعديلات",
                        icon = Icons.Default.Check,
                        onClick = {
                            val parts = costParts.toDoubleOrNull() ?: 0.0
                            val fee = serviceFee.toDoubleOrNull() ?: 0.0
                            val paid = paidAmount.toDoubleOrNull() ?: 0.0
                            val total = parts + fee

                            val saved = (initialTicket ?: RepairTicket(
                                ticketNumber = ticketNumber,
                                customerName = customerName.ifBlank { "عميل صيانة" },
                                customerPhone = customerPhone.ifBlank { "-" },
                                deviceModel = deviceModel.ifBlank { "هاتف ذكي" },
                                deviceColor = deviceColor,
                                devicePasscode = devicePasscode,
                                problemDescription = problemDescription.ifBlank { "فحص وإصلاح" },
                                repairType = repairType,
                                costParts = parts,
                                serviceFee = fee,
                                totalCost = total,
                                paidAmount = paid,
                                technicianName = technicianName,
                                notes = notes,
                                status = status
                            )).copy(
                                ticketNumber = ticketNumber,
                                customerName = customerName.ifBlank { "عميل صيانة" },
                                customerPhone = customerPhone.ifBlank { "-" },
                                deviceModel = deviceModel.ifBlank { "هاتف ذكي" },
                                deviceColor = deviceColor,
                                devicePasscode = devicePasscode,
                                problemDescription = problemDescription.ifBlank { "فحص وإصلاح" },
                                repairType = repairType,
                                costParts = parts,
                                serviceFee = fee,
                                totalCost = total,
                                paidAmount = paid,
                                technicianName = technicianName,
                                notes = notes,
                                status = status
                            )
                            onSave(saved)
                        },
                        modifier = Modifier.weight(1.5f),
                        testTag = "save_repair_button"
                    )
                }
            }
        }
    }
}
