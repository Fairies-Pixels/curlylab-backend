package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.Favourite
import com.curlylab.curlylabback.repository.FavouriteRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class FavouriteServiceTest {

    private lateinit var favouriteRepository: FavouriteRepository
    private lateinit var favouriteService: FavouriteService

    @BeforeEach
    fun setUp() {
        favouriteRepository = mockk()
        favouriteService = FavouriteService(favouriteRepository)
    }

    @Test
    fun `получение всех избранных продуктов для пользователя - успешный сценарий`() {
        val userId = UUID.randomUUID()
        val favourites = listOf(
            Favourite(userId = userId, productId = UUID.randomUUID()),
            Favourite(userId = userId, productId = UUID.randomUUID())
        )

        every { favouriteRepository.getALLForUser(userId) } returns favourites

        val result = favouriteService.getALLForUser(userId)

        assert(result == favourites)
        verify { favouriteRepository.getALLForUser(userId) }
    }

    @Test
    fun `получение всех избранных продуктов для пользователя - пустой список`() {
        val userId = UUID.randomUUID()
        val favourites = emptyList<Favourite>()

        every { favouriteRepository.getALLForUser(userId) } returns favourites

        val result = favouriteService.getALLForUser(userId)

        assert(result == favourites)
        verify { favouriteRepository.getALLForUser(userId) }
    }

    @Test
    fun `получение избранного по пользователю и продукту - успешный сценарий`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val favourite = Favourite(userId = userId, productId = productId)

        every { favouriteRepository.getByUserAndProduct(userId, productId) } returns favourite

        val result = favouriteService.getByUserAndProduct(userId, productId)

        assert(result == favourite)
        verify { favouriteRepository.getByUserAndProduct(userId, productId) }
    }

    @Test
    fun `получение избранного по пользователю и продукту - запись не найдена`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        every { favouriteRepository.getByUserAndProduct(userId, productId) } returns null

        val result = favouriteService.getByUserAndProduct(userId, productId)

        assert(result == null)
        verify { favouriteRepository.getByUserAndProduct(userId, productId) }
    }

    @Test
    fun `удаление избранного по пользователю и продукту - успешный сценарий`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        every { favouriteRepository.deleteByUserAndProduct(userId, productId) } returns true

        val result = favouriteService.deleteByUserAndProduct(userId, productId)

        assert(result == true)
        verify { favouriteRepository.deleteByUserAndProduct(userId, productId) }
    }

    @Test
    fun `удаление избранного по пользователю и продукту - запись не найдена`() {
        val userId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        every { favouriteRepository.deleteByUserAndProduct(userId, productId) } returns false

        val result = favouriteService.deleteByUserAndProduct(userId, productId)

        assert(result == false)
        verify { favouriteRepository.deleteByUserAndProduct(userId, productId) }
    }

    @Test
    fun `создание избранного - успешный сценарий`() {
        val favourite = Favourite(userId = UUID.randomUUID(), productId = UUID.randomUUID())

        every { favouriteRepository.add(favourite) } returns true

        val result = favouriteService.create(favourite)

        assert(result == true)
        verify { favouriteRepository.add(favourite) }
    }

    @Test
    fun `создание избранного - ошибка при создании`() {
        val favourite = Favourite(userId = UUID.randomUUID(), productId = UUID.randomUUID())

        every { favouriteRepository.add(favourite) } returns false

        val result = favouriteService.create(favourite)

        assert(result == false)
        verify { favouriteRepository.add(favourite) }
    }

    @Test
    fun `удаление избранного по ID продукта - успешный сценарий`() {
        val productId = UUID.randomUUID()

        every { favouriteRepository.delete(productId) } returns true

        val result = favouriteService.delete(productId)

        assert(result == true)
        verify { favouriteRepository.delete(productId) }
    }

    @Test
    fun `удаление избранного по ID продукта - запись не найдена`() {
        val productId = UUID.randomUUID()

        every { favouriteRepository.delete(productId) } returns false

        val result = favouriteService.delete(productId)

        assert(result == false)
        verify { favouriteRepository.delete(productId) }
    }
}