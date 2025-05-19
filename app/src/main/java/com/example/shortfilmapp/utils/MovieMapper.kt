package com.example.shortfilmapp.utils

import com.example.shortfilmapp.api.models.TrailerDto
import com.example.shortfilmapp.api.models.MovieDto
import com.example.shortfilmapp.domain.models.Movie
import com.example.shortfilmapp.domain.models.Trailer

object MovieMapper {
    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

    fun mapToDomainModel(dto: MovieDto): Movie {
        return Movie(
            id = dto.id,
            title = dto.title,
            overview = dto.overview,
            posterUrl = if (dto.poster_path != null) "$IMAGE_BASE_URL${dto.poster_path}" else "",
            backdropUrl = if (dto.backdrop_path != null) "$IMAGE_BASE_URL${dto.backdrop_path}" else "",
            releaseDate = dto.release_date,
            rating = dto.vote_average
        )
    }
}

