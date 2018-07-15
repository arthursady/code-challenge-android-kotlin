package com.arctouch.codechallenge.repositories

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.helpers.RetrofitHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


object MoviesRepository {

    var nextPage: Long = 1
    var totalPages: Long = 1
    var searchText: String = ""
        set(value) {
            if (field != value) {
                field = value
                nextPage = 1
                totalPages = 1
                moviesLiveData.value = listOf()
                fetchMoviesList()
            }
        }

    var compositeSubscription = CompositeDisposable()

    private val movies: ArrayList<Movie> = arrayListOf()

    val genresLiveData = MutableLiveData<List<Genre>>()
    val moviesLiveData = MutableLiveData<List<Movie>>()
    val selectedMovieLiveData = MutableLiveData<Movie>()
    val lastPageLiveData = MutableLiveData<Int>()
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()

    fun fetchGenres() {
        RetrofitHelper.getApi()?.genres(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    genresLiveData.value = it.genres
                }, {
                    Log.e("API", "Could not fetch genres list", it)
                })
    }

    fun fetchMoviesList() {
        if (nextPage > totalPages || loadingLiveData.value == true) return
        val api = RetrofitHelper.getApi() ?: return
        loadingLiveData.value = true

        compositeSubscription.clear()
        val subs = if (searchText.isBlank()) {
            api.upcomingMovies(TmdbApi.API_KEY, nextPage)
                    .subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({
                        val currentMovies = moviesLiveData.value ?: listOf()
                        val movies = ArrayList<Movie>(currentMovies)
                        it.results.forEach {
                            if (!movies.contains(it)) movies.add(it)
                        }
                        moviesLiveData.value = movies
                        loadingLiveData.value = false
                        totalPages = it.totalPages
                        nextPage++
                        if (totalPages < nextPage) {
                            lastPageLiveData.value = R.string.last_page
                        }
                    }, {
                        Log.e("API", "Could not fetch genres list", it)
                        loadingLiveData.value = false
                    })
        } else {
            api.searchMovies(TmdbApi.API_KEY, nextPage, searchText)
                    .subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({
                        val currentMovies = moviesLiveData.value ?: listOf()
                        val movies = ArrayList<Movie>(currentMovies)
                        it.results.forEach {
                            if (!movies.contains(it)) movies.add(it)
                        }
                        moviesLiveData.value = movies
                        loadingLiveData.value = false
                        totalPages = it.totalPages
                        nextPage++
                        if (totalPages < nextPage) {
                            lastPageLiveData.value = R.string.last_page
                        }
                    }, {
                        Log.e("API", "Could not fetch genres list", it)
                        loadingLiveData.value = false
                    })
        }

        subs?.let { compositeSubscription.add(subs) }
    }

    fun fetchMovieById(id: Long) {
        val selectedMovie = moviesLiveData.value?.firstOrNull { it.id == id }
        if (selectedMovie == null) {
            RetrofitHelper.getApi()?.movie(id, TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({ movie ->
                        selectedMovieLiveData.value = makeMovieWithGenre(movie)
                    }, {
                        Log.e("API", "Could not fetch genres list", it)
                    })
        } else {
            selectedMovieLiveData.value = makeMovieWithGenre(selectedMovie)
        }
    }

    private fun makeMovieWithGenre(movie: Movie): Movie {
        return movie.copy(genres = genresLiveData.value?.filter { movie.genreIds?.contains(it.id) == true })
    }

}