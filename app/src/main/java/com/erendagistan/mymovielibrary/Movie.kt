package com.erendagistan.mymovielibrary

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
@Entity
class Movie (
    @ColumnInfo(name = "movie_name")
    val movieName : String,
    @ColumnInfo(name = "movie_date")
    val movieDate:String?,
    @ColumnInfo(name = "movie_comment")
    val movieComment : String?,
    @ColumnInfo(name = "movie_image")
    val movieImage: ByteArray?)
{
    @PrimaryKey (autoGenerate = true)
    var id : Int =0
}