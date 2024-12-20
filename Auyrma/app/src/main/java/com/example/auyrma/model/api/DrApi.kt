package com.example.auyrma.model.api

import com.example.auyrma.model.entity.Dr
import com.example.auyrma.model.entity.DrResponse
import com.example.auyrma.model.entity.FavoriteRequest
import com.example.auyrma.model.entity.FavoriteRequestToHospital
import com.example.auyrma.model.entity.FavoriteResponse
import com.example.auyrma.model.entity.Hospital
import com.example.auyrma.model.entity.Pharmacy
import com.example.auyrma.model.entity.Session
import com.example.auyrma.model.entity.SessionRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface DrApi {
    @GET("medics/")
    suspend fun fetchMedicsWithParams(
        @QueryMap params: Map<String, String?>
    ): DrResponse

    @GET("medics/")
    fun fetchMedicsWithParams2(
        @QueryMap params: Map<String, String?>
    ): Call<DrResponse>

    @GET("hospitals/")
    fun fetchHospitals(@QueryMap params: Map<String, String?>): Call<List<Hospital>>

    @GET("pharmacy/")
    fun fetchPharmacies(@QueryMap params: Map<String, String?>): Call<List<Pharmacy>>

    @Headers("Content-Type: application/json")
    @POST("add_favorite_medic/")
    fun addFavoriteDr(@Body favoriteRequest: FavoriteRequest): Call<FavoriteResponse>

    @POST("remove_favorite_medic/")
    fun removeFavoriteDr(@Body favoriteRequest: FavoriteRequest): Call<FavoriteResponse>

    @POST("add_favorite/")
    fun addFavoriteHospital(@Body favoriteRequest: FavoriteRequestToHospital): Call<FavoriteResponse>

    @POST("remove_favorite_hospital/")
    fun removeFavoriteHospital(@Body favoriteRequest: FavoriteRequestToHospital): Call<FavoriteResponse>

    @GET("favorite_hospitals/2/")
    fun fetchFavoriteHospitals(): Call<List<Hospital>>

    @GET("favorite_medics/2/")
    fun fetchFavoriteDoctor(): Call<List<Dr>>

    @GET("sessions/")
    fun fetchSessions(@QueryMap params: Map<String, String?>): Call<List<Session>>

    @GET("medics_all/")
    fun fetchAllDr(): Call<List<Dr>>

    @POST("sessions/")
    fun addSession(@Body sessionRequest: SessionRequest): Call<FavoriteResponse>

    @DELETE("sessions/{id}/")
    fun deleteSession(@Path("id") sessionId: Int): Call<Void>
}