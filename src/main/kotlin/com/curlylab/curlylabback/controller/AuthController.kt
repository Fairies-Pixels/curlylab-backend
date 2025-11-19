package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.service.AuthService
import com.curlylab.curlylabback.service.GoogleAuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val auth: AuthService,
    private val googleAuth: GoogleAuthService
) {

    @PostMapping("/register")
    fun register(@RequestBody req: RegisterRequest) =
        auth.register(req.email, req.password, req.username)

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest) =
        auth.login(req.email, req.password)

    @PostMapping("/google")
    fun google(@RequestBody req: GoogleRequest) =
        googleAuth.loginWithGoogle(req.idToken)
}

data class RegisterRequest(val email: String, val password: String, val username: String)
data class LoginRequest(val email: String, val password: String)
data class GoogleRequest(val idToken: String)
