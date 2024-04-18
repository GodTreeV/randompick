package com.app.randompick

import android.app.Application
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions

var debug = false

class SeedViewModel(app: Application, private val controller: NavController) : AndroidViewModel(app) {

    private val seedDao by lazy { SeedDb.getInstance(app).getDao() }

    var reApplySeedGroup: SeedGroups? = null

    fun navigateTo(@IdRes id: Int, args: Bundle? = null) {
        controller.navigate(
            id,
            args,
            NavOptions.Builder().setEnterAnim(androidx.appcompat.R.anim.abc_fade_in).setExitAnim(
                androidx.appcompat.R.anim.abc_fade_out
            ).build()
        )
    }

    fun useDao(use: (SeedDao) -> Unit, back: (() -> Unit)? = null) {
        mainLaunch {
            ioLaunch {
                use(seedDao)
                back?.invoke()
            }
        }
    }
}

class SeeViewModleFactory(private val app: Application, private val controller: NavController) :
    ViewModelProvider.AndroidViewModelFactory(app) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SeedViewModel(app, controller) as T
    }
}