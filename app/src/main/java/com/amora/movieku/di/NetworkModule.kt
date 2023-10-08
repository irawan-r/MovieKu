package com.amora.movieku.di

import com.amora.movieku.BuildConfig
import com.amora.movieku.BuildConfig.BASE_URL
import com.amora.movieku.network.ApiService
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
	private const val AUTH = "Authorization"

	@Provides
	fun provideOkHttpClient(): OkHttpClient {
		val loggingInterceptor = when {
			BuildConfig.DEBUG -> HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
			else -> HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
		}

		val authInterceptor = Interceptor { chain ->
			val request = chain.request().newBuilder()
				.addHeader(AUTH, "Bearer ${BuildConfig.API_KEY}")
				.build()
			chain.proceed(request)
		}

		return OkHttpClient.Builder()
			.addInterceptor(loggingInterceptor)
			.addInterceptor(authInterceptor)
			.connectTimeout(10, TimeUnit.SECONDS)
			.readTimeout(10, TimeUnit.SECONDS)
			.build()
	}

	@Provides
	@Singleton
	fun provideApiServices(client: OkHttpClient): ApiService {
		return Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
			.baseUrl(BASE_URL)
			.client(client)
			.build()
			.create(ApiService::class.java)
	}
}