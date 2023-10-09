package com.amora.movieku.utils

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.amora.movieku.data.model.network.Movie
import kotlinx.coroutines.flow.Flow


class FakeStoryPagingSource : PagingSource<Int, Flow<List<Movie>>>() {
	companion object {
		fun snapshot(items: List<Movie>): PagingData<Movie> {
			return PagingData.from(items)
		}
	}

	override fun getRefreshKey(state: PagingState<Int, Flow<List<Movie>>>): Int {
		return 0
	}

	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Flow<List<Movie>>> {
		return LoadResult.Page(emptyList(), 0, 1)
	}
}