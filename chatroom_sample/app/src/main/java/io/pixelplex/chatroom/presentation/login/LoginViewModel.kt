package io.pixelplex.chatroom.presentation.login

import android.arch.lifecycle.MutableLiveData
import io.pixelplex.chatroom.data.auth.AuthorizationRepository
import io.pixelplex.chatroom.data.auth.RegistrationException
import io.pixelplex.chatroom.data.service.EchoFrameworkService
import io.pixelplex.chatroom.data.service.RoomsService
import io.pixelplex.chatroom.data.service.UserService
import io.pixelplex.chatroom.model.User
import io.pixelplex.chatroom.presentation.base.BaseViewModel
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

/**
 * View model for [LoginActivity]
 *
 * Contains all logic of information preparing for user presentation
 */
class LoginViewModel(
    private val authorizationRepository: AuthorizationRepository,
    private val frameworkService: EchoFrameworkService,
    private val userService: UserService,
    private val roomsService: RoomsService
) : BaseViewModel() {

    val loginState = MutableLiveData<Any>()
    val error = MutableLiveData<Exception>()

    /**
     * Login user with required account [name] and [password]
     */
    fun login(name: String, password: String) {
        launch(UI, parent = job) {
            when {
                name.isEmpty() || name.isBlank() ->
                    error.postValue(RegistrationException(RegistrationException.ExceptionType.INCORRECT_ACCOUNT_NAME))
                password.isEmpty() || password.isBlank() ->
                    error.postValue(RegistrationException(RegistrationException.ExceptionType.INCORRECT_ACCOUNT_NAME))
                else -> loginInternal(name, password)
            }
        }
    }

    private suspend fun loginInternal(name: String, password: String) {
        val loggedAsync = async { authorizationRepository.login(name, password) }

        try {
            if (loggedAsync.await()) {
                val account = getAccount(name)
                userService.saveUser(
                    User(
                        account.name,
                        account.getObjectId(),
                        password
                    )
                )
                roomsService.addNonExistentAccount(account.getObjectId())

                loginState.postValue(Any())
            }
        } catch (ex: Exception) {
            error.postValue(RegistrationException(RegistrationException.ExceptionType.INCORRECT_ACCOUNT_NAME))
        }
    }

    private suspend fun getAccount(accountNameOdId: String) =
        withContext(DefaultDispatcher) { frameworkService.getAccount(accountNameOdId) }

}