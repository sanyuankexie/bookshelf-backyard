package org.kexie.common

import org.kexie.bookshelfbackyard.model.User
import org.kexie.bookshelfbackyard.model.Member
import org.kexie.logUtility.common.Logger
import org.springframework.stereotype.Component
import java.lang.Exception
import java.lang.StringBuilder
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
object TokenUtil {

    class VDToken {
        var values = mutableMapOf<String, String>()

        init {
            put("time", LocalDateTime.now(ZoneOffset.UTC).toString())
                    .put("expire", LocalDateTime.now(ZoneOffset.UTC).plusSeconds(expireTime).toString())
        }

        constructor() {}

        constructor(generator: String) {
            put("generator", generator)
        }

        constructor(vals: MutableMap<String, String>) {
            values.putAll(vals)
        }

        fun put(key: String, value: String): VDToken {
            values[key] = value
            return this
        }

        override fun toString(): String {
            val builder = StringBuilder()
            if (values.isEmpty()) throw Exception("bad token origin")
            for (item in values)
                builder.append("${
                    item.key.replace(valueSpl, "")
                            .replace(barrier, "")
                }$valueSpl${
                    item.value
                            .replace(valueSpl, "")
                            .replace(barrier, "")
                }$barrier")
            builder.append(builder.toString().sha256())
            return builder.toString()
        }

        /**
         * @author VisualDust
         * @since 0.0, extension method named .sha256 for String type
         */
        private fun String.sha256(): String {
            val md = MessageDigest.getInstance("SHA-256")
            return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
        }

        companion object {
            var valueSpl = "="
            var barrier = "|"
            public val expireTime: Long = 60 * 60 * 24;
        }
    }

    val logger = Logger(this)

    val expiredRomovingThread = Thread {
        while (true) {
            try {
                Thread.sleep(VDToken.expireTime * 1000)
            } catch (e: Exception) {
                logger.log(e)
            }
            val ldt = LocalDateTime.now(ZoneOffset.UTC)
            for (tokenReg in tokenList)
                if (ldt.isBefore(LocalDateTime.parse(from(tokenReg.value).values["expire"])))
                    tokenList.remove(tokenReg.key)
        }
    }

    init {
        expiredRomovingThread.start()
    }

    private fun String.isValidVDToken(): Boolean =
            from(this).toString().split(VDToken.barrier).last() == this.split(VDToken.barrier).last()

    private fun String.toTokenOrigins(): MutableMap<String, String> {
        val values = mutableMapOf<String, String>()
        for (item in this.split(VDToken.barrier))
            if (2 == item.split(VDToken.valueSpl).size)
                values[item.split(VDToken.valueSpl)[0]] = item.split(VDToken.valueSpl)[1]
        return values
    }

    fun generate(generator: String): VDToken = VDToken(generator)

    fun generate(vals: MutableMap<String, String>) = VDToken(vals)

//    fun generate(user: User): VDToken = VDToken()
//            .put("generator", user.name)

    fun from(tokenStr: String): VDToken = VDToken(tokenStr.toTokenOrigins())

    fun whom(token: String): String? {
        return if (token.isValidVDToken())
            from(token).values["generator"]
        else null
    }

    fun whomIfRegistered(token: String): String? {
        val whom = whom(token)
        return if (null != whom && tokenList.containsKey(whom) && tokenList[whom] == token)
            whom
        else null
    }

    fun isRegisteredToken(username: String, token: String): Boolean = tokenList.containsKey(username) && tokenList[username] == token

    private var tokenList = mutableMapOf<String, String>()

    public fun renew(token: String?): String? {
        val whom = whomIfRegistered(token ?: return null)
        return if (null != whom) {
            tokenList.remove(whom)
            generate(whom).toString().register(whom)
        } else null
    }

    private fun String.register(username: String): String {
        tokenList[username] = this
        return this
    }
}

fun main() {
    val str = LocalDateTime.now().toInstant(ZoneOffset.UTC).toString()
    println(str)
    println(LocalDateTime.parse(str.removeSuffix("Z")).plusHours(1).toString())
}

