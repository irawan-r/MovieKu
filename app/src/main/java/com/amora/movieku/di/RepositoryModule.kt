package com.amora.movieku.di

import com.amora.movieku.data.persistence.AppDatabase
import com.amora.movieku.data.repository.MainRepository
import com.amora.movieku.data.repository.MainRepositoryImpl
import com.amora.movieku.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

	@Provides
	@Singleton
	fun provideRepository(
		apiService: ApiService,
		appDatabase: AppDatabase
	): MainRepository {
		// Provide an instance of the RepositoryMainImpl
		return MainRepositoryImpl(
			apiService = apiService,
			database = appDatabase
		)
	}
}