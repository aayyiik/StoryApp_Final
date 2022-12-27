package com.ari.submission.storyapp.ui.home

import android.annotation.SuppressLint
import androidx.activity.viewModels
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ari.submission.storyapp.R
import com.ari.submission.storyapp.databinding.ActivityMainBinding
import com.ari.submission.storyapp.loading.LoadingStateAdapter
import com.ari.submission.storyapp.preferences.SharedPreferences
import com.ari.submission.storyapp.ui.addstory.AddStoryActivity
import com.ari.submission.storyapp.ui.login.LoginActivity
import com.ari.submission.storyapp.ui.maps.MapsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoriesAdapter
    private lateinit var sph: SharedPreferences

    private var storyName: ArrayList<String>? = null
    private var storyDesc: ArrayList<String>? = null
    private var storyLat: ArrayList<Float>? = null
    private var storyLong: ArrayList<Float>? = null


    private val viewModel by viewModels<MainViewModel>{
        MainViewModel.Factory(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sph = SharedPreferences(this)

        showStories()
        getNewStories()


    }


    private fun showStories(){
        sph.setStatusLogin(true)
        val token = sph.getUserToken()
        viewModel.allStory.observe(this){
            adapter.submitData(lifecycle, it)

        }
        storyName = ArrayList()
        storyDesc = ArrayList()
        storyLat = ArrayList()
        storyLong = ArrayList()
        viewModel.getAllStories("Bearer $token")
        viewModel.getStoriesPaging().observe(this) {
            binding.progressBar.visibility = View.VISIBLE
            if (it != null) {
                binding.progressBar.visibility = View.GONE
                for (i in it.indices) {
                    storyName?.add(it[i].name.toString())
                    storyDesc?.add(it[i].description.toString())
                    storyLat?.add(it[i].lat.toString().toFloat())
                    storyLong?.add(it[i].lon.toString().toFloat())

                }
            }else{
                binding.progressBar.visibility = View.VISIBLE

            }


        }
        listAction()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun listAction(){
        adapter = StoriesAdapter()
        adapter.notifyDataSetChanged()

        binding.apply {
            rvStories.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStories.setHasFixedSize(true)
            rvStories.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter{
                    adapter.retry()
                }
            )
        }


    }

    private fun getNewStories() {
        binding.apply {
            if (intent != null) {
                val isNewStory = intent.extras?.getBoolean(SUCCESS_UPLOAD_STORY)
                if (isNewStory != null && isNewStory) {
                    showStories()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bottom, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btn_logout->{
                val builder = AlertDialog.Builder(this)
                with(builder)
                {
                    setTitle("Log Out")
                    setMessage("Are you sure to log out?")
                    setPositiveButton("Yes") { dialogInterface, which ->
                        sph.clearUserLogin()
                        sph.clearUserToken()
                        sph.setStatusLogin(false)
                        Intent(this@MainActivity, LoginActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }

                    }
                    setNegativeButton("Cancel"){ dialogInterface, which ->
                        Toast.makeText(this@MainActivity, "Okay! Have a nice use StoryApp", Toast.LENGTH_SHORT).show()
                    }
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(true)
                    alertDialog.show()
                }

            }
            R.id.btn_add_photo->{
                Intent(this, AddStoryActivity::class.java).apply {
                    startActivity(this)
                }
            }
            R.id.btn_maps->{
                Intent(this, MapsActivity::class.java).apply{
                    startActivity(this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }



    companion object {
        const val SUCCESS_UPLOAD_STORY = "success upload story"
    }
}


