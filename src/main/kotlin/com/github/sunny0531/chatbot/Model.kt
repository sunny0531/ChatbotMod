package com.github.sunny0531.chatbot

import org.tensorflow.Tensor
import org.tensorflow.ndarray.NdArrays
import org.tensorflow.ndarray.Shape
import org.tensorflow.types.TFloat32

fun runModel(message: String): String {
    var newMessage: String = ""
    message.split("").forEach {
        if (!punctuation.contains(it)) {
            newMessage += it
        }
    }
    var input: MutableList<Double> = textToSequence(newMessage)
    if (input.all { it == 0.toDouble() }) {
        return "I'm sorry, I don't understand."
    } else {
        input = bundle.signatures()[1].inputs["input_1"]?.shape?.asArray()?.get(1)
            ?.let { padSequences(input as MutableList<Any>, it.toInt()) as MutableList<Double> }!!
        val a = NdArrays.ofFloats(Shape.of(input.size.toLong(), 1))
        for (i in 0 until input.size) {
            a.setFloat(input[i].toFloat(), i.toLong(), 0)
        }

        val b = TFloat32.tensorOf(a)
        val output: Tensor =
            bundle.session().runner().feed("serving_default_input_1", b).fetch("StatefulPartitionedCall").run()[0]
        var outputList: MutableList<Float> = mutableListOf<Float>()
        for (ii in 0 until output.size()) {
            outputList.add(output.asRawTensor().data().asFloats().getFloat(ii))
        }
        b.close()
        output.close()
        return toList(
            responses.asJsonObject[encoder.asJsonObject[(outputList.argmax()).toString()].asString].asJsonArray
        ).random()
    }
}