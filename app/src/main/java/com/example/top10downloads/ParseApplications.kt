package com.example.top10downloads

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.lang.Exception


// this is the class used to parse i.e. extract the useful data from the XML received
class ParseApplications {
    private val tag = "ParseApplications"
    // a list  of type to store all the entry data
     val applications = ArrayList<FeedEntry>()

    fun parse(xmlData:String):Boolean{
        Log.d(tag,"parse: parse called with $xmlData")
        var status = true            //returned by the function if the data is parsed correctly it will return true
        var inEntry = false          // this tell whether we are still in the enrty block
        var textValue = ""           // this stores the actual data as a string


        // so android has a build in class for parsing the data, we will be using them.
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT){
                val tagName = xpp.name?.toLowerCase()

                when(eventType){
                    XmlPullParser.START_TAG ->{
                        Log.d(tag,"parse: Starting tag for $tagName")
                        if(tagName == "entry"){
                            inEntry = true
                        }
                    }

                    XmlPullParser.TEXT ->textValue = xpp.text

                    XmlPullParser.END_TAG-> {
                        Log.d(tag, "Ending Tag for $tagName")
                        if (inEntry){
                            when(tagName){
                                "entry" ->{
                                    applications.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry()  //creating a new object for next record
                                }
                                "name" -> currentRecord.name = textValue
                                "artist" ->currentRecord.artist = textValue
                                "releasedate" -> currentRecord.releaseDate = textValue
                                "summary" -> currentRecord.summary = textValue
                                "image" -> currentRecord.imageURL = textValue
                            }
                        }
                    }
                }
                //nothing else to do
                eventType = xpp.next()
            }
            for(app in applications){
                Log.d(tag,"*******************")
                Log.d(tag,app.toString())
            }

        }catch (e:Exception){
            e.printStackTrace()
            status = false
        }

     return status
    }
}