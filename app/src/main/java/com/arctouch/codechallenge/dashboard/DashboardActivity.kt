package com.arctouch.codechallenge.dashboard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.dashboard.movielist.MovieListFragment
import com.arctouch.codechallenge.details.DetailsActivity
import com.arctouch.codechallenge.details.detailsFragment.DetailsFragment
import com.arctouch.codechallenge.util.constants.IntentConstants
import com.arctouch.codechallenge.viewmodels.MovieViewModel

class DashboardActivity: AppCompatActivity() {

    private var isPhone: Boolean = false
    var viewModel: MovieViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)
        setupScreenSize()
        viewModel = ViewModelProviders.of(this).get(MovieViewModel::class.java)
        setupView()
        registerObservers()
    }

    //region Private

    private fun setupView() {
        supportActionBar?.setTitle(R.string.upcoming)
        setupMovieListFragment()
    }

    private fun setupMovieListFragment() {
        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainerFrameLayout, MovieListFragment()).commit()
        }
    }

    private fun updateDetails(id: Long) {
        val bundle = Bundle()
        bundle.putLong(IntentConstants.MOVIE_ID, id)
        val fragment = DetailsFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerFrameLayoutDetails, fragment).commit()
    }

    private fun registerObservers() {
        viewModel?.detailsTransitionLiveData?.observe(this, Observer {
            it?.let {
                if (isPhone) {
                    val intent = Intent(this, DetailsActivity::class.java)
                    intent.putExtra(IntentConstants.MOVIE_ID, it)
                    startActivity(intent)
                } else {
                    updateDetails(it)
                }
            }
        })
    }

    private fun setupScreenSize() {
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = resources.displayMetrics.density
        val dpHeight = outMetrics.heightPixels / density
        val dpWidth = outMetrics.widthPixels / density
        isPhone = dpHeight < 600 || dpWidth < 600
    }

    //endregion
}
