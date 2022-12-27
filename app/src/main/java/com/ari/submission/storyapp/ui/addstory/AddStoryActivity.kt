package com.ari.submission.storyapp.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.ari.submission.storyapp.databinding.ActivityAddStoryBinding
import com.ari.submission.storyapp.preferences.SharedPreferences
import com.ari.submission.storyapp.ui.home.MainActivity
import com.ari.submission.storyapp.utils.MyResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String

    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    private var getFile: File? = null
    private lateinit var sph: SharedPreferences

    private val addViewModel by viewModels<AddViewModel>{
        AddViewModel.Factory(this)
    }

    companion object{
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sph = SharedPreferences(this)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.progressBar.visibility = View.GONE
        binding.btnOpenCamera.setOnClickListener {
            startCamera()
        }

        binding.btnOpenGalery.setOnClickListener {
            startGallery()
        }


        binding.buttonAdd.setOnClickListener {
            uploadImage()
        }

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)



    }


    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            getFile = myFile

            val result = BitmapFactory.decodeFile( myFile.path)

            binding.imgView.setImageBitmap(result)
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
         && ActivityCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),100)
            return
        } else {

            //mengambil latitude dan longitude
           val location = fusedLocationProvider.lastLocation
            location.addOnSuccessListener {
                if(it!=null){
                    val textLatitude = "${it.latitude}"
                    val textLongitude = "${it.longitude}"
                    binding.tvLatitude.text = textLatitude
                    binding.tvLongitude.text = textLongitude
                }
            }
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.ari.submission.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
     

            getFile = myFile
            binding.imgView.setImageURI(selectedImg)
            getMyLocation()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile

            )
            uploadImageToServer(imageMultipart)

        } else {
            Toast.makeText(this@AddStoryActivity, "Silakan masukkan berkas gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToServer(img: MultipartBody.Part){
        val token = "Bearer ${sph.getUserToken()}"
        val description = binding.edAddDescription.text.trim().toString().toRequestBody("text/plain".toMediaType())
        val latitude = binding.tvLatitude.text.trim().toString().toFloat()
        val longitude = binding.tvLongitude.text.trim().toString().toFloat()

        addViewModel.addStories(token, description, img, latitude, longitude).observe(this@AddStoryActivity){
            if(it != null){
                when(it){
                    is MyResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is MyResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@AddStoryActivity, "Berhasil", Toast.LENGTH_SHORT).show()
                        Intent(this@AddStoryActivity,
                            MainActivity::class.java).also { intent ->
                            intent.putExtra(MainActivity.SUCCESS_UPLOAD_STORY, true)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            }
                    }
                    is MyResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}