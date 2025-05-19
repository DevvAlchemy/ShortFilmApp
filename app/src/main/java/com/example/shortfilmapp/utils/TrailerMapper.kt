package com.example.shortfilmapp.utils

import com.example.shortfilmapp.api.models.TrailerDto
import com.example.shortfilmapp.domain.models.Trailer

object TrailerMapper {
    fun mapToDomainModel(dto: TrailerDto): Trailer? {
        // Only map YouTube trailers
        if (dto.site.equals("YouTube", ignoreCase = true)) {
            return Trailer(
                id = dto.id,
                key = dto.key,
                name = dto.name,
                site = dto.site,
                type = dto.type
            )
        }
        return null
    }
}