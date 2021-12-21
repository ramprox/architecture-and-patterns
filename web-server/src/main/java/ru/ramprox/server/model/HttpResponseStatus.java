package ru.ramprox.server.model;

public enum HttpResponseStatus {
    OK(200), NOT_FOUND(404), REDIRECT(302), METHOD_NOT_ALLOWED(405);
    private int code;

    HttpResponseStatus(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code + " " + super.toString();
    }
}
