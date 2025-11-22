package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.User
import com.curlylab.curlylabback.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val s3Service: S3Service
) : BaseServiceImpl<User>(userRepository) {

    fun uploadUserAvatar(userId: UUID, file: MultipartFile): String {
        val user =
            getById(userId) ?: throw IllegalArgumentException("User not found with id: $userId")

        user.imageUrl?.let { oldImageUrl ->
            val oldFileKey = s3Service.extractFileKeyFromUrl(oldImageUrl)
            oldFileKey?.let { s3Service.deleteFile(it) }
        }

        val fileKey = s3Service.uploadFile(userId, file)
        val newImageUrl = s3Service.getFileUrl(fileKey)

        val updatedUser = user.copy(imageUrl = newImageUrl)
        update(userId, updatedUser)

        return newImageUrl
    }

    fun deleteUserAvatar(userId: UUID) {
        val user =
            getById(userId) ?: throw IllegalArgumentException("User not found with id: $userId")

        user.imageUrl?.let { imageUrl ->
            val fileKey = s3Service.extractFileKeyFromUrl(imageUrl)
            fileKey?.let { s3Service.deleteFile(it) }

            val updatedUser = user.copy(imageUrl = null)
            update(userId, updatedUser)
        } ?: throw IllegalArgumentException("User does not have an avatar to delete")
    }
}