package com.erendagistan.mymovielibrary

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable

@Dao
interface MovieDao {
    @Query("SELECT movie_name,id FROM Movie")
    fun getMovieWithNameAndId() : Flowable<List<Movie>>

    @Query("SELECT * FROM Movie WHERE id= :id")
    fun getMovieById(id:Int) : Flowable<Movie>

    @Insert
    fun insert(movie: Movie) : Completable

    @Delete
    fun delete(movie: Movie) : Completable
}