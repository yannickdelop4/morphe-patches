package app.yannick.patches.wetteronline

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall
import app.morphe.patcher.extensions.InstructionExtensions.removeInstruction
import app.morphe.patcher.patch.patch
import app.yannick.patches.shared.Constants.WETTERONLINE_COMPATIBILITY
import com.android.tools.smali.dexlib2.AccessFlags

private object WetterOnlineWerbefreiCallFingerprint : Fingerprint(
    definingClass = "Lcma;",
    name = "invoke",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Ljava/lang/Object;",
    parameters = listOf("Ljava/lang/Object;", "Ljava/lang/Object;", "Ljava/lang/Object;"),
    filters = listOf(
        methodCall(smali = "Lde/wetteronline/purchase/ui/b;->f(ILandroidx/compose/runtime/Composer;)V")
    )
)

/**
 * Experimental only. Two call sites are confirmed in WetterOnline 2026.22:
 * Lcma;->invoke(...) and Lj97;->invoke(...). This initial patch still targets
 * only Lcma and is known not to remove the promo cards on-device.
 */
@Suppress("unused")
val wetterOnlineWerbefreiPromoPatch = patch(
    name = "WetterOnline: Werbefrei-Promo ausblenden",
    description = "Experimentell: unvollständige Entfernung der Werbefrei-Promokarte.",
    default = false
) {
    compatibleWith(WETTERONLINE_COMPATIBILITY)

    execute {
        val matches = WetterOnlineWerbefreiCallFingerprint.method.instructionMatches
        check(matches.size == 1) {
            "WetterOnline 2026.22: Erwartete genau einen Promo-Aufruf in Lcma.invoke(), gefunden: ${matches.size}."
        }
        WetterOnlineWerbefreiCallFingerprint.method.removeInstruction(matches.first())
    }
}
