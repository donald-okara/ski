package com.example.resources

import ski.shared.resources.generated.resources.Res
import ski.shared.resources.generated.resources.app_name
import ski.shared.resources.generated.resources.google_sans_bold
import ski.shared.resources.generated.resources.google_sans_regular
import ski.shared.resources.generated.resources.ian_dooley
import ski.shared.resources.generated.resources.ivana_cajina
import ski.shared.resources.generated.resources.rafaella_mendes
import ski.shared.resources.generated.resources.roboto_mono_extra_light


object Resources {
    object Strings {
        val APP_NAME = Res.string.app_name
    }
    object Font {
        val GOOGLE_SANS_EXTRA_LIGHT = Res.font.google_sans_regular
        val GOOGLE_SANS_BOLD = Res.font.google_sans_bold
        val ROBOTO_MONO_EXTRA_LIGHT = Res.font.roboto_mono_extra_light
    }

    object Images {

        //People
        val IAN = Res.drawable.ian_dooley
        val IVANA = Res.drawable.ivana_cajina
        val RAFAELLA = Res.drawable.rafaella_mendes
    }
}