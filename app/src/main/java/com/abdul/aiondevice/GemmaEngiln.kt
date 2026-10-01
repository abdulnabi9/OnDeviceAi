package com.abdul.aiondevice
import android.content.Context
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class GemmaEngine(private val engine: Engine, private val conversation: Conversation){
    fun replay(prompt:String): Flow<String> {
        return conversation.sendMessageAsync(prompt ).map { it.toString() }
    }
    fun close(){
        conversation.close()
        engine.close()
    }
    companion object {

        suspend fun create(context: Context): GemmaEngine =
            withContext(Dispatchers.IO) {

                val modelPath =
                    ModelDownloader.modelFile(context).absolutePath

                println("Gemma: Model path = $modelPath")

                val config = EngineConfig(
                    modelPath = modelPath,
                    backend = Backend.CPU(),
                    cacheDir = context.cacheDir.absolutePath
                )

                println("Gemma: Creating engine")

                val engine = Engine(config)

                println("Gemma: Initializing engine")


                engine.initialize()

                println("Gemma: Engine initialized = ${engine.isInitialized()}")

                val conversation = engine.createConversation(
                    ConversationConfig(
                        systemInstruction = Contents.of(
                            "You are a helpful assistant."
                        )
                    )
                )

                println("Gemma: Conversation created")

                GemmaEngine(
                    engine = engine,
                    conversation = conversation
                )
            }
    }

}