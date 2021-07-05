package org.kexie.bookshelfbackyard.controller

import org.kexie.bookshelfbackyard.service.BookingService
import org.kexie.logUtility.common.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
class BookingController {
    private val logger = Logger(this)

    @Autowired
    lateinit var userController: UserController

    @Autowired
    lateinit var bookingService: BookingService

    init {
        logger.log(true, "Controller ${javaClass.simpleName} was successfully initialized")
    }

}