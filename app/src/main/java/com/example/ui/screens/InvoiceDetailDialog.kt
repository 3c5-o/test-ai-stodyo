package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SaleInvoice
import com.example.data.model.StoreProfile
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InvoiceDetailDialog(
    invoice: SaleInvoice,
    storeProfile: StoreProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dateFormatted = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar", "SA")).format(Date(invoice.createdAt))

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.2.dp, GoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
            color = ObsidianCardBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Actions Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "معاينة الفاتورة الإلكترونية",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Printable / Viewable Receipt Paper Container
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                        .verticalScroll(rememberScrollState()),
                    color = ObsidianSurfaceBg
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Store Luxury Header
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(GoldPrimary, GoldDark))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = ObsidianNavyBg, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = storeProfile.storeName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${storeProfile.address} • هاتف: ${storeProfile.phoneNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                        if (storeProfile.taxNumber.isNotBlank()) {
                            Text(
                                text = "الرقم الضريبي: ${storeProfile.taxNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = ObsidianBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Invoice Details Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("رقم الفاتورة", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(invoice.invoiceNumber, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GoldAccent)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("التاريخ والوقت", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(dateFormatted, style = MaterialTheme.typography.bodySmall, color = TextWhite)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("اسم العميل", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(invoice.customerName, style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("رقم الهاتف", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(invoice.customerPhone, style = MaterialTheme.typography.bodySmall, color = TextWhite)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = ObsidianBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Items
                        Text(
                            text = "الأصناف والمنتجات",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ObsidianCardBg)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = invoice.itemsSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextWhite,
                                lineHeight = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Totals Breakdown
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("المجموع الفرعي:", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                                Text(formatMoney(invoice.totalAmount, storeProfile.currency), style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                            }
                            if (invoice.discountAmount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("الخصم الممنوح:", style = MaterialTheme.typography.bodyMedium, color = CrimsonRed)
                                    Text("-${formatMoney(invoice.discountAmount, storeProfile.currency)}", style = MaterialTheme.typography.bodyMedium, color = CrimsonRed)
                                }
                            }
                            HorizontalDivider(color = ObsidianBorder, thickness = 0.5.dp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("المجموع الصافي الإجمالي:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                                Text(formatMoney(invoice.netTotal, storeProfile.currency), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldAccent)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("المبلغ المدفوع (${when(invoice.paymentMethod){"CASH"->"نقدي";"CARD"->"مدى/بطاقة";"TRANSFER"->"تحويل بنكي";else->"أقساط"}}):", style = MaterialTheme.typography.bodyMedium, color = EmeraldGreen)
                                Text(formatMoney(invoice.paidAmount, storeProfile.currency), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = EmeraldGreen)
                            }
                            if (invoice.remainingAmount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("المبلغ المتبقي (آجل / ذمم):", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = AmberWarning)
                                    Text(formatMoney(invoice.remainingAmount, storeProfile.currency), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = AmberWarning)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = ObsidianBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Warranty & Policy
                        Text(
                            text = "شروط الضمان وسياسة الاستبدال:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = invoice.warrantyTerms,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextMuted,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // QR Verification Stamp Box
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "QR Code",
                                tint = Color.Black,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "فاتورة ضريبية مبسطة معتمدة",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = TextDim
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Buttons with WhatsApp, Share/Copy, and Dismiss
                val formattedInvoiceText = """
                    ━━━━━━━━━━━━━━━━━━━━━━
                    🧾 *فاتورة إلكترونية - ${storeProfile.storeName}*
                    ━━━━━━━━━━━━━━━━━━━━━━
                    🔖 *رقم الفاتورة:* ${invoice.invoiceNumber}
                    📅 *التاريخ:* $dateFormatted
                    👤 *العميل:* ${invoice.customerName}
                    📞 *الهاتف:* ${invoice.customerPhone}

                    📦 *المنتجات والأصناف:*
                    ${invoice.itemsSummary}

                    💰 *الإجمالي:* ${formatMoney(invoice.totalAmount, storeProfile.currency)}
                    ${if (invoice.discountAmount > 0) "🏷️ *الخصم الممنوح:* -${formatMoney(invoice.discountAmount, storeProfile.currency)}\n" else ""}💵 *الصافي الإجمالي:* ${formatMoney(invoice.netTotal, storeProfile.currency)}
                    ✅ *المبلغ المدفوع:* ${formatMoney(invoice.paidAmount, storeProfile.currency)}
                    ${if (invoice.remainingAmount > 0) "⚠️ *المبلغ المتبقي:* ${formatMoney(invoice.remainingAmount, storeProfile.currency)}\n" else ""}💳 *طريقة الدفع:* ${when(invoice.paymentMethod){"CASH"->"نقدي";"CARD"->"مدى / بطاقة";"TRANSFER"->"تحويل بنكي";else->"أقساط"}}

                    🛡️ *الضمان والسياسة:* ${invoice.warrantyTerms}
                    📍 ${storeProfile.address} • هاتف: ${storeProfile.phoneNumber}
                    ━━━━━━━━━━━━━━━━━━━━━━
                    ✨ شكراً لتعاملكم معنا!
                """.trimIndent()

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // WhatsApp Direct Share Button
                        Button(
                            onClick = {
                                try {
                                    val cleanPhone = invoice.customerPhone.replace("[^0-9]".toRegex(), "")
                                    val whatsappUri = if (cleanPhone.length >= 9) {
                                        android.net.Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${android.net.Uri.encode(formattedInvoiceText)}")
                                    } else {
                                        android.net.Uri.parse("https://api.whatsapp.com/send?text=${android.net.Uri.encode(formattedInvoiceText)}")
                                    }
                                    val waIntent = Intent(Intent.ACTION_VIEW, whatsappUri)
                                    context.startActivity(waIntent)
                                } catch (e: Exception) {
                                    // Fallback to standard share
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, formattedInvoiceText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "مشاركة الفاتورة عبر واتساب"))
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("whatsapp_invoice_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = ObsidianNavyBg, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إرسال واتساب", color = ObsidianNavyBg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        // Copy Text Button
                        LuxuryOutlinedButton(
                            text = "نسخ النص",
                            icon = Icons.Default.ContentCopy,
                            onClick = {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("InvoiceText", formattedInvoiceText))
                                android.widget.Toast.makeText(context, "تم نسخ تفاصيل الفاتورة بنجاح 📋", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "copy_invoice_button"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LuxuryOutlinedButton(
                            text = "مشاركة عامة",
                            icon = Icons.Default.Share,
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, formattedInvoiceText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "مشاركة الفاتورة"))
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "share_invoice_button"
                        )

                        LuxuryButton(
                            text = "إغلاق",
                            icon = Icons.Default.Check,
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            testTag = "dismiss_invoice_button"
                        )
                    }
                }
            }
        }
    }
}
