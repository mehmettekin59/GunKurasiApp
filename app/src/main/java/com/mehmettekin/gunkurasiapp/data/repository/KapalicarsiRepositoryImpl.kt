package com.mehmettekin.gunkurasiapp.data.repository


import com.mehmettekin.gunkurasiapp.data.api.HttpErrorInterceptor
import com.mehmettekin.gunkurasiapp.data.api.KapalicarsiApi
import com.mehmettekin.gunkurasiapp.data.local.SettingsDataStore
import com.mehmettekin.gunkurasiapp.domain.model.Currency
import com.mehmettekin.gunkurasiapp.domain.model.Gold
import com.mehmettekin.gunkurasiapp.domain.repository.KapalicarsiRepository
import com.mehmettekin.gunkurasiapp.util.ResultState
import com.mehmettekin.gunkurasiapp.util.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class KapalicarsiRepositoryImpl @Inject constructor(
    private val api: KapalicarsiApi,
    private val settingsDataStore: SettingsDataStore
) : KapalicarsiRepository {

    private val _currencies = MutableStateFlow<ResultState<List<Currency>>>(ResultState.Idle)
    private val _gold = MutableStateFlow<ResultState<List<Gold>>>(ResultState.Idle)

    override fun getCurrencies(): Flow<ResultState<List<Currency>>> = _currencies.asStateFlow()

    override fun getGold(): Flow<ResultState<List<Gold>>> = _gold.asStateFlow()

    override suspend fun refreshData() {
        fetchCurrencies()
        fetchGold()
    }

    override fun getUpdateInterval(): Flow<Int> = settingsDataStore.getApiUpdateInterval()

    override suspend fun setUpdateInterval(seconds: Int) {
        settingsDataStore.setApiUpdateInterval(seconds)
    }

    private suspend fun fetchCurrencies() {
        _currencies.value = ResultState.Loading

        try {
            val response = api.getCurrencies()

            if (response.success && response.data != null) {
                val currencies = response.data.map { it.toDomain() }
                _currencies.value = ResultState.Success(currencies)
            } else {
                val errorMessage = response.error?.message
                    ?: UiText.stringResource(R.string.error_unknown)
                _currencies.value = ResultState.Error(UiText.dynamicString(errorMessage))
            }
        } catch (e: HttpErrorInterceptor.NoConnectivityException) {
            _currencies.value = ResultState.Error(UiText.dynamicString(e.message ?: "İnternet bağlantı hatası"))
        } catch (e: HttpErrorInterceptor.ServerException) {
            _currencies.value = ResultState.Error(UiText.dynamicString(e.message ?: "Sunucu hatası"))
        } catch (e: HttpErrorInterceptor.TimeoutException) {
            _currencies.value = ResultState.Error(UiText.dynamicString(e.message ?: "Zaman aşımı hatası"))
        } catch (e: Exception) {
            _currencies.value = ResultState.Error(UiText.dynamicString(e.message ?: "Bilinmeyen hata"))
        }
    }

    private suspend fun fetchGold() {
        _gold.value = ResultState.Loading

        try {
            val response = api.getGold()

            if (response.success && response.data != null) {
                val gold = response.data.map { it.toDomain() }
                _gold.value = ResultState.Success(gold)
            } else {
                val errorMessage = response.error?.message
                    ?: UiText.stringResource(R.string.error_unknown)
                _gold.value = ResultState.Error(UiText.dynamicString(errorMessage))
            }
        } catch (e: HttpErrorInterceptor.NoConnectivityException) {
            _gold.value = ResultState.Error(UiText.dynamicString(e.message ?: "İnternet bağlantı hatası"))
        } catch (e: HttpErrorInterceptor.ServerException) {
            _gold.value = ResultState.Error(UiText.dynamicString(e.message ?: "Sunucu hatası"))
        } catch (e: HttpErrorInterceptor.TimeoutException) {
            _gold.value = ResultState.Error(UiText.dynamicString(e.message ?: "Zaman aşımı hatası"))
        } catch (e: Exception) {
            _gold.value = ResultState.Error(UiText.dynamicString(e.message ?: "Bilinmeyen hata"))
        }
    }
}