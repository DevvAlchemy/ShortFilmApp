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
                // Create some dummy movies for now
                createDummyMovies("Popular")
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching popular movies", e)
                emptyList()
            }
        }
    }

    override suspend fun getTopRatedMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                // Create some dummy movies for now
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
                // Create some dummy movies for now
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
                // Create some dummy search results
                createDummyMovies("Search: $query")
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error searching movies", e)
                emptyList()
            }
        }
    }

    override suspend fun getMovieTrailers(movieId: Int): List<Trailer> {
        return withContext(Dispatchers.IO) {
            try {
                // Return dummy trailers
                listOf(
                    Trailer(
                        id = "1",
                        key = "dQw4w9WgXcQ", // Rick Roll
                        name = "Official Trailer",
                        site = "YouTube",
                        type = "Trailer"
                    )
                )
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching movie trailers", e)
                emptyList()
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