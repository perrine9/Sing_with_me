package fr.enssat.singwithme.Imane_Perrine.Player

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Density
import androidx.compose.ui.text.TextLayoutResult

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.dp


@Composable
fun KaraokeSimpleText(text: String, progress: Float) {
    // État pour stocker la largeur totale du texte en pixels
    var textWidthPx by remember { mutableStateOf(0) }

    Box {
        // Texte complet en rouge
        Text(
            text = text,
            color = Color.Red,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .onSizeChanged {
                    textWidthPx = it.width // Stocker la largeur en pixels
                }
        )
        // Texte lu en noir (superposé)
        Text(
            text = text,
            color = Color.Black,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            modifier = Modifier
                .drawWithContent {
                    // Calculer la largeur du texte "lu" basé sur le progrès
                    val width = textWidthPx * progress
                    clipRect(left = 0f, top = 0f, right = width, bottom = size.height) {
                        this@drawWithContent.drawContent()
                    }
                }
        )
    }
}



