package model

data class Transaction (
    val identifier: String,
    val operations: List<Operation>,
    var status: String
)