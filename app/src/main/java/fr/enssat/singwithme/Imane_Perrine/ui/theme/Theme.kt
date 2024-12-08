package fr.enssat.singwithme.Imane_Perrine.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun SingWithMeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColors(
            primary = Purple500,
            primaryVariant = Purple700,
            secondary = Teal200
        )
    } else {
        lightColors(
            primary = Purple500,
            primaryVariant = Purple700,
            secondary = Teal200
        )
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}