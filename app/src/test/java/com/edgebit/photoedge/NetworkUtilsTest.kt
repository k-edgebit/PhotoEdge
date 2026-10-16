package com.edgebit.photoedge

import com.edgebit.photoedge.domain.util.NetworkUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkUtilsTest {

    @Test
    fun testFormatBytes() {
        assertEquals("0 B", NetworkUtils.formatBytes(0))
        assertEquals("1.0 KB", NetworkUtils.formatBytes(1024))
        assertEquals("1.0 MB", NetworkUtils.formatBytes(1024 * 1024))
        assertEquals("1.5 MB", NetworkUtils.formatBytes((1.5 * 1024 * 1024).toLong()))
    }
}
