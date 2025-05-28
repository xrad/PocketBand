package de.nullgrad.pocketband.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes

data object ShapingDefaults {
    internal const val EXTRA_SMALL = 2
    internal const val SMALL = 5
    internal const val MEDIUM = 7
    internal const val LARGE = 12
    internal const val EXTRA_LARGE = 15
}

fun getShapes() = Shapes(
    extraSmall = ShapeDefaults.ExtraSmall.copy(
        CornerSize(percent = ShapingDefaults.EXTRA_SMALL)
    ),
    small = ShapeDefaults.Small.copy(
        CornerSize(percent = ShapingDefaults.SMALL)
    ),
    medium = ShapeDefaults.Medium.copy(
        CornerSize(percent = ShapingDefaults.MEDIUM)
    ),
    large = ShapeDefaults.Large.copy(
        CornerSize(percent = ShapingDefaults.LARGE)
    ),
    extraLarge = ShapeDefaults.ExtraLarge.copy(
        CornerSize(percent = ShapingDefaults.EXTRA_LARGE)
    ),
)