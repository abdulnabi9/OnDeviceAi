package com.abdul.aiondevice

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ModelDownloader {

    private const val MODEL_NAME = "gemma3-1b-it-int4.litertlm"


fun modelFile(context: Context) = File(context.filesDir,MODEL_NAME)


    suspend fun ensureModel(context: Context): String = withContext(Dispatchers.IO){

        val target = modelFile(context)


        if (!target.exists() || target.length() == 0L){
            context.assets.open("gemma3-1b-it-int4.litertlm").use {
                input ->
                target.outputStream().use { output ->

                    input.copyTo(output)
                }
            }
        }
        target.absoluteFile.toString()


    }



 
}