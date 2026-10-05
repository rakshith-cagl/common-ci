package com.iexceed.utils.localstorage

import android.content.Context
import android.os.ParcelFileDescriptor
import com.iexceed.common.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.InputStream

object FileAccessHelper {
    fun copyFileInto(inputStream: InputStream, outFile: File) {
        val out = FileOutputStream(outFile)
        FileUtils.copyFile(inputStream, out)
        inputStream.close()
        out.flush()
        out.close()
    }

    fun copyFile(lInputStream: InputStream, file: File) {
        val lOutStream = FileOutputStream(file)
        FileUtils.copy(lInputStream, lOutStream)
        lInputStream.close()
        lOutStream.flush()
        lOutStream.close()
    }

    fun getFileOutPutStream(file : File)  = FileOutputStream(file)
    fun getWriter(directory: File) = FileWriter(directory, false)
    fun getExternalFileDirFilePath(context: Context, filepath: String) =
        context.getExternalFilesDir(null)!!.absolutePath + File.separator + filepath
    fun getExternalFileDirFile(context: Context) =
        context.getExternalFilesDir(null)?.absolutePath.toString()
    fun getExternalFileDirFileWithType(context: Context): String =
        context.getExternalFilesDir("")!!.path

    fun getFileDescriptor(destination: ParcelFileDescriptor?) =
        FileOutputStream(destination?.fileDescriptor)
}