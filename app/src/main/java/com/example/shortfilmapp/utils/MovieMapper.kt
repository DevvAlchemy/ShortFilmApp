package com.example.shortfilmapp.utils

import com.example.shortfilmapp.api.models.MovieDto
import com.example.shortfilmapp.domain.models.Movie

object MovieMapper {
    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

    fun mapToDomainModel(dto: MovieDto): Movie {
        return Movie(
            id = dto.id,
            title = dto.title,
            overview = dto.overview,
            posterUrl = if (dto.posterPath != null) "$IMAGE_BASE_URL${dto.posterPath}" else "",
            backdropUrl = if (dto.backdropPath != null) "$IMAGE_BASE_URL${dto.backdropPath}" else "",
            releaseDate = dto.releaseDate,
            rating = dto.voteAverage
        )
    }
}

