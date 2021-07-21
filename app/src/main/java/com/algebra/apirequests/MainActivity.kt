package com.algebra.apirequests

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

private const val JOKES_ENDPOINT = "https://api.icndb.com/jokes/random/3"

class MainActivity : AppCompatActivity( ) {

    private lateinit var bJokes    : Button
    lateinit var         tvResults : TextView

    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_main )
        initWidgets( )
        setupListeners( )
    }

    private fun initWidgets( ) {
        bJokes = findViewById( R.id.bJoke )
        tvResults = findViewById ( R.id.tvResult )
    }

    private fun setupListeners( ) {
        bJokes.setOnClickListener {
            FetchJokes( tvResults ).execute( JOKES_ENDPOINT )
        }
    }

}
