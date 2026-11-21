package app.yannick.patches.wetteronline

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall
import app.morphe.patcher.extensions.InstructionExtensions.removeInstruction
import app.morphe.patcher.patch.patch
import app.yannick.patches.shared.Constants.WETTERONLINE_COMPATIBILITY
import com.android.tools.smali.dexlib2.AccessFlags

private const val PROMO_METHOD =
    "Lde/wetteronline/purchase/ui/b;->f(ILandroidx/compose/runtime/Composer;)V"

private object WetterOnlineHomePromoFingerprint : Fingerprint(
    definingClass = "Lcma;",
    name = "invoke",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Ljava/lang/Object;",
    parameters = listOf(
        "Ljava/lang/Object;",
        "Ljava/lang/Object;",
        "Ljava/lang/Object;"
    ),
    filters = listOf(methodCall(smali = PROMO_METHOD))
)

private object WetterOnlineSecondPromoFingerprint : Fingerprint(
    definingClass = "Lj97;",
    name = "invoke",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Ljava/lang/Object;",
    parameters = listOf(
        "Ljava/lang/Object;",
        "Ljava/lang/Object;"
    ),
    filters = listOf(methodCall(smali = PROMO_METHOD))
)

/**
 * Experimental patch for WetterOnline 2026.22.
 *
 * The APK analysis found one call site in Lcma.invoke(Object,Object,Object)
 * and one in Lj97.invoke(Object,Object). Both fingerprints are exact and
 * version compatibility is restricted to 2026.22.
 *
 * This patch has not yet been rebuilt or verified on-device with both
 * call sites removed. It also does not yet prove that all reserved ad-slot
 * height is collapsed.
 */
@Suppress("unused")
val wetterOnlineWerbefreiPromoPatch = patch(
    name = "WetterOnline: Werbefrei-Promo ausblenden",
    description = "Experimentell: entfernt die beiden bekannten Werbefrei-Promo-Aufrufe.",
    default = false
) {
    compatibleWith(WETTERONLINE_COMPATIBILITY)

    execute {
        val homeMatches = WetterOnlineHomePromoFingerprint.instructionMatches
        check(homeMatches.size == 1) {
            "WetterOnline 2026.22: Erwartete genau einen Promo-Aufruf in Lcma.invoke(), gefunden: ${homeMatches.size}."
        }

        val secondMatches = WetterOnlineSecondPromoFingerprint.instructionMatches
        check(secondMatches.size == 1) {
            "WetterOnline 2026.22: Erwartete genau einen Promo-Aufruf in Lj97.invoke(), gefunden: ${secondMatches.size}."
        }

        WetterOnlineHomePromoFingerprint.method.removeInstruction(homeMatches.single().index)
        WetterOnlineSecondPromoFingerprint.method.removeInstruction(secondMatches.single().index)
    }
}
