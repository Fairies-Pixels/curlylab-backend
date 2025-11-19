package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.User
import com.curlylab.curlylabback.model.UserProvider
import com.curlylab.curlylabback.model.UserRefreshToken
import com.curlylab.curlylabback.repository.RefreshTokenRepository
import com.curlylab.curlylabback.repository.UserProviderRepository
import com.curlylab.curlylabback.repository.UserRepository
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class GoogleAuthService(
    private val userRepo: UserRepository,
    private val providerRepo: UserProviderRepository,
    private val refreshRepo: RefreshTokenRepository,
    private val jwt: JwtService,
    @Value("\${google.client-id}") private val clientId: String
) {

    fun loginWithGoogle(idTokenString: String): Tokens {

        val jsonFactory = GsonFactory.getDefaultInstance()

        val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), jsonFactory)
            .setAudience(listOf(clientId))
            .build()

        val idToken = verifier.verify(idTokenString)
            ?: throw IllegalArgumentException("Invalid Google ID token")

        val payload = idToken.payload

        val googleId = payload.subject
        val email = payload["email"] as String
        val pictureUrl = payload["picture"] as String?

        val existingProvider = providerRepo.find("GOOGLE", googleId)

        val user = if (existingProvider != null) {
            userRepo.get(existingProvider.userId)
        } else {
            val newUser = User(
                id = UUID.randomUUID(),
                username = email.substringBefore("@"),
                createdAt = LocalDateTime.now(),
                imageUrl = pictureUrl
            )

            userRepo.add(newUser)

            providerRepo.add(
                UserProvider(
                    userId = newUser.id!!,
                    provider = "GOOGLE",
                    providerUserId = googleId,
                    email = email,
                    createdAt = LocalDateTime.now()
                )
            )

            newUser
        } ?: throw IllegalStateException("User not found after creation")

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
}
