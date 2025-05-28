package de.nullgrad.pocketband.test.util

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

suspend fun Job.joinTimeout(
    timeout: Duration = 1000.toDuration(DurationUnit.MILLISECONDS)
) : Boolean {
    val res = withTimeoutOrNull(timeout) {
        join()
        1
    }
    if (res == null) {
        cancelAndJoin()
    }
    return res == 1
}