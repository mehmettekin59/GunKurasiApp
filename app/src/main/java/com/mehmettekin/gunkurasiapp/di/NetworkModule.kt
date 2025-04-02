package com.mehmettekin.gunkurasiapp.di


import com.mehmettekin.gunkurasiapp.data.api.HttpErrorInterceptor
import com.mehmettekin.gunkurasiapp.data.api.KapalicarsiApi
import com.mehmettekin.gunkurasiapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.squareup.moshi.Moshi
import okhttp3.ConnectionSpec
import okhttp3.TlsVersion
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // NetworkModule'dan provideMoshi metodunu kaldırdık
    // AppModule'daki provideMoshi metodu kullanılacak

    @Provides
    @Singleton
    fun provideOkHttpClient(httpErrorInterceptor: HttpErrorInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // TLS sorunları için güvenlik yapılandırması oluştur
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        // SSL bağlam yapılandırması
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Bağlantı spesifikasyonlarını oluştur
        val connectionSpecs = listOf(
            ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
                .build(),
            ConnectionSpec.CLEARTEXT
        )

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(httpErrorInterceptor)
            .connectionSpecs(connectionSpecs)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideKapalicarsiApi(retrofit: Retrofit): KapalicarsiApi {
        return retrofit.create(KapalicarsiApi::class.java)
    }
}
