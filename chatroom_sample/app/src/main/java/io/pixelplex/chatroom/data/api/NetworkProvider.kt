package io.pixelplex.chatroom.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.echo.mobile.framework.model.AccountOptions
import org.echo.mobile.framework.model.AssetAmount
import org.echo.mobile.framework.model.Transaction
import org.echo.mobile.framework.model.network.Network
import org.echo.mobile.framework.model.operations.AccountCreateOperation
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Provides [ApiService] for network calls
 */
fun provideApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)

/**
 * Provides [Retrofit] instance
 *
 * @param url Base retrofit url
 * @param client The HTTP client used for requests
 */
fun provideRetrofit(url: String, gson: Gson, client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(client)
        .build()
}

/**
 * Provides [Gson] instance for response parsing
 */
fun provideGson(network: Network): Gson =
    GsonBuilder()
        .registerTypeAdapter(Transaction::class.java, Transaction.TransactionDeserializer())
        .registerTypeAdapter(
            AccountCreateOperation::class.java,
            AccountCreateOperation.Deserializer()
        )
        .registerTypeAdapter(AccountOptions::class.java, AccountOptions.Deserializer(network))
        .registerTypeAdapter(AssetAmount::class.java, AssetAmount.Deserializer())
        .create()

/**
 * Provides [OkHttpClient] instance
 *
 * @param interceptors Interceptor for requests
 */
fun provideOkHttpClient(vararg interceptors: Interceptor): OkHttpClient {
    val builder = OkHttpClient.Builder()
    builder.interceptors().addAll(interceptors)
    builder.readTimeout(60, TimeUnit.SECONDS)
    builder.connectTimeout(60, TimeUnit.SECONDS)
    return builder.build()
}

/**
 * Provides interceptor for http logging
 */
fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    return interceptor
}

