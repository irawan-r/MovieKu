package com.amora.movieku.data.repository.remotemediator.popular

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.amora.movieku.data.model.network.MovieResponse.Companion.toPopularEntity
import com.amora.movieku.data.model.network.RemoteKeysPopular
import com.amora.movieku.data.model.persistence.MoviePopularEntity
import com.amora.movieku.data.persistence.AppDatabase
import com.amora.movieku.network.ApiService
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PopularRemoteMediator @Inject constructor(
	private val database: AppDatabase,
	private val apiService: ApiService,
	private var page: Int?,
): RemoteMediator<Int, MoviePopularEntity>() {
	private companion object {
		const val INITIAL_PAGE_INDEX = 1
	}
	override suspend fun initialize(): InitializeAction {
		return InitializeAction.LAUNCH_INITIAL_REFRESH
	}

	override suspend fun load(
		loadType: LoadType,
		state: PagingState<Int, MoviePopularEntity>
	): MediatorResult {
		page = when (loadType) {
			LoadType.REFRESH ->{
				val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
				remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
			}
			LoadType.PREPEND -> {
				val remoteKeys = getRemoteKeyForFirstItem(state)
				val prevKey = remoteKeys?.prevKey
					?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
				prevKey
			}
			LoadType.APPEND -> {
				val remoteKeys = getRemoteKeyForLastItem(state)
				val nextKey = remoteKeys?.nextKey
					?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
				nextKey
			}
		}
		try {
			val responseData = apiService.getPopularMovies(page)
			val data = responseData.results
			val endOfPaginationReached = data.isEmpty()
			database.withTransaction {
				if (loadType == LoadType.REFRESH) {
					database.movieDao().deleteMoviesPopularList()
					database.remoteKeysDao().deleteRemoteKeysPopular()
				}
				val prevKey = if (page == 1) null else page!! - 1
				val nextKey = if (endOfPaginationReached) null else page!! + 1
				val keys = data.toPopularEntity().map {
					RemoteKeysPopular(id = it.id, prevKey = prevKey, nextKey = nextKey)
				}
				database.movieDao().insertMoviesPopularList(data.toPopularEntity())
				database.remoteKeysDao().insertAllKeysPopular(keys)
			}
			return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
		} catch (exception: Exception) {
			return MediatorResult.Error(exception)
		}
	}

	private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MoviePopularEntity>): RemoteKeysPopular? {
		return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
			database.remoteKeysDao().getRemoteKeysPopularId(data.id)
		}
	}
	private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MoviePopularEntity>): RemoteKeysPopular? {
		return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
			database.remoteKeysDao().getRemoteKeysPopularId(data.id)
		}
	}
	private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, MoviePopularEntity>): RemoteKeysPopular? {
		return state.anchorPosition?.let { position ->
			state.closestItemToPosition(position)?.id?.let { id ->
				database.remoteKeysDao().getRemoteKeysPopularId(id)
			}
		}
	}
}