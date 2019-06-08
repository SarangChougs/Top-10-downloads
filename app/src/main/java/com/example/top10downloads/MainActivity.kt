package com.example.top10downloads

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
//import java.io.InputStream
import java.io.InputStreamReader
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
                    val connection :HttpURLConnection =  url.openConnection() as HttpURLConnection
                    val response = connection.responseCode
                    Log.d(TAG,"downloadXML: responceCode is $response")

//            val inputStream = connection.inputStream
//            val inputStreamReader = InputStreamReader(inputStream)
//            val reader = BufferedReader(inputStreamReader)

                    // creating the reader which will read the stream of characters and store them in a buffer
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))

                    //appending the characters received in the reader
                    val inputBuffer = CharArray(500)
                    var charRead = 0
                    while (charRead >= 0){
                        charRead = reader.read(inputBuffer)
                        if(charRead > 0){
                            xmlResult.append(String(inputBuffer,0,charRead))
                        }
                    }
                    reader.close()

                    Log.d(TAG,"Received ${xmlResult.length} bytes")
                    return xmlResult.toString()

                }catch (e:MalformedURLException){
                    Log.e(TAG,"downloadXML :invalid URL ${e.message}")
                }catch (e:IOException){
                    Log.e(TAG,"downloadXML :IO exception reading data : ${e.message}")
                }catch (e:SecurityException){
                    Log.e(TAG, "downloadXML : Security Exception , Need permission : ${e.message}")
                }catch (e: Exception){
                    Log.e(TAG, "downloadXML :unknown error: ${e.message}")
                }

                return "" // if it get till here means there is some error hence return a empty string.
            }
        }
    }


}


