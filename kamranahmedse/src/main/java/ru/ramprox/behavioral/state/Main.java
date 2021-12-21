package ru.ramprox.behavioral.state;

import ru.ramprox.behavioral.state.writingstate.DefaultText;
import ru.ramprox.behavioral.state.writingstate.LowerCase;
import ru.ramprox.behavioral.state.writingstate.UpperCase;

public class Main {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor(new DefaultText());
        editor.type("First line");
        editor.setState(new UpperCase());
        editor.type("Second line");
        editor.type("Third line");
        editor.setState(new LowerCase());
        editor.type("Fourth line");
        editor.type("Fifth line");
    }
}
