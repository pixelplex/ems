package io.pixelplex.chatroom.data.service

import org.echo.mobile.framework.Callback
import org.echo.mobile.framework.model.Account
import org.echo.mobile.framework.model.contract.input.InputValue

/**
 * Describes functionality of service for working with contacts
 */
interface ContractService {

    /**
     * Queries required contract's [contractId] method [methodName] with params [methodParams]
     */
    fun queryContract(
        userNameOrId: String,
        contractId: String,
        methodName: String,
        methodParams: List<InputValue>,
        callback: Callback<String>
    )

    /**
     * Calls required contract's [contractId] method [methodName] with params [methodParams]
     */
    fun callContract(
        userNameOrId: String,
        password: String,
        contractId: String,
        methodName: String,
        methodParams: List<InputValue>,
        callback: Callback<Boolean>
    )

    /**
     * Encodes message [message] using private key derived from [fromAccount]
     * and it's [password] and public key from [toAccount]
     */
    fun encodeWithSignedHash(
        message: String,
        fromAccount: Account,
        password: String,
        toAccount: Account
    ): String

    /**
     * Decodes message [message] using private key derived from [fromAccount]
     * and it's [password] and public key from [toAccount]
     */
    fun decodeWithSignedHash(
        message: String,
        fromAccount: Account,
        password: String,
        toAccount: Account
    ): String

    /**
     * Parses boolean contract result
     */
    fun parseBooleanMessage(input: String): Boolean

    /**
     * Waits for created by [ownerAccountId] contract id
     */
    suspend fun waitContractId(ownerAccountId: String): String

    /**
     * Creates contract [byteCode] by [userNameOrId] signed with [password]
     */
    suspend fun createContract(userNameOrId: String, password: String, byteCode: String)

}