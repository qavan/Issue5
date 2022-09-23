@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.issue5

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadLayoutScope
import androidx.compose.ui.unit.dp

@Composable
fun ComposableRoot(
    stateHoldersHolder: StateHoldersHolder,
) {
    val items = remember {
        movableContentWithReceiverOf<LookaheadLayoutScope, List<StateHolder>> { items ->
            items.forEachIndexed { index, item ->
                Spacer(modifier = Modifier.height(12.dp))
                ComposableItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animatePlacement(this),
                    stateHolder = item,
                )
            }
        }
    }
    Lookahead(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(stateHoldersHolder.holders)
        }
    }
}