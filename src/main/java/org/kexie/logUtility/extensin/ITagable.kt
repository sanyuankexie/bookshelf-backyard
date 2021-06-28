package org.kexie.logUtility.extensin

interface ITagable<KT, VT> : Iterator<MutableMap.MutableEntry<KT, VT>> {
    //    val dictionary: HashMap<KT, VT>
    fun searchValue(key: KT): VT?
    fun put(key: KT, value: VT): VT?
    fun containsKey(key: KT): Boolean
    fun containsValue(value: VT): Boolean
    fun remove(key: KT): VT?
}