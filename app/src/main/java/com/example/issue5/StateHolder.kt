package com.example.issue5

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class StateHolder(
    val uuid: String = UUID.randomUUID().toString(),
    val items: List<String> = buildList {
        repeat(10) {
            add("Item $it")
        }
    },
    private var selectionState: Boolean = false,
) : Parcelable {
    @IgnoredOnParcel
    var selection by mutableStateOf(selectionState)

    fun changeSelection() {
        selection = !selection
        selectionState = selection
    }
}