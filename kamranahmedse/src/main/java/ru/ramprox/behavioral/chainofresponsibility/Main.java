package ru.ramprox.behavioral.chainofresponsibility;

import ru.ramprox.behavioral.chainofresponsibility.account.Account;
import ru.ramprox.behavioral.chainofresponsibility.account.Bank;
import ru.ramprox.behavioral.chainofresponsibility.account.Bitcoin;
import ru.ramprox.behavioral.chainofresponsibility.account.Paypal;

public class Main {
    public static void main(String[] args) {
        Account bank = new Bank(100);
        Account paypal = new Paypal(200);
        Account bitcoin = new Bitcoin(1000);
        bank.setNext(paypal);
        paypal.setNext(bitcoin);
        bank.pay(259);
    }
}
