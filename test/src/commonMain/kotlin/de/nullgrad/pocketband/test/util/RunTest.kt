package de.nullgrad.pocketband.test.util

import de.nullgrad.pocketband.di.DefaultDispatcher
import de.nullgrad.pocketband.di.IoDispatcher
import de.nullgrad.pocketband.di.LOCATOR
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

private fun injectTestingCoroutines(scheduler: TestCoroutineScheduler) {
    LOCATOR.register(IoDispatcher::class) {
        StandardTestDispatcher(scheduler)
    }
    LOCATOR.register(DefaultDispatcher::class) {
        StandardTestDispatcher(scheduler)
    }
}

/* like runTest but injects testing coroutines */
fun runTestDi(
    testBody: suspend TestScope.() -> Unit
): TestResult =
    runTest {
        injectTestingCoroutines(testScheduler)
        testBody()
    }

