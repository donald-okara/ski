package com.example.navigation.di

import com.example.navigation.impl.Navigator
import com.example.navigation.domain.Screens
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.scope.Scope
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module

val navigationModule = module {
    singleOf(::Navigator)
}

inline fun navigationModuleHelper(crossinline block: Module.() -> Unit): Module {
    return module {
        includes(navigationModule)
        block()
    }
}

fun Scope.navigateBack() {
    get<Navigator>().back()
}

fun Scope.navigateBackOrStart() {
    get<Navigator>().backOrStart()
}

fun Scope.navigateToScreen(screen: Screens) {
    get<Navigator>().navigateToScreen(screen)
}
