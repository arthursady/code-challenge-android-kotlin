package com.arctouch.codechallenge.repositories

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.helpers.RetrofitHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


object MoviesRepository {

    val genresLiveData = MutableLiveData<List<Genre>>()
    val moviesLiveData = MutableLiveData<List<Movie>>()
    val selectedMovieLiveData = MutableLiveData<Movie>()
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
        RetrofitHelper.getApi()?.upcomingMovies(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE, 1, TmdbApi.DEFAULT_REGION)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    moviesLiveData.value = it.results
                }, {
                    Log.e("API", "Could not fetch genres list", it)
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