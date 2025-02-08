package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private Collection<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        this.products.add(product);
    }

    public void addProduct(Product product, Integer quantity) {
        if (quantity<= 0){
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        for (int i = 0; i < quantity; i++) {
            this.addProduct(product);
        }
    }

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = new BigDecimal(0);
        if (products == null) {return subtotal;}
        for (Product product : products) {
            subtotal = subtotal.add(product.getPrice());
        }
        return subtotal;
    }

    public BigDecimal getTax() {
        BigDecimal taxValue = new BigDecimal(0);
        if (products == null) {return taxValue;}
        for (Product product : products) {
            taxValue = taxValue.add(product.getTaxPercent().multiply(product.getPrice()));
        }
        return taxValue;
    }

    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0);
        if (products == null) {return total;}
        for (Product product : products) {
            total = total.add(product.getPriceWithTax());
        }
        return total;
    }
}
