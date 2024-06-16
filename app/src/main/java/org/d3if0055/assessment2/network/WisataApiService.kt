package org.d3if0055.assessment2.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.d3if0055.assessment2.model.Wisata
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://raw.githubusercontent.com/" +
        "kevinsianturii/static-api1/main/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()
interface WisataApiService {
    @GET("db.json")
    suspend fun getWisata():List<Wisata>
}

object WisataApi {
    fun getWisataUrl(imageId: String): String {
        return "$BASE_URL$imageId.jpg"
    }

    val service: WisataApiService by lazy {
        retrofit.create(WisataApiService::class.java)
    }
}

enum class ApiStatus {LOADING, SUCCES, FAILED}