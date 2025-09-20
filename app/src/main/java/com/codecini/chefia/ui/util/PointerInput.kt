package com.codecini.chefia.ui.util

import android.annotation.SuppressLint
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput

@SuppressLint("ModifierFactoryUnreferencedReceiver", "ReturnFromAwaitPointerEventScope")
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.clearHoverFocus(): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            if (event.type == PointerEventType.Move) {
                event.changes.forEach { it.consume() }
            }
        }
    }
}

