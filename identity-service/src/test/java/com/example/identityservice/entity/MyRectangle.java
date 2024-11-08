package com.example.identityservice.entity;

import lombok.Data;

@Data
public class MyRectangle {
    private double width, height;

    public MyRectangle() {
        super();
    }

    public MyRectangle(double width, double height) {
        super();
        this.height = height;
        this.width = width;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        if (width > 0) this.width = width;
        else this.width = 0;
    }

    public boolean isSquare() {
        if (width < 0 && height < 0) {
            this.width = 0;
            this.height = 0;
        }
        return width == height;
    }

    public boolean equalseValue(double valueDefault, double value) {
        double epsilon = 0.0001; // độ chính xác
        return Math.abs(valueDefault - value) < epsilon;
    }

    // Tính diện tích của hình vuông
    public double getSquareArea() {
        if (isSquare()) {
            return width * width;
        } else {
            throw new IllegalArgumentException("Không phải là hình vuông!");
        }
    }

    // Tính chu vi của hình vuông
    public double getSquarePerimeter() {
        if (isSquare()) {
            return 4 * width;
        } else {
            throw new IllegalArgumentException("Không phải là hình vuông!");
        }
    }

    // tinh ban kinh hinh tron
    public double getCircleRadius() {
        return Math.sqrt(width * width + height * height) / 2;
    }

    // Tính diện tích của hình tròn
    public double getCircleArea() {
        double radius = getCircleRadius();
        return Math.PI * radius * radius;
    }

    // Tính chu vi của hình tròn
    public double getCirclePerimeter() {
        double radius = getCircleRadius();
        return 2 * Math.PI * radius;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        if (height > 0) this.height = height;
        else this.height = 0;
    }

    public double getArea() {
        return this.width * this.height;
    }

    public double getPerimeter() {
        return (this.width + this.height) * 2;
    }
}
