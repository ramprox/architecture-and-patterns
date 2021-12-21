package ru.ramprox.behavioral.mediator;

import java.time.LocalDateTime;

public class ChatRoom implements ChatRoomMediator {
    @Override
    public void showMessage(User user, String message) {
        LocalDateTime dateTime = LocalDateTime.now();
        String sender = user.getName();
        System.out.println(dateTime + " [" + sender + "]: " + message);
    }
}
