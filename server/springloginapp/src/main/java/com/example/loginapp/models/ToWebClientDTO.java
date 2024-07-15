package com.example.loginapp.models;


public record ToWebClientDTO(int keyboardsInUse, int maxAvailableKeyboards, int currentTempo, String command,
                             boolean wasCommandExecuted,boolean consumerIsConnected) {

}
