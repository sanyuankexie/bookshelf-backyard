package org.kexie.bookshelfbackyard.service

import org.kexie.bookshelfbackyard.mapper.BookMapper
import org.kexie.bookshelfbackyard.model.Book
import org.kexie.bookshelfbackyard.model.BookExample
import org.kexie.bookshelfbackyard.model.User
import org.kexie.logUtility.common.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BookingService {
    private val logger = Logger(this)

    @Autowired
    lateinit var bookMapper: BookMapper

    @Autowired
    lateinit var userService: UserService

    init {
        updateLibrary()
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
    private fun User.borrow(guid: String) =
        if (bookWithGUID(guid)!!.borrowerStuId != null) {
            logger.log("User${this.nickname} tried to borrow the book but failed.")
            2 // someone has already borrowed the book
        } else {
            val book = bookWithGUID(guid)
            book!!.borrowerStuId = this.stuId
            bookMapper.updateByPrimaryKey(book)
            logger.log(true, "User${this.nickname} borrowed book called ${book.name}(${book.guid})")
            0 // borrow procedure succeed
        }.also { updateLibrary() }

    fun doBorrow(stuID: String, guid: String) = when {
        userService.getUserByStuId(stuID) == null -> 3
        bookWithGUID(guid) == null -> 1
        else -> userService.getUserByStuId(stuID)!!.borrow(guid)
    }

    /**
     * @author VisualDust
     * @since 0.0
     *      let an user remand a book
     * @param guid: String, the guid of the book
     * @throws NullPointerException if there is no such book
     * @return succeed or not
     */
    private fun User.remand(guid: String) =
        if (bookWithGUID(guid)!!.borrowerStuId == null) {
            logger.log("User${this.nickname} tried to remand the book but failed. No one borrowed the book before.")
            2
        } else {
            val book = bookWithGUID(guid)
            if (book!!.borrowerStuId == this.stuId) {
                book.borrowerStuId = null
                bookMapper.updateByPrimaryKey(book)
                logger.log(true, "User${this.nickname} remanded the book called ${book.name}(${book.guid})")
                0
            } else {
                logger.log("User${this.nickname} tried to remand the book but failed. The user has not borrowed the book.")
                2
            }
        }.also { updateLibrary() }

    fun doRemand(stuID: String, guid: String) = when {
        userService.getUserByStuId(stuID) == null -> 3
        bookWithGUID(guid) == null -> 1
        else -> userService.getUserByStuId(stuID)!!.remand(guid)
    }

    fun insertBook(guid: String, isbn: String, name: String, author: String): Int {
        return if (GUID2Book[guid] == null) {
            val book = Book()
            book.guid = guid
            book.isbn = isbn
            book.name = name
            book.author = author
            logger.log("Trying to insert book: ${book.name} ${book.isbn} (${book.guid})")
            try {
                bookMapper.insert(book)
                logger.log(true, "\tNew book inserted: ${book.name}($guid, ISBN=$isbn)")
                0
            } catch (e: Exception) {
                logger.log(false, "\tInset failed: ${e.toString()}")
                2
            }
        } else {
            logger.log(false,"\t Insert failed: book already exist.")
            1 //book already exists
        }.also { updateLibrary() }
    }

}