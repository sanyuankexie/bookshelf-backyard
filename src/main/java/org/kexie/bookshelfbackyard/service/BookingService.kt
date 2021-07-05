package org.kexie.bookshelfbackyard.service

import org.kexie.bookshelfbackyard.mapper.BookMapper
import org.kexie.bookshelfbackyard.model.Book
import org.kexie.logUtility.common.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.lang.Exception

@Service
class BookingService {
    private val logger = Logger(this)

    @Autowired
    lateinit var bookMapper: BookMapper

    init {
        logger.log(true, "Controller ${javaClass.simpleName} was successfully initialized")
    }

    val GUID2Book  = mutableMapOf<String,Book>()

    fun updateLibrary(){

    }

//    fun getBookByGUID(guid: String): Book? {
//        return try {
//            bookMapper.selectByPrimaryKey(guid)
//        } catch (e: Exception) {
//            null
//        }
//    }


}
