package com.amora.movieku.data.persistence

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.data.model.persistence.MoviePopularEntity
import com.amora.movieku.data.model.persistence.MovieUpcomingEntity

@Dao
interface MovieDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertMoviesPopularList(movie: List<MoviePopularEntity>)

	@Query("SELECT * FROM movie_popular WHERE id = :id_")
	suspend fun getMoviePopular(id_: Long): Movie

	@Query("SELECT * FROM movie_popular")
	fun getMoviesPopularList(): PagingSource<Int, MoviePopularEntity>

	@Query("DELETE FROM movie_popular")
	suspend fun deleteMoviesPopularList()

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertMoviesUpcomingList(movie: List<MovieUpcomingEntity>)

	@Query("SELECT * FROM movie_upcoming WHERE id = :id_")
	suspend fun getMovieUpcoming(id_: Long): Movie

	@Query("SELECT * FROM movie_upcoming")
	fun getMoviesUpcomingList(): PagingSource<Int, MovieUpcomingEntity>

	@Query("DELETE FROM movie_upcoming")
	suspend fun deleteMoviesUpcomingList()
}