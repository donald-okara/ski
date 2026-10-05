package ke.don.resources

import ski.shared.resources.generated.resources.*


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
        val ANDROID_ROBOT = Res.drawable.android_head_3D
    }

    object Videos {
        /** Path of the bundled sample video, relative to the resources root. Resolve it with `resolveVideoUriForPlayer`. */
        const val SAMPLE_VIDEO_PATH = "files/sample_video.mov"
    }
}