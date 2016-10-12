/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import java.math.BigDecimal;

/**
 *
 * @author Meerim
 */
public class IntermediateResult {
    int iter;
    BigDecimal x, funcVal;
    BigDecimal b_a;
    
    IntermediateResult(int iter, BigDecimal x, BigDecimal funcVal, BigDecimal b_a){
        super();
        this.iter=iter;
        this.funcVal=funcVal;
        this.x=x;
        this.b_a=b_a;
        
    }
    
}
