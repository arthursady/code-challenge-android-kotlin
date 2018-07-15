package com.arctouch.codechallenge.dashboard.movielist

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.Intent
import com.arctouch.codechallenge.details.DetailsActivity
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.repositories.MoviesRepository
import com.arctouch.codechallenge.util.constants.IntentConstants


class MovieListViewModel: ViewModel() {
    private val genres: ArrayList<Genre> = arrayListOf()

    val movieListLiveData = MediatorLiveData<List<Movie>>()
    val loadingLiveData = MediatorLiveData<Boolean>()
    val lastPageLiveData = MediatorLiveData<Int>()

    init {
        loadingLiveData.value = true

        movieListLiveData.addSource(MoviesRepository.genresLiveData, {
            it?.let {
                this.genres.addAll(it)
            }
            movieListLiveData.value?.let { movies ->
                val moviesWithGenres = movies.map { movie ->
                    movie.copy(genres = genres.filter { movie.genreIds?.contains(it.id) == true })
                }
                movieListLiveData.value = moviesWithGenres
            }
        })

        movieListLiveData.addSource(MoviesRepository.moviesLiveData, { newMovies ->
            val moviesWithGenres: ArrayList<Movie> = ArrayList(newMovies?.map { movie ->
                movie.copy(genres = genres.filter { movie.genreIds?.contains(it.id) == true })
            })
            loadingLiveData.value = false
            movieListLiveData.value = moviesWithGenres
        })

        lastPageLiveData.addSource(MoviesRepository.lastPageLiveData, { it?.let { lastPageLiveData.value = it } })
        loadingLiveData.addSource(MoviesRepository.loadingLiveData, { loadingLiveData.value = it })

        MoviesRepository.fetchGenres()
        MoviesRepository.fetchMoviesList()
    }

    fun onMovieClicked(context: Context, id: Long) {
        val intent = Intent(context, DetailsActivity::class.java)
        intent.putExtra(IntentConstants.MOVIE_ID, id)
        context.startActivity(intent)
    }

    fun paginationTriggered() {
        MoviesRepository.fetchMoviesList()
    }

    fun onSearchTextChange(text: String) {
        MoviesRepository.searchText = text
    }
}
