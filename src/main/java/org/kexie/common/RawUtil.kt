package org.kexie.common

import org.kexie.logUtility.common.Logger
import java.io.*
import java.nio.charset.Charset

object RawUtil {
    private val logger = Logger(this)
    fun readFromFile(filePath: String) = readFromFile(File(filePath))
    fun readFromFile(file: File, charset: Charset = Charsets.UTF_8): MutableList<String> {
        return try {
            var raws = mutableListOf<String>()
            val inputStream = FileInputStream(file)
            val reader = BufferedReader(InputStreamReader(inputStream, charset))
            do {
                val str: String? = reader.readLine()
                if (null != str) raws.add(str)
            } while (null != str)
            raws
        } catch (e: Exception) {
            throw e
        }
    }

    fun writeToFile(file: File, raws: MutableList<String>, charset: Charset=Charsets.UTF_8) {
        try {
            var outStream = FileOutputStream(file, true)
            val bufferedWriter = BufferedWriter(OutputStreamWriter(outStream, charset))
            for (raw in raws){
                bufferedWriter.write("$raw\n")
            }
            bufferedWriter.close()
        } catch (e: Exception) {
            throw e
        }
    }
}
