package io.pixelplex.chatroom.presentation.registration

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.pixelplex.chatroom.data.auth.AuthorizationRepository
import io.pixelplex.chatroom.data.auth.RegistrationException
import io.pixelplex.chatroom.data.service.EchoFrameworkService
import io.pixelplex.chatroom.data.service.RoomsService
import io.pixelplex.chatroom.data.service.UserService
import io.pixelplex.chatroom.model.User
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

/**
 * View model for [RegistrationActivity]
 *
 * Contains all logic of information preparing for user presentation
 */
class RegistrationViewModel(
    private val authorizationRepository: AuthorizationRepository,
    private val echoFrameworkService: EchoFrameworkService,
    private val userService: UserService,
    private val roomsService: RoomsService
) : ViewModel() {

    val registrationState = MutableLiveData<Unit>()
    val error = MutableLiveData<Exception>()

    private val job = Job()

    /**
     * Registers user [name] with [password] and validates [confPassword]
     */
    fun register(name: String, password: String, confPassword: String) {
        launch(UI, parent = job) {
            when {
                name.isEmpty() || name.isBlank() ->
                    error.postValue(RegistrationException(RegistrationException.ExceptionType.INCORRECT_ACCOUNT_NAME))
                password.isEmpty() || password.isBlank() ->
                    error.postValue(RegistrationException(RegistrationException.ExceptionType.INCORRECT_PASSWORD))
                confPassword != password ->
                    error.postValue(RegistrationException(RegistrationException.ExceptionType.PASSWORDS_DO_NOT_MATCH))
                else -> registerInternal(name, password)

            }

        }
    }

    private suspend fun registerInternal(name: String, password: String) {
        val loggedAsync = async { authorizationRepository.register(name, password) }

        try {
            if (loggedAsync.await()) {
                val account =
                    withContext(DefaultDispatcher) { echoFrameworkService.getAccount(name) }
                userService.saveUser(
                    User(
                        account.name,
                        account.getObjectId(),
                        password
                    )
                )
                roomsService.addNonExistentAccount(account.getObjectId())

                registrationState.postValue(Unit)
            }
        } catch (ex: Exception) {
            error.postValue(RegistrationException(RegistrationException.ExceptionType.INCORRECT_ACCOUNT_NAME))
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}