package com.telakuR.easyorder.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.telakuR.easyorder.R
import java.io.File

class MyFileProvider : FileProvider(R.xml.file_paths) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "easy_order_profile_${System.currentTimeMillis()}",
                ".jpg",
                directory,
            )
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}