package com.ari.submission.storyapp.ui.detailstories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.ari.submission.storyapp.R
import com.ari.submission.storyapp.data.Stories
import com.ari.submission.storyapp.databinding.ActivityDetailStoriesBinding
import com.bumptech.glide.Glide

class DetailStoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoriesBinding

    companion object {
        const val EXTRA_NAME = "extra_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        geDetailItem()

    }


    private fun geDetailItem(){
        val detail = intent.getParcelableExtra<Stories>(EXTRA_NAME) as Stories


        supportActionBar!!.title = detail.name
        binding.ivDetailName.text = detail.name
        Glide.with(this)
            .load(detail.photoUrl)
            .error(R.drawable.ic_launcher_background)
            .into(binding.ivDetailPhoto)

        binding.ivDetailDescription.text = detail.description
        binding.tvLatitude.text = detail.lat.toString()
        binding.tvLongitude.text = detail.lon.toString()


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