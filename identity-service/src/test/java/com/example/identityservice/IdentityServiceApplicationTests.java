package com.example.identityservice;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.identityservice.entity.MyRectangle;

@SpringBootTest
public class IdentityServiceApplicationTests {
    //
    //    @Test
    //    void contextLoads() {}
    //
    //    @Test
    //    void testConstructor() {
    //        Calculator calculator = new Calculator(3.0, 6.0);
    //        boolean result = Calculator.equalsValue(9.0, calculator.cong(3.0, 6.0));
    //        assertTrue(result);
    //    }
    //
    //    @Test
    //    void testEquals() {
    //        Calculator calculator = new Calculator();
    //        boolean result = Calculator.equalsValue(9.0, calculator.cong(3.0, 6.0));
    //        assertTrue(result);
    //    }
    //
    //    @Test
    //    void testCong() {
    //        Calculator calculator = new Calculator();
    //        boolean result = Calculator.equalsValue(9.0, calculator.cong(3.0, 6.0));
    //        assertTrue(result);
    //    }
    //
    //    @Test
    //    void testTru() {
    //        Calculator calculator = new Calculator();
    //        boolean result = Calculator.equalsValue(2.0, calculator.tru(5.0, 3.0));
    //        assertTrue(result);
    //    }
    //
    //    @Test
    //    void testNhan() {
    //        Calculator calculator = new Calculator();
    //        boolean result = Calculator.equalsValue(15.0, calculator.nhan(3.0, 5.0));
    //        assertTrue(result);
    //    }
    //
    //    @Test
    //    void testChia() {
    //        Calculator calculator = new Calculator();
    //        boolean result = Calculator.equalsValue(2.0, calculator.chia(6.0, 3.0));
    //        assertTrue(result);
    //    }
    @Test
    void testSquareArea() {
        MyRectangle myRectangle = new MyRectangle(5.0, 5.0);
        boolean result = myRectangle.equalseValue(25.0, myRectangle.getSquareArea());
        assertTrue(result);
    }

    @Test
    void testIsSquare() {
        MyRectangle myRectangle = new MyRectangle(5.0, 5.0);
        boolean result = myRectangle.isSquare();
        assertTrue(result);
    }

    @Test
    void testIsSquares() {
        MyRectangle myRectangle = new MyRectangle(-5.0, -3.0);
        boolean result = myRectangle.isSquare();
        assertTrue(result);
    }

    @Test
    void testSquarePerimeter() {
        MyRectangle myRectangle = new MyRectangle(5.0, 5.0);
        boolean result = myRectangle.equalseValue(20.0, myRectangle.getSquarePerimeter());
        assertTrue(result);
    }

    @Test
    void testCircleRadius() {
        MyRectangle myRectangle = new MyRectangle(5.0, 5.0);
        boolean result = myRectangle.equalseValue(3.5355, myRectangle.getCircleRadius()); // Đúng giá trị là 3.5355
        assertTrue(result);
    }

    @Test
    void testCircleArea() {
        MyRectangle myRectangle = new MyRectangle(5.0, 5.0);
        boolean result = myRectangle.equalseValue(39.27, myRectangle.getCircleArea()); // Kết quả đúng là 39.27
        assertTrue(result);
    }

    @Test
    void testCirclePerimeter() {
        MyRectangle myRectangle = new MyRectangle(5.0, 5.0);
        double expected = 22.21441469079183;
        double actual = myRectangle.getCirclePerimeter();
        boolean result = myRectangle.equalseValue(expected, actual);
        assertTrue(result);
    }
}
