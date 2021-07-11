package org.kexie.bookshelfbackyard.service

import org.kexie.common.ConfigUtil
import org.kexie.common.RawUtil
import org.kexie.logUtility.common.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDateTime
import java.util.function.Consumer

@Component
class Monitor {
    @Autowired
    lateinit var mailService: MailService

    val logger = Logger(this)

    private val exceptionMap = mutableMapOf<Any, MutableList<Exception>>()
    private var exceptionCnt: Long = 0
    private val eventMap = mutableMapOf<Any, MutableList<String>>()
    private var eventCnt: Long = 0

    private lateinit var targetMails: MutableMap<String, String>

    var refreshDelay: Long = 86400

    private val refreshThread = Thread {
        while (true) {
            try {
                Thread.sleep(refreshDelay * 1000)
                var mailStr = "在过去的${refreshDelay}秒内，BookshelfBackyard共产生了$exceptionCnt 个异常以及 $eventCnt 个事件。其中异常包括"
                for (item in exceptionMap)
                    mailStr += "来自${item.key.javaClass.simpleName}的${item.value.size}个异常, "
                mailStr += "事件或API访问包括"
                for (item in eventMap)
                    mailStr += "来自${item.key.javaClass.simpleName}的${item.value.size}个事件或API访问, "
                mailStr += "详情请从空间折跃门前往后端控制台。祝您今天过得愉快。"
                for (mail in targetMails)
                    mailService.sendMailTo(
                        mail.value, "尊敬的观测者${mail.key}",
                        "后端の观测报告：$exceptionCnt 个异常以及 $eventCnt 个事件",
                        mailStr
                    )
                eventCnt = 0
                exceptionCnt = 0
                exceptionMap.clear()
                eventMap.clear()
            } catch (e: Exception) {
                logger.log(e)
            }
        }
    }

    init {
        Logger.addEventResolver(Consumer {
            if (!eventMap.containsKey(it.who)) eventMap[it.who] = mutableListOf()
            eventMap[it.who]!!.add(it.containing)
            eventCnt++
        })
        Logger.addExceptionResolver(Consumer {
            if (!exceptionMap.containsKey(it.who)) exceptionMap[it.who] = mutableListOf()
            exceptionMap[it.who]!!.add(it.containing)
            exceptionCnt++
        })
        refreshThread.start()
        Thread {
            try {
                ConfigUtil.fromFile("monitor_mail_targets.config")
            } catch (e: Exception) {
                logger.log(false, "Monitor mail targets configure not found, creating...")
                RawUtil.writeToFile(
                    File("monitor_mail_targets.config"),
                    mutableListOf(
                        "# 以下邮箱将会收到阶段性运行报告\n" +
                                "# 书写格式为\"称呼=邮箱\"，例如 :\n" +
                                "# Akasaki=Miya@Akasaki.space\n" +
                                "# 你可以继续添加其它会收到邮件的用户"
                    )
                )
                ConfigUtil.fromFile("monitor_mail_targets.config")
            }.also { targetMails = it }
            try {
                Thread.sleep(1000)
                var platformProp = ""
                for (prop in Logger.getPlatformProperties())
                    platformProp += "${prop.key}=${prop.value}; "
                for (mail in targetMails)
                    mailService.sendMailTo(
                        mail.value, "尊敬的管理员${mail.key}",
                        "后端の启动事件",
                        "科协借书小程序后端程序于${LocalDateTime.now()}被成功启动，此后每过${refreshDelay}秒，您都会收到一份简略的运行报告。启动平台信息：${platformProp}。祝愉快。"
                    )
            } catch (e: Exception) {
                logger.log(e)
            }
        }.start()
        logger.log(true, "Monitor was successfully initialized")
    }
}