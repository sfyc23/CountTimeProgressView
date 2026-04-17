package com.sfyc.ctpv

/**
 * Formats millisecond values as HH:MM:SS without depending on Android framework APIs.
 */
object ClockTimeFormatter {

    /**
     * Converts milliseconds to an "HH:MM:SS" string.
     *
     * @param millis millisecond value. Values <= 0 return "00:00:00".
     * @return formatted time, for example "01:02:03".
     */
    @JvmStatic
    fun format(millis: Long): String {
        if (millis <= 0) return "00:00:00"
        var totalTime = (millis / 1000).toInt()
        var hour = 0
        var minute = 0
        val second: Int

        if (3600 <= totalTime) {
            hour = totalTime / 3600
            totalTime -= 3600 * hour
        }
        if (60 <= totalTime) {
            minute = totalTime / 60
            totalTime -= 60 * minute
        }
        second = totalTime

        val sb = StringBuilder(8)
        if (hour < 10) sb.append("0").append(hour).append(":")
        else sb.append(hour).append(":")

        if (minute < 10) sb.append("0").append(minute).append(":")
        else sb.append(minute).append(":")

        if (second < 10) sb.append("0").append(second)
        else sb.append(second)

        return sb.toString()
    }
}
