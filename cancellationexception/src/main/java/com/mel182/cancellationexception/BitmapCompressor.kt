package com.mel182.cancellationexception

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

class BitmapCompressor(
    private val context: Context
) {

    suspend fun compressImage(
        contentUri: Uri,
        compressionThreshold: Long
    ): Bitmap? {
        return withContext(Dispatchers.IO) {
            val inputBytes = context
                .contentResolver
                .openInputStream(contentUri)?.use { inputStream ->
                    inputStream.readBytes()
                } ?: return@withContext null

            //yield()
            // for the cancellation it will behave the same as 'ensureActive()',
            // On the crucial difference it is a suspendable, so it might enter the pause state.
            // It does not have to but it might and since it is a suspend fun in case a coroutine
            // is cancelled it give them a chances to finish their code if necessary.
            // While in the case of 'ensureActive()' that is not the case.

            ensureActive() // Check if coroutine is still active otherwise jump out of the coroutine

            withContext(Dispatchers.Default) {
                val bitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.size)
                ensureActive() // Check if coroutine is still active otherwise jump out of the coroutine
                var outputBytes: ByteArray
                var quality = 100
                do {
                    ByteArrayOutputStream().use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                        outputBytes = outputStream.toByteArray()
                        quality -= (quality * 0.1).roundToInt()
                    }
                } while(outputBytes.size > compressionThreshold && quality > 5)
                ensureActive() // Check if coroutine is still active otherwise jump out of the coroutine
                BitmapFactory.decodeByteArray(outputBytes, 0, outputBytes.size).also {
                    Log.i("TAG35","Decoded byte array!")
                }
            }
        }
    }
}