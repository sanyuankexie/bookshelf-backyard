package org.kexie.logUtility.common

import java.awt.Color
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.InetAddress
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap
import kotlin.random.Random
import org.kexie.logUtility.common.AttributedHtmlStr as AHS
import org.kexie.logUtility.common.AttributedHtmlStr.Companion.BuiltInColors as BIColor
import org.kexie.logUtility.common.AttributedHtmlStr.Companion.BuiltInColors as WebColor
import org.kexie.logUtility.common.AttributedShellStr as ASS
import org.kexie.logUtility.common.AttributedShellStr.Companion.BGs as ShellBG
import org.kexie.logUtility.common.AttributedShellStr.Companion.FGs as ShellFG
import org.kexie.logUtility.common.AttributedShellStr.Companion.Styles as ShellStyle


class Logger {
    private val generator: Any
    private var genShellBG = ShellBG.White
    private var genHtmlBG = WebColor.White
    private var timeout = DefaultTimeout

    init {
        while (genShellBG == ShellBG.White || genShellBG == ShellBG.Black)
            genShellBG = ShellBG.values().elementAt(Random.nextInt(0, ShellBG.values().size))
        while (genHtmlBG == WebColor.White)
            genHtmlBG = WebColor.values().elementAt(Random.nextInt(0, WebColor.values().size))
        if (!AutoTerminalBinded && AutoBindToTerminal
        ) {
            addTarget(
                    OutStreamWithType(
                            System.out,
                            LogType.Shell
                    )
            )
            AutoTerminalBinded = true
        }
    }

    constructor(generator: Any) {
        this.generator = generator
        if (!DefaultLogFileAdded) {
            addTarget(
                    OutStreamWithType(
                            FileOutputStream(
                                    File(
                                            DefaultLogFileName
                                    ), true
                            ), LogType.HTML
                    )
            )
            DefaultLogFileAdded = true
        }
    }

    constructor(generator: Any, autoAddDefaultLogFile: Boolean) {
        this.generator = generator
        if ((!DefaultLogFileAdded) && autoAddDefaultLogFile) {
            addTarget(
                    OutStreamWithType(
                            FileOutputStream(
                                    File(
                                            DefaultLogFileName
                                    ), true
                            ), LogType.HTML
                    )
            )
            DefaultLogFileAdded = true
        }
    }

    fun addTarget(stream: OutStreamWithType, channel: Int) {
        if (!channelDictionary.containsKey(channel))
            channelDictionary[channel] = mutableListOf()
        if (channelDictionary.getValue(channel).contains(stream)) return
        channelDictionary.getValue(channel).add(stream)
        var initMessage =
                "<p>---[---(LogUtility)---(github.com/VisualDust/LogUtility)---(Version:$Version)---]--- got involved.</p>\n"
        write(stream.stream, initMessage)
    }

    fun addTarget(stream: OutStreamWithType) = addTarget(stream, 0)

    fun removeTarget(tag: String, channel: Int): Int {
        var rmCnt = 0
        for (target in channelDictionary[channel]!!)
            if (target.containsTag(tag))
                channelDictionary[channel]!!.remove(target);rmCnt++
        return rmCnt
    }

    fun removeTarget(tag: String): Int {
        var rmCnt = 0
        for (channel in channelDictionary.keys)
            rmCnt += removeTarget(tag, channel)
        return rmCnt
    }

    fun debug(str: String) = debug(str, 0)
    fun debug(str: String, channel: Int) {
        if (EnableDebugging
        ) logFormatted("%dbg%$LogSeparator%gen%$LogSeparator${str}\n", channel)
    }

    fun debug(e: Exception) = debug(e, 0)
    fun debug(e: Exception, channel: Int) = debug("", e, "", channel)
    fun debug(prefix: String, e: Exception, postfix: String) = debug(prefix, e, postfix, 0)

    fun debug(prefix: String, e: Exception, postfix: String, channel: Int) {
        if (EnableDebugging) {
            if (staticExceptionResolvers.containsKey(channel))
                for (resolver in staticExceptionResolvers.getValue(channel))
                    resolver.accept(RelatedEvent(generator, e))
            var eMessage = "${e.message}"
            var traceCnt = 0
            for (i in e.stackTrace.lastIndex downTo 0) {
                eMessage += "$LogSeparator${e.stackTrace[i]}"
                if (++traceCnt >= LimitStackTraceOnException + 1) break
            }
            eMessage = prefix + eMessage + postfix
            logFormatted(
                    "%dbg%$LogSeparator%false%$LogSeparator%tif%$LogSeparator%gen%$LogSeparator${eMessage}\n",
                    channel
            )
            if (PrintStackTraceOnException) e.printStackTrace()
        }
    }

    fun debug(succeed: Boolean, message: String) = debug(succeed, message, 0)
    fun debug(succeed: Boolean, message: String, channel: Int) {
        if (EnableDebugging) logFormatted(
                "%dbg%$LogSeparator%${
                    succeed.toString()
                        .lowercase(Locale.getDefault())
                }%$LogSeparator%tif%$LogSeparator%gen%$LogSeparator${message}\n",
                channel
        )
    }

    fun log(str: String, channel: Int) {
        if (staticEventResolvers.containsKey(channel))
            for (resolver in staticEventResolvers.getValue(channel))
                resolver.accept(RelatedEvent(generator, str))
        logFormatted("%tif%$LogSeparator%gen%$LogSeparator${str}\n", channel)
    }

    fun log(str: String) = log(str, 0)
    fun log(e: Exception, channel: Int) = log("", e, "", channel)
    fun log(prefix: String, e: Exception, postfix: String) = log(prefix, e, postfix, 0)

    fun log(prefix: String, e: Exception, postfix: String, channel: Int) {
        if (staticExceptionResolvers.containsKey(channel))
            for (resolver in staticExceptionResolvers.getValue(channel))
                resolver.accept(RelatedEvent(generator, e))
        var eMessage = "${e.message}"
        var traceCnt = 0
        for (i in e.stackTrace.lastIndex downTo 0) {
            eMessage += "$LogSeparator${e.stackTrace[i]}"
            if (++traceCnt >= LimitStackTraceOnException + 1) break
        }
        eMessage = prefix + eMessage + postfix
        logFormatted("%false%$LogSeparator%tif%$LogSeparator%gen%$LogSeparator${eMessage}\n", channel)
        if (PrintStackTraceOnException) e.printStackTrace()
    }

    fun log(e: Exception) = log(e, 0)
    fun log(succeed: Boolean, message: String) = log(succeed, message, 0)

    fun log(succeed: Boolean, message: String, channel: Int) {
        if (staticEventResolvers.containsKey(channel))
            for (resolver in staticEventResolvers.getValue(channel))
                resolver.accept(RelatedEvent(generator, message))
        logFormatted(
                "%${succeed.toString().lowercase(Locale.getDefault())}%$LogSeparator%tif%$LogSeparator%gen%$LogSeparator${message}\n",
                channel
        )
    }

    /**
     * @param formattedStr can include escape characters below :
     * %dbg%   ->  show an debug tag
     * %gen%   ->  generator name
     * %true%  ->  show a "√"
     * %false% ->  show a "×"
     * %tif%   ->  time in full
     * %tis%   ->  time in short
     * %year%  ->  year
     * %mon%   ->  month
     * %dow%   ->  day of week
     * %dom%   ->  day of month
     * %doy%   ->  day of year
     * %hour%  ->  hour
     * %min%   ->  min
     * %sec%   ->  second
     * %nano%  ->  nano of second
     */
    fun logFormatted(formattedStr: String, channel: Int) = logFormatted(formattedStr,
        LogType.Any, channel)

    fun logFormatted(formattedStr: String) = logFormatted(formattedStr, 0)

    fun logFormatted(str: String, type: LogType, channel: Int) {
        val ldt = LocalDateTime.now()
        when (type) {
            LogType.TextOnly -> {
                val processedStr = str
                        .replace("%dbg%", "[$DebugPrefix]")
                        .replace("%gen%", "$generator")
                        .replace("%true%", "[√]")
                        .replace("%false%", "[×]")
                        .replace("%tif%", "$ldt")
                        .replace("%tis%", "${ldt.dayOfYear}" + "${ldt.hour}" + "${ldt.minute}" + "${ldt.nano}")
                        .replace("%year%", "${ldt.year}")
                        .replace("%mon%", "${ldt.month}")
                        .replace("%dow%", "${ldt.dayOfWeek}")
                        .replace("%dom%", "${ldt.dayOfMonth}")
                        .replace("%doy%", "${ldt.dayOfYear}")
                        .replace("%hour%", "${ldt.hour}")
                        .replace("%min%", "${ldt.minute}")
                        .replace("%sec%", "${ldt.second}")
                        .replace("%nano%", "${ldt.nano}")
                broadcast(str, processedStr,
                    LogType.TextOnly, channel)
            }
            LogType.Shell -> {
                val processedStr = str
                        .replace(
                                "%dbg%",
                                ASS("[$DebugPrefix]").applyBG(ShellBG.Purple).applyFG(ShellFG.White)
                                        .applyStyle(ShellStyle.Flicker).toString()
                        )
                        .replace(
                                "%gen%",
                                ASS(generator).applyBG(genShellBG).applyFG(ShellFG.White).applyStyle(ShellStyle.Underline)
                                        .toString()
                        )
                        .replace("%true%", ASS("[√]").applyFG(ShellFG.Green).applyStyle(ShellStyle.Inverse).toString())
                        .replace(
                                "%false%",
                                ASS("[×]").applyBG(ShellBG.Red).applyFG(ShellFG.White).applyStyle(ShellStyle.Flicker).toString()
                        )
                        .replace("%tif%", ASS(ldt).applyStyle(ShellStyle.Inverse).toString())
                        .replace(
                                "%tis%",
                                ASS(ldt.dayOfYear + ldt.hour + ldt.minute + ldt.nano).applyStyle(ShellStyle.Inverse).toString()
                        )
                        .replace("%year%", ASS(ldt.year).applyStyle(ShellStyle.Inverse).toString())
                        .replace("%mon%", ASS(ldt.month).applyStyle(ShellStyle.Inverse).toString())
                        .replace("%dow%", ASS(ldt.dayOfWeek).applyStyle(ShellStyle.Inverse).toString())
                        .replace("%dom%", ASS(ldt.dayOfMonth).applyStyle(ShellStyle.Inverse).toString())
                        .replace("%doy%", ASS(ldt.dayOfYear).applyStyle(ShellStyle.Inverse).toString())
                        .replace("%hour%", ASS(ldt.hour).applyStyle(ShellStyle.Inverse).toString())
                        .replace("%min%", ASS(ldt.minute).applyStyle(ShellStyle.Inverse).toString())
                        .replace("%sec%", ASS(ldt.second).applyStyle(ShellStyle.Inverse).toString())
                        .replace("%nano%", ASS(ldt.nano).applyStyle(ShellStyle.Inverse).toString())
                broadcast(str, processedStr,
                    LogType.Shell, channel)
            }
            LogType.HTML -> {
                val processedStr = "<p>" + str
                        .replace("%dbg%", AHS("[$DebugPrefix]").applyBG(BIColor.Orange).applyFG(BIColor.White).toString())
                        .replace(
                                "%gen%",
                                AHS(generator).applyBG(Color(140, 0, 176)).applyFG(BIColor.White).toString()
                        )
                        .replace("%true%", AHS("[√]").applyBG(BIColor.Green).applyFG(BIColor.White).toString())
                        .replace("%false%", AHS("[×]").applyBG(BIColor.Red).applyFG(BIColor.White).toString())
                        .replace("%tif%", AHS(ldt).applyBG(BIColor.Gray).applyFG(BIColor.White).toString())
                        .replace(
                                "%tis%",
                                AHS(ldt.dayOfYear + ldt.hour + ldt.minute + ldt.nano).applyBG(BIColor.Gray)
                                        .applyFG(BIColor.White).toString()
                        )
                        .replace("%year%", AHS(ldt.year).applyBG(BIColor.Gray).applyFG(BIColor.White).toString())
                        .replace("%mon%", AHS(ldt.month).applyBG(BIColor.Gray).applyFG(BIColor.White).toString())
                        .replace("%dow%", AHS(ldt.dayOfWeek).applyBG(BIColor.Gray).applyFG(BIColor.White).toString())
                        .replace("%dom%", AHS(ldt.dayOfMonth).applyBG(BIColor.Gray).applyFG(BIColor.White).toString())
                        .replace("%doy%", AHS(ldt.dayOfYear).applyBG(BIColor.Gray).applyFG(BIColor.White).toString())
                        .replace("%hour%", AHS(ldt.hour).applyBG(BIColor.Gray).applyFG(BIColor.White).toString())
                        .replace("%min%", AHS(ldt.minute).applyBG(BIColor.Gray).applyFG(BIColor.White).toString())
                        .replace("%sec%", AHS(ldt.second).applyBG(BIColor.Gray).applyFG(BIColor.White).toString())
                        .replace("%nano%", AHS(ldt.nano).applyBG(BIColor.Gray).applyFG(BIColor.White).toString()) + "</p>"
                broadcast(str, processedStr,
                    LogType.HTML, channel)
            }
            LogType.Any -> {
                logFormatted(str, LogType.HTML, channel)
                logFormatted(str, LogType.Shell, channel)
                logFormatted(str, LogType.TextOnly, channel)
            }
        }
    }

    private fun broadcast(oriMessage: String, message: String, channel: Int) =
            broadcast(oriMessage, message, LogType.Any, channel)

    private fun broadcast(oriMessage: String, message: String, type: LogType, channel: Int) {
        var finalMsg = message
        if (StaticPrefixDictionary.containsKey(type)) finalMsg = StaticPrefixDictionary.getValue(type) + finalMsg
        if (StaticPostfixDictionary
                        .containsKey(type)) finalMsg += StaticPostfixDictionary.getValue(type)

        if (!channelDictionary.containsKey(channel)) return
        if (channel != 0) broadcast(oriMessage, finalMsg, type, 0)
        val subscriberList = channelDictionary.getValue(channel)
        for (subscriber in subscriberList)
            if (subscriber.type == type) write(subscriber.stream, finalMsg)
    }

    private fun write(stream: OutputStream, message: String) {
        //todo rewrite this to queue like
//        Thread(StreamAttendant(stream, message, timeout)).start()
        stream.write(message.toByteArray())
    }


    companion object {
        const val Version = "0.1.0.0"

        @JvmStatic
        val DefaultLoggerName: String = "Logger"

        @JvmStatic
        private val OsProperties = System.getProperties()

        @JvmStatic
        var StartUpTime: LocalDateTime = LocalDateTime.now()

        @JvmStatic
        val DefaultTimeout = 10000L

        @JvmStatic
        var LogSeparator = ">"

        @JvmStatic
        var DebugPrefix = "Δ"

        @JvmStatic
        var LimitStackTraceOnException = 1

        @JvmStatic
        var EnableDebugging = true

        @JvmStatic
        var PrintStackTraceOnException = false

        @JvmStatic
        var AutoBindToTerminal = true

        @JvmStatic
        private val StaticPrefixDictionary = hashMapOf<LogType, String>()

        @JvmStatic
        private val StaticPostfixDictionary = hashMapOf<LogType, String>()

        @JvmStatic
        fun setStaticPrefix(type: LogType, prefix: String) {
            StaticPrefixDictionary[type] = prefix
        }

        @JvmStatic
        fun setStaticPostfix(type: LogType, postfix: String) {
            StaticPostfixDictionary[type] = postfix
        }

        private var AutoTerminalBinded = false
        private var DefaultLogFileAdded = false

        private var staticExceptionResolvers:
                HashMap<Int, MutableList<Consumer<RelatedEvent<Any, Exception>>>> = HashMap()
        private var staticEventResolvers: HashMap<Int, MutableList<Consumer<RelatedEvent<Any, String>>>> = HashMap()

        fun addEventResolver(resolver: Consumer<RelatedEvent<Any, String>>) = addEventResolver(resolver, 0)
        fun addEventResolver(resolver: Consumer<RelatedEvent<Any, String>>, channel: Int) {
            if (!staticEventResolvers.containsKey(channel)) staticEventResolvers.put(channel, mutableListOf())
            staticEventResolvers.getValue(channel).add(resolver)
        }

        fun removeEventResolver(resolver: Consumer<RelatedEvent<Any, String>>, channel: Int) {
            if (!staticEventResolvers.containsKey(channel)) return
            staticEventResolvers.getValue(channel).remove(resolver)
        }

        fun addExceptionResolver(resolver: Consumer<RelatedEvent<Any, Exception>>) =
                addExceptionResolver(resolver, 0)

        fun addExceptionResolver(resolver: Consumer<RelatedEvent<Any, Exception>>, channel: Int) {
            if (!staticExceptionResolvers.containsKey(channel)) staticExceptionResolvers.put(channel, mutableListOf())
            staticExceptionResolvers.getValue(channel).add(resolver)
        }

        fun removeExceptionResolver(resolver: Consumer<RelatedEvent<Any, Exception>>, channel: Int) {
            if (!staticExceptionResolvers.containsKey(channel)) return
            staticExceptionResolvers.getValue(channel).remove(resolver)
        }

        fun getPlatformProperties(): MutableMap<String, String> {
            val properties = mutableMapOf<String, String>()
            val env = System.getenv()
            val prop = System.getProperties()
            val envMap = mutableMapOf<String, String>(
                    "USERNAME" to "用户名",
                    "COMPUTERNAME" to "计算机名",
                    "USERDOMAIN" to "计算机域名"
            )
            for (kv in envMap)
                if (null != env[kv.key]) properties[kv.value] = env[kv.key]!!
            val propMap = mutableMapOf<String, String>(
                    "os.name" to "操作系统名称",
                    "os.version" to "操作系统版本",
                    "java.version" to "JVM版本"
            )
            properties["ip地址"] = InetAddress.getLocalHost().hostAddress.toString()
            for (kv in propMap)
                if (null != prop.getProperty(kv.key)) properties[kv.value] = prop.getProperty(kv.key)
            return properties
        }

        @JvmStatic
        var DefaultLogFileName =
                "${DefaultLoggerName}_" + "${StartUpTime.year}_" + "${StartUpTime.month}_" + "${StartUpTime.dayOfMonth}_Log_.html"
        private var channelDictionary = HashMap<Int, MutableList<OutStreamWithType>>()
//        @JvmStatic private var remoteAttendant : RemoteMonitorAttendant? =null
//        fun EnableRemoteConnection(port: Int) {
//            if (remoteAttendant==null) remoteAttendant = RemoteMonitorAttendant(port)
//            else Logger(this).log(Exception("Already opened remote attendant on ${remoteAttendant!!.port}"))
//        }
//        fun StartRemoteAttendant(){
//            if (remoteAttendant!=null){
//                if (remoteAttendant.start())
//            }
//        }
//        fun StopRemoteAttendant(){}
//        fun DisableRemoteAttendant(){}
    }

    enum class LogType {
        Any, HTML, Shell, TextOnly
    }


    private class StreamAttendant
    (var stream: OutputStream, message: String, var timeout: Long) : Runnable {
        init {
            stream.write(message.toByteArray())
            stream.flush()
        }

        override fun run() {
            try {
                Thread.sleep(timeout)
                stream.close()
            } catch (e: Exception) {
            }
        }
    }
}
