package com.example.top10downloads

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
//import java.io.BufferedReader
import java.io.IOException
//import java.io.InputStream
//import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG,"onCreate called.")

        val downloadData = DownloadData()
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        Log.d(TAG,"onCreate ends.")
    }

    companion object {
        private class DownloadData : AsyncTask<String, Void, String>() {
            private val TAG = "DownloadData"
            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                Log.d(TAG,"onPostExecute: parameter is $result")
            }

            override fun doInBackground(vararg url: String?): String {
                Log.d(TAG,"doInBackground:starts with ${url[0]}")

                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()){
                    Log.e(TAG,"doInBackground: Error downloading  rssFeed.")
                }
                return rssFeed
            }

            //downloadXML function
            private fun downloadXML(urlpath:String?):String {

                val xmlResult = StringBuilder()

                try {
                    val url = URL(urlpath)
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    val response = connection.responseCode
                    Log.d(TAG, "downloadXML: responceCode is $response")


                    //kotlin way of doing things
                    connection.inputStream.buffered().reader().use{xmlResult.append(it.readText()) }

                    Log.d(TAG, "Received ${xmlResult.length} bytes")
                    return xmlResult.toString()

                }catch (e: Exception){
                    val errorMessage:String = when (e){
                        is MalformedURLException -> "downloadXML :invalid URL ${e.message}"
                        is IOException -> "downloadXML :IO exception reading data : ${e.message}"
                        is SecurityException -> { e.printStackTrace()
                            "downloadXML : Security Exception , Need permission : ${e.message}"}
                        else -> "downloadXML :unknown error: ${e.message}"
                    }
                    Log.d(TAG,errorMessage)
                }

                return "" // if it get till here means there is some error hence return a empty string.
            }
        }
    }


}


