package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private Map<Product, Integer> products = new HashMap<Product, Integer>();
    private static int invoice_count = 1;

    private int id;

    public Invoice() {
        this.id = invoice_count;
        invoice_count++;
    }

    public int getId() {
        return this.id;
    }

    public static void resetId() {
        invoice_count = 1;
    }

    public Map<Product, Integer> getProducts() {
        return this.products;
    }

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
        if (!this.products.keySet().stream()
                .map(prod -> prod.getName())
                .toList()
                .contains(product.getName())) {
            products.put(product, quantity);
        } else {
            products.put(
                    getProductByName(product.getName()),
                    products.get(getProductByName(product.getName())) + quantity
            );
        }
    }

    public String generateDescription() {
        StringBuilder invoiceDescription = new StringBuilder(
                String.format("Faktura nr %s\n", this.id)
        );
        for (Map.Entry<Product, Integer> entry : this.products.entrySet()) {
            invoiceDescription.append(String.format(
                    "%s Liczba sztuk: %d Cena j. %f\n",
                    entry.getKey().getName(), entry.getValue(), entry.getKey().getPrice()
            ));
        }
        invoiceDescription.append(
                String.format("Liczba pozycji na fakturze: %d", this.products.size())
        );
        return String.valueOf(invoiceDescription);
    }

    public int getProductNumber() {
        return this.products.size();
    }

    public Product getProductByName(String productName) {
        return this.products.keySet().stream()
                .filter(prod -> prod.getName().equals(productName))
                .toList()
                .get(0);
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
        }
        return totalGross;
    }
}
