package ru.ramprox.structural.bridge.page;

import ru.ramprox.structural.bridge.theme.Theme;

public class About implements WebPage {

    protected Theme theme;

    public About(Theme theme) {
        this.theme = theme;
    }

    @Override
    public String getContent() {
        return "About page in " + theme.getColor();
    }
}
