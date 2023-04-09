package com.telakuR.easyorder.utils

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class FcmHelper {

    companion object {
        private const val TAG = "FcmHelper"
        private const val SENDER_ID = "YOUR_SENDER_ID"

        fun sendMessage(title: String, content: String,topic: String) {
            GlobalScope.launch {
                val endpoint = "https://fcm.googleapis.com/fcm/send"
                try {
                    val url = URL(endpoint)
                    val httpsURLConnection: HttpsURLConnection = url.openConnection() as HttpsURLConnection
                    httpsURLConnection.readTimeout = 10000
                    httpsURLConnection.connectTimeout = 15000
                    httpsURLConnection.requestMethod = "POST"
                    httpsURLConnection.doInput = true
                    httpsURLConnection.doOutput = true

                    // Adding the necessary headers
                    httpsURLConnection.setRequestProperty("authorization", "key=1")
                    httpsURLConnection.setRequestProperty("Content-Type", "application/json")

                    // Creating the JSON with post params
                    val body = JSONObject()

                    val data = JSONObject()
                    data.put("title", title)
                    data.put("content", content)
                    body.put("data",data)

                    body.put("to","/topics/$topic")

                    val outputStream: OutputStream = BufferedOutputStream(httpsURLConnection.outputStream)
                    val writer = BufferedWriter(OutputStreamWriter(outputStream, "utf-8"))
                    writer.write(body.toString())
                    writer.flush()
                    writer.close()
                    outputStream.close()
                    val responseCode: Int = httpsURLConnection.responseCode
                    val responseMessage: String = httpsURLConnection.responseMessage
                    Log.d("Response:", "$responseCode $responseMessage")
                    var result = String()
                    var inputStream: InputStream? = null
                    inputStream = if (responseCode in 400..499) {
                        httpsURLConnection.errorStream
                    } else {
                        httpsURLConnection.inputStream
                    }

                    if (responseCode == 200) {
                        Log.e("Success:", "notification sent $title \n $content")
                        // The details of the user can be obtained from the result variable in JSON format
                    } else {
                        Log.e("Error", "Error Response")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
