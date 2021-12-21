package ru.ramprox.behavioral.mediator;

public class Main {
    public static void main(String[] args) {
        ChatRoomMediator mediator = new ChatRoom();
        User john = new User("John Doe", mediator);
        User jane = new User("Jane Doe", mediator);

        john.send("Hi there!");
        jane.send("Hey!");
    }
}
