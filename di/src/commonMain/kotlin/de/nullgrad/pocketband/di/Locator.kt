package de.nullgrad.pocketband.di

import kotlin.reflect.KClass

typealias ServiceFactory = () -> Any

interface Service

class Locator {
    data class ServiceEntry(
        val service: Any?,
        val factory: ServiceFactory
    )

    val services = mutableMapOf<KClass<out Service>, ServiceEntry>()

    // strongly typed getter which returns <T : Service> based on T
    inline fun <reified T: Service> get(): T {
        services[T::class]?.let {
            var service = it.service
            if (service == null) {
                service = it.factory()
                services[T::class] = it.copy(service = service)
            }
            return service as T
        }
        throw IllegalStateException("Service ${T::class.simpleName} not found")
    }

    // untyped getter which returns <Any> based on <T : Service>
    inline fun <reified T: Service, reified U: Any> get(kClass: KClass<T>): U {
        services[kClass]?.let {
            var service = it.service
            if (service == null) {
                service = it.factory()
                services[kClass] = it.copy(service = service)
            }
            return service as U
        }
        throw IllegalStateException("Service ${kClass.simpleName} not found")
    }

    fun <T: Service> register(kClass: KClass<T>, factory: ServiceFactory) {
        if (services.containsKey(kClass)) {
            return
        }
        services[kClass] = ServiceEntry(null, factory)
    }

    fun clear() {
        services.clear()
    }
}

val LOCATOR = Locator()
