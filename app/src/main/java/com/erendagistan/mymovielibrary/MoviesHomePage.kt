package com.erendagistan.mymovielibrary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.erendagistan.mymovielibrary.databinding.FragmentMoviesHomePageBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MoviesHomePage : Fragment() {
    private lateinit var movieAdapter: MovieAdapter
    private val mDisposable = CompositeDisposable()
    private lateinit var movieDao : MovieDao
    private lateinit var movieDatabase : MovieDatabase
    private var _binding : FragmentMoviesHomePageBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        movieDatabase= Room.databaseBuilder(requireContext(),MovieDatabase::class.java,"Movie").build()
        movieDao= movieDatabase.movieDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMoviesHomePageBinding.inflate(layoutInflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFromSql()
    }

    private fun getFromSql() {
        mDisposable.add(movieDao.getMovieWithNameAndId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse))
    }

    private fun handleResponse(movieList : List<Movie>){
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        movieAdapter= MovieAdapter(movieList)
        binding.recyclerView.adapter=movieAdapter
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}