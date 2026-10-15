package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.PhoneDevice
import com.example.data.model.RepairTicket
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel

enum class MainTab(val title: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("الرئيسية", Icons.Default.Dashboard, "tab_dashboard"),
    INVENTORY("المخزن", Icons.Default.PhoneIphone, "tab_inventory"),
    POS("المبيعات (POS)", Icons.Default.PointOfSale, "tab_pos"),
    REPAIRS("الصيانة", Icons.Default.Build, "tab_repairs"),
    VALUATION("تقييم مستعمل", Icons.Default.FactCheck, "tab_valuation"),
    INSTALLMENTS("الأقساط", Icons.Default.AccountBalanceWallet, "tab_installments"),
    ACCESSORIES("الإكسسوارات", Icons.Default.Category, "tab_accessories"),
    SETTINGS("الإعدادات", Icons.Default.Settings, "tab_settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: StoreViewModel = viewModel()) {
    var currentTab by remember { mutableStateOf(MainTab.DASHBOARD) }
    var showAddPhoneDialog by remember { mutableStateOf(false) }
    var showAddRepairDialog by remember { mutableStateOf(false) }

    val activeRepairs by viewModel.activeRepairs.collectAsState()
    val lowStockAcc by viewModel.lowStockAccessories.collectAsState()

    // Primary bottom bar tabs
    val bottomTabs = listOf(
        MainTab.DASHBOARD,
        MainTab.INVENTORY,
        MainTab.POS,
        MainTab.REPAIRS,
        MainTab.VALUATION,
        MainTab.INSTALLMENTS,
        MainTab.ACCESSORIES,
        MainTab.SETTINGS
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianNavyBg),
        containerColor = ObsidianNavyBg,
        bottomBar = {
            NavigationBar(
                containerColor = ObsidianCardBg,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                // We show the 5 most frequent tabs + access to others
                listOf(
                    MainTab.DASHBOARD,
                    MainTab.INVENTORY,
                    MainTab.POS,
                    MainTab.REPAIRS,
                    MainTab.INSTALLMENTS,
                    MainTab.SETTINGS
                ).forEach { tab ->
                    val isSelected = currentTab == tab
                    val badgeCount = when (tab) {
                        MainTab.REPAIRS -> activeRepairs.size.takeIf { it > 0 }
                        else -> null
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (badgeCount != null) {
                                        Badge(
                                            containerColor = AmberWarning,
                                            contentColor = ObsidianNavyBg
                                        ) {
                                            Text("$badgeCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianNavyBg,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextDim
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "screen_transition") { tab ->
                when (tab) {
                    MainTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToInventory = { currentTab = MainTab.INVENTORY },
                        onNavigateToPOS = { currentTab = MainTab.POS },
                        onNavigateToRepairs = { currentTab = MainTab.REPAIRS },
                        onNavigateToValuation = { currentTab = MainTab.VALUATION },
                        onNavigateToInstallments = { currentTab = MainTab.INSTALLMENTS },
                        onNavigateToAccessories = { currentTab = MainTab.ACCESSORIES },
                        onAddPhoneClick = { showAddPhoneDialog = true },
                        onAddRepairClick = { showAddRepairDialog = true }
                    )
                    MainTab.INVENTORY -> InventoryScreen(
                        viewModel = viewModel,
                        onNavigateToPOS = { currentTab = MainTab.POS }
                    )
                    MainTab.POS -> POSSalesScreen(
                        viewModel = viewModel
                    )
                    MainTab.REPAIRS -> RepairsScreen(
                        viewModel = viewModel
                    )
                    MainTab.VALUATION -> UsedPhoneValuationScreen(
                        viewModel = viewModel,
                        onPhoneAddedToInventory = { currentTab = MainTab.INVENTORY }
                    )
                    MainTab.INSTALLMENTS -> InstallmentsScreen(
                        viewModel = viewModel
                    )
                    MainTab.ACCESSORIES -> AccessoriesScreen(
                        viewModel = viewModel,
                        onNavigateToPOS = { currentTab = MainTab.POS }
                    )
                    MainTab.SETTINGS -> SettingsScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // Floating Global Add Phone Dialog
    if (showAddPhoneDialog) {
        AddEditPhoneDialog(
            initialPhone = null,
            onDismiss = { showAddPhoneDialog = false },
            onSave = { phone ->
                viewModel.savePhone(phone)
                showAddPhoneDialog = false
                currentTab = MainTab.INVENTORY
            }
        )
    }

    // Floating Global Add Repair Dialog
    if (showAddRepairDialog) {
        AddEditRepairDialog(
            initialTicket = null,
            onDismiss = { showAddRepairDialog = false },
            onSave = { ticket ->
                viewModel.saveRepairTicket(ticket)
                showAddRepairDialog = false
                currentTab = MainTab.REPAIRS
            }
        )
    }
}
