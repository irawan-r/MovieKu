package com.amora.movieku.data.persistence

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.data.model.persistence.MovieFavoriteEntity
import com.amora.movieku.data.model.persistence.MoviePopularEntity

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

	@Query("SELECT * FROM movie_favorite where id = :id_")
	suspend fun getMovieFavorite(id_: Long): Movie

	@Query("SELECT * FROM movie_favorite")
	suspend fun getMoviesFavorite(): List<Movie>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFavoriteMovies(movie: MovieFavoriteEntity)

	@Query("DELETE FROM movie_favorite WHERE id = :id_")
	suspend fun deleteFavoriteMovie(id_: Long)

	@Query("DELETE FROM movie_favorite")
	suspend fun deleteFavoriteMovies()
}