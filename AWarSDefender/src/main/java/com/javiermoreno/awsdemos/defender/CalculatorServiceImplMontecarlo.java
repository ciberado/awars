package com.javiermoreno.awsdemos.defender;

import java.math.BigDecimal;
import java.util.Random;

/**
 *
 * @author ciberado
 */
public class CalculatorServiceImplMontecarlo implements CalculatorService {
    private static final BigDecimal RADIOUS = new BigDecimal("10000.0");             
    private static final BigDecimal RADIOUSxRADIOUS = RADIOUS.pow(2);
    private static final BigDecimal DIAMETER = RADIOUS.multiply(new BigDecimal("2.0"));
    private static final BigDecimal FOUR = new BigDecimal("4.0");
    private static final Random RND = new Random();
    private static final int ITERATIONS = 10000000;
    
    
    @Override
    public BigDecimal calculatePI() {
        return calculatePI(ITERATIONS);
    }
    
    
    private BigDecimal calculatePI(int iterations) {
        
        int m = 0;
        for (int n=0; n < iterations; n++) {
            BigDecimal x = new BigDecimal(RND.nextDouble()).multiply(DIAMETER).subtract(RADIOUS);
            BigDecimal y = new BigDecimal(RND.nextDouble()).multiply(DIAMETER).subtract(RADIOUS);
            if (x.pow(2).add(y.pow(2)).compareTo(RADIOUSxRADIOUS) <= 0) {
                m = m + 1;
            }
        }
        return FOUR.multiply(new BigDecimal(m)).divide(new BigDecimal(iterations));
    }
    
}
