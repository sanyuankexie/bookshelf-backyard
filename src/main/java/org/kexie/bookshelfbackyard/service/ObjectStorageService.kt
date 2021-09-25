package org.kexie.bookshelfbackyard.service

import com.aliyun.oss.OSSClient
import com.aliyun.oss.common.utils.BinaryUtil
import com.aliyun.oss.model.MatchMode
import com.aliyun.oss.model.PolicyConditions
import org.kexie.logUtility.common.Logger
import org.springframework.stereotype.Service
import java.util.*

@Service
class ObjectStorageService {
    private val logger = Logger(this)

    init {
        logger.log(true, "Service ${javaClass.simpleName} was successfully initialized")
    }

    fun getOSSPolicy() = getOSSPolicy(endpoint, accessId, accessKey)

    fun getOSSPolicy(endpoint: String, accessId: String, accessKey: String): MutableMap<String, String> {
        val ossClient = OSSClient(endpoint, accessId, accessKey)
        val policyConds = PolicyConditions()
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000)
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir)
        val expireEndTime = System.currentTimeMillis() + policyExpireTime
        val postPolicy = ossClient.generatePostPolicy(Date(expireEndTime), policyConds)
        ossClient.shutdown()
        return mutableMapOf(
            "access-id" to accessId,
            "policy" to BinaryUtil.toBase64String(postPolicy.toByteArray()),
            "signature" to ossClient.calculatePostSignature(postPolicy)
        )
    }

    companion object {
        const val endpoint = "oss-cn-beijing.aliyuncs.com"
        const val dir = "kexie-bookshelf/"
        const val policyExpireTime = 120 * 1000
        const val accessId = "LTAI5tQfzXgsgJ7F9uwJnKzG"
        const val accessKey = "f2E9fxf7GnfIxhoSSST5xhmZFzTuF4"
    }
}