package com.example.grievify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.DetectIntentRequest
import com.google.cloud.dialogflow.v2.DetectIntentResponse
import com.google.cloud.dialogflow.v2.QueryInput
import com.google.cloud.dialogflow.v2.SessionName
import com.google.cloud.dialogflow.v2.SessionsClient
import com.google.cloud.dialogflow.v2.SessionsSettings
import com.google.cloud.dialogflow.v2.TextInput
import com.example.grievify.adapters.ChatBotAdapter
import com.example.grievify.models.Message
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList
import java.util.UUID


class ChatBot : AppCompatActivity() {
    private var messageList: ArrayList<Message> = ArrayList()
    private lateinit var chatView:RecyclerView
    private lateinit var btnSend:ImageButton
    private lateinit var editMessage:EditText
    //dialogFlow
    private var sessionsClient: SessionsClient? = null
    private var sessionName: SessionName? = null
    private val uuid = UUID.randomUUID().toString()
    private val TAG = "mainactivity"
    private lateinit var chatAdapter: ChatBotAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)
        chatView = findViewById(R.id.chatView)
        btnSend = findViewById(R.id.btnSend)
        editMessage = findViewById(R.id.editMessage)
        //setting adapter to recyclerview
        chatAdapter = ChatBotAdapter(this, messageList)
        chatView.adapter = chatAdapter

        //onclick listener to update the list and call dialogflow
        btnSend.setOnClickListener {
            val message: String = editMessage.text.toString()
            if (message.isNotEmpty()) {
                addMessageToList(message, false)
                sendMessageToBot(message)
            } else {
                Toast.makeText(this@ChatBot, "Please enter text!", Toast.LENGTH_SHORT).show()
            }
        }

        //initialize bot config
        setUpBot()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun addMessageToList(message: String, isReceived: Boolean) {
        messageList.add(Message(message, isReceived))
        editMessage.setText("")
        chatAdapter.notifyDataSetChanged()
        chatView.layoutManager?.scrollToPosition(messageList.size - 1)
    }
    private fun setUpBot() {
        try {
            val stream = this.resources.openRawResource(R.raw.credentials)
            val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform")
            val projectId: String = (credentials as ServiceAccountCredentials).projectId
            val settingsBuilder: SessionsSettings.Builder = SessionsSettings.newBuilder()
            val sessionsSettings: SessionsSettings = settingsBuilder.setCredentialsProvider(
                FixedCredentialsProvider.create(credentials)
            ).build()
            sessionsClient = SessionsClient.create(sessionsSettings)
            sessionName = SessionName.of(projectId, uuid)
            Log.d(TAG, "projectId : $projectId")
        } catch (e: Exception) {
            Log.d(TAG, "setUpBot: " + e.message)
        }
    }

    private fun sendMessageToBot(message: String) {
        val input = QueryInput.newBuilder()
            .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build()
        GlobalScope.launch {
            sendMessageInBg(input)
        }
    }

    private suspend fun sendMessageInBg(
        queryInput: QueryInput
    ) {
        withContext(Default) {
            try {
                val detectIntentRequest = DetectIntentRequest.newBuilder()
                    .setSession(sessionName.toString())
                    .setQueryInput(queryInput)
                    .build()
                val result = sessionsClient?.detectIntent(detectIntentRequest)
                if (result != null) {
                    runOnUiThread {
                        updateUI(result)
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.d(TAG, "doInBackground: " + e.message)
                e.printStackTrace()
            }
        }
    }

    private fun updateUI(response: DetectIntentResponse) {
        val botReply: String = response.queryResult.fulfillmentText
        if (botReply.isNotEmpty()) {
            addMessageToList(botReply, true)
        } else {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
        }
    }
}
