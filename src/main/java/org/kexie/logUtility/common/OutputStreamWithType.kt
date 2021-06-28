package org.kexie.logUtility.common

import java.io.OutputStream

class OutStreamWithType(var stream: OutputStream, var type: Logger.LogType) {
    var tags = mutableListOf<String>()
    fun addTag(tag: String): OutStreamWithType {
        tags.add(tag)
        return this
    }

    fun removeTag(tag: String) = tags.remove(tag)
    fun containsTag(tag: String): Boolean = tags.contains(tag)
}