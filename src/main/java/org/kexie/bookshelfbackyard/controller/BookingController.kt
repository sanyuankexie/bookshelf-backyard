package org.kexie.bookshelfbackyard.controller

import com.alibaba.fastjson.JSONObject
import org.kexie.bookshelfbackyard.model.Book
import org.kexie.bookshelfbackyard.service.BookingService
import org.kexie.bookshelfbackyard.service.UserService
import org.kexie.bookshelfbackyard.service.WechatOpenAPIService
import org.kexie.logUtility.common.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class BookingController {
    private val logger = Logger(this)

    @Autowired
    lateinit var bookingService: BookingService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var wechatOpenAPIService: WechatOpenAPIService

    init {
        logger.log(true, "Controller ${javaClass.simpleName} was successfully initialized")
    }


    @ResponseBody
    @RequestMapping(value = ["/borrow"], method = arrayOf(RequestMethod.POST))
            /**
             * @author VisualDust
             * @since 0.0
             *      Api located at bookshelf-backyard/borrow
             * @param jsonObject contains   :
             *      openid_code : openid_code
             *      bookid : guid of the book
             * @return a map contains   :
             *      result   : succeed(0) or not(others)
             */
    fun borrow(@RequestBody jsonObject: JSONObject): MutableMap<String, String> {
        val bookGUID = jsonObject["bookid"].toString()
        val openIDCode = jsonObject["openid_code"].toString()
        val openID = wechatOpenAPIService.openIDOf(openIDCode)
        val uid = UserController.openID2User[openID] ?: return mutableMapOf("errorcode" to "3").also {
            logger.debug("user tried to call borrow API without login: uid = ${UserController.openID2User[openID]}")
        }
        val user = userService.getUserByUserID(uid)
        return if (user == null) {
            mutableMapOf("errorcode" to "3")
        } else {
            logger.debug("${user.nickname} is trying to borrow $bookGUID")
            val errorcode = bookingService.doBorrow(user.stuId, guid = bookGUID)
            mutableMapOf("errorcode" to errorcode.toString())
        }
    }

    @ResponseBody
    @RequestMapping(value = ["/remand"], method = arrayOf(RequestMethod.POST))
            /**
             * @author VisualDust
             * @since 0.0
             *      Api located at bookshelf-backyard/remand
             * @param jsonObject contains   :
             *      openid_code : openid_code
             * @return a map contains   :
             *      result   : user's information
             */
    fun remand(@RequestBody jsonObject: JSONObject): MutableMap<String, String> {
        val bookGUID = jsonObject["bookid"].toString()
        val openIDCode = jsonObject["openid_code"].toString()
        val openID = wechatOpenAPIService.openIDOf(openIDCode)
        val uid = UserController.openID2User[openID] ?: return mutableMapOf("errorcode" to "3").also {
            logger.debug("user tried to call remand API without login: uid = ${UserController.openID2User[openID]}")
        }
        val user = userService.getUserByUserID(uid)
        return if (user == null) {
            mutableMapOf("errorcode" to "3")
        } else {
            logger.debug("${user.nickname} is trying to remand $bookGUID")
            val errorcode = bookingService.doRemand(user.stuId, guid = bookGUID)
            mutableMapOf("errorcode" to errorcode.toString())
        }
    }

    @ResponseBody
    @RequestMapping(value = ["/putbook"], method = arrayOf(RequestMethod.POST))
            /**
             * @author VisualDust
             * @since 0.0
             *      Api located at bookshelf-backyard/putbook
             * @param jsonObject contains   :
             *      openid_code : openid_code
             *      bookid : the guid of the book
             *      bookname : the name of the book
             *      authors : the authors of the book
             *      isbn : the isbn of the book
             * @return a map contains   :
             *      errorcode : 0-success or others-what happened
             */
    fun putBook(@RequestBody jsonObject: JSONObject): MutableMap<String, String> {
        val openIDCode = jsonObject["openid_code"].toString()
        val bookGUID = jsonObject["bookid"].toString()
        val isbn = jsonObject["isbn"].toString()
        val bookName = jsonObject["bookname"].toString()
        val authors = jsonObject["authors"].toString()
        val openID = wechatOpenAPIService.openIDOf(openIDCode)
        val uid = UserController.openID2User[openID] ?: return mutableMapOf("errorcode" to "2").also {
            logger.debug("user tried to call remand API without login: uid = ${UserController.openID2User[openID]}")
        }
        val errorcode = bookingService.insertBook(guid = bookGUID, isbn, bookName, authors)
        return mutableMapOf("errorcode" to errorcode.toString())
    }

    @ResponseBody
    @RequestMapping(value = ["/bookshelf"], method = arrayOf(RequestMethod.POST))
            /**
             * @author VisualDust
             * @since 0.0
             *      Api located at bookshelf-backyard/bookshelf
             * @param jsonObject contains   :
             *      openid_code : openid_code
             * @return a map contains   :
             *      all the books
             */
    fun getAllBook(@RequestBody jsonObject: JSONObject): MutableMap<String, Book> {
        val openIDCode = jsonObject["openid_code"].toString()
        val openID = wechatOpenAPIService.openIDOf(openIDCode)
        UserController.openID2User[openID] ?: return mutableMapOf()
        return bookingService.getAll()
    }
}