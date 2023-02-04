package com.example.kinopoisk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        supportFragmentManager.beginTransaction().add(R.id.main_fragment, MainFragment(), "home").commit()
    }

    override fun onBackPressed() {
        val movieFragment = supportFragmentManager.findFragmentByTag("movie")
        if (movieFragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.main_fragment, MainFragment(), "home").commit()
        } else {
            super.onBackPressed()
        }
    }
}