package com.example.plevent.response;

public class MessageResponse {


private String message;

// Constructeur par défaut
public MessageResponse() {
}

// Constructeur avec message
public MessageResponse(String message) {
    this.message = message;
}

// Getter et Setter
public String getMessage() {
    return message;
}

public void setMessage(String message) {
    this.message = message;
}


}
