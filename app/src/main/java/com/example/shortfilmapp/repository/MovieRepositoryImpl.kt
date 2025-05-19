package com.example.shortfilmapp.repository

import android.util.Log
import com.example.shortfilmapp.domain.models.Movie
import com.example.shortfilmapp.domain.models.Trailer
import com.example.shortfilmapp.api.MovieApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale


//class MovieRepositoryImpl(
//    private val apiService: MovieApiService,
//    private val apiKey: String
//) : MovieRepository {
//
//    override suspend fun getPopularMovies(): List<Movie> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = apiService.getPopularMovies(apiKey)
//                response.results.map { dto ->
//                    // Direct mapping to Movie without accessing problematic properties
//                    Movie(
//                        id = dto.id,
//                        title = dto.title,
//                        overview = dto.overview,
//                        // Use safe calls to access properties that might be using different names
//                        posterUrl = try {
//                            val path = dto::class.java.getDeclaredField("poster_path").get(dto) as? String
//                            if (path != null) "https://image.tmdb.org/t/p/w500$path" else ""
//                        } catch (e: Exception) {
//                            ""
//                        },
//                        backdropUrl = try {
//                            val path = dto::class.java.getDeclaredField("backdrop_path").get(dto) as? String
//                            if (path != null) "https://image.tmdb.org/t/p/w500$path" else ""
//                        } catch (e: Exception) {
//                            ""
//                        },
//                        releaseDate = try {
//                            dto::class.java.getDeclaredField("release_date").get(dto) as String
//                        } catch (e: Exception) {
//                            ""
//                        },
//                        rating = try {
//                            dto::class.java.getDeclaredField("vote_average").get(dto) as Double
//                        } catch (e: Exception) {
//                            0.0
//                        }
//                    )
//                }
//            } catch (e: Exception) {
//                Log.e("MovieRepository", "Error fetching popular movies", e)
//                emptyList()
//            }
//        }
//    }
//
//    override suspend fun getMovieTrailers(movieId: Int): List<Trailer> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = apiService.getMovieTrailers(movieId, apiKey)
//                response.results
//                    .filter { it.site.equals("YouTube", ignoreCase = true) }
//                    .map { dto ->
//                        Trailer(
//                            id = dto.id,
//                            key = dto.key,
//                            name = dto.name,
//                            site = dto.site,
//                            type = dto.type
//                        )
//                    }
//            } catch (e: Exception) {
//                Log.e("MovieRepository", "Error fetching movie trailers", e)
//                emptyList()
//            }
//        }
//    }
//}

//Same Code but with safeAddProperty function

class MovieRepositoryImpl(
    private val apiService: MovieApiService,
    private val apiKey: String
) : MovieRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPopularMovies(apiKey)
                response.results.map { dto ->
                    // Create a HashMap of the DTO properties to avoid direct access
                    val properties = HashMap<String, Any?>()

                    // We know these properties are safe
                    properties["id"] = dto.id
                    properties["title"] = dto.title
                    properties["overview"] = dto.overview

                    // For the problematic properties, we'll use a function
                    safeAddProperty(properties, dto, "poster_path")
                    safeAddProperty(properties, dto, "backdrop_path")
                    safeAddProperty(properties, dto, "release_date")
                    safeAddProperty(properties, dto, "vote_average")

                    // Now create the Movie from the properties map
                    Movie(
                        id = properties["id"] as Int,
                        title = properties["title"] as String,
                        overview = properties["overview"] as String,
                        posterUrl = if (properties["poster_path"] != null)
                            "https://image.tmdb.org/t/p/w500${properties["poster_path"]}" else "",
                        backdropUrl = if (properties["backdrop_path"] != null)
                            "https://image.tmdb.org/t/p/w500${properties["backdrop_path"]}" else "",
                        releaseDate = properties["release_date"] as? String ?: "",
                        rating = properties["vote_average"] as? Double ?: 0.0
                    )
                }
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching popular movies", e)
                emptyList()
            }
        }
    }

    override suspend fun getMovieTrailers(movieId: Int): List<Trailer> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMovieTrailers(movieId, apiKey)
                response.results
                    .filter { it.site.equals("YouTube", ignoreCase = true) }
                    .map { dto ->
                        Trailer(
                            id = dto.id,
                            key = dto.key,
                            name = dto.name,
                            site = dto.site,
                            type = dto.type
                        )
                    }
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching movie trailers", e)
                emptyList()
            }
        }
    }

    private fun safeAddProperty(map: HashMap<String, Any?>, obj: Any, propertyName: String) {
        try {
            val method = obj.javaClass.methods.find {
                it.name == "get${propertyName.capitalize(Locale.ROOT)}" ||
                        it.name == propertyName
            }
            if (method != null) {
                map[propertyName] = method.invoke(obj)
            } else {
                val field = obj.javaClass.declaredFields.find { it.name == propertyName }
                if (field != null) {
                    field.isAccessible = true
                    map[propertyName] = field.get(obj)
                }
            }
        } catch (e: Exception) {
            // Property not found, just continue
        }
    }
}


