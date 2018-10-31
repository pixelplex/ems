package io.pixelplex.chatroom.data.api

import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Api service for registration server
 */
interface ApiService {

    /**
     * Register user with specified [authData]
     */
    @Headers("Content-Type: application/json")
    @POST("/faucet/registration")
    fun register(@Body authData: AuthData): Deferred<List<AccountCreateResponse>>

}