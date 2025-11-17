package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.Favourite
import com.curlylab.curlylabback.repository.FavouriteRepository
import org.springframework.stereotype.Service

@Service
class FavouriteService(
    val favouriteRepository: FavouriteRepository
) : BaseServiceImpl<Favourite>(favouriteRepository) {
}
