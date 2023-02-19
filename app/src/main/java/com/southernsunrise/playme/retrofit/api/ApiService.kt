package com.southernsunrise.playme.retrofit.api

import com.southernsunrise.playme.dataObjectModels.album.AlbumModel
import com.southernsunrise.playme.dataObjectModels.artist.ArtistModel
import com.southernsunrise.playme.dataObjectModels.genre.GenreModel
import com.southernsunrise.playme.retrofit.utils.ApiConstants.API_KEY
import com.southernsunrise.playme.dataObjectModels.playlist.PlaylistModel
import com.southernsunrise.playme.dataObjectModels.searchResult.SearchResultModel
import com.southernsunrise.playme.dataObjectModels.track.TrackModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/v2.2/albums/top")
    fun getTopAlbums(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("limit") limit: Int = 30
    ): Call<AlbumModel>

    @GET("/v2.2/artists/top")
    fun getTopArtists(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("limit") limit: Int = 25, /*@Query("range") range: String = "year"*/
    ): Call<ArtistModel>


    @GET("/v2.2/playlists")
    fun getDaysPlaylists(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("limit") limit: Int = 0,
        //  @Query("offset") offset: Int = 10
    ): Call<PlaylistModel>

    @GET("/v2.2/artists/{artistID}/albums/top")
    fun getArtistTopAlbums(
        @Path("artistID") artistID: String,
        @Query("apikey") apiKey: String = API_KEY,
        @Query("limit") limit: Int = 4
    ): Call<AlbumModel>


    @GET("/v2.2/artists/{artistID}/albums/new")
    fun getArtistNewReleases(
        @Path("artistID") artistID: String,
        @Query("apikey") apiKey: String = API_KEY,
        @Query("limit") limit: Int = 4
    ): Call<AlbumModel>


    @GET("/v2.2/artists/{artistID}")
    fun getArtistById(
        @Path("artistID") artistID: String,
        @Query("apikey") apiKey: String = API_KEY,
    ): Call<ArtistModel>

    @GET("/v2.2/genres/{genreID}")
    fun getGenreByID(
        @Path("genreID") genreID: String,
        @Query("apikey") apiKey: String = API_KEY,
    ): Call<GenreModel>

    @GET("/v2.2/artists/{artistID}/similar")
    fun getSimilarArtistsOfArtist(
        @Path("artistID") artistID: String,
        @Query("apikey") apiKey: String = API_KEY,
        @Query("limit") limit: Int = 4
    ): Call<ArtistModel>


    @GET("/v2/albums/{albumID}/similar")
    fun getSimilarAlbumsByAlbumID(
        @Path("albumID") albumID: String,
        @Query("apikey") apiKey: String = API_KEY,
    ): Call<AlbumModel>

    @GET("/v2.2/{objType}/{objID}/tracks")
    fun getAlbumPlaylistTracks(
        @Path("objType") objType: String,
        @Path("objID") objID: String,
        @Query("limit") limit: Int = 10,
        @Query("apikey") apiKey: String = API_KEY,

        ): Call<TrackModel>


    @GET("/v2.2/genres")
    fun getAllGenres(
        @Query("apikey") apiKey: String = API_KEY
    ): Call<GenreModel>


    @GET("/v2.2/search")
    fun getSearchResults(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("query") query: String,
        @Query("type") type: String = "album,artist,track",
        @Query("per_type_limit") type_limit: String = "5"
    ): Call<SearchResultModel>


    @GET("/v2.2/artists/{artist_id}/albums/new")
    fun getNewReleasesByArtistId(
        @Path("artist_id") artistID: String,
        @Query("apikey") apiKey: String = API_KEY,
        @Query("limit") limit: Int = 10,
    ): Call<AlbumModel>

    @GET("/v2.2/tags/{tag_id}/playlists")
    fun getPlaylistsByTag(
        @Path("tag_id") tagID: String,
        @Query("apikey") apiKey: String = API_KEY,
        @Query("limit") limit: Int = 10,
        @Query("sort") sort: String = "popularity",
    ): Call<PlaylistModel>


   /* @GET("/v2/playlists/{playlist_id}/tracks")
    suspend fun getPlaylistTracks(
        @Path("playlist_id") playlistID: String,
        @Query("apikey") apiKey: String = API_KEY,
        @Query("limit") limit: Int = 3,
    ): Response<PlaylistTrackModel>

    @GET("/v2.2/albums/new")
    suspend fun getNewAlbumReleases(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("limit") limit: Int = 15
    ): Response<AlbumModel>


    @GET("/v2.0/artists/{artistID}/tracks/top")
    suspend fun getArtistTopTracks(
        @Path("artistID") artistID: String,
        @Query("apikey") apiKey: String = API_KEY,
        @Query("limit") limit: Int = 10
    ): Response<ArtistTopTracksModel>


    */

}