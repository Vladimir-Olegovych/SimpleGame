package org.example.core.models.settings

data class ServerPreference(
    val sensorRadius: Float = 1F,
    val maxPlayerSpeed: Float = 18F,
    val chunkRadius: Int = 5,
    val blockSize: Float = 1F,
    val chunkSize: Float = 4F,
    val dayTime: Float = 1200F,
    val eveningTime: Float = 500F,
    val nightTime: Float = 500F,
    val dawnTime: Float = 500F,
    val timeScale: Float = 10F
) {
    override fun toString(): String {
        return  "Sensor Radius:    $sensorRadius\n" +
                "Max Player Speed: $maxPlayerSpeed\n" +
                "Chunk Radius:     $chunkRadius\n" +
                "Block Size:       $blockSize\n" +
                "Chunk Size:       $chunkSize\n" +
                "dayTime:          $dayTime\n" +
                "eveningTime:      $eveningTime\n" +
                "nightTime:        $nightTime\n" +
                "dawnTime:         $dawnTime\n" +
                "timeScale:        $timeScale"

    }
}