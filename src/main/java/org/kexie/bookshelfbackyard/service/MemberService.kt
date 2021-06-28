package org.kexie.bookshelfbackyard.service

import org.kexie.bookshelfbackyard.model.MemberExample
import org.kexie.bookshelfbackyard.model.mapper.MemberMapper
import org.kexie.logUtility.common.Logger
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class MemberService {
    private val logger = Logger(this)

    @Resource
    lateinit var memberMapper: MemberMapper

    init {
        logger.log(true, "Service ${javaClass.simpleName} was successfully initialized")
    }

    /**
     * @author VisualDust
     * @since 0.0, check if any one in Members table who has the specific stu_id
     * @param stu_id : String student id
     * @return  exist or null
     */
    fun someoneWithId(stu_id: String): Boolean {
        return try {
            memberMapper.selectByPrimaryKey(stu_id) != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * @author VisualDust
     * @since 0.0, check if any one in Members table who has the specific email
     * @param mail : String the email
     * @return  exist or null
     */
    fun someoneWithEmail(mail: String): Boolean {
        return try {
            val example = MemberExample()
            example.createCriteria().andMailEqualTo(mail)
            !memberMapper.selectByExample(example).isEmpty()
        } catch (e: Exception) {
            false
        }
    }

}