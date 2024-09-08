package webFrame.webTestSwapCalculator.impl;


public class NewCalculator implements CalculatorInterface {
    @Override
    public int calculate(int a, int b) {
        return a *b;  // 将加法改为乘法
    }
}
