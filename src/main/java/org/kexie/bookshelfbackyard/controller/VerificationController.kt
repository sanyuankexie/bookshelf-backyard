package org.kexie.bookshelfbackyard.controller

import com.alibaba.fastjson.JSONObject
import org.kexie.bookshelfbackyard.service.Verification
import org.kexie.bookshelfbackyard.service.VerificationCodeService
import org.kexie.logUtility.common.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.util.function.Consumer

@Controller
class VerificationController {

    @Autowired
    lateinit var verificationService: VerificationCodeService

    val logger = Logger(this)

    init {
        logger.log(true, "Controller ${javaClass.simpleName} was successfully initialized")
    }

    @ResponseBody
    @RequestMapping(value = ["/verify"], method = arrayOf(RequestMethod.POST))
            /**
             * @author VisualDust
             * @since 0.0
             *      Api located at analysthugo/verify
             * @param jsonObject contains :
             *      code : String
             *      username : String
             *      token : String, can be null here
             * @return result : String, "success" or "faild"
             */
    fun verify(@RequestBody jsonObject: JSONObject): MutableMap<String, String> {
        val code = jsonObject["code"]
        val username = jsonObject["username"]
        val result = verificationService.verify(username.toString(), code.toString())
        logger.log(true, "verify AIP was called")
        return when (result) {
            true -> mutableMapOf("result" to "success")
            false -> mutableMapOf("result" to "failed")
        }
    }

    @ResponseBody
    @RequestMapping(value = ["/requestvcode"], method = arrayOf(RequestMethod.POST))
            /**
             * @author VisualDust
             *  @since 0.0
             *      Api located at analysthugo/requestvcode
             *  @param jsonObject contains :
             *      description : String, describes why we send this code
             *      mail        : String, who well receive this code
             *  @return the result should be "success"
             */
    fun requestCode(@RequestBody jsonObject: JSONObject): MutableMap<String, String> {
        val username = jsonObject["username"]
        val mail = jsonObject["mail"]
        val description = jsonObject["description"]
        verificationService.putVerification(
            Verification(
                username.toString(),
                description.toString(),
                mail.toString(),
                Consumer { })
        )
        return mutableMapOf("result" to "success")
    }
}