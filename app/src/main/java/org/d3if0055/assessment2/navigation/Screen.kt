package org.d3if0055.assessment2.navigation

import org.d3if0055.assessment2.ui.screen.KEY_ID_RESEP

sealed class Screen(val route:String) {
    data object Home : Screen("mainScreen")
    data object FormBaru : Screen("detailScreen")
    data object FormUbah: Screen("detailScreen/{$KEY_ID_RESEP}") {
        fun widthId(id: Long) = "detailScreen/$id"
    }
}