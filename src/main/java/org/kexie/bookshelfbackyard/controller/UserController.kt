package org.kexie.bookshelfbackyard.controller

import com.alibaba.fastjson.JSONObject
import org.kexie.bookshelfbackyard.service.MailService
import org.kexie.bookshelfbackyard.service.UserService
import org.kexie.bookshelfbackyard.service.Verification
import org.kexie.bookshelfbackyard.service.VerificationCodeService
import org.kexie.common.TokenUtil
import org.kexie.logUtility.common.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.math.BigInteger
import java.security.MessageDigest
import java.util.function.Consumer


@RestController
class UserController {

    private val logger = Logger(this)

//    @Autowired
//    lateinit var userService: UserService

    @Autowired
    lateinit var mailService: MailService

    @Autowired
    lateinit var userService:UserService

    @Autowired
    lateinit var verificationService: VerificationCodeService

    init {
        logger.log(true, "Controller ${javaClass.simpleName} was successfully initialized")
    }

    @ResponseBody
    @RequestMapping(value = ["/register"], method = arrayOf(RequestMethod.POST))
            /**
             * @author VisualDust
             * @since 0.0
             *      Api located at analysthugo/signUp
             * @param jsonObject contains :
             *      username : String
             *      password : String
             *      email    : String
             *      token    : String, can be null here.
             * @return a result in String type, default should be "success"
             */
    fun register(@RequestBody jsonObject: JSONObject): MutableMap<String, String> {
        val username = jsonObject["username"]
        val password = jsonObject["password"]
        val email = jsonObject["email"]
        val exist = null != userService.getUserByNickname(username.toString())
        if (exist) return mutableMapOf(
            "result" to "failed",
            "reason" to "username already exists"
        )
        try {
            userService.pendingUserQueue[username.toString()] = email.toString()
            verificationService.putVerification(Verification(username.toString(), "用户注册Analyst-Hugoの验证码", email.toString(),
                Consumer {
                    userService.pendingUserQueue.remove(username.toString())
                    if (userService.putUser(username.toString(), password.toString(), email.toString()))
                        mailService.sendMailTo(email.toString(), "欢迎来到AnalystHugo", "您好，用户$username，欢迎您来到最最最最最最最最最最最最菜的时事热点分析平台AnalystHugo！我们在努力爬了，希望能早日写完全部功能....")
                }), true)
        } catch (e: Exception) {
            return mutableMapOf(
                "result" to "failed",
                "reason" to "email invalid"
            )
        }
        logger.log(true, "signup AIP was called successfully")
        return mutableMapOf(
            "result" to "success",
            "reason" to "none"
        )
    }

//    @ResponseBody
//    @RequestMapping(value = ["/register_wechat"], method = arrayOf(RequestMethod.POST))
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
//    fun register_wechat(@RequestBody jsonObject: JSONObject): MutableMap<String, String> {
//        val username = jsonObject["username"]
//        val password = jsonObject["password"]
//        val email = jsonObject["email"]
//    }


    /**
     * @author VisualDust
     * @since 0.0, extension method named .sha256 for String type
     */
    private fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
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
