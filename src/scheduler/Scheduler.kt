package scheduler

import data.Transactions
import enums.LockState
import model.Operation
import model.Record
import model.Transaction

class Scheduler {

    private var database: MutableMap<String, Record> = mutableMapOf()
    private var memory: MutableMap<String, Int> = mutableMapOf()

    var transactions: List<Transaction> = emptyList()
    var initialHistory: MutableList<Operation> = mutableListOf()
    var finalHistory: List<Operation> = emptyList()

    init {
        transactions = Transactions().list

        buildDatabase()
        buildInitialHistory()
        buildFinalHistory()
    }

    private fun buildDatabase() {
        database["a"] = Record("a", 500, LockState.UNLOCKED)
        database["b"] = Record("b", 300, LockState.UNLOCKED)

        memory["a"] = 0
        memory["b"] = 0
    }

    fun run() {
        println("\n")
        println("Valores iniciais:")
        database.forEach { label, record ->
            println("${label}: ${record.value}")
        }
        println("\nExecutando...\n")

        finalHistory.forEach { op ->
            when(op.type) {
                "lock" -> lock(op)
                "unlock" -> unlock(op)
                "read" -> read(op)
                "update" -> update(op)
                "write" -> write(op)
            }
        }
        println("\nValores finais:")
        database.forEach { label, record ->
            println("${label}: ${record.value}")
        }
        println("\n\n")

    }

    private fun lock(operation: Operation) {
        operation.apply {
            if (value!!.compareTo("exclusive") == 0) {
                database[target]?.lockState = LockState.EXCLUSIVE_LOCK
            } else {
                database[target]?.lockState = LockState.SHARED_LOCK
            }
        }
    }

    private fun unlock(operation: Operation) {
        database[operation.target]?.lockState = LockState.UNLOCKED
    }

    private fun read(operation: Operation) {
        operation.apply {
            memory[target] = database[target]!!.value
            println("${transaction} | ${target} read  : ${memory[target]}")
        }
    }

    private fun update(operation: Operation) {
        val toAdd = operation.value!!.toInt()
        operation.apply {
            var mem = memory[target]!!
            mem += toAdd
            memory[target] = mem
            println("${transaction} | ${target} update: ${memory[target]!!}")
        }
    }

    private fun write(operation: Operation) {
        operation.apply {
            database[target]!!.value = memory[target]!!
            println("${transaction} | ${target} write : ${database[target]!!.value}")
        }
    }


    private fun buildFinalHistory() {
        var queue = mutableListOf<Operation>()
        var toBeAnalyzed = initialHistory

        // step 1: prioritize exclusive lock transactions
        toBeAnalyzed.forEach { current ->
            if (current.type.compareTo("lock") == 0 &&
                    current.value?.compareTo("exclusive") == 0) {
                val relatedOps = toBeAnalyzed.filter { op -> current.transaction.compareTo(op.transaction) == 0 }
                queue.addAll(relatedOps)
                toBeAnalyzed = toBeAnalyzed.filter { op -> current.transaction.compareTo(op.transaction) != 0 } as MutableList<Operation>
            }
        }

        // step 2: the rest
        toBeAnalyzed.forEach { current ->
            queue.add(current)
        }

        this.finalHistory = queue
    }

    private fun buildInitialHistory() {
        var i: Int = 0
        var totalOps = 0
        transactions.forEach { t -> totalOps += t.operations.size }

        while (totalOps > 0) {
            var added = 0
            transactions.forEach { transaction ->
                transaction.operations.apply {
                    if (i <= lastIndex) {
                        added += 1
                        this[i].transaction = transaction.identifier
                        initialHistory.add(this[i])
                    }
                }
            }
            i++
            totalOps -= added
        }

    }
}