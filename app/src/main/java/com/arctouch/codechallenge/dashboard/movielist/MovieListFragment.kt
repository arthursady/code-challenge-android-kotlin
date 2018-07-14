package com.arctouch.codechallenge.dashboard.movielist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.adapters.MoviesAdapter
import com.arctouch.codechallenge.util.helpers.PaginationHelper
import com.arctouch.codechallenge.util.listeners.MovieClickListener
import kotlinx.android.synthetic.main.movie_list_fragment.*

class MovieListFragment: Fragment() {

    private var adapter: MoviesAdapter? = null
    var viewModel: MovieListViewModel? = null

    //region Lifecycle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.movie_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MovieListViewModel::class.java)
        setupView()
        registerObservers()
    }

    //endregion

    //region Private

    private fun setupView() {
        setupMoviesAdapter()
    }

    private fun setupMoviesAdapter() {
        adapter = MoviesAdapter(object: MovieClickListener {
            override fun onMovieClicked(id: Long) {
                context?.let {
                    viewModel?.onMovieClicked(it, id)
                }
            }
        })
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(PaginationHelper(object: PaginationHelper.PaginationListener {
            override fun onPaginationTriggered() {
                viewModel?.paginationTriggered()
            }
        }))
    }

    private fun registerObservers() {
        viewModel?.movieListLiveData?.observe(this, Observer { updateList(it) })
        viewModel?.loadingLiveData?.observe(this, Observer { showLoading(it == true) })
        viewModel?.lastPageLiveData?.observe(this, Observer { it?.let { Toast.makeText(activity, getString(it), Toast.LENGTH_SHORT).show() } })
    }

    private fun showLoading(show: Boolean) {
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun updateList(movieList: List<Movie>?) {
        adapter?.movies = movieList ?: listOf()
    }

    //endregion
}
