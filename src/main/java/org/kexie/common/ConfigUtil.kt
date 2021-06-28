package org.kexie.common

import java.io.File
import java.lang.Exception

object ConfigUtil {
    var delimiter = "="
    var annotater = "#"

    fun fromFile(filePath: String, ignoreError: Boolean = true) = fromFile(File(filePath), ignoreError)
    fun fromFile(file: File, ignoreError: Boolean = true): MutableMap<String, String> {
        val configs = mutableMapOf<String, String>()
        for (item in RawUtil.readFromFile(file)) {
            if (item.startsWith('#')) continue
            val kv = item.split(delimiter.toRegex(), 2)
            if (2 > kv.size)
                if (ignoreError) continue
                else throw Exception("raw error : missing delimiter")
            configs[kv[0]] = kv[1]
        }
        return configs
    }

    fun toFile(file: File, configs: MutableMap<String, String>) {
        val list = mutableListOf<String>()
        for (item in configs)
            list.add("${item.key}=${item.value}")
        RawUtil.writeToFile(file, list)
    }
}