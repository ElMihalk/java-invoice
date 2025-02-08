package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public class TaxFreeProduct extends Product {
    public TaxFreeProduct(String name, BigDecimal price) {
        super(name, price, BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getTaxPercent() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getPriceWithTax() {
        return this.getPrice();
    }


}
