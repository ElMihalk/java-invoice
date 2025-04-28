package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

public class FuelCanister extends Product {
    private LocalDate date;

    public FuelCanister(String name, BigDecimal price) {
        super(name, price, new BigDecimal("0.23"));
    }

    public FuelCanister(String name, BigDecimal price, LocalDate date) {
        super(name, price, new BigDecimal("0.23"));
        this.date = date;
    }

    public BigDecimal getTaxPercent() {
        if (this.isMotherInLawDay()) {
            return BigDecimal.ZERO;
        } else {
            return super.getTaxPercent();
        }
    }

    public BigDecimal getPriceWithTax() {
        final double constantTax = 5.56;
        if (this.isMotherInLawDay()) {
            return super.getPriceWithTax();
        } else {
            return new BigDecimal(constantTax).add(super.getPriceWithTax());
        }
    }

    public boolean isMotherInLawDay() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
        final int fifth = 5;
        if (date.getMonth() == Month.MARCH && date.getDayOfMonth() == fifth) {
            return true;
        }
        return false;
    }
}
