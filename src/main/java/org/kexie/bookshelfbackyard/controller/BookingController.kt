package org.kexie.bookshelfbackyard.controller

import com.alibaba.fastjson.JSONObject
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
    lateinit var userController: UserController

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
        val uid = userController.openIDCode2User[openIDCode]
        val user = userService.getUserByUserID(uid!!)
        return if (user == null) {
            mutableMapOf("errorcode" to "3")
        } else {
            val errorcode = if (bookingService.doBorrow(user.stuId, guid = bookGUID)) 0 else 1
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
        val stuID = userController.openIDCode2User[openIDCode]
        val user = userService.getUserByUserID(stuID!!)
        return if (user == null) {
            mutableMapOf("errorcode" to "3")
        } else {
            val errorcode = if (bookingService.doBorrow(user.stuId, guid = bookGUID)) 0 else 1
            mutableMapOf("errorcode" to errorcode.toString())
        }
    }
}