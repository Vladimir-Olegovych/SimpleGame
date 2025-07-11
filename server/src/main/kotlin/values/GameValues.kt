package org.example.values

import org.example.models.ServerPreference

object GameValues {
    private var serverPreference: ServerPreference? = null

    fun getServerPreference(): ServerPreference = serverPreference!!

    fun setServerPreference(serverPreference: ServerPreference) {
        this.serverPreference = serverPreference
    }
}