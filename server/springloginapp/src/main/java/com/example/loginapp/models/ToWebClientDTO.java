package com.example.loginapp.models;


public record ToWebClientDTO(int keyboardsInUse, int maxAvailableKeyboards, int currentTempo, String command,
                             boolean wasCommandExecuted, boolean consumerConnected, boolean startCommandReceived,
                             int barLength, String title,String composerName) {


}
