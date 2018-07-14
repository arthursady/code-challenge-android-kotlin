package com.arctouch.codechallenge.dashboard

import android.os.Bundle
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.base.BaseActivity
import com.arctouch.codechallenge.dashboard.movielist.MovieListFragment

class DashboardActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)
        setupView()

    }

    //region Private

    private fun setupView() {
        setupMovieListFragment()
    }

    private fun setupMovieListFragment() {
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerFrameLayout, MovieListFragment()).commit()
    }

    //endregion
}
