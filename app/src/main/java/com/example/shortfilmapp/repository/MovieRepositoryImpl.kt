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


import android.util.Log
import com.example.shortfilmapp.api.MovieApiService
import com.example.shortfilmapp.api.models.MovieDto
import com.example.shortfilmapp.api.models.TrailerDto
import com.example.shortfilmapp.domain.models.Movie
import com.example.shortfilmapp.domain.models.Trailer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepositoryImpl(
    private val apiService: MovieApiService,
    private val apiKey: String
) : MovieRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("MovieRepository", "Fetching popular movies from API...")
                val response = apiService.getPopularMovies(apiKey)
                Log.d("MovieRepository", "API call successful, got ${response.results.size} movies")

                if (response.results.isNotEmpty()) {
                    val mappedMovies = response.results.map { dto ->
                        mapMovieDtoToMovie(dto)
                    }
                    Log.d("MovieRepository", "Successfully mapped ${mappedMovies.size} movies")
                    mappedMovies
                } else {
                    Log.w("MovieRepository", "Empty response from API, using dummy data")
                    createDummyMovies("Popular")
                }
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching popular movies: ${e.message}", e)
                createDummyMovies("Popular")
            }
        }
    }

    override suspend fun getTopRatedMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("MovieRepository", "Fetching top rated movies from API...")
                val response = apiService.getTopRatedMovies(apiKey)
                Log.d("MovieRepository", "API call successful, got ${response.results.size} movies")

                if (response.results.isNotEmpty()) {
                    response.results.map { dto ->
                        mapMovieDtoToMovie(dto)
                    }
                } else {
                    createDummyMovies("Top Rated")
                }
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching top rated movies: ${e.message}", e)
                createDummyMovies("Top Rated")
            }
        }
    }

    override suspend fun getUpcomingMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("MovieRepository", "Fetching upcoming movies from API...")
                val response = apiService.getUpcomingMovies(apiKey)
                Log.d("MovieRepository", "API call successful, got ${response.results.size} movies")

                if (response.results.isNotEmpty()) {
                    response.results.map { dto ->
                        mapMovieDtoToMovie(dto)
                    }
                } else {
                    createDummyMovies("Upcoming")
                }
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching upcoming movies: ${e.message}", e)
                createDummyMovies("Upcoming")
            }
        }
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("MovieRepository", "Searching movies with query: $query")
                val response = apiService.searchMovies(apiKey, query)
                Log.d("MovieRepository", "Search successful, got ${response.results.size} results")

                response.results.map { dto ->
                    mapMovieDtoToMovie(dto)
                }
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error searching movies: ${e.message}", e)
                emptyList()
            }
        }
    }

    override suspend fun getMovieTrailers(movieId: Int): List<Trailer> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("MovieRepository", "Fetching trailers for movie ID: $movieId")
                val response = apiService.getMovieTrailers(movieId, apiKey)

                if (response.results.isNotEmpty()) {
                    Log.d("MovieRepository", "Found ${response.results.size} trailers")
                    val firstTrailer = response.results[0]
                    Log.d("MovieRepository", "First trailer: id=${firstTrailer.id}, key=${firstTrailer.key}, name=${firstTrailer.name}, site=${firstTrailer.site}")

                    response.results
                        .filter { it.site.equals("YouTube", ignoreCase = true) }
                        .map { dto ->
                            mapTrailerDtoToTrailer(dto)
                        }.also {
                            Log.d("MovieRepository", "Mapped ${it.size} YouTube trailers")
                        }
                } else {
                    Log.w("MovieRepository", "No trailers found for movie ID: $movieId")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching movie trailers: ${e.message}", e)
                emptyList()
            }
        }
    }

    private fun mapMovieDtoToMovie(dto: MovieDto): Movie {
        val posterUrl = if (!dto.posterPath.isNullOrEmpty()) {
            // Use this exact format for TMDB API
            "https://image.tmdb.org/t/p/w500${dto.posterPath}"
        } else {
            ""
        }

        val backdropUrl = if (!dto.backdropPath.isNullOrEmpty()) {
            "https://image.tmdb.org/t/p/w500${dto.backdropPath}"
        } else {
            ""
        }

        Log.d("MovieRepository", "Mapping movie: ${dto.title}")
        Log.d("MovieRepository", "posterPath: ${dto.posterPath}, posterUrl: $posterUrl")
        Log.d("MovieRepository", "backdropPath: ${dto.backdropPath}, backdropUrl: $backdropUrl")

        return Movie(
            id = dto.id,
            title = dto.title,
            overview = dto.overview,
            posterUrl = posterUrl,
            backdropUrl = backdropUrl,
            releaseDate = dto.releaseDate,
            rating = dto.voteAverage
        )
    }

    private fun mapTrailerDtoToTrailer(dto: TrailerDto): Trailer {
        return Trailer(
            id = dto.id,
            key = dto.key,
            name = dto.name,
            site = dto.site,
            type = dto.type
        )
    }

    private fun createDummyMovies(prefix: String): List<Movie> {
        return listOf(
            Movie(
                id = 1,
                title = "$prefix Movie 1",
                overview = "This is a placeholder movie.",
                posterUrl = "https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w500/9n2tJBplPbgR2ca05hS5CKXwP2c.jpg",
                releaseDate = "2023-01-01",
                rating = 8.5
            ),
            Movie(
                id = 2,
                title = "$prefix Movie 2",
                overview = "This is another placeholder movie.",
                posterUrl = "https://image.tmdb.org/t/p/w500/8Vt6mWEReuy4Of61Lnj5Xj704m8.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w500/5YZbUmjbMa3ClvSW1Wj3D6XGolb.jpg",
                releaseDate = "2023-02-01",
                rating = 7.9
            ),
            Movie(
                id = 3,
                title = "$prefix Movie 3",
                overview = "This is yet another placeholder movie.",
                posterUrl = "https://image.tmdb.org/t/p/w500/fiVW06jE7z9YnO4trhaMEdclSiC.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w500/9m2Ubu9kUgxQMgUX2QeaV1rQpvk.jpg",
                releaseDate = "2023-03-01",
                rating = 6.7
            )
        )
    }
}