package com.arctouch.codechallenge.repositories

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.helpers.RetrofitHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


object MoviesRepository {

    var nextPage: Long = 1
    var totalPages: Long = 1

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
        loadingLiveData.value = true
        RetrofitHelper.getApi()?.upcomingMovies(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE, nextPage, TmdbApi.DEFAULT_REGION)
                ?.subscribeOn(Schedulers.io())
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

    fun fetchMovieById(id: Long) {
        val selectedMovie = moviesLiveData.value?.firstOrNull { it.id == id }
        if (selectedMovie == null) {
            RetrofitHelper.getApi()?.movie(id, TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({
                        selectedMovieLiveData.value = it
                    }, {
                        Log.e("API", "Could not fetch genres list", it)
                    })
        } else {
            selectedMovieLiveData.value = selectedMovie
        }

    }

}