package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.model.Favourite
import com.curlylab.curlylabback.service.FavouriteService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class favouriteController(
    private val favouriteService: FavouriteService
) {
    @GetMapping("/users/{user_id}/favourites")
    fun getUserFavourites(): List<Favourite>? {
        return null
    }

    @PostMapping("/users/{user_id}/favourites")
    fun addToFavourites(): Favourite? {
        return null
    }

    @DeleteMapping("/users/{user_id}/favourites/{product_id}")
    fun deleteFavourite(): Boolean {
        return false
    }

    @GetMapping("/products/{product_id}/is_favourite/{user_id}")
    fun getProductUsers(): Boolean {
        return false
    }
}
