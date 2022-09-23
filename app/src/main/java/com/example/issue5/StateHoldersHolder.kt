package com.example.issue5

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
class StateHoldersHolder(
    val holders: List<StateHolder> = buildList {
        repeat(Random.nextInt(5, 8)) {
           add(StateHolder())
        }
    },
) : Parcelable