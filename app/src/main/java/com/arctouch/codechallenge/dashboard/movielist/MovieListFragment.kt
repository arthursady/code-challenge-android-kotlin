package com.arctouch.codechallenge.dashboard.movielist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.Toast
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.adapters.MoviesAdapter
import com.arctouch.codechallenge.util.extensions.setListener
import com.arctouch.codechallenge.util.helpers.PaginationHelper
import com.arctouch.codechallenge.util.helpers.mainThread
import com.arctouch.codechallenge.util.listeners.MovieClickListener
import kotlinx.android.synthetic.main.movie_list_fragment.*

class MovieListFragment: Fragment() {

    private var adapter: MoviesAdapter? = null
    var viewModel: MovieListViewModel? = null
    var searchView: SearchView? = null

    //region Lifecycle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.movie_list_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MovieListViewModel::class.java)
        setupView()
        registerObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.search, menu)
        val searchItem = menu?.findItem(R.id.btnSearch)

        searchItem?.let {
            searchView = it.actionView as SearchView
        }

        searchView?.setListener { text ->
            Log.d("SEARCH", text)
            mainThread {
                viewModel?.onSearchTextChange(text ?: "")
            }
        }

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
