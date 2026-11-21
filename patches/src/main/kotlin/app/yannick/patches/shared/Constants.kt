package app.yannick.patches.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Constants {
    val WETTERONLINE_COMPATIBILITY = Compatibility(
        name = "WetterOnline",
        packageName = "de.wetteronline.wetterapp",
        apkFileType = ApkFileType.APKM,
        appIconColor = 0xB8D3E2,
        targets = listOf(
            AppTarget(
                version = "2026.22",
                isExperimental = true
            )
        )
    )
}
