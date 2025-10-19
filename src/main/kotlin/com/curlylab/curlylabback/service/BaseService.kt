package com.curlylab.curlylabback.service

import com.curlylab.curlylabback.repository.BaseInterfaceRepository
import java.util.*

interface BaseInterfaceService<T> {
    fun getById(id: UUID): T?
    fun create(entity: T): Boolean
    fun update(id: UUID, entity: T): T?
    fun delete(id: UUID): Boolean
}

abstract class BaseServiceImpl<T>(
    private val repository: BaseInterfaceRepository<T>
) : BaseInterfaceService<T> {

    override fun getById(id: UUID): T? =
        repository.get(id)

    override fun create(entity: T): Boolean =
        repository.add(entity)

    override fun update(id: UUID, entity: T): T? =
        repository.edit(id, entity)

    override fun delete(id: UUID): Boolean =
        repository.delete(id)
}