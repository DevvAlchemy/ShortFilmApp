package com.example.shortfilmapp.repository



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

import android.R.attr.rating
import android.util.Log
import com.example.shortfilmapp.domain.models.Movie
import com.example.shortfilmapp.domain.models.Trailer
import com.example.shortfilmapp.api.MovieApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Field

class MovieRepositoryImpl(
    private val apiService: MovieApiService,
    private val apiKey: String
) : MovieRepository {

    // In your getPopularMovies method in MovieRepositoryImpl.kt
    override suspend fun getPopularMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPopularMovies(apiKey)

                // Debug the raw response
                val firstMovie = response.results.firstOrNull()
                if (firstMovie != null) {
                    Log.d("MovieRepository", "First movie title: ${firstMovie.title}")
                    Log.d("MovieRepository", "First movie poster path: ${getFieldValue(firstMovie, "poster_path")}")
                    Log.d("MovieRepository", "First movie backdrop path: ${getFieldValue(firstMovie, "backdrop_path")}")
                }

                response.results.mapNotNull { dto ->
                    val movie = safeMapToMovie(dto)
                    // Log the constructed URL
                    Log.d("MovieRepository", "Mapped movie: ${movie?.title}, Poster URL: ${movie?.posterUrl}")
                    movie
                }
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching popular movies", e)
                emptyList()
            }
        }
    }

    override suspend fun getTopRatedMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                createDummyMovies("Top Rated")
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching top rated movies", e)
                emptyList()
            }
        }
    }

    override suspend fun getUpcomingMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                createDummyMovies("Upcoming")
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching upcoming movies", e)
                emptyList()
            }
        }
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchMovies(apiKey, query)
                response.results.mapNotNull { dto ->
                    safeMapToMovie(dto)
                }
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error searching movies", e)
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
                // Return a dummy trailer for testing if needed
                listOf(
                    Trailer(
                        id = "dummy",
                        key = "dQw4w9WgXcQ", // Rick Roll for testing
                        name = "Trailer",
                        site = "YouTube",
                        type = "Trailer"
                    )
                )
            }
        }
    }

    // here

    // In MovieRepositoryImpl.kt, add this logging function
    private fun logMovieDetails(dto: Any, movie: Movie) {
        try {
            val posterPath = getFieldValue(dto, "poster_path")
            val backdropPath = getFieldValue(dto, "backdrop_path")

            Log.d("MovieRepository", "===== MOVIE DETAILS =====")
            Log.d("MovieRepository", "Title: ${movie.title}")
            Log.d("MovieRepository", "Raw poster_path: $posterPath")
            Log.d("MovieRepository", "Raw backdrop_path: $backdropPath")
            Log.d("MovieRepository", "Constructed posterUrl: ${movie.posterUrl}")
            Log.d("MovieRepository", "Constructed backdropUrl: ${movie.backdropUrl}")
            Log.d("MovieRepository", "=========================")
        } catch (e: Exception) {
            Log.e("MovieRepository", "Error logging movie details", e)
        }
    }

    // Update your safeMapToMovie function to use this logging
    private fun safeMapToMovie(dto: Any): Movie? {
        return try {
            // These should match your Movie class constructor parameters
            val id = getFieldValue(dto, "id") as? Int ?: 0
            val title = getFieldValue(dto, "title") as? String ?: ""
            val overview = getFieldValue(dto, "overview") as? String ?: ""

            // Try different possible naming conventions for poster path
            val posterPath = getFieldValue(dto, "poster_path") as? String?
                ?: getFieldValue(dto, "posterPath") as? String?
                ?: getFieldValue(dto, "poster") as? String?

            // Similarly for backdrop path
            val backdropPath = getFieldValue(dto, "backdrop_path") as? String?
                ?: getFieldValue(dto, "backdropPath") as? String?
                ?: getFieldValue(dto, "backdrop") as? String?

            // Try different names for release date
            val releaseDate = getFieldValue(dto, "release_date") as? String?
                ?: getFieldValue(dto, "releaseDate") as? String?
                ?: ""

            // Try different names for vote average
            val rating = getFieldValue(dto, "vote_average") as? Double?
                ?: getFieldValue(dto, "voteAverage") as? Double?
                ?: getFieldValue(dto, "rating") as? Double?
                ?: 0.0

            val movie = Movie(
                id = id,
                title = title,
                overview = overview,
                posterUrl = if (!posterPath.isNullOrEmpty()) {
                    // Use this exact format for TMDB API
                    "https://image.tmdb.org/t/p/w500$posterPath"
                } else {
                    ""
                },
                backdropUrl = if (!backdropPath.isNullOrEmpty()) {
                    "https://image.tmdb.org/t/p/w500$backdropPath"
                } else {
                    ""
                },
                releaseDate = releaseDate?.toString() ?: "",
                rating = rating
            )

            // Log the details
            logMovieDetails(dto, movie)

            movie
        } catch (e: Exception) {
            Log.e("MovieRepository", "Error mapping MovieDto to Movie", e)
            null
        }
    }

    // Helper method to get a field value using reflection
    private fun getFieldValue(obj: Any, fieldName: String): Any? {
        return try {
            // Try to find the field directly
            val field = obj.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true
            field.get(obj)
        } catch (e: Exception) {
            // If not found, look through all fields with case-insensitive comparison
            obj.javaClass.declaredFields.find {
                it.name.equals(fieldName, ignoreCase = true)
            }?.let { field ->
                field.isAccessible = true
                field.get(obj)
            }
        }
    }

    // Helper method to create dummy movies
    private fun createDummyMovies(prefix: String): List<Movie> {
        return listOf(
            Movie(
                id = 1,
                title = "$prefix Movie 1",
                overview = "This is a placeholder movie. The actual API integration will be fixed later.",
                posterUrl = "https://via.placeholder.com/500x750.png?text=Movie+1",
                backdropUrl = "https://via.placeholder.com/1280x720.png?text=Movie+1",
                releaseDate = "2023-01-01",
                rating = 8.5
            ),
            Movie(
                id = 2,
                title = "$prefix Movie 2",
                overview = "This is another placeholder movie. The actual API integration will be fixed later.",
                posterUrl = "https://via.placeholder.com/500x750.png?text=Movie+2",
                backdropUrl = "https://via.placeholder.com/1280x720.png?text=Movie+2",
                releaseDate = "2023-02-01",
                rating = 7.9
            ),
            Movie(
                id = 3,
                title = "$prefix Movie 3",
                overview = "This is yet another placeholder movie. The actual API integration will be fixed later.",
                posterUrl = "https://via.placeholder.com/500x750.png?text=Movie+3",
                backdropUrl = "https://via.placeholder.com/1280x720.png?text=Movie+3",
                releaseDate = "2023-03-01",
                rating = 6.7
            )
        )
    }
}