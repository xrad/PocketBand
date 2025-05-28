package de.nullgrad.pocketband.di

import android.app.Application

private var androidApplication: Application? = null

var AndroidApplication: Application
    set(value) {
        androidApplication ?: run {
            androidApplication = value
            return
        }
        throw RuntimeException("Android Application already set")
    }
    get() {
        androidApplication?.let { return it }
        throw RuntimeException("Android Application not set")
    }

