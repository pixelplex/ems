package io.pixelplex.chatroom.presentation.main

import android.arch.lifecycle.MutableLiveData
import io.pixelplex.chatroom.data.service.RoomsService
import io.pixelplex.chatroom.data.service.UserService
import io.pixelplex.chatroom.model.Room
import io.pixelplex.chatroom.model.User
import io.pixelplex.chatroom.presentation.base.BaseViewModel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import kotlin.coroutines.experimental.coroutineContext

/**
 * View model for [MainActivity]
 *
 * Contains all logic of information preparing for user presentation
 */
class MainViewModel(
    private val userService: UserService,
    private val roomService: RoomsService
) : BaseViewModel() {

    val rooms = MutableLiveData<List<Room>>()
    val user = MutableLiveData<User>()

    val deleteError = MutableLiveData<Exception>()
    val fetchError = MutableLiveData<Exception>()

    /**
     * Removes [room] from local storage
     */
    fun deleteRoom(room: Room) {
        launch(parent = job) {
            try {
                val currentUser = userService.getUser()!!
                val updatedRooms = withContext(coroutineContext) {
                    roomService.deleteRoom(currentUser.id, room)
                }
                rooms.postValue(updatedRooms)
            } catch (exception: Exception) {
                deleteError.postValue(exception)
            }
        }
    }

    /**
     * Receives all rooms associated with chat current user
     */
    fun fetchRooms() {
        launch(parent = job) {
            try {
                val currentUser = userService.getUser()!!
                val roomsList =
                    withContext(coroutineContext) { roomService.getRooms(currentUser.id) }
                rooms.postValue(roomsList)
            } catch (exception: Exception) {
                fetchError.postValue(exception)
            }
        }
    }

    /**
     * Defines right companion participant id
     */
    fun getCompanionId(room: Room): String =
        if (room.ownerName == userService.getUser()?.name) {
            room.ownerName
        } else {
            room.companionName
        }

    /**
     * Receives current application user
     */
    fun fetchUser() {
        user.postValue(userService.getUser()!!)
    }

}
