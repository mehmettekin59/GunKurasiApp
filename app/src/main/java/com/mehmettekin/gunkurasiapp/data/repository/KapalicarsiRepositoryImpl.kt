package com.mehmettekin.gunkurasiapp.data.repository



import android.util.Log
import com.mehmettekin.gunkurasiapp.R
import com.mehmettekin.gunkurasiapp.data.api.HttpErrorInterceptor
import com.mehmettekin.gunkurasiapp.data.api.KapalicarsiApi
import com.mehmettekin.gunkurasiapp.data.api.model.CurrencyResponse
import com.mehmettekin.gunkurasiapp.data.local.SettingsDataStore
import com.mehmettekin.gunkurasiapp.domain.model.Currency
import com.mehmettekin.gunkurasiapp.domain.model.Gold
import com.mehmettekin.gunkurasiapp.domain.repository.KapalicarsiRepository
import com.mehmettekin.gunkurasiapp.util.Constants
import com.mehmettekin.gunkurasiapp.util.ResultState
import com.mehmettekin.gunkurasiapp.util.UiText
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KapalicarsiRepositoryImpl @Inject constructor(
    private val api: KapalicarsiApi,
    private val settingsDataStore: SettingsDataStore,
    private val moshi: Moshi
) : KapalicarsiRepository {

    private val _currencies = MutableStateFlow<ResultState<List<Currency>>>(ResultState.Idle)
    private val _gold = MutableStateFlow<ResultState<List<Gold>>>(ResultState.Idle)

    override fun getCurrencies(): Flow<ResultState<List<Currency>>> = _currencies.asStateFlow()

    override fun getGold(): Flow<ResultState<List<Gold>>> = _gold.asStateFlow()

    override suspend fun refreshData() {
        try {
            Log.d("KapalicarsiRepo", "refreshData başlatıldı")
            _currencies.value = ResultState.Loading
            _gold.value = ResultState.Loading

            // API'den ham veriyi al
            val response = api.getRawResponse()

            if (response.isSuccessful) {
                val responseBody = response.body()?.string() ?: ""
                Log.d("KapalicarsiRepo", "API yanıtı alındı, boyut: ${responseBody.length}")

                // Yanıt içeriğini log'a yaz (büyük yanıtlarda ilk 500 karakter)
                if (responseBody.length > 500) {
                    Log.d("KapalicarsiRepo", "API yanıtı (ilk 500): ${responseBody.substring(0, 500)}...")
                } else {
                    Log.d("KapalicarsiRepo", "API yanıtı: $responseBody")
                }

                // Yanıtı parse et ve işle
                val allData = api.getCurrencies().body() ?: emptyList()

                Log.d("KapalicarsiRepo", "Alınan toplam veri sayısı: ${allData.size}")

                // Tüm kodları logla
                val allCodes = allData.map { it.code }
                Log.d("KapalicarsiRepo", "Tüm kodlar: $allCodes")

                // Döviz verilerini filtrele
                processCurrencyData(allData)

                // Altın verilerini filtrele
                processGoldData(allData)

            } else {
                val errorCode = response.code()
                val errorBody = response.errorBody()?.string() ?: ""
                Log.e("KapalicarsiRepo", "API hatası: $errorCode, $errorBody")

                val errorMsg = "HTTP Hatası: $errorCode - ${response.message()}"
                _currencies.value = ResultState.Error(UiText.dynamicString(errorMsg))
                _gold.value = ResultState.Error(UiText.dynamicString(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("KapalicarsiRepo", "Genel hata: ${e.message}", e)
            handleNetworkException(e)
        }
    }

    private fun processCurrencyData(allData: List<CurrencyResponse>) {
        Log.d("KapalicarsiRepo", "processCurrencyData başlatıldı")

        // Döviz kodlarını logla
        Log.d("KapalicarsiRepo", "Aranan döviz kodları: ${Constants.CurrencyCodes.CURRENCY_LIST}")

        // Döviz verilerini filtrele
        val filteredCurrencies = allData.filter { data ->
            val isMatch = Constants.CurrencyCodes.CURRENCY_LIST.contains(data.code)
            Log.d("KapalicarsiRepo", "Döviz kodu kontrol: ${data.code} - eşleşiyor mu? $isMatch")
            isMatch
        }

        Log.d("KapalicarsiRepo", "Filtrelenmiş döviz sayısı: ${filteredCurrencies.size}")

        if (filteredCurrencies.isNotEmpty()) {
            try {
                // Domain modellere dönüştür
                val currencies = filteredCurrencies.map {
                    val currency = it.toDomain()
                    Log.d("KapalicarsiRepo", "Döviz dönüştürüldü: ${currency.code}, ${currency.name}")
                    currency
                }
                _currencies.value = ResultState.Success(currencies)
            } catch (e: Exception) {
                Log.e("KapalicarsiRepo", "Döviz dönüşüm hatası: ${e.message}", e)
                _currencies.value = ResultState.Error(UiText.dynamicString("Döviz verisi dönüşüm hatası: ${e.message}"))
            }
        } else {
            // Veri bulunamadı, tüm kodları görüntüle ve içinde arama yap
            val apiCodes = allData.map { it.code }
            Log.d("KapalicarsiRepo", "API'de bulunan tüm kodlar: $apiCodes")

            // Elle filtreleme deneyin (büyük/küçük harf duyarsız)
            val manuallyFilteredCurrencies = allData.filter { data ->
                Constants.CurrencyCodes.CURRENCY_LIST.any { code ->
                    data.code.equals(code, ignoreCase = true) ||
                            data.code.replace("_", "").equals(code, ignoreCase = true) ||
                            code.replace("_", "").equals(data.code, ignoreCase = true)
                }
            }

            if (manuallyFilteredCurrencies.isNotEmpty()) {
                Log.d("KapalicarsiRepo", "Manuel filtreleme ile döviz bulundu: ${manuallyFilteredCurrencies.size}")
                val currencies = manuallyFilteredCurrencies.map { it.toDomain() }
                _currencies.value = ResultState.Success(currencies)
            } else {
                Log.e("KapalicarsiRepo", "Hiç döviz verisi bulunamadı")
                _currencies.value = ResultState.Error(UiText.stringResource(R.string.error_no_currencies_found))
            }
        }
    }

    private fun processGoldData(allData: List<CurrencyResponse>) {
        Log.d("KapalicarsiRepo", "processGoldData başlatıldı")

        // Altın kodlarını logla
        Log.d("KapalicarsiRepo", "Aranan altın kodları: ${Constants.GoldCodes.GOLD_LIST}")

        // Altın verilerini filtrele
        val filteredGold = allData.filter { data ->
            val isMatch = Constants.GoldCodes.GOLD_LIST.contains(data.code)
            Log.d("KapalicarsiRepo", "Altın kodu kontrol: ${data.code} - eşleşiyor mu? $isMatch")
            isMatch
        }

        Log.d("KapalicarsiRepo", "Filtrelenmiş altın sayısı: ${filteredGold.size}")

        if (filteredGold.isNotEmpty()) {
            try {
                // Domain modellere dönüştür
                val goldList = filteredGold.map {
                    val gold = Gold(
                        code = it.code,
                        name = Constants.GoldCodes.getDisplayName(it.code),
                        buyPrice = convertToDouble(it.alis),
                        sellPrice = convertToDouble(it.satis),
                        lastUpdated = parseDate(it.tarih)
                    )
                    Log.d("KapalicarsiRepo", "Altın dönüştürüldü: ${gold.code}, ${gold.name}")
                    gold
                }
                _gold.value = ResultState.Success(goldList)
            } catch (e: Exception) {
                Log.e("KapalicarsiRepo", "Altın dönüşüm hatası: ${e.message}", e)
                _gold.value = ResultState.Error(UiText.dynamicString("Altın verisi dönüşüm hatası: ${e.message}"))
            }
        } else {
            // Veri bulunamadı, tüm kodları görüntüle ve içinde arama yap
            val apiCodes = allData.map { it.code }
            Log.d("KapalicarsiRepo", "API'de bulunan tüm kodlar: $apiCodes")

            // Elle filtreleme deneyin (büyük/küçük harf duyarsız)
            val manuallyFilteredGold = allData.filter { data ->
                Constants.GoldCodes.GOLD_LIST.any { code ->
                    data.code.equals(code, ignoreCase = true) ||
                            data.code.replace("_", "").equals(code, ignoreCase = true) ||
                            code.replace("_", "").equals(data.code, ignoreCase = true)
                }
            }

            if (manuallyFilteredGold.isNotEmpty()) {
                Log.d("KapalicarsiRepo", "Manuel filtreleme ile altın bulundu: ${manuallyFilteredGold.size}")
                val goldList = manuallyFilteredGold.map {
                    Gold(
                        code = it.code,
                        name = guessGoldName(it.code),
                        buyPrice = convertToDouble(it.alis),
                        sellPrice = convertToDouble(it.satis),
                        lastUpdated = parseDate(it.tarih)
                    )
                }
                _gold.value = ResultState.Success(goldList)
            } else {
                Log.e("KapalicarsiRepo", "Hiç altın verisi bulunamadı")
                _gold.value = ResultState.Error(UiText.stringResource(R.string.error_no_gold_found))
            }
        }
    }

    private fun guessGoldName(code: String): String {
        // Kod tam eşleşmiyorsa, benzer bir kod bulmaya çalış
        return when {
            code.contains("CEYREK", ignoreCase = true) && code.contains("ESKI", ignoreCase = true) -> "Çeyrek (Eski)"
            code.contains("CEYREK", ignoreCase = true) && code.contains("YENI", ignoreCase = true) -> "Çeyrek (Yeni)"
            code.contains("YARIM", ignoreCase = true) && code.contains("ESKI", ignoreCase = true) -> "Yarım (Eski)"
            code.contains("YARIM", ignoreCase = true) && code.contains("YENI", ignoreCase = true) -> "Yarım (Yeni)"
            code.contains("TEK", ignoreCase = true) && code.contains("ESKI", ignoreCase = true) -> "Tam (Eski)"
            code.contains("TEK", ignoreCase = true) && code.contains("YENI", ignoreCase = true) -> "Tam (Yeni)"
            code.contains("ATA", ignoreCase = true) && code.contains("ESKI", ignoreCase = true) -> "Ata (Eski)"
            code.contains("ATA", ignoreCase = true) && code.contains("YENI", ignoreCase = true) -> "Ata (Yeni)"
            code.equals("ALTIN", ignoreCase = true) || code.contains("ALTIN", ignoreCase = true) -> "Gram Altın"
            code.equals("ONS", ignoreCase = true) -> "Ons Altın"
            else -> code
        }
    }

    private fun convertToDouble(value: Any): Double {
        return when (value) {
            is String -> value.toDoubleOrNull() ?: 0.0
            is Double -> value
            is Int -> value.toDouble()
            else -> 0.0
        }
    }

    private fun parseDate(dateStr: String): Long {
        return try {
            val parts = dateStr.split(" ")
            val dateParts = parts[0].split("-")
            val timeParts = parts[1].split(":")

            val day = dateParts[0].toInt()
            val month = dateParts[1].toInt() - 1 // Ay 0-11 arasında
            val year = dateParts[2].toInt()

            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()
            val second = timeParts[2].toInt()

            java.util.Calendar.getInstance().apply {
                set(year, month, day, hour, minute, second)
            }.timeInMillis
        } catch (e: Exception) {
            Log.e("KapalicarsiRepo", "Tarih ayrıştırma hatası: ${e.message}")
            System.currentTimeMillis()
        }
    }

    override fun getUpdateInterval(): Flow<Int> = settingsDataStore.getApiUpdateInterval()

    override suspend fun setUpdateInterval(seconds: Int) {
        settingsDataStore.setUpdateInterval(seconds)
    }

    private fun handleNetworkException(e: Exception) {
        when (e) {
            is HttpErrorInterceptor.NoConnectivityException ->
                setErrorState("İnternet bağlantı hatası: ${e.message}")
            is HttpErrorInterceptor.ServerException ->
                setErrorState("Sunucu hatası: ${e.message}")
            is HttpErrorInterceptor.TimeoutException ->
                setErrorState("Zaman aşımı hatası: ${e.message}")
            is HttpException -> {
                val errorMsg = when (e.code()) {
                    500 -> "Sunucu hatası (500): API servisinde bir sorun var"
                    502 -> "Kötü ağ geçidi (502): API servisi geçici olarak kullanılamıyor"
                    503 -> "Servis kullanılamıyor (503): API servisine şu anda erişilemiyor"
                    504 -> "Ağ geçidi zaman aşımı (504): API yanıt vermedi"
                    404 -> "Bulunamadı (404): API endpoint'i bulunamadı"
                    401 -> "Yetkilendirme hatası (401): Erişim izniniz yok"
                    403 -> "Yasak (403): Bu kaynağa erişim izniniz yok"
                    else -> "HTTP Hatası: ${e.code()} - ${e.message()}"
                }
                setErrorState(errorMsg)
            }
            is IOException ->
                setErrorState("Ağ hatası: ${e.message}")
            else ->
                setErrorState("Bilinmeyen hata: ${e.message}")
        }
    }

    private fun setErrorState(message: String) {
        val error = UiText.dynamicString(message)
        _currencies.value = ResultState.Error(error)
        _gold.value = ResultState.Error(error)
    }
}