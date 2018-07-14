package com.arctouch.codechallenge.dashboard.movielist

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
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
    val loadingLiveData = MutableLiveData<Boolean>()

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

        MoviesRepository.fetchGenres()
        MoviesRepository.fetchMoviesList()
    }

    fun onMovieClicked(context: Context, id: Long) {
        val intent = Intent(context, DetailsActivity::class.java)
        intent.putExtra(IntentConstants.MOVIE_ID, id)
        context.startActivity(intent)
    }
}
