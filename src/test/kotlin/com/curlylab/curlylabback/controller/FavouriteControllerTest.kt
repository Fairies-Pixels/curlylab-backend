package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.model.Favourite
import com.curlylab.curlylabback.service.FavouriteService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*

class FavouriteControllerTest {

    private lateinit var favouriteService: FavouriteService
    private lateinit var favouriteController: favouriteController

    @BeforeEach
    fun setUp() {
        favouriteService = mockk()
        favouriteController = favouriteController(favouriteService)
    }

    @Test
    fun `получение избранного пользователя - успешный сценарий`() {
        val userId = UUID.randomUUID()
        val favourites = listOf(
            Favourite(userId = userId, productId = UUID.randomUUID()),
            Favourite(userId = userId, productId = UUID.randomUUID())
        )

        every { favouriteService.getALLForUser(userId) } returns favourites

        val result = favouriteController.getUserFavourites(userId)

        assert(result.statusCode == HttpStatus.OK)
        assert(result.body == favourites)
        verify { favouriteService.getALLForUser(userId) }
    }

    @Test
    fun `получение избранного пользователя - пустой список`() {
        val userId = UUID.randomUUID()
        val favourites = emptyList<Favourite>()

        every { favouriteService.getALLForUser(userId) } returns favourites

        val result = favouriteController.getUserFavourites(userId)

        assert(result.statusCode == HttpStatus.OK)
        assert(result.body == favourites)
        verify { favouriteService.getALLForUser(userId) }
    }

    @Test
    fun `добавление в избранное - успешный сценарий`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val favourite = Favourite(userId = null, productId = productId)

        every { favouriteService.create(any()) } returns true

        val result = favouriteController.addToFavourites(userId, favourite)

        assert(result.statusCode == HttpStatus.OK)
        assert(result.body == "Favourite product has added!")
        verify { favouriteService.create(any()) }
    }

    @Test
    fun `добавление в избранное - ошибка при добавлении`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val favourite = Favourite(userId = null, productId = productId)

        every { favouriteService.create(any()) } returns false

        val result = favouriteController.addToFavourites(userId, favourite)

        assert(result.statusCode == HttpStatus.BAD_REQUEST)
        assert(result.body == "Failed to add a favourite product")
        verify { favouriteService.create(any()) }
    }

    @Test
    fun `добавление в избранное - неверный запрос (userId уже установлен)`() {
        val userId = UUID.randomUUID()
        val favourite = Favourite(userId = UUID.randomUUID(), productId = UUID.randomUUID())

        val result = favouriteController.addToFavourites(userId, favourite)

        assert(result.statusCode == HttpStatus.BAD_REQUEST)
        assert(result.body == "Failed to add a favourite product")
        verify(exactly = 0) { favouriteService.create(any()) }
    }

    @Test
    fun `удаление из избранного - успешный сценарий`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        every { favouriteService.deleteByUserAndProduct(userId, productId) } returns true

        val result = favouriteController.deleteFavourite(userId, productId)

        assert(result.statusCode == HttpStatus.OK)
        assert(result.body == "Review has deleted!")
        verify { favouriteService.deleteByUserAndProduct(userId, productId) }
    }

    @Test
    fun `удаление из избранного - запись не найдена`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        every { favouriteService.deleteByUserAndProduct(userId, productId) } returns false

        val result = favouriteController.deleteFavourite(userId, productId)

        assert(result.statusCode == HttpStatus.NOT_FOUND)
        verify { favouriteService.deleteByUserAndProduct(userId, productId) }
    }

    @Test
    fun `проверка наличия продукта в избранном - продукт есть в избранном`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val favourite = Favourite(userId = userId, productId = productId)

        every { favouriteService.getByUserAndProduct(userId, productId) } returns favourite

        val result = favouriteController.getProductUsers(userId, productId)

        assert(result == true)
        verify { favouriteService.getByUserAndProduct(userId, productId) }
    }

    @Test
    fun `проверка наличия продукта в избранном - продукта нет в избранном`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        every { favouriteService.getByUserAndProduct(userId, productId) } returns null

        val result = favouriteController.getProductUsers(userId, productId)

        assert(result == false)
        verify { favouriteService.getByUserAndProduct(userId, productId) }
    }
}