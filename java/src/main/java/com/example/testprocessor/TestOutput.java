package com.example.testprocessor;

public class TestOutput {
    private String name;
    private double value;
    private String fullName;

    public TestOutput() {
    }

    public TestOutput(String name, double value, String fullName) {
        this.name = name;
        this.value = value;
        this.fullName = fullName;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "TestOutput{" +
            "name='" + name + '\'' +
            ", value=" + value +
            ", fullName='" + fullName + '\'' +
            '}';
    }
}
