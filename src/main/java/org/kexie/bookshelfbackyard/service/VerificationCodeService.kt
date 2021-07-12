package org.kexie.bookshelfbackyard.service

import org.kexie.logUtility.common.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Consumer

@Service
class VerificationCodeService {
    private val logger = Logger(this)

    init {
        Thread {
            while (true) {
                try {
                    Thread.sleep(60 * 1000)
                    for (record in queue)
                        if (record.value.timeRem-- <= 0)
                            queue.remove(record.key)
                } catch (e: Exception) {
                    logger.log(e)
                }
            }
        }.start()
        logger.log(true, "Service ${javaClass.simpleName} was successfully initialized")
    }


    @Autowired
    lateinit var mailService: MailService

    /**
     * @author VisualDust
     * @since 0.0, randomly generates verification code with six digits based on UUID
     * @return a random verification code
     */
    fun generate(): String {
        val uuid = UUID.randomUUID().toString()
        val str = uuid.substring(0, uuid.indexOf('-'))
        var res = ""
        for (i in str.indices)
            res += when (str[i].isDigit()) {
                true -> str[i]
                false -> str[i] - 'A'
            }
        res = when (res.length > 6) {
            true -> res.substring(0, 6)
            false -> res
        }
        return res
    }

    /**
     * @author VisualDust
     * @since 0.0, put a verification into the queue.
     * @param verification : the verification
     * @param sendEmail default true, weather or not to send a verification code to the target mail
     */
    fun putVerification(verification: Verification<Boolean>, sendEmail: Boolean = true): String {
        val code = generate()
        if (sendEmail)
            try {
                mailService.sendMailTo(
                    verification.mail,
                    verification.username,
                    "验证码",
                    "${verification.description} : ${code} , 请尽快验证以免失效"
                )
            } catch (e: Exception) {
                logger.log(e)
                throw e
            }
        queue[code] = verification
        return code
    }

    /**
     * @author VisualDust
     * @since 0.0, check and run the related task if the verification code is legal
     * @param code : String, the verification code
     * @return true if it's legal or false in opposite
     */
    fun verify(username: String, code: String): Boolean {
        if (queue.containsKey(code) && queue[code]!!.username == username) {
            Thread { queue[code]!!.consumer.accept(true);queue.remove(code) }.start()
            return true
        }
        return false
    }

    /**
     * @author VisualDust
     * @since 0.0, send a verification code to the target email
     * @param mail : String, target mail
     * @param code : String, the verification code
     */
    fun sendVerificationCodeTo(username: String, mail: String, code: String) {
        mailService.sendMailTo(mail, "验证码", username, "您好，这是您在AnalystHugo申请的验证码：${code},请在过期前使用")
    }

    // The verification queue
    var queue = mutableMapOf<String, Verification<Boolean>>()
}

class Verification<T>(
    var username: String,
    var description: String,
    var mail: String,
    var consumer: Consumer<T>,
    var timeRem: Int = 5
)