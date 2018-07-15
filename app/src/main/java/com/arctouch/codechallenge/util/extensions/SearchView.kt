package com.arctouch.codechallenge.util.extensions

import android.support.v7.widget.SearchView
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView
import rx.Subscription
import java.util.concurrent.TimeUnit

fun SearchView.setListener(callback: (String?) -> Unit): Subscription {
    return RxSearchView.queryTextChanges(this)
            .debounce(250, TimeUnit.MILLISECONDS)
            .skip(1)    //skips the first time, when we subscribe to the observer
            .map { it.toString() }
            .subscribe({ text ->
                callback(text)
            })
}