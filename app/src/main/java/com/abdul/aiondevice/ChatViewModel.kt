package com.abdul.aiondevice

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class Chatmessage(
    val text:String,
    val fromuser:Boolean

)
 class ChatViewModel(app: Application): AndroidViewModel(app){



     private val _message = MutableStateFlow(emptyList<Chatmessage>())

     val messages = _message.asStateFlow()

     private val _isGenerateing = MutableStateFlow(false)
     val isGenerating = _isGenerateing.asStateFlow()


     private val _isReady = MutableStateFlow(false)
     val isReady = _isReady.asStateFlow()

     private val _error = MutableStateFlow<String?>(null)
     val error = _error.asStateFlow()

     private val _status = MutableStateFlow<String?>("Preparing model")
     val status = _status.asStateFlow()


     private var gemmaEngine: GemmaEngine?=null

     init {
         viewModelScope.launch {
             try {
                 _status.value="Preparing Model...."

                 ModelDownloader.ensureModel(getApplication())
                 _status.value="Loading Ready"

                 gemmaEngine= GemmaEngine.create(getApplication())
                 _status.value=""
                 _isReady.value=true

             }catch (e:Exception){
                 Log.d("WRONG","somethingwend wrong ${e.message}")
                _status.value="something went wrong ${e.message}"
             }
             }
         }
     fun send(userText:String) {


         val engine= gemmaEngine?: return
         if (_isGenerateing.value) return

         viewModelScope.launch {

             _message.value += Chatmessage(userText,true)
             _message.value += Chatmessage("",false)
             _isGenerateing.value=true
             try {
                 engine.replay(userText).collect {
                     val current = _message.value.toMutableList()
                     val last = current.last()
                     current[current.size - 1] = last.copy(text = last.text.plus(it))
                     _message.value = current
                 }
             }catch (
                 e:Exception
             ){
                 val current = _message.value.toMutableList()
                 val last = current.last()
                 current[current.size-1] =last.copy(text =" something went wrong")
                 _message.value= current
             }finally {
                 _isGenerateing.value=false
             }
         }
     }

     override fun onCleared() {

         super.onCleared()
         gemmaEngine?.close()
     }
     }










