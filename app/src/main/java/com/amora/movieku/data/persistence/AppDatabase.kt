package com.amora.movieku.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amora.movieku.data.model.network.RemoteKeysPopular
import com.amora.movieku.data.model.network.RemoteKeysUpcoming
import com.amora.movieku.data.model.persistence.MovieDetailEntity
import com.amora.movieku.data.model.persistence.MoviePopularEntity
import com.amora.movieku.data.model.persistence.MovieUpcomingEntity
import com.amora.movieku.data.model.persistence.VideoMovieEntity
import com.amora.movieku.utils.Converters

@Database(
	entities = [
		MoviePopularEntity::class,
		MovieUpcomingEntity::class,
		RemoteKeysUpcoming::class,
		RemoteKeysPopular::class,
		VideoMovieEntity::class,
		MovieDetailEntity::class
	],
	version = 1,
	exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

	abstract fun movieDao(): MovieDao

	abstract fun remoteKeysDao(): RemoteKeysDao

	abstract fun videoDao(): VideoDao

	abstract fun movieDetailDao(): MovieDetailDao
}