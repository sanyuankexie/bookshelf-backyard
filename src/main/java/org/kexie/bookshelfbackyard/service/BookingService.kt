package org.kexie.bookshelfbackyard.service

import org.kexie.bookshelfbackyard.mapper.BookMapper
import org.kexie.bookshelfbackyard.model.Book
import org.kexie.bookshelfbackyard.model.BookExample
import org.kexie.bookshelfbackyard.model.User
import org.kexie.logUtility.common.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class BookingService {
    private val logger = Logger(this)

    @Autowired
    lateinit var bookMapper: BookMapper

    @Autowired
    lateinit var userService: UserService

    init {
        logger.log(true, "Controller ${javaClass.simpleName} was successfully initialized")
    }

    var GUID2Book = mutableMapOf<String, Book>()

    fun updateLibrary() {
        val libTemp = mutableMapOf<String, Book>()
        try {
            val example: BookExample = BookExample()
            example.createCriteria().andGuidIsNotNull()
            for (book in bookMapper.selectByExample(example))
                libTemp[book.guid] = book
            GUID2Book = libTemp
        } catch (e: Exception) {
            logger.log("failed when syncing library: $e")
        }
    }

    private fun bookWithGUID(guid: String): Book? = GUID2Book[guid]

    //    fun getBookByGUID(guid: String): Book? {
//        return try {
//            bookMapper.selectByPrimaryKey(guid)
//        } catch (e: Exception) {
//            null
//        }
//    }

    fun getAll(): MutableMap<String, Book> = GUID2Book

    /**
     * @author VisualDust
     * @since 0.0
     *      let an user borrow a book
     * @param guid: String, the guid of the book
     * @throws NullPointerException if there is no such book
     * @return succeed or not
     */
    fun User.borrow(guid: String): Boolean {
        return if (bookWithGUID(guid)!!.borrowerStuId != null) {
            logger.log("User${this.nickname} tried to borrow the book but failed.")
            false
        } else {
            val book = bookWithGUID(guid)
            book!!.borrowerStuId = this.stuId
            bookMapper.updateByPrimaryKey(book)
            logger.log(true, "User${this.nickname} borrowed book called ${book.name}(${book.guid})")
            true
        }
    }

    fun doBorrow(stuID: String, guid: String): Boolean {
        return if (userService.getUserByStuId(stuID) == null) {
            false
        } else if (bookWithGUID(guid) == null) {
            false
        } else {
            userService.getUserByStuId(stuID)!!.borrow(guid)
        }
    }

    /**
     * @author VisualDust
     * @since 0.0
     *      let an user remand a book
     * @param guid: String, the guid of the book
     * @throws NullPointerException if there is no such book
     * @return succeed or not
     *///todo
    fun User.remand(guid: String): Boolean {
        return if (bookWithGUID(guid)!!.borrowerStuId == null) {
            logger.log("User${this.nickname} tried to remand the book but failed. No one borrowed the book before.")
            false
        } else {
            val book = bookWithGUID(guid)
            if (book!!.borrowerStuId == this.stuId) {
                book.borrowerStuId = null
                bookMapper.updateByPrimaryKey(book)
                logger.log(true, "User${this.nickname} remanded the book called ${book.name}(${book.guid})")
                true
            } else {
                logger.log("User${this.nickname} tried to remand the book but failed. The user has not borrowed the book.")
                false
            }
        }
    }

    fun doRemand(stuID: String, guid: String): Boolean {
        return if (userService.getUserByStuId(stuID) == null) {
            false
        } else if (bookWithGUID(guid) == null) {
            false
        } else {
            userService.getUserByStuId(stuID)!!.remand(guid)
        }
    }
}
