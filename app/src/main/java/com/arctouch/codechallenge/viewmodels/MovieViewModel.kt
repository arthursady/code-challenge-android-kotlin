package com.arctouch.codechallenge.viewmodels

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.repositories.MoviesRepository
import com.arctouch.codechallenge.util.livedata.SingleLiveEvent


class MovieViewModel: ViewModel() {
    private val genres: ArrayList<Genre> = arrayListOf()

    val movieListLiveData = MediatorLiveData<List<Movie>>()
    val loadingLiveData = MediatorLiveData<Boolean>()
    val lastPageLiveData = MediatorLiveData<Int>()
    val selectedMovieLiveData = MediatorLiveData<Movie>()
    val detailsTransitionLiveData = SingleLiveEvent<Long>()

    init {
        loadingLiveData.value = true

        movieListLiveData.addSource(MoviesRepository.genresLiveData, {
            it?.let {
                this.genres.addAll(it)
            }
            movieListLiveData.value?.let { movies ->
                val moviesWithGenres = movies.map { movie ->
                    if (movie.genres?.isNotEmpty() == true) {
                        movie.copy()
                    } else {
                        makeMovieWithGenre(movie)
                    }

                }
                movieListLiveData.value = moviesWithGenres
            }
        })

        movieListLiveData.addSource(MoviesRepository.moviesLiveData, { newMovies ->
            val moviesWithGenres: ArrayList<Movie> = ArrayList(newMovies?.map { movie ->
                if (movie.genres?.isNotEmpty() == true) {
                    movie.copy()
                } else {
                    makeMovieWithGenre(movie)
                }
            })
            loadingLiveData.value = false
            movieListLiveData.value = moviesWithGenres
        })

        lastPageLiveData.addSource(MoviesRepository.lastPageLiveData, { it?.let { lastPageLiveData.value = it } })
        loadingLiveData.addSource(MoviesRepository.loadingLiveData, { loadingLiveData.value = it })
        selectedMovieLiveData.addSource(MoviesRepository.selectedMovieLiveData, { selectedMovieLiveData.value = it })

        MoviesRepository.fetchGenres()
        MoviesRepository.fetchMoviesList()
    }

    fun onMovieClicked(id: Long) {
        detailsTransitionLiveData.value = id
    }

    fun paginationTriggered() {
        MoviesRepository.fetchMoviesList()
    }

    fun onSearchTextChange(text: String) {
        MoviesRepository.searchText = text
    }

    fun fetchSelectedMovie(id: Long) {
        MoviesRepository.fetchMovieById(id)
    }

    private fun makeMovieWithGenre(movie: Movie): Movie {
        return movie.copy(genres = MoviesRepository.genresLiveData.value?.filter { movie.genreIds?.contains(it.id) == true })
    }
}
