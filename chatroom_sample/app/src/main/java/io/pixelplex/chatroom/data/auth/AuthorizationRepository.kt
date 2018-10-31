package io.pixelplex.chatroom.data.auth

import io.pixelplex.chatroom.data.api.ApiService
import io.pixelplex.chatroom.data.api.AuthData
import io.pixelplex.chatroom.data.service.EchoFrameworkService
import io.pixelplex.chatroom.support.toCallback
import io.pixelplex.chatroom.support.toMapCallback
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import kotlinx.coroutines.experimental.withContext
import org.echo.mobile.framework.core.crypto.CryptoCoreComponent
import org.echo.mobile.framework.model.AuthorityType
import retrofit2.HttpException
import kotlin.coroutines.experimental.coroutineContext

/**
 * Encapsulates authorization logic
 */
class AuthorizationRepository(
    private val echoFrameworkService: EchoFrameworkService,
    private val cryptoCoreComponent: CryptoCoreComponent,
    private val apiService: ApiService
) {

    /**
     * Register user [name] with [password] using [ApiService]
     */
    suspend fun register(name: String, password: String): Boolean {
        try {
            val ownerPublicKey = cryptoCoreComponent.getAddress(name, password, AuthorityType.OWNER)
            val activePublicKey =
                cryptoCoreComponent.getAddress(name, password, AuthorityType.ACTIVE)
            val memoPublicKey = cryptoCoreComponent.getAddress(name, password, AuthorityType.KEY)

            if (withContext(coroutineContext) { isAccountExist(name) }) {
                throw IllegalArgumentException()
            }

            val transactionResponses = withContext(coroutineContext) {
                apiService.register(AuthData(name, ownerPublicKey, activePublicKey, memoPublicKey))
            }

            transactionResponses.await()
            return true
        } catch (ex: RegistrationException) {
            throw ex
        } catch (ex: HttpException) {
            throw RegistrationException(
                RegistrationException.ExceptionType.INCORRECT_ACCOUNT_NAME,
                parseRegistrationMessage(ex)
            )
        } catch (ex: Exception) {
            throw RegistrationException(
                RegistrationException.ExceptionType.INCORRECT_ACCOUNT_NAME
            )
        }
    }

    private fun parseRegistrationMessage(ex: HttpException): String {
        return "Error registration process"
    }

    private suspend fun isAccountExist(name: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            echoFrameworkService.execute {
                this.checkAccountReserved(name, continuation.toCallback())
            }
        }
    }

    /**
     * Login user with [name] and [password]
     */
    suspend fun login(name: String, password: String): Boolean {
        try {
            return suspendCancellableCoroutine { continuation ->
                echoFrameworkService.execute {
                    this.isOwnedBy(
                        name,
                        password,
                        continuation.toMapCallback { continuation, _ ->
                            continuation.resume(true)
                        }
                    )
                }
            }
        } catch (ex: Exception) {
            throw IllegalArgumentException(ex)
        }
    }

}
