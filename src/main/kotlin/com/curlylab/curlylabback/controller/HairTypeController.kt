package com.curlylab.curlylabback.controller

import com.curlylab.curlylabback.model.HairType
import com.curlylab.curlylabback.service.HairTypeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/hairtypes")
class HairTypeController(
    private val hairTypeService: HairTypeService
) {

    @GetMapping("/{userId}")
    fun getHairType(@PathVariable userId: UUID): ResponseEntity<HairType> {
        val hairType = hairTypeService.getById(userId)
        return ResponseEntity.ok(hairType)
    }

    @PostMapping
    fun createHairType(@RequestBody hairType: HairType): ResponseEntity<String> {
        val created = hairTypeService.create(hairType)
        return if (created) ResponseEntity.ok("HairType created")
        else ResponseEntity.badRequest().body("Failed to create HairType")
    }

    @PutMapping("/{userId}")
    fun updateHairType(@PathVariable userId: UUID, @RequestBody hairType: HairType): ResponseEntity<HairType> {
        val updated = hairTypeService.update(userId, hairType)
        return if (updated != null) ResponseEntity.ok(updated)
        else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{userId}")
    fun deleteHairType(@PathVariable userId: UUID): ResponseEntity<String> {
        val deleted = hairTypeService.delete(userId)
        return if (deleted) ResponseEntity.ok("HairType deleted")
        else ResponseEntity.notFound().build()
    }
}