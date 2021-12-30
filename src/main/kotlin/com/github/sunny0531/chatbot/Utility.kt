package com.github.sunny0531.chatbot

import com.google.gson.JsonArray


fun <T : Comparable<T>> Iterable<T>.argmax(): Int? {
    return withIndex().maxByOrNull { it.value }?.index
}

fun textToSequence(a: String): MutableList<Double> {
    var result = mutableListOf<Double>()
    for (i: String in a.split(" ")) {
        try {
            result.add(wordDict.asJsonObject[i].asDouble)
        } catch (_: NullPointerException) {

        }
    }

    return result
}


fun toList(array: JsonArray): List<String> {
    var out = mutableListOf<String>()
    for (i in 0 until array.size()) {
        out.add(array[i].asString)
    }
    return out
}

fun padSequences(a: MutableList<Any>, shape: Int): MutableList<Any> {
    if (shape != a.size) {
        while (a.size != shape) {
            a.add(0, 0.0)
        }
    }
    return a
}
