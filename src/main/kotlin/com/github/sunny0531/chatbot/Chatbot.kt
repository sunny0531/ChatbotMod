package com.github.sunny0531.chatbot

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import net.minecraft.client.MinecraftClient
import net.minecraft.network.MessageType
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.text.LiteralText
import org.apache.commons.io.FileUtils
import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.tensorflow.SavedModelBundle
import org.tensorflow.Tensor
import org.tensorflow.ndarray.NdArrays
import org.tensorflow.ndarray.Shape
import org.tensorflow.types.TFloat32
import java.io.File
import java.net.URL
import java.util.*
import kotlin.concurrent.thread


// For support join https://discord.gg/v6v4pMv
@Suppress("unused")
lateinit var encoder: JsonElement
lateinit var jobs: JsonElement
lateinit var responses: JsonElement
lateinit var wordDict: JsonElement
lateinit var modelFolder: File
lateinit var bundle: SavedModelBundle
val punctuation = """!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~"""
fun init() {
    println("Initializing...")
    val directory = File(System.getProperty("user.home") + File.separator + ".chatbot")
    val encoderFile: File = File(directory.path + File.separator + "encoder.json")
    val jobsFile: File = File(directory.path + File.separator + "jobs.json")
    val modelTar: File = File(directory.path + File.separator + "model.tar.gz")
    val responsesFile: File = File(directory.path + File.separator + "responses.json")
    val wordDictFile: File = File(directory.path + File.separator + "word_dict.json")
    modelFolder = File(directory.path + File.separator + "model")
    val tarFile: File = File(directory.path + File.separator + "model.tar")
    val httpClient = HttpClients.createDefault()
    var webResult: Int = 0
    httpClient.use { httpClient ->
        val request = HttpGet("https://raw.githubusercontent.com/sunny0531/chatbot/mod/version.txt")
        val response: CloseableHttpResponse = httpClient.execute(request)
        response.use { response ->
            val entity: HttpEntity = response.entity
            webResult = EntityUtils.toString(entity).toInt()
        }
    }
    val localFile = File(directory.path + File.separator + "version.txt")
    var localResult: Int = 0
    if (localFile.exists()) {
        localResult = localFile.readText().toInt()
    }
    FileUtils.copyURLToFile(
        URL("https://raw.githubusercontent.com/sunny0531/chatbot/mod/version.txt"),
        File(directory.path + File.separator + "version.txt")
    )
    if (localResult < webResult) {
        FileUtils.copyURLToFile(
            URL("https://raw.githubusercontent.com/sunny0531/chatbot/mod/encoder_reverse.json"),
            encoderFile
        )
        FileUtils.copyURLToFile(
            URL("https://raw.githubusercontent.com/sunny0531/chatbot/mod/jobs.json"),
            jobsFile
        )
        FileUtils.copyURLToFile(
            URL("https://github.com/sunny0531/chatbot/blob/mod/model.tar.gz?raw=true"),
            modelTar
        )
        FileUtils.copyURLToFile(
            URL("https://raw.githubusercontent.com/sunny0531/chatbot/mod/responses.json"),
            responsesFile
        )
        FileUtils.copyURLToFile(
            URL("https://raw.githubusercontent.com/sunny0531/chatbot/mod/word_dict.json"),
            wordDictFile
        )
        unTar.main(arrayOf(modelTar.path, directory.path))
    }
    modelFolder.renameTo(File(directory.path + File.separator + "model"))
    encoder = JsonParser().parse(encoderFile.readText())
    jobs = JsonParser().parse(jobsFile.readText())
    responses = JsonParser().parse(responsesFile.readText())
    wordDict = JsonParser().parse(wordDictFile.readText())
    bundle = SavedModelBundle.load(modelFolder.toString(), "serve")
}

fun respond(message: String) {
    if (message[0]!="/"[0]) {
        val mc: MinecraftClient = MinecraftClient.getInstance()
        val reply = runModel(message)
        //println(runModel(message))
        //mc.player!!.sendChatMessage(reply)
        thread {
            Thread.sleep(500)
            mc.inGameHud.addChatMessage(
                MessageType.SYSTEM,
                LiteralText("<Chatbot> $reply"),
                mc.player?.uuid ?: UUID.randomUUID()
            )
        }
    }
}
