package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.Favourite
import com.curlylab.curlylabback.repository.FavouriteRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class FavouriteService(
    val favouriteRepository: FavouriteRepository
) : BaseServiceImpl<Favourite>(favouriteRepository) {

    fun getALLForUser(user_id: UUID): List<Favourite>? {
        return favouriteRepository.getALLForUser(user_id)
    }

    fun deleteByUserAndProduct(user_id: UUID, product_id: UUID): Boolean {
        return favouriteRepository.deleteByUserAndProduct(user_id, product_id)
    }

    fun getByUserAndProduct(user_id: UUID, product_id: UUID): Favourite? {
        return favouriteRepository.getByUserAndProduct(user_id, product_id)
    }
}
