package de.nullgrad.pocketband.di

import kotlinx.coroutines.Dispatchers

// we want to register different CoroutineDispatcher here, so we
// need to resort to using dummy/annotation interface to distinguish
// between them.
//
// Users can inject them as follows:
// ioDispatcher: CoroutineContext = LOCATOR.get(IoDispatcher::class)

interface IoDispatcher : Service
interface DefaultDispatcher : Service

object DiCoroutines {
    private fun registerIoDispatcher() {
        LOCATOR.register(IoDispatcher::class) {
            Dispatchers.IO
        }
    }

    private fun registerDefaultDispatcher() {
        LOCATOR.register(DefaultDispatcher::class) {
            Dispatchers.Default
        }
    }

    fun initialize() {
        registerIoDispatcher()
        registerDefaultDispatcher()
    }
}
