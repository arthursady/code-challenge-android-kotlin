package com.arctouch.codechallenge.details

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.details.detailsFragment.DetailsFragment
import com.arctouch.codechallenge.util.constants.IntentConstants

class DetailsActivity: AppCompatActivity() {

    //region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_activity)
        val selectedMovieId = intent.extras.getLong(IntentConstants.MOVIE_ID)
        setupDetailsFragment(selectedMovieId)
    }

    //endregion

    //region Private

    private fun setupDetailsFragment(id: Long) {
        val bundle = Bundle()
        bundle.putLong(IntentConstants.MOVIE_ID, id)
        val fragment = DetailsFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerFrameLayout, fragment).commit()
    }

    //endregion
}
