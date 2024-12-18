package com.yourssu.soomsil.usaint.ui.entities

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.yourssu.soomsil.usaint.R

@Immutable
class Grade(private val tier: String) {

    @DrawableRes
    val id: Int = getTierResourceId(tier)

    @DrawableRes
    private fun getTierResourceId(resIdString: String): Int {
        return when (resIdString) {
            "A+" -> R.drawable.ic_tier_ap
            "A0" -> R.drawable.ic_tier_a0
            "A-" -> R.drawable.ic_tier_am
            "B+" -> R.drawable.ic_tier_bp
            "B0" -> R.drawable.ic_tier_b0
            "B-" -> R.drawable.ic_tier_bm
            "C+" -> R.drawable.ic_tier_cp
            "C0" -> R.drawable.ic_tier_c0
            "C-" -> R.drawable.ic_tier_cm
            "D+" -> R.drawable.ic_tier_dp
            "D0" -> R.drawable.ic_tier_d0
            "D-" -> R.drawable.ic_tier_dm
            "P" -> R.drawable.ic_tier_pass
            "F" -> R.drawable.ic_tier_fail
            else -> R.drawable.ic_tier_unknown
        }
    }

    fun toGrade(): Score = tier.toGrade()

    companion object {
        val Unknown = Grade("?")
    }
}
