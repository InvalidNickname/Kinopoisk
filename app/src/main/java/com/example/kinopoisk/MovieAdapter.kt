package com.example.kinopoisk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapter(private val movies: List<Movie>, private val listener: OnItemClickListener) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(id: Int)
    }

    class MovieViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        val moviePoster: ImageView = binding.findViewById(R.id.moviePoster)
        val movieName: TextView = binding.findViewById(R.id.movieName)
        val movieGenreAndDate: TextView = binding.findViewById(R.id.movieGenreAndDate)
    }

    override fun getItemCount(): Int = movies.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.movieName.text = movie.name
        holder.movieGenreAndDate.text = String.format(holder.itemView.context.getString(R.string.genre_date), movie.genre, movie.year)
        Glide.with(holder.itemView.context).load(movie.posterURL).into(holder.moviePoster)
        holder.itemView.setOnClickListener {
            listener.onItemClick(movie.id)
        }
    }
}