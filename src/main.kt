import model.Operation
import scheduler.Scheduler

fun main() {

    val scheduler: Scheduler = Scheduler()

    println("\n\n")

    println("História inicial:")
    println("Transacao\t| Tipo\t\t| Alvo\t| Valor")
    scheduler.initialHistory.forEach {
        printOp(it)
    }

    println("\n\n")

    println("História final:")
    println("Transacao\t| Tipo\t\t| Alvo\t| Valor")
    scheduler.finalHistory.forEach {
        printOp(it)
    }

    scheduler.run()
}

fun printOp(op: Operation) {
    var s: String = ""
    s += " ${op.transaction}\t\t\t|"
    if (op.type.compareTo("read") == 0 || op.type.compareTo("lock") == 0 || op.type.compareTo("write") == 0) {
        s += " ${op.type}\t\t|"
    } else {
        s += " ${op.type}\t|"
    }
    s += " ${op.target}\t\t|"
    if (!op.value.isNullOrEmpty()) {
        s += " ${op.value}"
    }
    println(s)
}