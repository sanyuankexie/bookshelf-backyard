package org.kexie.bookshelfbackyard.controller

import com.alibaba.fastjson.JSONObject
import org.kexie.bookshelfbackyard.service.*
import org.kexie.common.TokenUtil
import org.kexie.logUtility.common.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.math.BigInteger
import java.security.MessageDigest
import java.util.function.Consumer


@Controller
class UserController {

    private val logger = Logger(this)

//    @Autowired
//    lateinit var userService: UserService

    @Autowired
    lateinit var mailService: MailService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var memberService: MemberService

    @Autowired
    lateinit var verificationService: VerificationCodeService

    @Autowired
    lateinit var objectStorageService: ObjectStorageService

    @Autowired
    lateinit var wechatOpenAPIService: WechatOpenAPIService

    init {
        logger.log(true, "Controller ${javaClass.simpleName} was successfully initialized")
    }

    @ResponseBody
    @RequestMapping(value = ["/oss-signature"], method = arrayOf(RequestMethod.POST))
    fun getOSSSagnature(@RequestBody jsonObject: JSONObject): MutableMap<String, String> {
        return objectStorageService.getOSSPolicy()
    }

    @ResponseBody
    @RequestMapping(value = ["/register"], method = arrayOf(RequestMethod.POST))
    fun register(@RequestBody jsonObject: JSONObject): MutableMap<String, String> {
        val nickname = jsonObject["nickname"].toString()
        val stuID = jsonObject["student_id"].toString()
        val openIDCode = jsonObject["openid_code"].toString()
        val member = memberService.getMemberByStuID(stuID)
        return when {
            userService.anyoneWithStuID(stuID) -> {
                mutableMapOf("errorcode" to "1")
            }
            userService.anyoneWithNickname(nickname) -> {
                mutableMapOf("errorcode" to "3")
            }
            else -> {
                val openID = wechatOpenAPIService.openIDOf(openIDCode)
                try {
                    val email = member.mail
                    userService.pendingUserQueue[nickname] = email
                    verificationService.putVerification(
                        Verification(
                            nickname,
                            "用户注册KexieBookshelf的验证码",
                            email.toString(),
                            Consumer {
                                userService.pendingUserQueue.remove(nickname)
                                if (userService.putUser(nickname, openID, email.toString()))
                                    mailService.sendMailTo(
                                        email.toString(),
                                        "欢迎来到KexieBookshelf",
                                        "您好，用户$nickname，您已经开始试图借书了。"
                                    )
                            }), true
                    )
                } catch (e: Exception) {
                    mutableMapOf("errorcode" to "4")
                }
                logger.log(true, "doRegister AIP was called successfully")
                mutableMapOf("errorcode" to "0")
            }
        }
    }

//    @ResponseBody
//    @RequestMapping(value = ["/register"], method = arrayOf(RequestMethod.POST))
//            /**
//             * @author VisualDust
//             * @since 0.0
//             *      Api located at analysthugo/signUp
//             * @param jsonObject contains :
//             *      username : String
//             *      password : String
//             *      email    : String
//             *      token    : String, can be null here.
//             * @return a result in String type, default should be "success"
//             */
//    fun register(@RequestBody jsonObject: JSONObject): MutableMap<String, String> {
//        val username = jsonObject["username"].toString()
//        val password = jsonObject["password"].toString()
//        val email = jsonObject["email"].toString()
//        val exist = null != userService.getUserByNickname(username.toString())
//        if (exist) return mutableMapOf(
//            "result" to "failed",
//            "reason" to "username already exists"
//        )
//        return if (doRegister(username, password, email)) {
//            logger.log(true, "signup AIP was called successfully")
//            mutableMapOf(
//                "result" to "success",
//                "reason" to "none"
//            )
//        } else {
//            mutableMapOf(
//                "result" to "failed",
//                "reason" to "email invalid"
//            )
//        }
//    }


    /**
     * @author VisualDust
     * @since 0.0, extension method named .sha256 for String type
     */
    private fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }


    @ResponseBody
    @RequestMapping(value = ["/login"], method = arrayOf(RequestMethod.POST))
            /**
             * @author VisualDust
             * @since 0.0
             *      Api located at bookshelf-backyard/login
             * @param jsonObject contains   :
             *      username : String
             *      openid_code : openid_code
             * @return a map contains   :
             *      result   : String, true if succeed or false when failed
             */
    fun login(@RequestBody jsonObject: JSONObject): MutableMap<String, String> {
        val username = jsonObject["username"].toString()
        val password = wechatOpenAPIService.openIDOf(jsonObject["openid_code"].toString())
        val result = userService.verify(username, password)
        return mutableMapOf(
            "result" to result.first.toString()
        )
    }

//    @ResponseBody
//    @RequestMapping(value = ["/login"], method = arrayOf(RequestMethod.POST))
//            /**
//             * @author VisualDust
//             * @since 0.0
//             *      Api located at analysthugo/login
//             * @param jsonObject contains   :
//             *      username : String
//             *      password : String
//             *      token    : String, can be null here
//             * @return a map contains   :
//             *      result   : String, "success" or "failed"
//             *      token    : String, a generated token for online user
//             */
//    fun login(@RequestBody jsonObject: JSONObject): MutableMap<String, String> {
//        val username = jsonObject["username"]
//        val password = jsonObject["password"]
//        val result = userService.verify(username.toString(), password.toString())
//        return when (result.first) {
//            true -> {
//                val user = userService.getUserByName(username.toString())
//                logger.log(true, "login AIP was called successfully")
//                mutableMapOf(
//                    "result" to "success",
//                    "token" to TokenUtil.register(user!!)
//                )
//            }
//            else -> mutableMapOf(
//                "result" to "failed",
//                "reason" to result.second
//            )
//        }
//    }
}
