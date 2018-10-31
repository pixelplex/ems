package io.pixelplex.chatroom.presentation.addingroom.jointoroom

import android.arch.lifecycle.MutableLiveData
import io.pixelplex.chatroom.data.contract.RoomRepository
import io.pixelplex.chatroom.data.service.EchoFrameworkService
import io.pixelplex.chatroom.data.service.RoomsService
import io.pixelplex.chatroom.data.service.UserService
import io.pixelplex.chatroom.model.User
import io.pixelplex.chatroom.presentation.base.BaseViewModel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.echo.mobile.framework.exception.NotFoundException
import kotlin.coroutines.experimental.coroutineContext

/**
 * View model for [JoinToRoomActivity] screen
 *
 * Contains all logic of information preparing for user presentation
 */
class JoinToRoomViewModel(
    private val roomsRepository: RoomRepository,
    private val userService: UserService,
    private val echoFrameworkService: EchoFrameworkService
) : BaseViewModel() {

    val joined = MutableLiveData<Unit>()
    val error = MutableLiveData<JoinToRoomException>()

    /**
     * Join chat room [roomName] with required [contractId] and [companionNameOrId] as another participant
     */
    fun join(contractId: String, roomName: String, companionNameOrId: String) {
        launch(parent = job) {
            try {
                failFastContractId(contractId)

                val currentUser = userService.getUser()!!

                val canJoinToRoom = canJoin(currentUser.id, contractId)
                if (!canJoinToRoom) {
                    throw JoinToRoomException(JoinToRoomException.Type.WRONG_CONTRACT_ID)
                }

                joinInternal(contractId, roomName, companionNameOrId, currentUser)

                joined.postValue(Unit)
            } catch (ex: JoinToRoomException) {
                ex.printStackTrace()
                error.postValue(ex)
            } catch (ex: NotFoundException) {
                ex.printStackTrace()
                error.postValue(
                    JoinToRoomException(
                        JoinToRoomException.Type.ACCOUNT_NOT_FOUND,
                        ex.message, ex
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                error.postValue(
                    JoinToRoomException(
                        JoinToRoomException.Type.JOIN_ERROR,
                        exception.message, exception
                    )
                )
            }
        }
    }

    private suspend fun joinInternal(
        contractId: String,
        roomName: String,
        companionNameOrId: String,
        user: User
    ) =
        withContext(coroutineContext) {
            roomsRepository.joinRoom(contractId, roomName, companionNameOrId, user)
        }

    private fun failFastContractId(contractId: String) {
        val contractIdSplit = contractId.split(".")
        if (contractIdSplit.size != 3) {
            throw JoinToRoomException(JoinToRoomException.Type.WRONG_CONTRACT_ID)
        }
    }

    private suspend fun canJoin(userId: String, contractId: String) =
        withContext(coroutineContext) {
            roomsRepository.canJoinRoom(userId, contractId)
        }

    private suspend fun getAccount(accountId: String) =
        withContext(coroutineContext) {
            echoFrameworkService.getAccount(accountId)
        }

}
