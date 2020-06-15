package model

data class Operation (
        var transaction: String,
        val type: String,
        val target: String,
        var value: String? = null
)