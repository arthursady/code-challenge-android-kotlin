package com.arctouch.codechallenge.details.detailsFragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import com.arctouch.codechallenge.util.constants.IntentConstants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.details_fragment.*

class DetailsFragment: Fragment() {

    private var viewModel: DetailsViewModel? = null

    private var movieId: Long? = null

    //region Lifecycle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        movieId = arguments?.getLong(IntentConstants.MOVIE_ID)
        return inflater.inflate(R.layout.details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
        movieId?.let { viewModel?.fetchSelectedMovie(it) }
        registerObservers()
    }

    //endregion

    //region Private

    private fun registerObservers() {
        viewModel?.selectedMovieLiveData?.observe(this, Observer { updateSelectedMovie(it) })
    }

    private fun updateSelectedMovie(movie: Movie?) {
        movie ?: return
        titleTextView.text = movie.title
        genresTextView.text = movie.genres?.joinToString(separator = ", ") { it.name }
        releaseDateTextView.text = movie.releaseDate
        overViewTextView.text = movie.overview

        Glide.with(this)
                .load(movie.posterPath?.let { MovieImageUrlBuilder.buildPosterUrl(it) })
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(posterImageView)

        Glide.with(this)
                .load(movie.backdropPath?.let { MovieImageUrlBuilder.buildBackdropUrl(it) })
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(backdropImageView)
    }

    //endregion
}
