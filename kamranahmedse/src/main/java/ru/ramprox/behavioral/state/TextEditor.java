package ru.ramprox.behavioral.state;

import ru.ramprox.behavioral.state.writingstate.WritingState;

public class TextEditor {
    private WritingState state;

    public TextEditor(WritingState state) {
        this.state = state;
    }

    public void setState(WritingState state) {
        this.state = state;
    }

    public void type(String words) {
        state.write(words);
    }
}
