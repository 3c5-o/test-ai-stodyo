package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CustomerDebt
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallmentsScreen(
    viewModel: StoreViewModel
) {
    val allDebts by viewModel.allDebts.collectAsState()
    val storeProfile by viewModel.storeProfile.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var debtToPay by remember { mutableStateOf<CustomerDebt?>(null) }
    var debtToDelete by remember { mutableStateOf<CustomerDebt?>(null) }
    var paymentAmountInput by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    val totalOutstanding = allDebts.filter { it.status == "ACTIVE" }.sumOf { it.totalDebtAmount - it.totalPaidAmount }
    val totalCollected = allDebts.sumOf { it.totalPaidAmount }

    val filteredDebts = allDebts.filter {
        searchQuery.isBlank() ||
                it.customerName.contains(searchQuery, ignoreCase = true) ||
                it.customerPhone.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(ObsidianNavyBg).testTag("installments_screen"),
        containerColor = ObsidianNavyBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GoldPrimary,
                contentColor = ObsidianNavyBg,
                shape = CircleShape,
                modifier = Modifier.testTag("add_installment_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة تقسيط أو ذمة")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Bar
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
                            text = "التقسيط والذمم والديون",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GoldAccent
                        )
                        Text(
                            text = "متابعة جداول سداد أقساط الهواتف والمبالغ المتبقية",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    LuxuryBadge(
                        text = "${allDebts.count { it.status == "ACTIVE" }} عقود نشطة",
                        color = AmberWarning,
                        textColor = AmberWarning
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2 Small Stat Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianSurfaceBg)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("إجمالي المتبقي في السوق", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextMuted)
                            Text(formatMoney(totalOutstanding, storeProfile.currency), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = CrimsonRed)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianSurfaceBg)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("إجمالي الأقساط المحصلة", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextMuted)
                            Text(formatMoney(totalCollected, storeProfile.currency), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("بحث باسم العميل أو رقم الهاتف...") },
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
            }

            // Debts List
            if (filteredDebts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = TextMuted, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("لا توجد سجلات أقساط أو ديون مسجلة", color = TextMuted)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredDebts, key = { it.id }) { debt ->
                        val remaining = (debt.totalDebtAmount - debt.totalPaidAmount).coerceAtLeast(0.0)
                        val progress = if (debt.totalDebtAmount > 0) (debt.totalPaidAmount / debt.totalDebtAmount).toFloat().coerceIn(0f, 1f) else 0f
                        val isSettled = debt.status == "SETTLED" || remaining == 0.0
                        val dateFormatted = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(debt.nextDueDate))

                        LuxuryCard(
                            modifier = Modifier.fillMaxWidth().testTag("debt_card_${debt.id}"),
                            backgroundColor = ObsidianCardBg
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(debt.customerName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextWhite)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    LuxuryBadge(
                                        text = if (isSettled) "مكتمل السداد" else "نشط (قيد التحصيل)",
                                        color = if (isSettled) EmeraldGreen else AmberWarning,
                                        textColor = if (isSettled) EmeraldGreen else AmberWarning
                                    )
                                }

                                IconButton(onClick = { debtToDelete = debt }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = CrimsonRed.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                                }
                            }

                            Text("جوال: ${debt.customerPhone} • الهوية: ${debt.nationalIdOrRef.ifBlank { "غير مسجلة" }}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(debt.description, style = MaterialTheme.typography.bodyMedium, color = GoldLight)

                            Spacer(modifier = Modifier.height(10.dp))

                            // Payment Progress Bar
                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("تم سداد: ${formatMoney(debt.totalPaidAmount, storeProfile.currency)}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = EmeraldGreen)
                                    Text("المتبقي: ${formatMoney(remaining, storeProfile.currency)}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold), color = if (isSettled) EmeraldGreen else CrimsonRed)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = if (isSettled) EmeraldGreen else GoldPrimary,
                                    trackColor = ObsidianSurfaceBg
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("إجمالي العقد: ${formatMoney(debt.totalDebtAmount, storeProfile.currency)}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextDim)
                                    Text("القسط: ${formatMoney(debt.monthlyInstallment, storeProfile.currency)} • استحقاق $dateFormatted", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = GoldAccent)
                                }
                            }

                            if (!isSettled) {
                                Spacer(modifier = Modifier.height(10.dp))
                                LuxuryButton(
                                    text = "تسجيل دفعة قسط جديدة",
                                    icon = Icons.Default.Payments,
                                    onClick = {
                                        paymentAmountInput = if (debt.monthlyInstallment > 0) debt.monthlyInstallment.toString() else remaining.toString()
                                        debtToPay = debt
                                    },
                                    modifier = Modifier.fillMaxWidth().height(42.dp),
                                    containerColor = GoldPrimary,
                                    contentColor = ObsidianNavyBg,
                                    testTag = "pay_installment_btn_${debt.id}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Payment Recording Dialog
    debtToPay?.let { debt ->
        AlertDialog(
            onDismissRequest = { debtToPay = null },
            title = { Text("تسجيل دفعة قسط للعميل", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("العميل: ${debt.customerName}", color = TextWhite)
                    Text("المبلغ المتبقي الحالي: ${formatMoney(debt.totalDebtAmount - debt.totalPaidAmount, storeProfile.currency)}", color = TextMuted)
                    OutlinedTextField(
                        value = paymentAmountInput,
                        onValueChange = { paymentAmountInput = it },
                        label = { Text("مبلغ الدفعة المسددة") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val amount = paymentAmountInput.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        viewModel.recordDebtPayment(debt, amount)
                    }
                    debtToPay = null
                }) {
                    Text("تأكيد وحفظ الدفعة", color = EmeraldGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { debtToPay = null }) {
                    Text("إلغاء", color = TextMuted)
                }
            },
            containerColor = ObsidianCardBg
        )
    }

    // Add New Installment Dialog
    if (showAddDialog) {
        var cName by remember { mutableStateOf("") }
        var cPhone by remember { mutableStateOf("") }
        var cId by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var totalDebt by remember { mutableStateOf("") }
        var downPayment by remember { mutableStateOf("") }
        var monthsCount by remember { mutableStateOf("6") }

        Dialog(onDismissRequest = { showAddDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, GoldPrimary, RoundedCornerShape(20.dp)),
                color = ObsidianCardBg
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("إضافة عقد تقسيط / ذمة جديدة", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)

                    OutlinedTextField(
                        value = cName,
                        onValueChange = { cName = it },
                        label = { Text("اسم العميل") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )

                    OutlinedTextField(
                        value = cPhone,
                        onValueChange = { cPhone = it },
                        label = { Text("رقم الجوال") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("وصف الجهاز / المعاملة") },
                        placeholder = { Text("مثال: تقسيط iPhone 16 Pro Max 256GB") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = totalDebt,
                            onValueChange = { totalDebt = it },
                            label = { Text("إجمالي المبلغ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = downPayment,
                            onValueChange = { downPayment = it },
                            label = { Text("الدفعة الأولى") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                    }

                    OutlinedTextField(
                        value = monthsCount,
                        onValueChange = { monthsCount = it },
                        label = { Text("عدد شهور التقسيط") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LuxuryOutlinedButton(text = "إلغاء", onClick = { showAddDialog = false }, modifier = Modifier.weight(1f))
                        LuxuryButton(
                            text = "حفظ العقد",
                            onClick = {
                                val tDebt = totalDebt.toDoubleOrNull() ?: 0.0
                                val dPaid = downPayment.toDoubleOrNull() ?: 0.0
                                val count = monthsCount.toIntOrNull() ?: 6
                                val monthly = if (count > 0) (tDebt - dPaid) / count else (tDebt - dPaid)

                                viewModel.saveDebt(
                                    CustomerDebt(
                                        customerName = cName.ifBlank { "عميل تقسيط" },
                                        customerPhone = cPhone.ifBlank { "-" },
                                        nationalIdOrRef = cId,
                                        description = desc.ifBlank { "تقسيط هاتف" },
                                        totalDebtAmount = tDebt,
                                        totalPaidAmount = dPaid,
                                        monthlyInstallment = monthly,
                                        installmentsCount = count,
                                        paidInstallmentsCount = if (dPaid > 0) 1 else 0
                                    )
                                )
                                showAddDialog = false
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation
    debtToDelete?.let { debt ->
        AlertDialog(
            onDismissRequest = { debtToDelete = null },
            title = { Text("حذف السجل", color = CrimsonRed, fontWeight = FontWeight.Bold) },
            text = { Text("هل تريد حذف سجل الذمة للعميل ${debt.customerName} نهائياً؟", color = TextWhite) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteDebt(debt)
                    debtToDelete = null
                }) {
                    Text("حذف", color = CrimsonRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { debtToDelete = null }) {
                    Text("إلغاء", color = TextMuted)
                }
            },
            containerColor = ObsidianCardBg
        )
    }
}
