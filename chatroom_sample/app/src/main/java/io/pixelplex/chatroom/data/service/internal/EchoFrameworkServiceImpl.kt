package io.pixelplex.chatroom.data.service.internal

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.pixelplex.chatroom.data.service.EchoFrameworkService
import io.pixelplex.chatroom.support.toCallback
import kotlinx.coroutines.experimental.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import org.echo.mobile.framework.Callback
import org.echo.mobile.framework.EchoFramework
import org.echo.mobile.framework.core.socket.SocketMessengerListener
import org.echo.mobile.framework.core.socket.internal.SocketCoreComponentImpl
import org.echo.mobile.framework.core.socket.internal.SocketMessengerImpl
import org.echo.mobile.framework.exception.LocalException
import org.echo.mobile.framework.model.Account
import org.echo.mobile.framework.model.FullAccount
import org.echo.mobile.framework.model.socketoperations.SocketMethodType
import org.echo.mobile.framework.model.socketoperations.SocketOperation
import org.echo.mobile.framework.model.socketoperations.SocketOperationKeys
import org.echo.mobile.framework.service.internal.DatabaseApiServiceImpl
import org.echo.mobile.framework.support.Api
import org.echo.mobile.framework.support.Settings
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Implementation of [EchoFrameworkService]
 */
class EchoFrameworkServiceImpl : EchoFrameworkService {

    private var framework: EchoFramework? = null

    private val stateChanel = ConflatedBroadcastChannel(false)

    init {
        initEchoFramework(ECHO_URL)
    }

    override suspend fun restart() {
        initEchoFramework(ECHO_URL)
        startEchoFramework()
    }

    private fun initEchoFramework(url: String) {
        framework?.stop()
        framework = EchoFramework.create(
            configureSettings(url)
        )
    }

    private fun configureSettings(url: String): Settings =
        Settings.Configurator()
            .setUrl(url)
            .setApis(Api.DATABASE, Api.ACCOUNT_HISTORY, Api.NETWORK_BROADCAST)
            .setSocketMessenger(configureSocket())
            .configure()

    private fun configureSocket() =
        SocketMessengerImpl().apply { on(SocketConnectivityListener()) }

    private suspend fun startEchoFramework() {
        return suspendCoroutine { cont ->
            framework?.start(object : Callback<Any> {
                override fun onError(error: LocalException) {
                    stateChanel.offer(false)
                    cont.resumeWithException(error)
                }

                override fun onSuccess(result: Any) {
                    stateChanel.offer(true)
                    cont.resume(Unit)
                }
            })
        }
    }

    override fun execute(block: EchoFramework.() -> Unit) {
        block(framework!!)
    }

    override suspend fun getAccount(name: String): Account {
        return suspendCancellableCoroutine { continuation ->
            framework!!.getAccount(name, object : Callback<FullAccount> {
                override fun onError(error: LocalException) {
                    continuation.resumeWithException(error)
                }

                override fun onSuccess(result: FullAccount) {
                    continuation.resume(result.account!!)
                }

            })
        }
    }

    override suspend fun getContractAddressFromResult(historyId: String): String {
        // will be fixed in next update
        return suspendCancellableCoroutine { continuation ->
            val fields = DatabaseApiServiceImpl::class.java.declaredFields

            val field =
                fields.find { it.name == SOCKET_CORE_COMPONENT_FIELD_NAME }
                    ?.apply { isAccessible = true }!!

            val socketComponent =
                field.get(framework!!.databaseApiService) as SocketCoreComponentImpl

            val operation =
                GetContractResultSocketOperation(
                    framework?.databaseApiService!!.id,
                    historyId,
                    callId = socketComponent.currentId,
                    callback = continuation.toCallback()
                )

            socketComponent.emit(operation)
        }
    }

    override fun observeState() = stateChanel.openSubscription()

    private inner class SocketConnectivityListener : SocketMessengerListener {
        override fun onConnected() {
        }

        override fun onDisconnected() {
            stateChanel.offer(false)
        }

        override fun onEvent(event: String) {
        }

        override fun onFailure(error: Throwable) {
        }

    }

    companion object {
        private const val ECHO_URL = "wss://echo-devnet-node.pixelplex.io/"
        private const val SOCKET_CORE_COMPONENT_FIELD_NAME = "socketCoreComponent"
    }

    class GetContractResultSocketOperation(
        override val apiId: Int,
        private val resultId: String,
        callId: Int,
        callback: Callback<String>
    ) : SocketOperation<String>(
        SocketMethodType.CALL,
        callId,
        String::class.java,
        callback
    ) {

        override fun createParameters(): JsonElement =
            JsonArray().apply {
                add(apiId)
                add(SocketOperationKeys.GET_CONTRACT_RESULT.key)
                add(JsonArray().apply { add(resultId) })
            }

        override fun fromJson(json: String): String? {
            val parser = JsonParser()
            val jsonTree = parser.parse(json)

            if (!jsonTree.isJsonObject || jsonTree.asJsonObject.get(RESULT_KEY) == null) {
                return ""
            }

            val result = jsonTree.asJsonObject.get(RESULT_KEY)?.asJsonObject

            val execResult = result?.getAsJsonObject(EXECUTION_RESULT_NAME)

            return execResult?.get(ADDRESS_RESULT_NAME)?.asString
        }

        companion object {
            private const val EXECUTION_RESULT_NAME = "exec_res"
            private const val ADDRESS_RESULT_NAME = "new_address"
        }

    }

}
