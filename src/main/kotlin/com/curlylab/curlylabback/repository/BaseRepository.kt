package com.curlylab.curlylabback.repository

import java.util.UUID

interface BaseInterfaceRepository<T> {
    fun get(id: UUID): T?
    fun add(entity: T): Boolean
    fun delete(id: UUID): Boolean
    fun edit(id: UUID, entity: T): T?
}