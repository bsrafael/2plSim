package model

import enums.LockState

data class Record(
        val label: String,
        var value: Int,
        var lockState: LockState
)
