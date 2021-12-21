package ru.ramprox.behavioral.chainofresponsibility.account;

public abstract class Account {
    protected Account successor;
    protected float balance;

    public Account(float balance) {
        this.balance = balance;
    }

    public void setNext(Account account) {
        this.successor = account;
    }

    public void pay(float amountToPay) {
        if(canPay(amountToPay)) {
            System.out.printf("Paid %.2f using %s\n", amountToPay, getClass().getSimpleName());
        } else if(this.successor != null) {
            System.out.printf("Cannot pay using %s. Proceeding...\n", getClass().getSimpleName());
            this.successor.pay(amountToPay);
        } else {
            throw new RuntimeException("None of the accounts have enough balance");
        }
    }

    public boolean canPay(float amount) {
        return this.balance >= amount;
    }
}
