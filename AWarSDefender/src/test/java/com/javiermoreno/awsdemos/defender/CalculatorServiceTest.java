package com.javiermoreno.awsdemos.defender;

import com.javiermoreno.awsdemos.defender.CalculatorServiceImplMontecarlo;
import com.javiermoreno.awsdemos.defender.CalculatorServiceImplHighPrecision;
import java.math.BigDecimal;
import static org.junit.Assert.*;

/**
 *
 * @author ciberado
 */
public class CalculatorServiceTest {
    
    public CalculatorServiceTest() {
    }

    //@org.junit.Test
    public void checkPIMontecarlo() {
        CalculatorServiceImplMontecarlo service = 
                new CalculatorServiceImplMontecarlo();
        BigDecimal pi = service.calculatePI();
        BigDecimal difference = pi.subtract(new BigDecimal(Math.PI)).abs();
        
        assertTrue("PI calc failed: " + pi, difference.compareTo(new BigDecimal("0.001")) <= 0);
    }
    
    @org.junit.Test
    public void checkPIHighPrecision() {
        CalculatorServiceImplHighPrecision service = 
                new CalculatorServiceImplHighPrecision();
        BigDecimal pi = service.calculatePI();
        BigDecimal difference = pi.subtract(new BigDecimal(Math.PI)).abs();
        
        System.out.println(pi);
        assertTrue("PI calc failed: " + pi, difference.compareTo(new BigDecimal("0.0001")) <= 0);
    }    
    
}
