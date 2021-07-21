package com.algebra.apirequests

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import com.algebra.apirequests.model.Joke
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import org.json.JSONException
import org.json.JSONObject
import android.text.TextUtils
import android.widget.TextView

class FetchJokes( val tv : TextView ) : AsyncTask<String, Void, List<Joke>>() {

    val TAG = "FetchJokes"

    override fun doInBackground( vararg urls: String? ): List< Joke >? {

        if ( urls.isEmpty( ) ) {
            return null
        }
        val stringUrl = urls[0] ?: ""
        val url = createURL( stringUrl )
        val response = makeHTTPRequest( url )
        return getJokesFromResponse( response )
    }

    private fun createURL( url: String ) : URL? {
        return try {
            URL( url )
        } catch ( e: MalformedURLException ) {
            e.printStackTrace( )
            null
        }
    }

    private fun makeHTTPRequest(url: URL?): String {
        var jsonResponse = ""
        var httpURLConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null

        try {
            httpURLConnection = url?.openConnection( ) as HttpURLConnection
            httpURLConnection.readTimeout    = 2000
            httpURLConnection.connectTimeout = 2000
            httpURLConnection.requestMethod  = "GET"
            httpURLConnection.connect()
        } catch ( e: IOException ) {
            e.printStackTrace( )
        }


        println( httpURLConnection?.responseCode )
        try {
            if ( httpURLConnection?.responseCode==200 ) {
                inputStream  = httpURLConnection.inputStream
                jsonResponse = readFromStream( inputStream )
            } else {
                Log.e( TAG, "Error response code:  ${httpURLConnection?.responseCode}" )
            }
        } catch ( e : IOException ) {
            e.printStackTrace( )
        } finally {
            httpURLConnection?.disconnect( )
            inputStream?.close( )
        }
        return jsonResponse
    }

    private fun readFromStream( inputStream: InputStream? ) : String {
        val output = StringBuilder( )

        if ( inputStream != null ) {
            val reader = BufferedReader( InputStreamReader( inputStream, Charset.forName("UTF-8") ) )
            var line = reader.readLine( )
            while ( line!=null ) {
                output.append( line )
                line = reader.readLine( )
            }
        }
        return output.toString( )
    }

    private fun getJokesFromResponse( response: String ): List<Joke>? {

        val l: MutableList< Joke > = ArrayList( )
        // If the JSON string is empty or null, then return early.
        if ( TextUtils.isEmpty( response ) )
            return null

        try {
            val baseJsonResponse = JSONObject( response )
            val jokesArray = baseJsonResponse.getJSONArray( "value" )
            for ( i in 0 until jokesArray.length( ) ) {
                val jokeJSON = jokesArray.getJSONObject( i )
                val jokeStr  = jokeJSON.getString( "joke" )
                val joke     = Joke( i, jokeStr.replace( "&quot;", "\"") )
                l.add( joke )
            }
        } catch ( e: JSONException ) {
            e.printStackTrace( )
        }

        return l
    }

    override fun onPostExecute( result: List< Joke >? ) {

        val builder = StringBuilder( ).apply {
            result?.forEach{ append( "${it.jokeContent} \n\n" ) }
        }

        tv.text = builder.toString( )
    }

}