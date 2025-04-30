package org.example

fun main() {
    val serverApplication = ServerApplication(5000)
    serverApplication.start()
    readlnOrNull()
    serverApplication.stop()
}