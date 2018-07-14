package com.arctouch.codechallenge.util.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import com.arctouch.codechallenge.util.listeners.MovieClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.movie_item.view.*

class MoviesAdapter(private var listener: MovieClickListener): RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    var movies: List<Movie> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(itemView: View, private var listener: MovieClickListener): RecyclerView.ViewHolder(itemView) {

        var movie: Movie? = null
            set(value) {
                field = value
                value?.let { bind(it) }
            }

        init {
            itemView.setOnClickListener {
                movie?.let {
                    listener.onMovieClicked(it.id)
                }
            }
        }

        private fun bind(movie: Movie) {
            itemView.titleTextView.text = movie.title
            itemView.genresTextView.text = movie.genres?.joinToString(separator = ", ") { it.name }
            itemView.releaseDateTextView.text = movie.releaseDate

            Glide.with(itemView)
                    .load(movie.posterPath?.let { MovieImageUrlBuilder.buildPosterUrl(it) })
                    .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                    .into(itemView.posterImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return ViewHolder(view, listener)
    }

    override fun getItemCount() = movies.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.movie = movies[position]
    }

}
