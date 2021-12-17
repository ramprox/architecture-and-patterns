package ru.ramprox.structural.bridge;

import ru.ramprox.structural.bridge.page.About;
import ru.ramprox.structural.bridge.page.Careers;
import ru.ramprox.structural.bridge.page.WebPage;
import ru.ramprox.structural.bridge.theme.DarkTheme;
import ru.ramprox.structural.bridge.theme.Theme;

public class Main {
    public static void main(String[] args) {
        Theme theme = new DarkTheme();
        WebPage page = new About(theme);
        WebPage careers = new Careers(theme);
        System.out.println(page.getContent());
        System.out.println(careers.getContent());
    }
}
