package com.example.testprocessor;

public class TestInput {
    private String name;
    private double value;

    public TestInput() {
    }

    public TestInput(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TestInput{" +
            "name='" + name + '\'' +
            ", value=" + value +
            '}';
    }
}
