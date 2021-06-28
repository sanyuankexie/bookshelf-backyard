package org.kexie.bookshelfbackyard.service

import org.kexie.bookshelfbackyard.model.Member
import org.kexie.bookshelfbackyard.model.User
import org.kexie.bookshelfbackyard.model.UserExample
import org.kexie.bookshelfbackyard.model.mapper.UserMapper
import org.kexie.logUtility.common.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.security.MessageDigest
import javax.annotation.Resource


@Service
class UserService {
    private val logger = Logger(this)

    @Resource
    lateinit var userMapper: UserMapper

    @Autowired
    lateinit var vcodeService: VerificationCodeService

    init {
        logger.log(true, "Service ${javaClass.simpleName} was successfully initialized")
    }

    /**
     * @author VisualDust
     * @since 0.0, find a user with a specific uid
     * @param uid : Long, user id
     * @return a user if exist or null
     */
    fun getUserByUserId(uid: Long): User? {
        return try {
            userMapper.selectByPrimaryKey(uid)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * @author VisualDust
     * @since 0.0, find a user with a specific username
     * @param username : Int, user id
     * @return a user if exist or null
     */
    fun getUserByStuId(stu_id: String): User? {
        return try {
            val example = UserExample()
            example.createCriteria().andStuIdEqualTo(stu_id)
            userMapper.selectByExample(example)[0]
        } catch (e: Exception) {
            null
        }
    }

    private fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

    fun String.encrypt(salt: String): String = (salt + this).sha256()

    private fun String.pendSalt(salt: String? = null): String {
        val st = salt ?: vcodeService.generate()
        return "$st|${this.encrypt(st)}"
    }

    fun User.parse(member:Member): User {
        val user = User()
        user.stuId = member.stuId
        user.nickname = member.name
        return user
    }

    /**
     * @author VisualDust
     * @since 0.0, put a user into the database directly. NOT FOR EXTERNAL USE.
     * @param nickname : String, username
     * @param password : String
     * @param email : String , user's email
     */
    fun putUser(nickname: String, password: String, email: String): Boolean {
        val user = User()
        user.nickname = nickname
        user.mail = email
        user.password = password.pendSalt()
        user.type = 0
        logger.debug("Trying to insert user : $nickname to table")
        try {
            if (null == getUserByStuId(nickname))
                userMapper.insert(user)
            else {
                logger.debug(false, "user injection for $nickname failed : username already exists")
                return false
            }
        } catch (e: java.lang.Exception) {
            logger.log(e)
            logger.debug(false, "user injection for $nickname failed : $e")
            return false
        }
        logger.debug(true, "User $nickname was successfully put into table")
        return true
    }

    private fun String.escapeSalt(): String {
        return this.split("|")[1]
    }

    private fun String.salt(): String {
        return this.split("|")[0]
    }

    val pendingUserQueue = mutableMapOf<String, String>()

    fun verify(username: String, password: String): Pair<Boolean, String> {
        if (pendingUserQueue.containsKey(username)) return Pair(false, "email not verified")
        val user = getUserByStuId(username) ?: return Pair(false, "username not found")
        val salt = user.password.salt()
        val passport = user.password.escapeSalt()
        return if (passport == password.encrypt(salt))
            Pair(true, "success")
        else
            Pair(false, "invalid password")
    }

//    fun User.generateToken(): TokenUtil.VDToken {
//        return TokenUtil.generate(mutableMapOf(
//            "name" to name,
//            "time" to Timestamp.valueOf(LocalDateTime.now()).time.toString()
//        ))
//    }
}