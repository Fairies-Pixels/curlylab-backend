package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.service.AuthService
import com.curlylab.curlylabback.service.EmailAlreadyExistsException
import com.curlylab.curlylabback.service.GoogleAuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    fun register(@RequestBody req: RegisterRequest): ResponseEntity<Any> {
        return try {
            val tokens = auth.register(req.email, req.password, req.username)
            ResponseEntity.ok(tokens)

        } catch (e: EmailAlreadyExistsException) {
            if (e.message?.contains("already exists") == true) {
                ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(mapOf("error" to e.message))
            } else {
                ResponseEntity.badRequest()
                    .body(mapOf("error" to e.message))
            }
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): ResponseEntity<Any> {
        return try {
            val tokens = auth.login(req.email, req.password)
            ResponseEntity.ok(tokens)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Invalid credentials"))
        }
    }

    @PostMapping("/google")
    fun google(@RequestBody req: GoogleRequest): ResponseEntity<Any> {
        return try {
            val tokens = googleAuth.loginWithGoogle(req.idToken)
            ResponseEntity.ok(tokens)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Invalid Google token"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to (e.message ?: "Google login failed")))
        }
    }
}

data class RegisterRequest(val email: String, val password: String, val username: String)
data class LoginRequest(val email: String, val password: String)
data class GoogleRequest(val idToken: String)
