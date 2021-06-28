package org.kexie.logUtility.extensin

import kotlin.collections.MutableMap.*

class Tagable<KT, VT> : ITagable<KT, VT> {
    val dictionary: HashMap<KT, VT> = hashMapOf()
    override fun searchValue(key: KT): VT? = dictionary.getValue(key)
    override fun put(key: KT, value: VT): VT? = dictionary.put(key, value)
    fun putIfAbsent(key: KT, value: VT) = dictionary.putIfAbsent(key, value)
    override fun containsKey(key: KT): Boolean = dictionary.containsKey(key)
    override fun containsValue(value: VT): Boolean = dictionary.containsValue(value)
    override fun remove(key: KT): VT? = dictionary.remove(key)
    override fun hasNext(): Boolean = dictionary.iterator().hasNext()
    override fun next(): MutableEntry<KT, VT> = dictionary.iterator().next()
}