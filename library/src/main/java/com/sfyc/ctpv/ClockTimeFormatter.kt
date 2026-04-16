package com.sfyc.ctpv

/**
 * 时钟格式化工具，将毫秒数转为 HH:MM:SS 格式。
 * 独立于 View 类以便于 JVM 单元测试（避免 Android 框架依赖）。
 */
object ClockTimeFormatter {

    /**
     * 将毫秒数格式化为 "HH:MM:SS" 字符串。
     *
     * @param millis 毫秒数，<= 0 时返回 "00:00:00"
     * @return 格式化后的时间字符串，例如 "01:02:03"
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
