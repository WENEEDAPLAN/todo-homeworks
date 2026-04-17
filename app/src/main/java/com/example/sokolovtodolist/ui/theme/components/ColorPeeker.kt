package com.example.sokolovtodolist.ui.theme.components

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.*
import androidx.compose.ui.graphics.Color

private const val PALETTE_SIZE = 256

@Composable
fun ColorPickerScreen(
    initialColor: Int,
    onColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val paletteBitmap = remember {
        generatePaletteBitmap(PALETTE_SIZE, PALETTE_SIZE)
    }
    val paletteImage = paletteBitmap.asImageBitmap()

    val initialColorObj = Color(initialColor)
    val initialHsv = initialColorObj.toHsvManual()
    var hue by remember { mutableStateOf(initialHsv[0]) }
    var saturation by remember { mutableStateOf(initialHsv[1]) }
    var value by remember { mutableStateOf(initialHsv[2]) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Start)
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.hsv(hue, saturation, value))
        )

        BoxWithConstraints(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
            val widthPx = constraints.maxWidth
            val heightPx = constraints.maxHeight

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                event.changes.forEach { change ->
                                    if (change.pressed) {
                                        val x = (change.position.x / widthPx).coerceIn(0f, 1f)
                                        val y = (change.position.y / heightPx).coerceIn(0f, 1f)
                                        hue = x * 360f
                                        saturation = y
                                        change.consume()
                                    }
                                }
                            }
                        }
                    }
            ) {
                drawImage(paletteImage, dstSize = IntSize(widthPx, heightPx))
                val crossX = (hue / 360f) * widthPx
                val crossY = saturation * heightPx
                drawCircle(Color.White, radius = 10f, center = Offset(crossX, crossY))
                drawCircle(Color.Black, radius = 8f, center = Offset(crossX, crossY))
                drawCircle(Color.hsv(hue, saturation, value), radius = 6f, center = Offset(crossX, crossY))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Яркость", modifier = Modifier.width(50.dp))
            Slider(value = value, onValueChange = { value = it }, valueRange = 0f..1f, modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = onDismiss) { Text("Отмена") }
                Button(onClick = {
                    val selectedColor = Color.hsv(hue, saturation, value).toArgb()
                    val colorString = String.format("#%08X", selectedColor)
                    Log.d("ColorPicker", "Выбран цвет: $colorString")
                    onColorSelected(colorString)
                }) { Text("Выбрать") }
            }
        }
    }
}

private fun generatePaletteBitmap(width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    for (x in 0 until width) {
        val hue = (x.toFloat() / width) * 360f
        for (y in 0 until height) {
            val saturation = y.toFloat() / height
            val color = Color.hsv(hue, saturation, 1f).toArgb()
            bitmap.setPixel(x, y, color)
        }
    }
    return bitmap
}

private fun Color.toHsvManual(): FloatArray {
    val r = red; val g = green; val b = blue
    val max = maxOf(r, g, b); val min = minOf(r, g, b); val delta = max - min
    var hue = 0f; var saturation = 0f; val value = max
    if (delta != 0f) {
        saturation = delta / max
        when (max) {
            r -> hue = ((g - b) / delta) % 6f
            g -> hue = ((b - r) / delta) + 2f
            b -> hue = ((r - g) / delta) + 4f
        }
        hue *= 60f
        if (hue < 0) hue += 360f
    }
    return floatArrayOf(hue, saturation, value)
}