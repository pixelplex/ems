package io.pixelplex.chatroom.model

/**
 * Chat user model for application scope persistence.
 *
 * Lives only during application startup time
 */
data class User(val name: String, val id: String, val password: String)