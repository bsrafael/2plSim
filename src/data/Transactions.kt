package data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import model.Transaction
import java.io.File
import java.io.FileReader
import java.lang.reflect.Type
import java.util.*


class Transactions {

    var list: List<Transaction> = emptyList()

    init {
        val path = File("").resolve("src/resources/transactions.json").absolutePath
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<Transaction?>?>() {}.type
        list = gson.fromJson<List<Transaction>>( FileReader(path), listType )
    }
}