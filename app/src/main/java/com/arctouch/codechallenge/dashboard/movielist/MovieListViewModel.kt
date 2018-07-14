package com.arctouch.codechallenge.dashboard.movielist

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.model.Movie


class MovieListViewModel: ViewModel() {
    private val genres: ArrayList<Genre> = arrayListOf()
    private val moviesRepository = MoviesRepository()

    val movieListLiveData = MediatorLiveData<List<Movie>>()
    val loadingLiveData = MutableLiveData<Boolean>()

    init {
        loadingLiveData.value = true

        movieListLiveData.addSource(moviesRepository.genresLiveData, {
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

        movieListLiveData.addSource(moviesRepository.moviesLiveData, { newMovies ->
            var moviesWithGenres: ArrayList<Movie> = ArrayList()
            movieListLiveData.value?.let { movies ->
                moviesWithGenres = ArrayList(movies.map { movie ->
                    movie.copy(genres = genres.filter { movie.genreIds?.contains(it.id) == true })
                })
            }
            newMovies?.let {
                moviesWithGenres.addAll(it.map { movie ->
                    movie.copy(genres = genres.filter { movie.genreIds?.contains(it.id) == true })
                })
            }
            loadingLiveData.value = false
            movieListLiveData.value = moviesWithGenres
        })
    }
}
