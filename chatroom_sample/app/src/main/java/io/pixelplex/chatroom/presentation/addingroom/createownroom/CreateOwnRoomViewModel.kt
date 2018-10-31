package io.pixelplex.chatroom.presentation.addingroom.createownroom

import android.arch.lifecycle.MutableLiveData
import io.pixelplex.chatroom.data.contract.RoomRepository
import io.pixelplex.chatroom.data.service.RoomsService
import io.pixelplex.chatroom.data.service.UserService
import io.pixelplex.chatroom.model.Room
import io.pixelplex.chatroom.presentation.base.BaseViewModel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.echo.mobile.framework.exception.NotFoundException
import kotlin.coroutines.experimental.coroutineContext

/**
 * View model for [CreateOwnRoomActivity] screen
 *
 * Contains all logic of information preparing for user presentation
 */
class CreateOwnRoomViewModel(
    private val userService: UserService,
    private val roomRepository: RoomRepository,
    private val roomsService: RoomsService
) : BaseViewModel() {

    val success = MutableLiveData<Unit>()
    val error = MutableLiveData<CreateOwnRoomException>()

    /**
     * Creates new room with required [roomName] and [companionNameOrId] as another participant
     */
    fun newRoom(roomName: String, companionNameOrId: String) {
        launch(parent = job) {
            try {
                val user = userService.getUser()!!

                val room = createChat(user.name, user.password, companionNameOrId)
                addRoom(room, roomName, user.id)

                success.postValue(Unit)
            } catch (ex: NotFoundException) {
                ex.printStackTrace()
                error.postValue(
                    CreateOwnRoomException(
                        CreateOwnRoomException.Type.ACCOUNT_NOT_FOUND,
                        ex.message
                    )
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
                error.postValue(
                    CreateOwnRoomException(
                        CreateOwnRoomException.Type.CREATING_ERROR,
                        ex.message, ex.cause
                    )
                )

            }
        }
    }

    private suspend fun createChat(
        userName: String,
        userPassword: String,
        companionNameOrId: String
    ) =
        withContext(coroutineContext) {
            roomRepository.createRoom(userName, userPassword, companionNameOrId)
        }

    private suspend fun addRoom(
        room: Room,
        roomName: String,
        userId: String
    ) =
        withContext(coroutineContext) {
            roomsService.addRoom(userId, room.apply { name = roomName })
        }

}