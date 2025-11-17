package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.model.Favourite
import com.curlylab.curlylabback.service.FavouriteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class favouriteController(
    private val favouriteService: FavouriteService
) {
    @GetMapping("/users/{user_id}/favourites")
    fun getUserFavourites(@PathVariable user_id: UUID): ResponseEntity<List<Favourite>?> {
        val products = favouriteService.getALLForUser(user_id)
        return ResponseEntity.ok(products)
    }

    @PostMapping("/users/{user_id}/favourites")
    fun addToFavourites(@PathVariable user_id: UUID, @RequestBody favourite: Favourite): ResponseEntity<String> {
        if (favourite.userId != null) {
            return ResponseEntity.badRequest().body("Failed to add a favourite product")
        } else {
            favourite.userId = user_id
        }
        val created = favouriteService.create(favourite)
        return if (created) ResponseEntity.ok("Favourite product has added!")
        else ResponseEntity.badRequest().body("Failed to add a favourite product")
    }

    @DeleteMapping("/users/{user_id}/favourites/{product_id}")
    fun deleteFavourite(@PathVariable user_id: UUID, @PathVariable product_id: UUID): ResponseEntity<String> {
        val deleted = favouriteService.deleteByUserAndProduct(user_id, product_id)
        return if (deleted) ResponseEntity.ok("Review has deleted!")
        else ResponseEntity.notFound().build()
    }

    @GetMapping("/products/{product_id}/is_favourite/{user_id}")
    fun getProductUsers(@PathVariable user_id: UUID, @PathVariable product_id: UUID): Boolean {
        return if (favouriteService.getByUserAndProduct(user_id, product_id) == null) {
            false
        } else {
            true
        }
    }
}
