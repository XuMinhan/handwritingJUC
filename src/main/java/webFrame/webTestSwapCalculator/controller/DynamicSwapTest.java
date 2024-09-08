package webFrame.webTestSwapCalculator.controller;

import webFrame.Scan.annotation.Controller;
import webFrame.Scan.annotation.GetMapping;
import webFrame.Scan.annotation.RequestParam;
import webFrame.webTestSwapCalculator.impl.CalculatorInterface;
import webFrame.webTestSwapCalculator.impl.HotSwapEntry;
import webFrame.webTestSwapCalculator.impl.OldCalculator;


@Controller
public class DynamicSwapTest {
    public CalculatorInterface calculator = new OldCalculator();

    @GetMapping("/swap")
    public Integer swap() {
        try {

            calculator = HotSwapEntry.compilerAndSwapImpl();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    @GetMapping("/calculate")
    public Integer calculate(@RequestParam("num1") Integer num1, @RequestParam("num2") Integer num2) {
        return calculator.calculate(num1, num2);
    }
}
