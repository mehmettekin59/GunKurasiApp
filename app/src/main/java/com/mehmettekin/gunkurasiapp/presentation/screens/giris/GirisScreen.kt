package com.mehmettekin.gunkurasiapp.presentation.screens.giris

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mehmettekin.gunkurasiapp.domain.model.ItemType
import com.mehmettekin.gunkurasiapp.domain.model.Participant
import com.mehmettekin.gunkurasiapp.presentation.navigation.Screen
import com.mehmettekin.gunkurasiapp.ui.theme.Gold
import com.mehmettekin.gunkurasiapp.ui.theme.NavyBlue
import com.mehmettekin.gunkurasiapp.ui.theme.White
import com.mehmettekin.gunkurasiapp.util.Constants
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GirisScreen(
    navController: NavController,
    //viewModel: KapaliCarsiViewModel = hiltViewModel()
    viewModel: GirisViewModel = hiltViewModel()
) {
    /*

    val exchangeRatesState by viewModel.exchangeRates.collectAsStateWithLifecycle()

    // Gösterilecek veri türü için state (Altın / Döviz)
    var selectedItemType by remember { mutableStateOf(ItemType.GOLD) }

    val goldCodeToName = Constraints.goldCodeToName
    val currencyCodeToName = Constraints.currencyCodeToName
    val goldCodeList = Constraints.goldCodeList
    val currencyCodeList = Constraints.currencyCodeList
    // RateCard içinde isim bulmak için birleşik harita (sadece gerektiğinde hesaplanabilir)
    val codeToNameMap = remember { goldCodeToName + currencyCodeToName }

    // ViewModel'den gelen veriyi filtreleyerek altın ve döviz listelerini oluştur
    // `remember` ile state değişimine bağlı olarak yeniden hesapla
    val (goldRates, currencyRates) = remember(exchangeRatesState) {
        when (val state = exchangeRatesState) { // Smart cast için local variable
            is ResultState.Success -> {
                val data = state.data
                val gold = data.filter { it.code in goldCodeList.toSet() }
                val currency = data.filter { it.code in currencyCodeList.toSet() }
                Pair(gold, currency)
            }
            else -> Pair(emptyList(), emptyList()) // Loading, Error veya Idle durumları için boş listeler
        }
    }
     */

    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Navigation and error handling
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest {
            navController.navigate(Screen.Cark.route)
        }
    }

    LaunchedEffect(key1 = state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error.asString(context))
            viewModel.onEvent(GirisEvent.OnErrorDismiss)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        " Gün Kurası",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyBlue
                )
            )
        }
    ) { paddingValues ->
        GirisContent(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )

        // Show confirmation dialog
        if (state.isShowingConfirmDialog) {
            ConfirmationDialog(
                state = state,
                onConfirm = { viewModel.onEvent(GirisEvent.OnConfirmDialogConfirm) },
                onDismiss = { viewModel.onEvent(GirisEvent.OnConfirmDialogDismiss) }
            )
        }

        // Show loading indicator
        if (state.isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
fun GirisContent(
    state: GirisState,
    onEvent: (GirisEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Item type selection (TL, Currency, Gold)
        ItemTypeSelector(
            selectedItemType = state.selectedItemType,
            onItemTypeSelect = { onEvent(GirisEvent.OnItemTypeSelect(it)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Specific currency or gold type selection when applicable
        if (state.selectedItemType == ItemType.CURRENCY || state.selectedItemType == ItemType.GOLD) {
            SpecificItemSelector(
                selectedItemType = state.selectedItemType,
                selectedSpecificItem = state.selectedSpecificItem,
                currencyOptions = state.currencyOptions,
                goldOptions = state.goldOptions,
                onSpecificItemSelect = { onEvent(GirisEvent.OnSpecificItemSelect(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Monthly amount
        ModernTextField(
            value = state.monthlyAmount,
            onValueChange = { onEvent(GirisEvent.OnMonthlyAmountChange(it)) },
            label = "Aylık Miktar",
            keyboardType = KeyboardType.Decimal,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = Gold
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Duration in months
        ModernTextField(
            value = state.durationMonths,
            onValueChange = { onEvent(GirisEvent.OnDurationChange(it)) },
            label = "Süre (Ay)",
            keyboardType = KeyboardType.Number,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Gold
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Starting month and year
        Text(
            text = "Başlangıç Tarihi",
            style = MaterialTheme.typography.titleMedium,
            color = NavyBlue,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        ModernDateSelector(
            selectedMonth = state.startMonth,
            selectedYear = state.startYear,
            onMonthSelected = { onEvent(GirisEvent.OnStartMonthSelect(it)) },
            onYearSelected = { onEvent(GirisEvent.OnStartYearSelect(it)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Participants section
        ParticipantsSection(
            participants = state.participants,
            onAddParticipant = { onEvent(GirisEvent.OnAddParticipant(it)) },
            onRemoveParticipant = { onEvent(GirisEvent.OnRemoveParticipant(it)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Continue button
        Button(
            onClick = { onEvent(GirisEvent.OnContinueClick) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NavyBlue
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Devam Et",
                fontWeight = FontWeight.Bold,
                color = White
            )
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        leadingIcon = leadingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Gold,
            focusedLabelColor = Gold,
            cursorColor = Gold
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun ItemTypeSelector(
    selectedItemType: ItemType,
    onItemTypeSelect: (ItemType) -> Unit
) {
    val itemTypes = listOf(
        ItemType.TL to "Türk Lirası (₺)",
        ItemType.CURRENCY to "Döviz ($, €, £)",
        ItemType.GOLD to "Altın"
    )

    Column {
        Text(
            text = "Toplanacak Değer Türü",
            style = MaterialTheme.typography.titleMedium,
            color = NavyBlue,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemTypes.forEach { (type, label) ->
                SelectableChip(
                    text = label,
                    selected = type == selectedItemType,
                    onClick = { onItemTypeSelect(type) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = if (selected) Gold else Color.Transparent,
        border = BorderStroke(1.dp, if (selected) Gold else Color.Gray.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected) White else Color.Gray,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SpecificItemSelector(
    selectedItemType: ItemType,
    selectedSpecificItem: String,
    currencyOptions: List<String>,
    goldOptions: List<String>,
    onSpecificItemSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val title = if (selectedItemType == ItemType.CURRENCY) "Döviz Türü" else "Altın Türü"
    val options = if (selectedItemType == ItemType.CURRENCY) {
        currencyOptions.map { Constants.CurrencyCodes.getDisplayName(it) }
    } else {
        goldOptions.map { Constants.GoldCodes.getDisplayName(it) }
    }

    val selectedValue = if (selectedSpecificItem.isNotEmpty()) {
        if (selectedItemType == ItemType.CURRENCY) {
            Constants.CurrencyCodes.getDisplayName(selectedSpecificItem)
        } else {
            Constants.GoldCodes.getDisplayName(selectedSpecificItem)
        }
    } else ""

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = NavyBlue,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true },
            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedValue.ifEmpty { "Seçiniz" },
                    color = if (selectedValue.isEmpty()) Color.Gray.copy(alpha = 0.5f) else Color.DarkGray
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Gold
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        val code = if (selectedItemType == ItemType.CURRENCY) {
                            Constants.CurrencyCodes.getCodeFromDisplayName(option)
                        } else {
                            Constants.GoldCodes.getCodeFromDisplayName(option)
                        }
                        onSpecificItemSelect(code)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = NavyBlue
                    )
                )
            }
        }
    }
}

@Composable
fun ModernDateSelector(
    selectedMonth: Int,
    selectedYear: Int,
    onMonthSelected: (Int) -> Unit,
    onYearSelected: (Int) -> Unit
) {
    var showMonthDialog by remember { mutableStateOf(false) }
    var showYearDialog by remember { mutableStateOf(false) }

    val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MONTH, selectedMonth - 1)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Month selector
        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { showMonthDialog = true },
            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthFormat.format(calendar.time),
                    color = Color.DarkGray
                )

                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Gold
                )
            }
        }

        // Year selector
        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { showYearDialog = true },
            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedYear.toString(),
                    color = Color.DarkGray
                )

                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Gold
                )
            }
        }
    }

    // Month selection dialog
    if (showMonthDialog) {
        DateSelectorDialog(
            title = "Ay Seçiniz",
            options = (1..12).map {
                calendar.set(Calendar.MONTH, it - 1)
                monthFormat.format(calendar.time)
            },
            selectedIndex = selectedMonth - 1,
            onOptionSelected = { index ->
                onMonthSelected(index + 1)
                showMonthDialog = false
            },
            onDismiss = { showMonthDialog = false }
        )
    }

    // Year selection dialog
    if (showYearDialog) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        DateSelectorDialog(
            title = "Yıl Seçiniz",
            options = (0..9).map { (currentYear + it).toString() },
            selectedIndex = selectedYear - currentYear,
            onOptionSelected = { index ->
                onYearSelected(currentYear + index)
                showYearDialog = false
            },
            onDismiss = { showYearDialog = false }
        )
    }
}

@Composable
fun DateSelectorDialog(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = NavyBlue,
                fontWeight = FontWeight.Bold
            )
        },
        containerColor = White,
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            ) {
                items(options.size) { index ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOptionSelected(index) }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = options[index],
                                color = if (index == selectedIndex) Gold else Color.DarkGray,
                                fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal
                            )

                            if (index == selectedIndex) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Gold
                                )
                            }
                        }

                        if (index < options.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color.Gray.copy(alpha = 0.2f))
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Gold
                )
            ) {
                Text("Kapat")
            }
        }
    )
}

@Composable
fun ParticipantsSection(
    participants: List<Participant>,
    onAddParticipant: (String) -> Unit,
    onRemoveParticipant: (Participant) -> Unit
) {
    var name by remember { mutableStateOf("") }

    Column {
        Text(
            text = "Katılımcılar (${participants.size})",
            style = MaterialTheme.typography.titleMedium,
            color = NavyBlue,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Participant entry field and add button
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Katılımcı Adı") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                focusedLabelColor = Gold,
                cursorColor = Gold
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (name.isNotBlank()) {
                            onAddParticipant(name)
                            name = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ekle",
                        tint = Gold
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Participants list
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))
        ) {
            if (participants.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Henüz katılımcı eklenmedi",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn {
                    items(participants) { participant ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = participant.name,
                                color = NavyBlue
                            )

                            IconButton(
                                onClick = { onRemoveParticipant(participant) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Sil",
                                    tint = Color.Red.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.Gray.copy(alpha = 0.2f))
                        )
                    }
                }
            }
        }
    }
}


// CONFIRM PART

@Composable
fun ConfirmationDialog(
    state: GirisState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Kura Bilgilerini Onaylayın",
                color = NavyBlue,
                fontWeight = FontWeight.Bold
            )
        },
        containerColor = White,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ConfirmationItem(
                    label = "Katılımcı Sayısı",
                    value = "${state.participants.size} kişi"
                )

                val valueTypeAndItem = when(state.selectedItemType) {
                    ItemType.TL -> "Türk Lirası (₺)"
                    ItemType.CURRENCY -> "Döviz (${Constants.CurrencyCodes.getDisplayName(state.selectedSpecificItem)})"
                    ItemType.GOLD -> "Altın (${Constants.GoldCodes.getDisplayName(state.selectedSpecificItem)})"
                }

                ConfirmationItem(
                    label = "Toplanacak Değer",
                    value = valueTypeAndItem
                )

                ConfirmationItem(
                    label = "Aylık Miktar",
                    value = state.monthlyAmount
                )

                ConfirmationItem(
                    label = "Süre(Ay)",
                    value = "${state.durationMonths} ay"
                )

                // Başlangıç ayı ve yılı
                val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.MONTH, state.startMonth - 1)
                val monthName = monthFormat.format(calendar.time)

                ConfirmationItem(
                    label = "Başlangıç",
                    value = "$monthName ${state.startYear}"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyBlue
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Onaylıyorum")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NavyBlue
                ),
                border = BorderStroke(1.dp, NavyBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun ConfirmationItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$label:",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(120.dp)
        )

        Text(
            text = value,
            color = NavyBlue,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = White
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = Gold,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Lütfen bekleyiniz...",
                    color = NavyBlue,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }


}







