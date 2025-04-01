package com.mehmettekin.gunkurasiapp.data.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.mehmettekin.gunkurasiapp.R
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class HttpErrorInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected()) {
            throw NoConnectivityException()
        }

        val request = chain.request()
        try {
            val response = chain.proceed(request)

            when (response.code) {
                in 500..599 -> {
                    val message = when (response.code) {
                        500 -> context.getString(R.string.error_server_500)
                        502 -> context.getString(R.string.error_server_502)
                        503 -> context.getString(R.string.error_server_503)
                        504 -> context.getString(R.string.error_server_504)
                        else -> context.getString(R.string.error_server_generic)
                    }
                    throw ServerException(message)
                }
            }

            return response
        } catch (e: SocketTimeoutException) {
            throw TimeoutException()
        } catch (e: UnknownHostException) {
            throw NoConnectivityException()
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    class NoConnectivityException : IOException("İnternet bağlantısı bulunamadı. Lütfen bağlantınızı kontrol edin.")

    class ServerException(message: String) : IOException(message)

    class TimeoutException : IOException("İstek zaman aşımına uğradı. Lütfen daha sonra tekrar deneyin.")
}