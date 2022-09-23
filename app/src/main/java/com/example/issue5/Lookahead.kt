@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.issue5

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LookaheadLayout
import androidx.compose.ui.layout.LookaheadLayoutScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round
import kotlinx.coroutines.launch


@Composable
fun Lookahead(
    modifier: Modifier,
    content: @Composable LookaheadLayoutScope.() -> Unit,
) {
    LookaheadLayout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val maxWidth: Int = placeables.maxOf { it.width }
        val maxHeight = placeables.maxOf { it.height }
        // Position the children.
        layout(maxWidth, maxHeight) {
            placeables.forEach {
                it.place(0, 0)
            }
        }
    }
}

fun Modifier.animatePlacement(
    lookaheadScope: LookaheadLayoutScope,
    animationSpec: AnimationSpec<IntOffset> = spring(),
) = composed {
    var offsetAnimation: Animatable<IntOffset, AnimationVector2D>? by remember { mutableStateOf(null) }

    var placementOffset: IntOffset by remember { mutableStateOf(IntOffset.Zero) }
    var targetOffset: IntOffset? by remember { mutableStateOf(null) }
    // Create a `LaunchEffect` to handle target size change. This avoids creating side effects
    // from measure/layout phase.
    LaunchedEffect(Unit) {
        snapshotFlow {
            targetOffset
        }.collect { target ->
            if (target != null && target != offsetAnimation?.targetValue) {
                offsetAnimation?.run {
                    launch {
                        animateTo(
                            targetValue = target,
                            animationSpec = animationSpec
                        )
                    }
                } ?: Animatable(target, IntOffset.VectorConverter).let {
                    offsetAnimation = it
                }
            }
        }
    }

    with(lookaheadScope) {
        onPlaced { lookaheadScopeCoordinates, layoutCoordinates ->
            // This block of code has the LookaheadCoordinates of the LookaheadLayout
            // as the first parameter, and the coordinates of this modifier as the second
            // parameter.

            // localLookaheadPositionOf returns the *target* position of this
            // modifier in the LookaheadLayout's local coordinates.
            targetOffset = lookaheadScopeCoordinates
                .localLookaheadPositionOf(
                    layoutCoordinates
                )
                .round()
            // localPositionOf returns the *current* position of this
            // modifier in the LookaheadLayout's local coordinates.
            placementOffset = lookaheadScopeCoordinates
                .localPositionOf(
                    layoutCoordinates, Offset.Zero
                )
                .round()
        }
            // The measure logic in `intermediateLayout` is skipped in the lookahead pass, as
            // intermediateLayout is expected to produce intermediate stages of a layout
            // transform. When the measure block is invoked after lookahead pass, the lookahead
            // size of the child will be accessible as a parameter to the measure block.
            .intermediateLayout { measurable, constraints, _ ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    // offsetAnimation will animate the target position whenever it changes.
                    // In order to place the child at the animated position, we need to offset
                    // the child based on the target and current position in LookaheadLayout.
                    val (x, y) = offsetAnimation?.run { value - placementOffset }
                    // If offsetAnimation has not been set up yet (i.e. in the first frame),
                    // skip the animation
                        ?: (targetOffset!! - placementOffset)
                    placeable.place(x, y)
                }
            }
    }

}