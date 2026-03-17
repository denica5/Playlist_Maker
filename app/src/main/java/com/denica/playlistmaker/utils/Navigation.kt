package com.denica.playlistmaker.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

inline fun Fragment.runIfCurrentDestination(
    @IdRes destinationId: Int,
    block: () -> Unit,
) {
    val navController = findNavController()
    if (navController.currentDestination?.id == destinationId) {
        block()
    }
}

