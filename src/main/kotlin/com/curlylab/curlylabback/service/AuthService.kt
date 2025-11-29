package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.User
import com.curlylab.curlylabback.model.UserAuth
import com.curlylab.curlylabback.model.UserRefreshToken
import com.curlylab.curlylabback.repository.RefreshTokenRepository
import com.curlylab.curlylabback.repository.UserAuthRepository
import com.curlylab.curlylabback.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.security.crypto.bcrypt.BCrypt
import java.time.LocalDateTime
import java.util.*

@Service
class AuthService(
    private val userRepo: UserRepository,
    private val userAuthRepo: UserAuthRepository,
    private val refreshRepo: RefreshTokenRepository,
    private val jwt: JwtService
) {
    fun register(email: String, password: String, username: String): Tokens {
        if (userAuthRepo.findByEmail(email) != null)
            throw EmailAlreadyExistsException(email)

        val salt = BCrypt.gensalt()
        val hash = BCrypt.hashpw(password, salt)

        val user = User(
            id = UUID.randomUUID(),
            username = username,
            createdAt = LocalDateTime.now()
        )
        userRepo.add(user)

        userAuthRepo.add(
            UserAuth(
                userId = user.id!!,
                email = email,
                passwordHash = hash,
                salt = salt,
                createdAt = LocalDateTime.now()
            )
        )

        return generateTokens(user)
    }

    fun login(email: String, password: String): Tokens {
        val auth = userAuthRepo.findByEmail(email)
            ?: throw IllegalArgumentException("Invalid credentials")

        if (!BCrypt.checkpw(password, auth.passwordHash))
            throw IllegalArgumentException("Invalid credentials")

        val user = userRepo.get(auth.userId)!!
        return generateTokens(user)
    }

    private fun generateTokens(user: User): Tokens {
        val access = jwt.generateAccessToken(user)
        val refresh = UUID.randomUUID().toString()

        refreshRepo.save(
            UserRefreshToken(
                userId = user.id!!,
                token = refresh,
                expiresAt = LocalDateTime.now().plusDays(30)
            )
        )

        return Tokens(access, refresh)
    }

    fun logout(refreshToken: String) {
        val token = refreshRepo.findValid(refreshToken)
            ?: throw IllegalArgumentException("Invalid refresh token")

        refreshRepo.revoke(token.id)
    }

}

data class Tokens(
    val access: String,
    val refresh: String
)

class EmailAlreadyExistsException(email: String) :
    RuntimeException("User with email $email already exists")
