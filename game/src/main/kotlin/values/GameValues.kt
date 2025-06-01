package values

import models.ClientPreference

object GameValues {
    private var clientPreference: ClientPreference? = null

    fun getClientPreference(): ClientPreference = clientPreference!!

    fun setClientPreference(clientPreference: ClientPreference) {
        this.clientPreference = clientPreference
    }
}