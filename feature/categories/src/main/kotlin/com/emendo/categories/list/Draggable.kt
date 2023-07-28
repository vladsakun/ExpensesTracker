package com.emendo.categories.list

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }
@Composable
fun <T> DragTarget(
  modifier: Modifier,
  dataToDrop: T,
  content: @Composable (() -> Unit)
) {
  var currentPosition by remember { mutableStateOf(Offset.Zero) }
  val currentState = LocalDragTargetInfo.current

  Box(modifier = modifier
    .onGloballyPositioned {
      currentPosition = it.localToWindow(Offset.Zero)
    }
    .pointerInput(Unit) {
      // detect DragGestures After LongPress
      detectDragGesturesAfterLongPress(onDragStart = {
        currentState.dataToDrop = dataToDrop
        currentState.isDragging = true
        currentState.dragPosition = currentPosition + it
        currentState.draggableComposable = content
      }, onDrag = { change, dragAmount ->
        change.consume()
        currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
      }, onDragEnd = {
        currentState.isDragging = false
        currentState.dragOffset = Offset.Zero
      }, onDragCancel = {
        currentState.dragOffset = Offset.Zero
        currentState.isDragging = false
      })
    }) {
    content()
  }
}

@Composable
fun LongPressDraggable(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit
) {
  val state = remember { DragTargetInfo() }
  CompositionLocalProvider(
    LocalDragTargetInfo provides state
  ) {
    Box(
      modifier = modifier
        .fillMaxSize()
    ) {
      content()
    }
  }
}