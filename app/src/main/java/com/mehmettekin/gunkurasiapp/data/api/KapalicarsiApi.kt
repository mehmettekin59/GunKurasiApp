package com.mehmettekin.gunkurasiapp.data.api

import com.mehmettekin.gunkurasiapp.data.api.model.ApiResponse
import com.mehmettekin.gunkurasiapp.data.api.model.CurrencyResponse
import com.mehmettekin.gunkurasiapp.data.api.model.GoldResponse
import retrofit2.http.GET

interface KapalicarsiApi {
    @GET("currencies")
    suspend fun getCurrencies(): ApiResponse<List<CurrencyResponse>>

    @GET("gold")
    suspend fun getGold(): ApiResponse<List<GoldResponse>>
}