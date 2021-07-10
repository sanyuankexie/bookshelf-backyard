package org.kexie.bookshelfbackyard.service

import org.kexie.logUtility.common.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class MailService {

    @Autowired
    lateinit var mailSender: JavaMailSender

    private val logger = Logger(this)

    init {
        logger.log(true, "Service ${javaClass.simpleName} was successfully initialized")
    }

    @Autowired
    lateinit var templateEngine: TemplateEngine

    /**
     * @author VisualDust
     * @since 0.0, a overriding method of sendMailTo
     * @see sendMailTo
     */
    fun sendMailTo(mailAddr: String, title: String, content: String) = sendMailTo(mailAddr, "尊敬的用户", title, content)

    fun sendMailTo(mailAddr: String, name: String, title: String, content: String) {
        content.replace("\n", System.getProperty("line.separator"))
        val context = Context()
        context.setVariable("userName", name)
        context.setVariable("content", content)
        sendMailTo(mailAddr, title, context, "SimpleMailTemplate.html")
    }

    /**
     * @author VisualDust
     * @since 0.0, send a e-mail to the target mail address, using specific template
     * @param mailAddr : String, target mail address
     * @param title    : String
     */
    fun sendMailTo(mailAddr: String, title: String, context: Context, templateName: String): Boolean {
        try {
            val mimeMessage = mailSender.createMimeMessage()
            val messageHelper = MimeMessageHelper(mimeMessage)
            messageHelper.setFrom("noreply@Akasaki.space")
            messageHelper.setTo(mailAddr)
            messageHelper.setSubject(title)
            messageHelper.setText(templateEngine.process(templateName, context), true)
            mailSender.send(messageHelper.mimeMessage)
            logger.log(true, "Mailed to $mailAddr")
            return true
        } catch (e: Exception) {
            logger.log(e)
            return false
        }
    }
}
