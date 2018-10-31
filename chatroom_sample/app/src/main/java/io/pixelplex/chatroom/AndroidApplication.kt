package io.pixelplex.chatroom

import android.app.Application
import io.pixelplex.chatroom.data.api.*
import io.pixelplex.chatroom.data.auth.AuthorizationRepository
import io.pixelplex.chatroom.data.cache.*
import io.pixelplex.chatroom.data.contract.MessageRepository
import io.pixelplex.chatroom.data.contract.RoomRepository
import io.pixelplex.chatroom.data.contract.internal.MessageRepositoryImpl
import io.pixelplex.chatroom.data.contract.internal.RoomRepositoryImpl
import io.pixelplex.chatroom.data.provider.ContractCodeProvider
import io.pixelplex.chatroom.data.service.ContractService
import io.pixelplex.chatroom.data.service.EchoFrameworkService
import io.pixelplex.chatroom.data.service.RoomsService
import io.pixelplex.chatroom.data.service.UserService
import io.pixelplex.chatroom.data.service.internal.ContractServiceImpl
import io.pixelplex.chatroom.data.service.internal.EchoFrameworkServiceImpl
import io.pixelplex.chatroom.data.service.internal.RoomsServiceImpl
import io.pixelplex.chatroom.data.service.internal.UserServiceImpl
import io.pixelplex.chatroom.presentation.addingroom.createownroom.CreateOwnRoomViewModel
import io.pixelplex.chatroom.presentation.addingroom.jointoroom.JoinToRoomViewModel
import io.pixelplex.chatroom.presentation.base.BaseActivityViewModel
import io.pixelplex.chatroom.presentation.launcher.LauncherViewModel
import io.pixelplex.chatroom.presentation.login.LoginViewModel
import io.pixelplex.chatroom.presentation.main.MainViewModel
import io.pixelplex.chatroom.presentation.messages.ChatViewModel
import io.pixelplex.chatroom.presentation.registration.RegistrationViewModel
import okhttp3.Interceptor
import org.echo.mobile.framework.core.crypto.CryptoCoreComponent
import org.echo.mobile.framework.core.crypto.internal.CryptoCoreComponentImpl
import org.echo.mobile.framework.model.network.Echodevnet
import org.koin.android.ext.android.startKoin
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

/**
 * Root context of application
 *
 * <p>
 *     Initialize all necessary dependencies and entities required to project
 * </p>
 */
class AndroidApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(
            this,
            listOf(
                launcherModule,
                baseModule,
                mainModule,
                chatModule,
                authModule,
                networkModule,
                storageModule,
                addRoomModule
            )
        )
    }

    private val mainModule = module {
        viewModel { MainViewModel(get(), get()) }
    }

    private val chatModule = module {
        viewModel { ChatViewModel(get(), get()) }
    }

    private val addRoomModule = module {
        viewModel { CreateOwnRoomViewModel(get(), get(), get()) }
        viewModel { JoinToRoomViewModel(get(), get(), get()) }
    }

    private val baseModule: Module = module {
        viewModel { BaseActivityViewModel(get()) }
    }

    private val launcherModule: Module = module {
        viewModel { LauncherViewModel(get()) }
    }

    private val authModule: Module = module {
        viewModel { RegistrationViewModel(get(), get(), get(), get()) }
        viewModel { LoginViewModel(get(), get(), get(), get()) }
        single { AuthorizationRepository(get(), get(), get()) }
        single { CryptoCoreComponentImpl(Echodevnet()) as CryptoCoreComponent }
        single { UserServiceImpl(get()) as UserService }
        single { LruCache() as Cache }
        single { ContractServiceImpl(get(), get()) as ContractService }
        single { EchoFrameworkServiceImpl() as EchoFrameworkService }
        single { ContractCodeProvider(applicationContext) }
        single {
            RoomRepositoryImpl(
                get(),
                get(),
                get(),
                get()
            ) as RoomRepository
        }
        single {
            MessageRepositoryImpl(
                get(),
                get()
            ) as MessageRepository
        }
    }

    private val networkModule: Module = module {
        factory { provideOkHttpClient(get()) }
        factory { provideLoggingInterceptor() as Interceptor }
        factory { provideGson(Echodevnet()) }
        factory("devRetrofit") {
            provideRetrofit(
                FAUCET_API_URL,
                get(),
                get()
            )
        }
        single { provideApiService(get("devRetrofit")) }
    }

    private val storageModule: Module = module {
        single { RoomsServiceImpl(get()) as RoomsService }
        factory { ObjectStorage(cacheDir) }
        factory { StandardJavaSerializer() as Serializer }
    }

}