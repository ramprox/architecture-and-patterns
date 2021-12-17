package ru.ramprox.structural.bridge.page;

import ru.ramprox.structural.bridge.theme.Theme;

public class Careers implements WebPage {

    protected Theme theme;

    public Careers(Theme theme) {
        this.theme = theme;
    }

    @Override
    public String getContent() {
        return "Careers page in " + theme.getColor();
    }
}
