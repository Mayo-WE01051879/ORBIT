package io.infinite.orbit.controllers

import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import io.infinite.blackbox.BlackBox
import io.infinite.orbit.model.UnmanagedEmail
import io.infinite.orbit.services.UnmanagedEmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@BlackBox
@Slf4j
class UnmanagedEmailController {

    @Autowired
    UnmanagedEmailService unmanagedEmailService

    @PostMapping(value = "/orbit/unmanagedEmail")
    @ResponseBody
    @CompileDynamic
    @CrossOrigin
    void unmanagedEmail(@RequestParam("unmanagedEmail") UnmanagedEmail unmanagedEmail
    ) {
        unmanagedEmailService.unmanagedEmail(unmanagedEmail)
    }

}
