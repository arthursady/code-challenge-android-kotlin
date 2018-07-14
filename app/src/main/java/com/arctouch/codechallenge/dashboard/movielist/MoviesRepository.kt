package com.arctouch.codechallenge.dashboard.movielist

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.helpers.RetrofitHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MoviesRepository {

    val genresLiveData = MutableLiveData<List<Genre>>()
    val moviesLiveData = MutableLiveData<List<Movie>>()
    val errorLiveData = MutableLiveData<String>()

    init {
        fetchGenres()
        fetchMoviesList()
    }

    private fun fetchGenres() {
        RetrofitHelper.getApi()?.genres(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    genresLiveData.value = it.genres
                }, {
                    Log.e("API", "Could not fetch genres list", it)
                })
    }

    private fun fetchMoviesList() {
        RetrofitHelper.getApi()?.upcomingMovies(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE, 1, TmdbApi.DEFAULT_REGION)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    val movies = ArrayList(it.results)
                    moviesLiveData.value = movies
                }, {
                    Log.e("API", "Could not fetch genres list", it)
                })
    }

}