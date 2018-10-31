package io.pixelplex.chatroom.data.service.internal

import io.pixelplex.chatroom.data.cache.ObjectStorage
import io.pixelplex.chatroom.data.service.RoomException
import io.pixelplex.chatroom.data.service.RoomsService
import io.pixelplex.chatroom.model.Account
import io.pixelplex.chatroom.model.Room
import io.pixelplex.chatroom.model.addRoom
import io.pixelplex.chatroom.model.removeRoom

/**
 * Implementation of [RoomsService] based on [ObjectStorage]
 */
class RoomsServiceImpl(private val storage: ObjectStorage) :
    RoomsService {

    private val accounts: MutableList<Account> by lazy {
        storage.getObject<MutableList<Account>>(ROOM_STORAGE_KEY) ?: mutableListOf()
    }

    override fun getRooms(accountId: String): List<Room> =
        accounts.find { it.accountId == accountId }?.rooms
            ?: throw RoomException("Account $accountId does not exist")

    override fun addRoom(accountId: String, room: Room): List<Room> {
        val requiredAccount = accounts.find { it.accountId == accountId }
            ?: throw RoomException("Account $accountId does not exist")

        requiredAccount.addRoom(room)
        persist(accounts)

        return requiredAccount.rooms
    }

    override fun deleteRoom(accountId: String, room: Room): List<Room> {
        val requiredAccount = accounts.find { it.accountId == accountId }
            ?: throw RoomException("Account $accountId does not exist")

        requiredAccount.removeRoom(room)
        persist(accounts)

        return requiredAccount.rooms
    }

    override fun addNonExistentAccount(accountId: String) {
        if (accounts.find { it.accountId == accountId } != null) return

        val newAccount = Account(accountId)
        accounts.add(newAccount)
        persist(accounts)
    }

    private fun persist(accounts: List<Account>) = storage.putObject(ROOM_STORAGE_KEY, accounts)

    companion object {
        private const val ROOM_STORAGE_KEY = "roomStorage"
    }

}

