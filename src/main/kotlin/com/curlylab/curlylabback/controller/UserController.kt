package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.model.User
import com.curlylab.curlylabback.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: UUID): ResponseEntity<User> {
        val user = userService.getById(id)
        return if (user != null) ResponseEntity.ok(user)
        else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<String> {
        val created = userService.create(user)
        return if (created) ResponseEntity.ok("User created")
        else ResponseEntity.badRequest().body("Failed to create user")
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody user: User): ResponseEntity<User> {
        val updated = userService.update(id, user)
        return if (updated != null) ResponseEntity.ok(updated)
        else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<String> {
        val deleted = userService.delete(id)
        return if (deleted) ResponseEntity.ok("User deleted")
        else ResponseEntity.notFound().build()
    }

    @PostMapping("/{id}/upload_image", consumes = ["multipart/form-data"])
    fun uploadUserAvatar(
        @PathVariable id: UUID,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, String>> {
        return try {
            val imageUrl = userService.uploadUserAvatar(id, file)
            ResponseEntity.ok(mapOf("imageUrl" to imageUrl))
        } catch (e: IllegalArgumentException) {
            when (e.message) {
                "File size exceeds 5MB" ->
                    ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body(mapOf("error" to "File size exceeds 5MB"))

                else ->
                    ResponseEntity.badRequest()
                        .body(mapOf("error" to (e.message ?: "Upload failed")))
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest()
                .body(mapOf("error" to "Upload failed: ${e.message}"))
        }
    }

    @DeleteMapping("/{id}/avatar")
    fun deleteUserAvatar(@PathVariable id: UUID): ResponseEntity<Map<String, String>> {
        return try {
            userService.deleteUserAvatar(id)
            ResponseEntity.ok(mapOf("message" to "Avatar deleted successfully"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest()
                .body(mapOf("error" to (e.message ?: "Delete failed")))
        } catch (e: Exception) {
            ResponseEntity.badRequest()
                .body(mapOf("error" to "Delete failed: ${e.message}"))
        }
    }
}