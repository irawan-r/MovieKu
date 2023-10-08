package com.amora.movieku.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amora.movieku.data.model.network.MovieDetail
import com.amora.movieku.data.model.persistence.MovieDetailEntity

@Dao
interface MovieDetailDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertMovieDetail(poster: MovieDetailEntity)

	@Query("SELECT * FROM movie_detail WHERE id = :id_")
	suspend fun getStory(id_: Long): MovieDetailEntity?
}