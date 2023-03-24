package com.erendagistan.mymovielibrary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.erendagistan.mymovielibrary.databinding.RecyclerRowBinding

class MovieAdapter(val movieList : List<Movie>): RecyclerView.Adapter<MovieAdapter.MovieHolder>() {
    class MovieHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val binding= RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MovieHolder(binding)
    }

    override fun getItemCount(): Int {
        return movieList.size

    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        holder.binding.movieNameText.text= movieList[position].movieName
        holder.itemView.setOnClickListener {
            val action = MoviesHomePageDirections.actionMoviesHomePageToMovieDetailsPage(movieList[position].id,"old")
            Navigation.findNavController(it).navigate(action)
        }
    }
}