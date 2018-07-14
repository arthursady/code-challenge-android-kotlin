package com.arctouch.codechallenge.util.helpers

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView


class PaginationHelper(private val listener: PaginationListener?, var offset: Int = 5): RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        val layoutManager = recyclerView?.layoutManager as? LinearLayoutManager ?: return
        val previousItems = layoutManager.findFirstVisibleItemPosition() + layoutManager.childCount
        if (previousItems >= layoutManager.itemCount - (offset)) {
            listener?.onPaginationTriggered()
        }
    }

    interface PaginationListener {
        fun onPaginationTriggered()
    }
}