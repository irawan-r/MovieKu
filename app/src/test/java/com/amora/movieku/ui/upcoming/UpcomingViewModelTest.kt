package com.amora.movieku.ui.upcoming

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.map
import com.amora.movieku.data.State
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.data.model.network.Movie.Companion.toPopularEntity
import com.amora.movieku.data.model.network.Movie.Companion.toUpcomingEntity
import com.amora.movieku.data.repository.MainRepositoryImpl
import com.amora.movieku.ui.adapter.MoviesAdapter
import com.amora.movieku.ui.home.popular.PopularViewModel
import com.amora.movieku.ui.home.upcoming.UpcomingViewModel
import com.amora.movieku.utils.DataDummy
import com.amora.movieku.utils.FakeStoryPagingSource
import com.amora.movieku.utils.MainDispatcherRule
import com.amora.movieku.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UpcomingViewModelTest {

	@get:Rule
	val instantExecutorRule = InstantTaskExecutorRule()

	@OptIn(ExperimentalCoroutinesApi::class)
	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Mock
	private lateinit var repository: MainRepositoryImpl

	private lateinit var upcomingdViewModel: UpcomingViewModel
	private val dummyStories = DataDummy.generateDataDummy()
	private val emptyStories = DataDummy.generateEmptyStories()

	@Before
	fun setUp() {
		upcomingdViewModel = UpcomingViewModel(repository)
	}

	@Test
	fun `when GetMoviesUpcoming Should Not Null And Return Success`() = runBlocking {
		val data: PagingData<Movie> = FakeStoryPagingSource.snapshot(dummyStories)

		val expectedPagingData = MutableStateFlow<State<PagingData<Movie>>>(State.Empty())
		expectedPagingData.update {
			State.Success(data)
		}

		val expectedFlow = flowOf(data.map { it.toUpcomingEntity() })
		val pagingStory = repository.getUpcomingMovies()
		`when`(pagingStory).thenReturn(expectedFlow)

		val differ = AsyncPagingDataDiffer(
			diffCallback = MoviesAdapter.differCallback,
			updateCallback = Utils.noopListUpdateCallback,
			workerDispatcher = Dispatchers.Main
		)

		upcomingdViewModel.getUpcomingMovies()
		verify(repository).getUpcomingMovies()

		val actualData = upcomingdViewModel.moviesState.first()
		differ.submitData(actualData.data!!)

		assertTrue(actualData is State.Success)
		assertNotNull(differ.snapshot())
		assertEquals(dummyStories.size, differ.snapshot().size)
		assertEquals(dummyStories[0], differ.snapshot()[0])
	}

	@Test
	fun `when GetMoviesUpcoming Is Empty`() = runBlocking {
		val data: PagingData<Movie> = PagingData.empty()

		val expectedPagingData = MutableStateFlow<State<PagingData<Movie>>>(State.Empty())
		expectedPagingData.update {
			State.Success(data)
		}

		val expectedFlow = flowOf(data.map { it.toUpcomingEntity() })
		val pagingStory = repository.getUpcomingMovies()
		`when`(pagingStory).thenReturn(expectedFlow)

		val differ = AsyncPagingDataDiffer(
			diffCallback = MoviesAdapter.differCallback,
			updateCallback = Utils.noopListUpdateCallback,
			workerDispatcher = Dispatchers.Main
		)

		upcomingdViewModel.getUpcomingMovies()
		verify(repository).getUpcomingMovies()

		val actualData = upcomingdViewModel.moviesState.first()
		differ.submitData(actualData.data!!)

		assertTrue(actualData is State.Success)
		assertNotNull(differ.snapshot())
		assertEquals(emptyStories.size, differ.snapshot().size)
	}
}