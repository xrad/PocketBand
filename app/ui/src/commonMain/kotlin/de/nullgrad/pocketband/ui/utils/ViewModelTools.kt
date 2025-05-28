package de.nullgrad.pocketband.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

@Composable
fun rememberViewModelStoreOwner() : ViewModelStoreOwner {
    return remember {
        object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }
    }
}

@Composable
inline fun <reified VM : ViewModel> rememberTemporaryViewmodel(
    key: String? = null,
    noinline initializer: (CreationExtras.() -> VM)? = null,
) : VM {
    val viewModelStoreOwner = rememberViewModelStoreOwner()
    DisposableEffect(key1 = key) {
        onDispose {
            viewModelStoreOwner.viewModelStore.clear()
        }
    }
    val factory: ViewModelProvider. Factory? =
        if (initializer != null) viewModelFactory { initializer(initializer) } else null
    return viewModel(
        key = key,
        viewModelStoreOwner = viewModelStoreOwner,
        factory = factory,
    )
}

