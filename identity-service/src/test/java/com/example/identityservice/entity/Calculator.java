package com.example.identityservice.entity;

public class Calculator {
    double a;
    double b;

    public double cong(double a, double b) {
        return a + b;
    }

    public double tru(double a, double b) {
        return a - b;
    }

    public double nhan(double a, double b) {
        return a * b;
    }

    public double chia(double a, double b) {
        return a / b;
    }

    public Calculator(double a, double b) {
        this.a = a;
        this.b = b;
    }

    public Calculator() {}

    public static boolean equalsValue(double resultDefault, double valueEquals) {
        if (resultDefault == valueEquals) {
            return true;
        } else {
            return false;
        }
    }
}
