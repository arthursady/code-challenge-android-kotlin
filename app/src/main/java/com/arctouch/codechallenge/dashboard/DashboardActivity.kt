package com.arctouch.codechallenge.dashboard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.dashboard.movielist.MovieListFragment

class DashboardActivity: AppCompatActivity() {

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
        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainerFrameLayout, MovieListFragment()).commit()
        }
    }

    //endregion
}
