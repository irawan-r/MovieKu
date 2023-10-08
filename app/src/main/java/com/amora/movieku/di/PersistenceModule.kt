package com.amora.movieku.di

import android.app.Application
import androidx.room.Room
import com.amora.movieku.R
import com.amora.movieku.data.persistence.AppDatabase
import com.amora.movieku.data.persistence.MovieDao
import com.amora.movieku.data.persistence.MovieDetailDao
import com.amora.movieku.data.persistence.VideoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

	@Provides
	@Singleton
	fun provideAppDatabase(application: Application): AppDatabase {
		return Room.databaseBuilder(
			application,
			AppDatabase::class.java,
			application.getString(R.string.database)
		).fallbackToDestructiveMigration()
			.build()
	}

	@Provides
	@Singleton
	fun provideMovieDao(appDatabase: AppDatabase): MovieDao {
		return appDatabase.movieDao()
	}

	@Provides
	@Singleton
	fun provideMovieDetailDao(appDatabase: AppDatabase): MovieDetailDao {
		return appDatabase.movieDetailDao()
	}

	@Provides
	@Singleton
	fun provideVideoDao(appDatabase: AppDatabase): VideoDao {
		return appDatabase.videoDao()
	}
}