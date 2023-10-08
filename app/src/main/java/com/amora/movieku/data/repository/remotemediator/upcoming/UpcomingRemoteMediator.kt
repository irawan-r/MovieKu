package com.amora.movieku.data.repository.remotemediator.upcoming

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.amora.movieku.data.model.network.MovieResponse.Companion.toUpcomingEntity
import com.amora.movieku.data.model.network.RemoteKeysUpcoming
import com.amora.movieku.data.model.persistence.MovieUpcomingEntity
import com.amora.movieku.data.persistence.AppDatabase
import com.amora.movieku.network.ApiService
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class UpcomingRemoteMediator @Inject constructor(
	private val database: AppDatabase,
	private val apiService: ApiService,
	private var page: Int?,
): RemoteMediator<Int, MovieUpcomingEntity>() {
	private companion object {
		const val INITIAL_PAGE_INDEX = 1
	}
	override suspend fun initialize(): InitializeAction {
		return InitializeAction.LAUNCH_INITIAL_REFRESH
	}

	override suspend fun load(
		loadType: LoadType,
		state: PagingState<Int, MovieUpcomingEntity>
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
			val responseData = apiService.getMovieUpcoming(page)
			val data = responseData.results
			val endOfPaginationReached = data.isEmpty()
			database.withTransaction {
				if (loadType == LoadType.REFRESH) {
					database.movieDao().deleteMoviesUpcomingList()
					database.remoteKeysDao().deleteRemoteKeysUpcoming()
				}
				val prevKey = if (page == 1) null else page!! - 1
				val nextKey = if (endOfPaginationReached) null else page!! + 1
				val keys = responseData.results.map {
					RemoteKeysUpcoming(id = it.id, prevKey = prevKey, nextKey = nextKey)
				}
				database.remoteKeysDao().insertAllKeysUpcoming(keys)
				database.movieDao().insertMoviesUpcomingList(data.toUpcomingEntity())
			}
			return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
		} catch (exception: Exception) {
			return MediatorResult.Error(exception)
		}
	}

	private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MovieUpcomingEntity>): RemoteKeysUpcoming? {
		return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
			database.remoteKeysDao().getRemoteKeysUpcomingId(data.id)
		}
	}
	private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MovieUpcomingEntity>): RemoteKeysUpcoming? {
		return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
			database.remoteKeysDao().getRemoteKeysUpcomingId(data.id)
		}
	}
	private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, MovieUpcomingEntity>): RemoteKeysUpcoming? {
		return state.anchorPosition?.let { position ->
			state.closestItemToPosition(position)?.id?.let { id ->
				database.remoteKeysDao().getRemoteKeysUpcomingId(id)
			}
		}
	}
}