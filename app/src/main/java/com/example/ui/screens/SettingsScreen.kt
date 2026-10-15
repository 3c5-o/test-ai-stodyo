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

            // App Version & Luxury Edition Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("تطبيق مبايلي برو لإدارة محلات وصيانة الهواتف", style = MaterialTheme.typography.labelSmall, color = TextDim)
                    Text("إصدار المتاجر الفاخرة • Version 2026.1", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = GoldDark)
                }
            }

            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}
