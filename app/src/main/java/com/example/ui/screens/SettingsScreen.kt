package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StoreProfile
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: StoreViewModel
) {
    val currentProfile by viewModel.storeProfile.collectAsState()
    val context = LocalContext.current

    var storeName by remember { mutableStateOf(currentProfile.storeName) }
    var ownerName by remember { mutableStateOf(currentProfile.ownerName) }
    var phoneNumber by remember { mutableStateOf(currentProfile.phoneNumber) }
    var address by remember { mutableStateOf(currentProfile.address) }
    var taxNumber by remember { mutableStateOf(currentProfile.taxNumber) }
    var selectedCurrency by remember { mutableStateOf(currentProfile.currency) }
    var warrantyTerms by remember { mutableStateOf(currentProfile.defaultWarrantyTerms) }

    val currencies = listOf("ر.س", "$", "د.إ", "ج.م", "د.ع", "د.ك", "ر.ق", "ر.ع", "د.أ")

    Scaffold(
        modifier = Modifier.fillMaxSize().background(ObsidianNavyBg).testTag("settings_screen"),
        containerColor = ObsidianNavyBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Banner
            LuxuryCard(
                modifier = Modifier.fillMaxWidth(),
                borderBrush = Brush.horizontalGradient(listOf(GoldPrimary, ObsidianBorder)),
                backgroundColor = ObsidianCardBg
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(GoldPrimary, GoldDark)))
                            .border(2.dp, GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = ObsidianNavyBg, modifier = Modifier.size(30.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("هوية وإعدادات متجر الهواتف", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                        Text("تخصيص الفواتير، العملة، وسياسات الضمان", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
            }

            // Store Information Form
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Text("بيانات المتجر الرسمية", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextWhite)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text("اسم المحل / المتجر") },
                    modifier = Modifier.fillMaxWidth().testTag("store_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("المالك / المدير") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("هاتف التواصل") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("عنوان المتجر / المدينة والشارع") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = taxNumber,
                    onValueChange = { taxNumber = it },
                    label = { Text("الرقم الضريبي / السجل التجاري") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
            }

            // Currency & Pricing Settings
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Text("عملة التسعير والفواتير", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextWhite)
                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(currencies) { c ->
                        val isSel = selectedCurrency == c
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) GoldPrimary else ObsidianSurfaceBg)
                                .border(1.dp, if (isSel) GoldAccent else ObsidianBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedCurrency = c }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(c, color = if (isSel) ObsidianNavyBg else TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Warranty Terms Default
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Text("شروط الضمان وسياسة الفواتير الافتراضية", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextWhite)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = warrantyTerms,
                    onValueChange = { warrantyTerms = it },
                    label = { Text("نص شروط الضمان المطبوعة في الفواتير") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )
            }

            // Save Profile Button
            LuxuryButton(
                text = "حفظ إعدادات المتجر",
                icon = Icons.Default.Save,
                onClick = {
                    viewModel.updateStoreProfile(
                        StoreProfile(
                            storeName = storeName.ifBlank { "مبايلي برو للهواتف الذكية" },
                            ownerName = ownerName.ifBlank { "المدير" },
                            phoneNumber = phoneNumber,
                            address = address,
                            taxNumber = taxNumber,
                            currency = selectedCurrency,
                            defaultWarrantyTerms = warrantyTerms
                        )
                    )
                    Toast.makeText(context, "تم حفظ بيانات المتجر بنجاح ✨", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().testTag("save_settings_btn")
            )

            // Offline Backup & Restore Section
            var showRestoreDialog by remember { mutableStateOf(false) }
            var restoreJsonInput by remember { mutableStateOf("") }

            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = EmeraldGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("النسخ الاحتياطي واستعادة البيانات (أوفلاين)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextWhite)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "احفظ نسخة كاملة من سجلات الأجهزة والمخزون، تذاكر الصيانة، والفواتير على هاتفك واستعدها في أي وقت دون الحاجة للإنترنت.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LuxuryButton(
                        text = "تصدير نسخة احتياطية",
                        icon = Icons.Default.FileDownload,
                        onClick = {
                            viewModel.exportBackup { jsonStr ->
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("MobilyPro_Backup", jsonStr))

                                val shareIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(android.content.Intent.EXTRA_TEXT, jsonStr)
                                    type = "application/json"
                                }
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "حفظ أو إرسال النسخة الاحتياطية"))
                                Toast.makeText(context, "تم نسخ وتصدير بيانات المتجر بنجاح 💾", Toast.LENGTH_LONG).show()
                            }
                        },
                        containerColor = EmeraldGreen,
                        contentColor = ObsidianNavyBg,
                        modifier = Modifier.weight(1f),
                        testTag = "export_backup_btn"
                    )

                    LuxuryOutlinedButton(
                        text = "استعادة بيانات",
                        icon = Icons.Default.FileUpload,
                        onClick = {
                            restoreJsonInput = ""
                            showRestoreDialog = true
                        },
                        borderColor = EmeraldGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "import_backup_btn"
                    )
                }
            }

            // GitHub Actions CI/CD APK Info Card
            LuxuryCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = ObsidianSurfaceBg,
                borderBrush = Brush.horizontalGradient(listOf(ElectricBlue, ObsidianBorder))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BuildCircle, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("بناء وتنزيل APK عبر GitHub Actions", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = ElectricBlue)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "تم إعداد سير عمل GitHub Actions تلقائي في المستودع (.github/workflows/build-apk.yml):\n" +
                            "• عند كل Push أو رفع للمستودع، يقوم GitHub تلقائياً ببناء ملف app-debug.apk.\n" +
                            "• يتم رفع ملف الـ APK ونشره في تبويب Releases برابط تحميل فوري دون الحاجة لـ AndroidIDE.\n" +
                            "• يمكنك تحميل الملف وتثبيته على هاتفك مباشرة في أي وقت.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = TextMuted,
                    lineHeight = 18.sp
                )
            }

            // Restore Dialog
            if (showRestoreDialog) {
                AlertDialog(
                    onDismissRequest = { showRestoreDialog = false },
                    title = { Text("استعادة النسخة الاحتياطية", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            Text("الصق محتوى نص ملف النسخة الاحتياطية (JSON) أدناه:", color = TextWhite, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = restoreJsonInput,
                                onValueChange = { restoreJsonInput = it },
                                placeholder = { Text("{ \"app\": \"MobilyPro\", ... }", color = TextDim) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                maxLines = 6,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldGreen,
                                    unfocusedBorderColor = ObsidianBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                )
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (restoreJsonInput.isNotBlank()) {
                                    viewModel.restoreBackup(restoreJsonInput) { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                        if (success) showRestoreDialog = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("استعادة الآن", color = ObsidianNavyBg, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showRestoreDialog = false }) {
                            Text("إلغاء", color = TextMuted)
                        }
                    },
                    containerColor = ObsidianCardBg
                )
            }

            // App Version & Luxury Edition Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("تطبيق مبايلي برو لإدارة محلات وصيانة الهواتف", style = MaterialTheme.typography.labelSmall, color = TextDim)
                    Text("إصدار المتاجر الفاخرة • Version 2026.1 (مع GitHub Actions)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = GoldDark)
                }
            }

            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}
