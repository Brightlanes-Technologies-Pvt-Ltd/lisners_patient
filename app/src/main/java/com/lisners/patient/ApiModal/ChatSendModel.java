package com.lisners.patient.ApiModal;
import java.util.ArrayList;

public class ChatSendModel{

    public ChatSendModel(boolean stream, int temperature, ArrayList<Message> messages, String chatId) {
        this.stream = stream;
        this.temperature = temperature;
        this.messages = messages;
        this.chatId = chatId;
    }

    /*{
      "stream": false,
      "temperature": 0,
      "messages": [
        {
          "role": "user",
          "content": "What is Health"
        }
      ],
      "chatId": "XVUzsZtIN6lieX_mcOiPP"
    }*/
    public boolean stream;
    public int temperature;
    public ArrayList<Message> messages;
    public String chatId;

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}

