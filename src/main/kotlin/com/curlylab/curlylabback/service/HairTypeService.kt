package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.model.HairType
import com.curlylab.curlylabback.repository.HairTypeRepository
import org.springframework.stereotype.Service

@Service
class HairTypeService(
    hairTypeRepository: HairTypeRepository
) : BaseServiceImpl<HairType>(hairTypeRepository)