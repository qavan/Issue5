package com.example.issue5

import android.util.Log
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ComposableItem(
    modifier: Modifier,
    stateHolder: StateHolder,
) {
    val transition = updateTransition(targetState = stateHolder.selection, label = "Transition selection")
    val alpha = transition.animateFloat(label = "Alpha") {
        if (it) 1f else .7f
    }
    val translateX = transition.animateDp(label = "Translate") {
        if (it) -(16).dp else 0.dp
    }
    val rotationZ = transition.animateFloat(label = "Transform") {
        if (it) 180f else 0f
    }
    val alpha1 = transition.animateFloat(label = "Color1") {
        if (it) 1f else .5f
    }
    val alpha2 = transition.animateFloat(label = "Color2") {
        if (!it) 1f else .4f
    }
    Row(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .graphicsLayer {
                this@graphicsLayer.translationX = translateX.value.toPx()
            }
            .graphicsLayer {
                this@graphicsLayer.alpha = alpha.value
            }
            .graphicsLayer {
                this@graphicsLayer.rotationZ = rotationZ.value
            },
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .drawWithCache {
                    onDrawBehind {
                        drawRect(Color.Green, alpha = alpha1.value)
                    }
                }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(Color.Gray)
                .clickable {
                    stateHolder.changeSelection()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier,
                text = "Click here",
                fontSize = 20.sp,
            )
        }
        Box(
            modifier = Modifier
                .size(60.dp)
                .drawWithCache {
                    onDrawBehind {
                        drawRect(Color.Red, alpha = alpha2.value)
                    }
                }
        )
    }
    LaunchedEffect(Unit) {
        Log.w(BuildConfig.APPLICATION_ID, "Launched")
    }
}