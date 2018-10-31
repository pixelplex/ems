package io.pixelplex.chatroom.data.service.internal

import io.pixelplex.chatroom.data.UiException
import io.pixelplex.chatroom.data.service.ContractService
import io.pixelplex.chatroom.data.service.EchoFrameworkService
import io.pixelplex.chatroom.support.toMapCallback
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import org.echo.mobile.framework.AccountListener
import org.echo.mobile.framework.Callback
import org.echo.mobile.framework.core.crypto.CryptoCoreComponent
import org.echo.mobile.framework.exception.LocalException
import org.echo.mobile.framework.model.Account
import org.echo.mobile.framework.model.AuthorityType
import org.echo.mobile.framework.model.FullAccount
import org.echo.mobile.framework.model.contract.input.InputValue
import org.echo.mobile.framework.model.contract.output.AddressOutputValueType
import org.echo.mobile.framework.model.contract.output.BooleanOutputValueType
import org.echo.mobile.framework.model.contract.output.ContractOutputDecoder
import org.echo.mobile.framework.model.operations.OperationType
import org.spongycastle.util.encoders.Hex
import java.math.BigInteger
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Implementation of [ContractService]
 */
class ContractServiceImpl(
    private val echoFrameworkService: EchoFrameworkService,
    private val cryptoCoreComponent: CryptoCoreComponent
) : ContractService {

    override fun queryContract(
        userNameOrId: String,
        contractId: String,
        methodName: String,
        methodParams: List<InputValue>,
        callback: Callback<String>
    ) {
        echoFrameworkService.execute {
            queryContract(
                userNameOrId,
                ASSET_ID, contractId, methodName, methodParams, callback
            )
        }
    }

    override fun callContract(
        userNameOrId: String,
        password: String,
        contractId: String,
        methodName: String,
        methodParams: List<InputValue>,
        callback: Callback<Boolean>
    ) {
        echoFrameworkService.execute {
            callContract(
                userNameOrId,
                password,
                ASSET_ID,
                contractId,
                methodName,
                methodParams,
                callback = callback
            )
        }
    }

    override fun encodeWithSignedHash(
        message: String,
        fromAccount: Account, password: String,
        toAccount: Account
    ): String {
        val privateKey =
            cryptoCoreComponent.getPrivateKey(fromAccount.name, password, AuthorityType.KEY)
        val encryptedMessageBytes = cryptoCoreComponent.encryptMessage(
            privateKey,
            toAccount.options.memoKey!!.key,
            BigInteger.ZERO,
            message
        )

        return encryptedMessageBytes?.let { Hex.toHexString(it) } ?: ""
    }

    override fun decodeWithSignedHash(
        message: String,
        fromAccount: Account,
        password: String,
        toAccount: Account
    ): String {
        val decodedMessage = Hex.decode(message)
        val privateKey =
            cryptoCoreComponent.getPrivateKey(fromAccount.name, password, AuthorityType.KEY)

        return cryptoCoreComponent.decryptMessage(
            privateKey,
            toAccount.options.memoKey!!.key,
            BigInteger.ZERO,
            decodedMessage
        )
    }

    override fun parseBooleanMessage(input: String): Boolean {
        val decoder = ContractOutputDecoder()
        val output = decoder.decode(input.toByteArray(), listOf(BooleanOutputValueType()))

        return (output.firstOrNull()?.value as? Boolean) ?: false
    }

    override suspend fun waitContractId(ownerAccountId: String): String {
        return suspendCancellableCoroutine { continuation ->
            echoFrameworkService.execute {
                subscribeOnAccount(ownerAccountId, object : AccountListener {
                    override fun onChange(updatedAccount: FullAccount) {
                        launch {
                            try {
                                val address = getContractAddress(updatedAccount)

                                if (address.isNotBlank()) {
                                    stopWaitingContractId(ownerAccountId)
                                    continuation.resume(address)
                                }
                            } catch (exception: Exception) {
                                continuation.resumeWithException(
                                    UiException(exception.message)
                                )
                            }
                        }
                    }
                }, object : Callback<Boolean> {
                    override fun onError(error: LocalException) {
                        continuation.resumeWithException(
                            UiException(error.message)
                        )
                    }

                    override fun onSuccess(result: Boolean) {
                    }
                })
            }
        }
    }

    private suspend fun stopWaitingContractId(ownerAccountId: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            echoFrameworkService.execute {
                unsubscribeFromAccount(ownerAccountId, object : Callback<Boolean> {
                    override fun onError(error: LocalException) {
                        continuation.resumeWithException(
                            UiException(
                                error.message
                            )
                        )
                    }

                    override fun onSuccess(result: Boolean) {
                        continuation.resume(result)
                    }
                })
            }
        }
    }

    private suspend fun getContractAddress(updatedAccount: FullAccount): String {
        val resultId =
            getCreateContractOperationResultId(updatedAccount.account!!.getObjectId())

        if (resultId.isNotBlank()) {
            val address = getContractResult(resultId)
            return "$CONTRACT_ID_TEMPLATE${decodeAddress(
                address
            )}"
        }

        return ""
    }

    private suspend fun getCreateContractOperationResultId(accountId: String): String {
        return suspendCancellableCoroutine { continuation ->
            echoFrameworkService.execute {
                getAccountHistory(
                    accountId,
                    DEFAULT_HISTORY_ID,
                    DEFAULT_HISTORY_ID,
                    HISTORY_LIMIT,
                    continuation.toMapCallback({ continuation, result ->
                        var resultId: String? = null

                        for (transaction in result.transactions) {
                            if (transaction.operation?.type == OperationType.CONTRACT_OPERATION) {
                                resultId = transaction.result?.objectId ?: ""
                                break
                            }
                        }

                        continuation.resume(resultId ?: "")
                    },
                        { continuation, error ->
                            continuation.resumeWithException(UiException(error.message))
                        })
                )
            }
        }
    }

    private suspend fun getContractResult(resultId: String): String {
        return echoFrameworkService.getContractAddressFromResult(resultId)
    }

    private fun decodeAddress(encoded: String): String {
        val decoder = ContractOutputDecoder()

        val encodedContractId =
            appendNumericPattern(encoded.substring(encoded.length / 2, encoded.length))

        val decodedResult =
            decoder.decode(encodedContractId.toByteArray(), listOf(AddressOutputValueType()))

        return decodedResult.firstOrNull()?.value.toString()
    }

    private fun appendNumericPattern(value: String): String {
        return HASH_PATTERN.substring(0, HASH_PATTERN.length - value.length) + value
    }

    override suspend fun createContract(userNameOrId: String, password: String, byteCode: String) {
        return suspendCoroutine { continuation ->
            echoFrameworkService.execute {
                createContract(
                    userNameOrId, password,
                    ASSET_ID, byteCode,
                    callback = continuation.toMapCallback { cont: Continuation<Unit>, _ ->
                        cont.resume(Unit)
                    }
                )
            }
        }
    }


    companion object {
        private const val ASSET_ID = "1.3.0"

        private const val CONTRACT_ID_TEMPLATE = "1.16."
        private const val DEFAULT_HISTORY_ID = "1.11.0"
        private const val HISTORY_LIMIT = 5

        private const val HASH_PATTERN =
            "0000000000000000000000000000000000000000000000000000000000000000" //64b
    }

}