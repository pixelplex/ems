package io.pixelplex.chatroom.data.contract.internal

import io.pixelplex.chatroom.data.contract.RoomRepository
import io.pixelplex.chatroom.data.provider.ContractCodeProvider
import io.pixelplex.chatroom.data.service.ContractService
import io.pixelplex.chatroom.data.service.EchoFrameworkService
import io.pixelplex.chatroom.data.service.RoomsService
import io.pixelplex.chatroom.model.Room
import io.pixelplex.chatroom.model.User
import io.pixelplex.chatroom.presentation.addingroom.jointoroom.JoinToRoomException
import io.pixelplex.chatroom.support.toMapCallback
import kotlinx.coroutines.experimental.withContext
import org.echo.mobile.framework.model.contract.input.AddressInputValueType
import kotlin.coroutines.experimental.coroutineContext
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Implementation of [RoomRepository]
 */
class RoomRepositoryImpl(
    private val echoFrameworkService: EchoFrameworkService,
    private val roomService: RoomsService,
    private val contractService: ContractService,
    private val contractCodeProvider: ContractCodeProvider
) : RoomRepository {

    override suspend fun createRoom(
        ownerNameOrId: String,
        password: String,
        companionNameOrId: String
    ): Room {
        val companion = echoFrameworkService.getAccount(companionNameOrId)
        val companionId = companion.getObjectId()
        val owner = echoFrameworkService.getAccount(ownerNameOrId)
        val ownerId = owner.getObjectId()
        val companionAddress = companionId.split(DELIMITER_ACCOUNT_ID).last()

        val contractBytecode = contractCodeProvider.provide()
        val addressByteCode = AddressInputValueType().encode(companionAddress)

        contractService.createContract(ownerNameOrId, password, contractBytecode + addressByteCode)

        val contractId = contractService.waitContractId(ownerId)

        return Room(
            ownerName = owner.name,
            companionName = companion.name,
            contractId = contractId
        )
    }

    override suspend fun canJoinRoom(userNameOrId: String, contractId: String): Boolean {
        return suspendCoroutine { continuation ->
            contractService.queryContract(
                userNameOrId, contractId,
                METHOD_CAN_JOIN_TO_ROOM, listOf(),
                continuation.toMapCallback { cont, value ->
                    cont.resume(contractService.parseBooleanMessage(value))
                }
            )
        }
    }

    override suspend fun joinRoom(
        contractId: String, roomName: String, companionNameOrId: String, user: User
    ) {
        val canJoinToRoom = withContext(coroutineContext) {
            canJoinRoom(user.id, contractId)
        }
        if (!canJoinToRoom) {
            throw JoinToRoomException(JoinToRoomException.Type.WRONG_CONTRACT_ID)
        }

        val companionAccount = withContext(coroutineContext) {
            echoFrameworkService.getAccount(companionNameOrId)
        }

        val room = Room(roomName, user.name, companionAccount.name, contractId)
        roomService.addRoom(user.id, room)
    }

    companion object {
        private const val METHOD_CAN_JOIN_TO_ROOM = "canJoinRoom"
        private const val DELIMITER_ACCOUNT_ID = "."
    }

}