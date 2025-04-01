package com.mehmettekin.gunkurasiapp.presentation.screens.settings


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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mehmettekin.gunkurasiapp.presentation.common.LoadingIndicator
import com.mehmettekin.gunkurasiapp.ui.theme.OnPrimary
import com.mehmettekin.gunkurasiapp.ui.theme.Primary
import com.mehmettekin.gunkurasiapp.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Error handling
    LaunchedEffect(key1 = state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error.asString(context))
            viewModel.onEvent(SettingsEvent.OnErrorDismiss)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar", color = OnPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                LoadingIndicator(fullScreen = true)
            } else {
                SettingsContent(
                    state = state,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun SettingsContent(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // API Update Interval Setting
        SettingSection(title = "API Güncelleme Sıklığı") {
            ApiUpdateIntervalSetting(
                currentInterval = state.apiUpdateInterval,
                onIntervalChange = { onEvent(SettingsEvent.OnApiUpdateIntervalChange(it)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language Setting
        SettingSection(title = "Dil Seçimi") {
            LanguageSetting(
                currentLanguage = state.selectedLanguage,
                onLanguageChange = { onEvent(SettingsEvent.OnLanguageChange(it)) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Uygulama Bilgisi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Gün Kurası App",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Versiyon: 1.0.0",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "© 2023 Gün Kurası App",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun SettingSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        content()
    }
}

@Composable
fun ApiUpdateIntervalSetting(
    currentInterval: Int,
    onIntervalChange: (Int) -> Unit
) {
    val intervals = listOf(
        Pair("1 saniye", 1),
        Pair("5 saniye", 5),
        Pair("15 saniye", 15),
        Pair("30 saniye", 30),
        Pair("1 dakika", 60),
        Pair("5 dakika", 300)
    )

    var expanded by remember { mutableStateOf(false) }

    val currentIntervalText = intervals.find { it.second == currentInterval }?.first ?: "30 saniye"

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "API verilerinin ne sıklıkla güncelleneceğini seçin:",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currentIntervalText)
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                intervals.forEach { interval ->
                    DropdownMenuItem(
                        text = { Text(interval.first) },
                        onClick = {
                            onIntervalChange(interval.second)
                            expanded = false
                        },
                        trailingIcon = {
                            if (interval.second == currentInterval) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Secondary
                                )
                            }
                        }
                    )
                }
            }
        }

        Divider()
    }
}

@Composable
fun LanguageSetting(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val languages = listOf(
        Pair("Türkçe", "tr"),
        Pair("English", "en"),
        Pair("العربية", "ar")
    )

    var expanded by remember { mutableStateOf(false) }

    val currentLanguageName = languages.find { it.second == currentLanguage }?.first ?: "Türkçe"

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Uygulama dilini seçin:",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currentLanguageName)
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                languages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language.first) },
                        onClick = {
                            onLanguageChange(language.second)
                            expanded = false
                        },
                        trailingIcon = {
                            if (language.second == currentLanguage) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Secondary
                                )
                            }
                        }
                    )
                }
            }
        }

        Divider()
    }
}