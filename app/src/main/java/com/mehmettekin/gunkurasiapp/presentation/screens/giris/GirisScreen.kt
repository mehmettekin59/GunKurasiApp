package com.mehmettekin.gunkurasiapp.presentation.screens.giris

import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mehmettekin.gunkurasiapp.domain.model.ItemType
import com.mehmettekin.gunkurasiapp.domain.model.Participant
import com.mehmettekin.gunkurasiapp.presentation.navigation.Screen
import com.mehmettekin.gunkurasiapp.ui.theme.OnPrimary
import com.mehmettekin.gunkurasiapp.ui.theme.Primary
import com.mehmettekin.gunkurasiapp.ui.theme.Secondary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GirisScreen(
    navController: NavController,
    viewModel: GirisViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Navigation event handling
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest {
            navController.navigate(Screen.Cark.route)
        }
    }

    // Error handling
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
                title = { Text("Gün Kurası", color = OnPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary
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
            ConfirmDialog(
                state = state,
                onConfirm = { viewModel.onEvent(GirisEvent.OnConfirmDialogConfirm) },
                onDismiss = { viewModel.onEvent(GirisEvent.OnConfirmDialogDismiss) }
            )
        }

        // Show loading
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Secondary)
            }
        }
    }
}

@Composable
fun GirisContent(
    state: GirisState,
    onEvent: (GirisEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Katılımcı sayısı
        item {
            OutlinedTextField(
                value = state.participantCount,
                onValueChange = { onEvent(GirisEvent.OnParticipantCountChange(it)) },
                label = { Text("Katılımcı Sayısı") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        // Katılımcı listesi
        item {
            Text(
                text = "Katılımcılar",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Katılımcı ekleme komponenti
        item {
            AddParticipantCard(onAddParticipant = { name ->
                onEvent(GirisEvent.OnAddParticipant(name))
            })
        }

        // Katılımcı kartları
        items(state.participants) { participant ->
            ParticipantCard(
                participant = participant,
                onRemove = { onEvent(GirisEvent.OnRemoveParticipant(participant)) }
            )
        }

        // Toplanacak şeyin cinsi
        item {
            Text(
                text = "Toplanacak Değerin Türü",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onEvent(GirisEvent.OnItemTypeSelect(ItemType.TL)) }
                ) {
                    RadioButton(
                        selected = state.selectedItemType == ItemType.TL,
                        onClick = { onEvent(GirisEvent.OnItemTypeSelect(ItemType.TL)) }
                    )
                    Text("Türk Lirası (TL)")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onEvent(GirisEvent.OnItemTypeSelect(ItemType.CURRENCY)) }
                ) {
                    RadioButton(
                        selected = state.selectedItemType == ItemType.CURRENCY,
                        onClick = { onEvent(GirisEvent.OnItemTypeSelect(ItemType.CURRENCY)) }
                    )
                    Text("Döviz")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onEvent(GirisEvent.OnItemTypeSelect(ItemType.GOLD)) }
                ) {
                    RadioButton(
                        selected = state.selectedItemType == ItemType.GOLD,
                        onClick = { onEvent(GirisEvent.OnItemTypeSelect(ItemType.GOLD)) }
                    )
                    Text("Altın")
                }
            }
        }

        // Döviz veya Altın seçiminde spesifik tür seçimi
        item {
            if (state.selectedItemType == ItemType.CURRENCY || state.selectedItemType == ItemType.GOLD) {
                Column {
                    Text(
                        text = if (state.selectedItemType == ItemType.CURRENCY) "Döviz Türü" else "Altın Türü",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownSelector(
                        selectedValue = state.selectedSpecificItem,
                        options = if (state.selectedItemType == ItemType.CURRENCY) state.currencyOptions else state.goldOptions,
                        onValueSelected = { onEvent(GirisEvent.OnSpecificItemSelect(it)) },
                        placeholder = if (state.selectedItemType == ItemType.CURRENCY) "Döviz seçiniz" else "Altın çeşidi seçiniz"
                    )
                }
            }
        }

        // Aylık miktar
        item {
            OutlinedTextField(
                value = state.monthlyAmount,
                onValueChange = { onEvent(GirisEvent.OnMonthlyAmountChange(it)) },
                label = { Text("Aylık Miktar") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        // Süre (ay olarak)
        item {
            OutlinedTextField(
                value = state.durationMonths,
                onValueChange = { onEvent(GirisEvent.OnDurationChange(it)) },
                label = { Text("Süre (Ay)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        // Başlangıç ayı
        item {
            Text(
                text = "Başlangıç Ayı",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            MonthYearSelector(
                selectedMonth = state.startMonth,
                selectedYear = state.startYear,
                onMonthSelected = { onEvent(GirisEvent.OnStartMonthSelect(it)) },
                onYearSelected = { onEvent(GirisEvent.OnStartYearSelect(it)) }
            )
        }

        // Devam butonu
        item {
            Button(
                onClick = { onEvent(GirisEvent.OnContinueClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Text("Devam Et")
            }
        }
    }
}

@Composable
fun AddParticipantCard(
    onAddParticipant: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Katılımcı Adı") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onAddParticipant(name)
                        name = ""
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ekle")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ekle")
            }
        }
    }
}

@Composable
fun ParticipantCard(
    participant: Participant,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = participant.name,
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun DropdownSelector(
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit,
    placeholder: String
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(4.dp)
            )
            .clip(RoundedCornerShape(4.dp))
            .clickable { expanded = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedValue.ifEmpty { placeholder },
                color = if (selectedValue.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.onSurface
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Açılır liste")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MonthYearSelector(
    selectedMonth: Int,
    selectedYear: Int,
    onMonthSelected: (Int) -> Unit,
    onYearSelected: (Int) -> Unit
) {
    var showMonthDialog by remember { mutableStateOf(false) }
    var showYearDialog by remember { mutableStateOf(false) }

    val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
    val calendar = Calendar.getInstance()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Ay seçimi
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
                .clip(RoundedCornerShape(4.dp))
                .clickable { showMonthDialog = true }
                .padding(16.dp)
        ) {
            calendar.set(Calendar.MONTH, selectedMonth - 1)
            Text(
                text = monthFormat.format(calendar.time),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Yıl seçimi
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
                .clip(RoundedCornerShape(4.dp))
                .clickable { showYearDialog = true }
                .padding(16.dp)
        ) {
            Text(
                text = selectedYear.toString(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Ay seçim diyaloğu
    if (showMonthDialog) {
        Dialog(onDismissRequest = { showMonthDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ay Seçiniz",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Divider()

                    LazyColumn {
                        items(12) { index ->
                            val month = index + 1
                            calendar.set(Calendar.MONTH, index)
                            val monthName = monthFormat.format(calendar.time)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onMonthSelected(month)
                                        showMonthDialog = false
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = monthName,
                                    modifier = Modifier.weight(1f)
                                )

                                if (month == selectedMonth) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = Secondary
                                    )
                                }
                            }

                            if (index < 11) {
                                Divider()
                            }
                        }
                    }
                }
            }
        }
    }

    // Yıl seçim diyaloğu
    if (showYearDialog) {
        Dialog(onDismissRequest = { showYearDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Yıl Seçiniz",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Divider()

                    LazyColumn {
                        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                        items(10) { index ->
                            val year = currentYear + index

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onYearSelected(year)
                                        showYearDialog = false
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = year.toString(),
                                    modifier = Modifier.weight(1f)
                                )

                                if (year == selectedYear) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = Secondary
                                    )
                                }
                            }

                            if (index < 9) {
                                Divider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    state: GirisState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Kura Bilgilerini Onaylayın")
        },
        text = {
            Column {
                Text("Katılımcı Sayısı: ${state.participantCount}")

                Spacer(modifier = Modifier.height(8.dp))

                Text("Toplanacak Değer: ${
                    when(state.selectedItemType) {
                        ItemType.TL -> "Türk Lirası"
                        ItemType.CURRENCY -> "Döviz (${state.selectedSpecificItem})"
                        ItemType.GOLD -> "Altın (${state.selectedSpecificItem})"
                    }
                }")

                Spacer(modifier = Modifier.height(8.dp))

                Text("Aylık Miktar: ${state.monthlyAmount}")

                Spacer(modifier = Modifier.height(8.dp))

                Text("Süre: ${state.durationMonths} ay")

                Spacer(modifier = Modifier.height(8.dp))

                // Başlangıç ayı ve yılı
                val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.MONTH, state.startMonth - 1)
                val monthName = monthFormat.format(calendar.time)

                Text("Başlangıç: $monthName ${state.startYear}")
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Text("Onaylıyorum")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}