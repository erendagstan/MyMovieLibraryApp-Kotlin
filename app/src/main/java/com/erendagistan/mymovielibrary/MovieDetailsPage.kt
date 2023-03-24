package com.erendagistan.mymovielibrary

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.erendagistan.mymovielibrary.databinding.FragmentMovieDetailsPageBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException

class MovieDetailsPage : Fragment() {
    var selectedPicture : Uri? = null
    var selectedBitmap : Bitmap? = null
    private var _binding : FragmentMovieDetailsPageBinding?=null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var movieDatabase : MovieDatabase
    private lateinit var movieDao : MovieDao
    private var mDisposable = CompositeDisposable()
    var movieFromMain : Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

        movieDatabase = Room.databaseBuilder(requireContext(),MovieDatabase::class.java,"Movie").build()

        movieDao = movieDatabase.movieDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMovieDetailsPageBinding.inflate(layoutInflater,container,false)
        val view = _binding!!.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val info = MovieDetailsPageArgs.fromBundle(it).info
            if(info.equals("new")){
                //new
                binding.etMovieName.setText("")
                binding.etDate.setText("")
                binding.etComment.setText("")
                binding.btnSave.visibility=View.VISIBLE
                binding.btnDelete.visibility=View.GONE

                val selectedImageBackground = BitmapFactory.decodeResource(context?.resources,
                    R.drawable.select_image)

                binding.movieImage.setImageBitmap(selectedImageBackground)
            }
            else{
                //old
                binding.btnSave.visibility= View.GONE
                binding.btnDelete.visibility=View.VISIBLE

                val selectedId = MovieDetailsPageArgs.fromBundle(it).id
                mDisposable.add(movieDao.getMovieById(selectedId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseWithOldMovie))
            }
        }


        binding.movieImage.setOnClickListener {
            activity?.let {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    if(ContextCompat.checkSelfPermission(requireActivity().applicationContext,android.Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
                        //requestPermission
                        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.READ_MEDIA_IMAGES)){
                            Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE)
                                .setAction("Give Permission") {
                                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                                }.show()
                        } else{
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    }
                    else{
                        val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        activityResultLauncher.launch(intentToGallery)
                    }
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val movieName = binding.etMovieName.text.toString()
            val movieDate = binding.etDate.text.toString()
            val comment = binding.etComment.text.toString()

            if(selectedBitmap!=null){
                val smallBitmap = makeSmallerBitmap(selectedBitmap!!,480)
                val outputStream = ByteArrayOutputStream()
                smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                val byteArray = outputStream.toByteArray()

                val movie = Movie(movieName,movieDate, comment,byteArray)
                println(movie.movieName)
                println(movie.movieDate)

                mDisposable.add(movieDao.insert(movie)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse))
            }

        }

        binding.btnDelete.setOnClickListener {
            movieFromMain?.let {
                mDisposable.add(movieDao.delete(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse))
            }
        }
    }

    private fun handleResponseWithOldMovie(movie: Movie){
        movieFromMain=movie
        binding.etMovieName.setText(movie.movieName)
        binding.etDate.setText(movie.movieDate)
        binding.etComment.setText(movie.movieComment)

        movie.movieImage?.let {
            val bitmap = BitmapFactory.decodeByteArray(it,0,it.size)
            binding.movieImage.setImageBitmap(bitmap)
        }
    }

    private fun handleResponse(){
        //go to main page
        val action = MovieDetailsPageDirections.actionMovieDetailsPageToMoviesHomePage()
        Navigation.findNavController(requireView()).navigate(action)
    }


    fun makeSmallerBitmap(image: Bitmap,maximumSize:Int): Bitmap{
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble()/height.toDouble()
        if(bitmapRatio>1){
            width=maximumSize  // width = 480
            val scaledHeight = width/bitmapRatio
            height= scaledHeight.toInt()
        }
        else{
            height=maximumSize //height = 480
            val scaledWidth = height*bitmapRatio
            width=scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height,false)
    }


    private fun registerLauncher(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if(result.resultCode == AppCompatActivity.RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult!=null){
                    selectedPicture=intentFromResult.data
                    try {
                        if(Build.VERSION.SDK_INT>=28){
                            val source = ImageDecoder.createSource(
                                requireActivity().contentResolver,selectedPicture!!
                            )
                            selectedBitmap=ImageDecoder.decodeBitmap(source)
                            binding.movieImage.setImageBitmap(selectedBitmap)
                        }
                        else{
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                selectedPicture
                            )
                            binding.movieImage.setImageBitmap(selectedBitmap)
                        }
                    } catch (e:IOException){
                        e.printStackTrace()
                    }
                }
            }

        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if(result){
                //permission granted
                val intentToGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else{
                //permission denied
                Toast.makeText(requireContext(),"Permission needed!",Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


}


