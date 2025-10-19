package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.model.User
import com.curlylab.curlylabback.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

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
}