package com.sfyc.ctpv

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * [ClockTimeFormatter.format] 的单元测试。
 * 覆盖零值、正常值、跨小时、亚秒截断、负数等边界场景。
 */
class FormatClockTimeTest {

    @Test
    fun `0毫秒应返回00-00-00`() {
        assertEquals("00:00:00", ClockTimeFormatter.format(0L))
    }

    @Test
    fun `5秒`() {
        assertEquals("00:00:05", ClockTimeFormatter.format(5000L))
    }

    @Test
    fun `59秒`() {
        assertEquals("00:00:59", ClockTimeFormatter.format(59000L))
    }

    @Test
    fun `1分钟整`() {
        assertEquals("00:01:00", ClockTimeFormatter.format(60000L))
    }

    @Test
    fun `12分3秒`() {
        assertEquals("00:12:03", ClockTimeFormatter.format(723000L))
    }

    @Test
    fun `1小时2分3秒`() {
        assertEquals("01:02:03", ClockTimeFormatter.format(3723000L))
    }

    @Test
    fun `10小时15分30秒`() {
        assertEquals("10:15:30", ClockTimeFormatter.format(36930000L))
    }

    @Test
    fun `亚秒毫秒值应被截断`() {
        assertEquals("00:00:05", ClockTimeFormatter.format(5999L))
    }

    @Test
    fun `负数毫秒应视为0`() {
        assertEquals("00:00:00", ClockTimeFormatter.format(-100L))
    }
}
