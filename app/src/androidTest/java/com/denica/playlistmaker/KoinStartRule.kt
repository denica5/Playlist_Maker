package com.denica.playlistmaker

import androidx.test.platform.app.InstrumentationRegistry
import com.denica.playlistmaker.di.dataModule
import com.denica.playlistmaker.di.interactorModule
import com.denica.playlistmaker.di.repositoryModule
import com.denica.playlistmaker.di.viewModelModule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

/**
 * Ensures the global Koin context is started before a test runs.
 *
 * Useful because instrumentation tests typically run in a single process and any
 * test that calls stopKoin() can break following tests.
 */
class KoinStartRule : TestWatcher() {

    override fun starting(description: Description) {
        ensureKoinStarted()
    }

    private fun ensureKoinStarted() {
        val started = try {
            GlobalContext.get()
            true
        } catch (_: IllegalStateException) {
            false
        }

        if (started) return

        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        startKoin {
            androidContext(context)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
    }
}

