package com.arctouch.codechallenge.details.detailsFragment

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.repositories.MoviesRepository


class DetailsViewModel: ViewModel() {

    val selectedMovieLiveData = MediatorLiveData<Movie>()

    init {
        selectedMovieLiveData.addSource(MoviesRepository.selectedMovieLiveData, { selectedMovieLiveData.value = it })
    }

    //region Private

    fun fetchSelectedMovie(id: Long) {
        MoviesRepository.fetchMovieById(id)
    }

    //endregion
}
