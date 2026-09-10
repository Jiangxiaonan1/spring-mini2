package com.minispring2.demo.model;

public class Fruit {
    Apple apple;
    Banana banana;

    public Apple getApple() {
        return apple;
    }

    public void setApple(Apple apple) {
        this.apple = apple;
    }

    public Banana getBanana() {
        return banana;
    }

    public void setBanana(Banana banana) {
        this.banana = banana;
    }

    @Override
    public String toString() {
        return "Fruit{" +
                "apple=" + apple +
                ", banana=" + banana +
                '}';
    }
}