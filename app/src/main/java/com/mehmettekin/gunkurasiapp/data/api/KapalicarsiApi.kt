package com.mehmettekin.gunkurasiapp.data.api


import com.mehmettekin.gunkurasiapp.data.api.model.CurrencyResponse
import com.mehmettekin.gunkurasiapp.data.api.model.GoldResponse
import retrofit2.http.GET

interface KapalicarsiApi {
    // Eğer API direkt liste dönüyorsa (ApiResponse wrapper'ı olmadan):
    @GET("kurlar/")
    suspend fun getCurrencies(): List<CurrencyResponse>

    // Altın verilerini çek
    @GET("kurlar/")
    suspend fun getGold(): List<GoldResponse>
}