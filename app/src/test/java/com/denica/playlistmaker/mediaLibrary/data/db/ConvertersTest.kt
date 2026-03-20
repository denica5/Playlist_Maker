package com.denica.playlistmaker.mediaLibrary.data.db

import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `trackId list converts to json and back`() {
        val input = listOf(1L, 2L, 3L)
        val json = converters.fromTrackIdList(input)
        val output = converters.toTrackIdList(json)
        assertEquals(input, output)
    }

    @Test
    fun `empty trackId list converts to json and back`() {
        val input = emptyList<Long>()
        val json = converters.fromTrackIdList(input)
        val output = converters.toTrackIdList(json)
        assertEquals(input, output)
    }
}

