package com.curlylab.curlylabback.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Service
class S3Service(
    private val s3Client: AmazonS3,
    @Value("\${yandex.s3.bucket}") private val bucketName: String
) {

    companion object {
        private const val MAX_FILE_SIZE = 5 * 1024 * 1024
        private val ALLOWED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/jpg")
    }

    fun uploadFile(userId: UUID, file: MultipartFile): String {
        validateFile(file)

        val fileExtension = getFileExtension(file.originalFilename)
        val fileName = "avatars/$userId/$userId${fileExtension}"

        val convertedFile = convertMultiPartToFile(file)

        try {
            s3Client.putObject(PutObjectRequest(bucketName, fileName, convertedFile))
            return fileName
        } finally {
            convertedFile.delete()
        }
    }

    fun deleteFile(fileKey: String) {
        s3Client.deleteObject(DeleteObjectRequest(bucketName, fileKey))
    }

    fun getFileUrl(fileKey: String): String {
        return s3Client.getUrl(bucketName, fileKey).toString()
    }

    fun extractFileKeyFromUrl(imageUrl: String?): String? {
        if (imageUrl.isNullOrEmpty()) return null
        return imageUrl.substringAfter("$bucketName/")
    }

    private fun validateFile(file: MultipartFile) {
        if (file.isEmpty) {
            throw IllegalArgumentException("File is empty")
        }

        if (file.size > MAX_FILE_SIZE) {
            throw IllegalArgumentException("File size exceeds 5MB")
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.contentType)) {
            throw IllegalArgumentException("Only JPEG and PNG images are allowed")
        }
    }

    private fun getFileExtension(originalFilename: String?): String {
        return when {
            originalFilename?.endsWith(".png", ignoreCase = true) == true -> ".png"
            originalFilename?.endsWith(".jpg", ignoreCase = true) == true -> ".jpg"
            originalFilename?.endsWith(".jpeg", ignoreCase = true) == true -> ".jpeg"
            else -> ".jpg"
        }
    }

    private fun convertMultiPartToFile(file: MultipartFile): File {
        val convertedFile = File.createTempFile("temp", null)
        FileOutputStream(convertedFile).use { fos ->
            fos.write(file.bytes)
        }
        return convertedFile
    }
}