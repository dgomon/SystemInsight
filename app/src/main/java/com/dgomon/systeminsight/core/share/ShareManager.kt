package com.dgomon.systeminsight.core.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.dgomon.systeminsight.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareManager @Inject constructor(
    @ApplicationContext private val context: Context) {

    fun shareAsFile(text: String, filename: String) {
        val file = File(context.cacheDir, filename)

        file.writeText(text)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, context.getString(R.string.share_file))
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
