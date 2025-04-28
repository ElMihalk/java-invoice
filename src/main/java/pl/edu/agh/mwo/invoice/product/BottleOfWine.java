package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public class BottleOfWine extends Product{
    public BottleOfWine(String name, BigDecimal price) {
        super(name, price, new BigDecimal("0.23"));
    }
    public BigDecimal getPriceWithTax(){
        return new BigDecimal(5.56).add(super.getPriceWithTax());
    }
}
