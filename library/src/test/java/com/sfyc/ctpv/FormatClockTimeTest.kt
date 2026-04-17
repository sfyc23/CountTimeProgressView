package com.sfyc.ctpv

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatClockTimeTest {

    @Test
    fun `zero milliseconds returns zero clock`() {
        assertEquals("00:00:00", ClockTimeFormatter.format(0L))
    }

    @Test
    fun `formats five seconds`() {
        assertEquals("00:00:05", ClockTimeFormatter.format(5000L))
    }

    @Test
    fun `formats fifty nine seconds`() {
        assertEquals("00:00:59", ClockTimeFormatter.format(59000L))
    }

    @Test
    fun `formats exactly one minute`() {
        assertEquals("00:01:00", ClockTimeFormatter.format(60000L))
    }

    @Test
    fun `formats twelve minutes and three seconds`() {
        assertEquals("00:12:03", ClockTimeFormatter.format(723000L))
    }

    @Test
    fun `formats one hour two minutes and three seconds`() {
        assertEquals("01:02:03", ClockTimeFormatter.format(3723000L))
    }

    @Test
    fun `formats ten hours fifteen minutes and thirty seconds`() {
        assertEquals("10:15:30", ClockTimeFormatter.format(36930000L))
    }

    @Test
    fun `truncates sub second milliseconds`() {
        assertEquals("00:00:05", ClockTimeFormatter.format(5999L))
    }

    @Test
    fun `negative milliseconds return zero clock`() {
        assertEquals("00:00:00", ClockTimeFormatter.format(-100L))
    }
}
