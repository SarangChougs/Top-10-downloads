package com.example.top10downloads

import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import kotlin.properties.Delegates

//class to parse the XML data
class FeedEntry{
    var name:String =""
    var artist:String =""
    var releaseDate:String =""
    var summary:String =""
    var imageURL:String =""

    override fun toString(): String {
        return """
            name = $name
            artist = $artist
            releaseDate = $releaseDate
            imageURL = $imageURL
        """.trimIndent()
    }
}


class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"
    private val downloadData by lazy { DownloadData(this,xmlListView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(tag,"onCreate called.")

        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        Log.d(tag,"onCreate ends.")
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData.cancel(true)
    }

    companion object {
        private class DownloadData(context:Context,listView :ListView) : AsyncTask<String, Void, String>() {
            private val tag = "DownloadData"
            var propContext: Context by Delegates.notNull()
            var propListView : ListView by Delegates.notNull()

            init {
                propContext = context
                propListView = listView
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
//                Log.d(tag, "onPostExecute: parameter is $result")
                val parseApplications = ParseApplications()
                parseApplications.parse(result)

                //creating the adapter for the ListView
//                val adapter = ArrayAdapter<FeedEntry>(propContext,R.layout.list_item,parseApplications.applications)
//                propListView.adapter = adapter

                //our custom adapter
                val feedAdapter = FeedAdapter(propContext,R.layout.list_record,parseApplications.applications)
                propListView.adapter = feedAdapter
            }

            override fun doInBackground(vararg url: String?): String {
                Log.d(tag, "doInBackground:starts with ${url[0]}")

                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(tag, "doInBackground: Error downloading  rssFeed.")
                }
                return rssFeed
            }

            //downloading the XML from net
            private fun downloadXML(urlPath: String?): String {
                return URL(urlPath).readText()
            }
        }
    }


}


