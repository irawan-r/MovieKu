package com.amora.movieku.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amora.movieku.data.model.persistence.VideoMovieEntity

@Dao
interface VideoDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertVideoMoviePopular(poster: VideoMovieEntity)

	@Query("SELECT * FROM video WHERE id = :id_")
	suspend fun getVideoMovie(id_: String): VideoMovieEntity?
}