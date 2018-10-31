package io.pixelplex.chatroom.data.provider

/**
 * Describes functionality of required data provider
 */
interface Provider<T> {

    /**
     * Provides required data with type [T]
     */
    fun provide(): T

}