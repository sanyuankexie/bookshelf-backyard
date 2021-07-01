package org.kexie.bookshelfbackyard.service

import okhttp3.OkHttpClient
import okhttp3.Request
import org.kexie.logUtility.common.Logger
import org.springframework.stereotype.Service

@Service
class WechatOpenAPIService {
    private val logger = Logger(this)

    init {
        logger.log(true, "Service ${javaClass.simpleName} was successfully initialized")
    }

    private val client = OkHttpClient()

    /**
     * @author VisualDust
     * @since 0.0
     *      Get wechat openid via openid code
     * @param openIDCode the specific openID
     * @return the openid
     */
    fun openIDOf(openIDCode: String): String {
        val url =
            "https://api.weixin.qq.com/sns/jscode2session?appid=${appid}&secret=${appsecret}&js_code=${openIDCode}&grant_type=${grantType}"
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        val result = client.newCall(request).execute()
        return result.body.toString()
    }

    companion object {
        val appid = "wxb53ed26e17dd8e42"
        val appsecret = "8c0c5d14e8b3f7dc33c2e84467de48db"
        val grantType="authorization_code"
    }

}