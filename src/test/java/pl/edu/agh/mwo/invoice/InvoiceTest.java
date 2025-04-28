package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.Invoice;
import pl.edu.agh.mwo.invoice.product.*;

import static java.lang.String.format;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertThat(new BigDecimal("20"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertThat(new BigDecimal("1000"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingNullProduct() {
        invoice.addProduct(null);
    }

    @Test
    public void testAutomaticInvoiceID() {
        Invoice invoice1 = new Invoice();
        Invoice invoice2 = new Invoice();
        Invoice invoice3 = new Invoice();
        int first_invoice_id = invoice1.getId();
        Assert.assertEquals(first_invoice_id+1, invoice2.getId());
        Assert.assertEquals(first_invoice_id+2, invoice3.getId());
    }

    @Test
    public void testInvoiceStringGenerationReturnsString(){
        Invoice.resetId();
        Invoice invoice = new Invoice();
        var description=invoice.generateDescription();
        Assert.assertTrue(description instanceof String);
    }

    @Test
    public void testInvoiceStringGenerationHasCorrectNumberOfLines(){
        Invoice.resetId();
        Invoice invoice = new Invoice();
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        Assert.assertEquals(invoice.generateDescription().lines().count(), 4);
    }

    @Test
    public void testInvoiceStringGenerationHasCorrectInvoiceNumber(){
        Invoice.resetId();
        Invoice invoice = new Invoice();
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        Assert.assertEquals("Faktura nr 1", invoice.generateDescription().lines().toArray()[0]);
    }

    @Test
    public void testInvoiceStringGenerationHasCorrectArticleNumber(){
        Invoice.resetId();
        Invoice invoice = new Invoice();
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        var invoiceLines = invoice.generateDescription().lines().toArray();
        Assert.assertEquals("Liczba pozycji na fakturze: 2", invoiceLines[invoiceLines.length - 1]);
    }

    @Test
    public void testInvoiceStringGenerationHasCorrectContent(){
        Invoice.resetId();
        Invoice invoice = new Invoice();
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        var invoiceLines = invoice.generateDescription().lines().toArray();
        String expectedString = format("""
                Faktura nr 1
                Kubek Liczba sztuk: 2 Cena j. 5,000000
                Kozi Serek Liczba sztuk: 3 Cena j. 10,000000
                Pinezka Liczba sztuk: 1000 Cena j. 0,010000
                Liczba pozycji na fakturze: 3""", invoice.getId(), invoice.getProductNumber());
        Assert.assertTrue(Arrays.asList(invoiceLines).contains("Kubek Liczba sztuk: 2 Cena j. 5,000000"));
        Assert.assertTrue(Arrays.asList(invoiceLines).contains("Kozi Serek Liczba sztuk: 3 Cena j. 10,000000"));
        Assert.assertTrue(Arrays.asList(invoiceLines).contains("Pinezka Liczba sztuk: 1000 Cena j. 0,010000"));
    }

    @Test
    public void testOnlyOneProductOnTheInvoiceWhenAddedTwice(){
        Invoice.resetId();
        Invoice invoice = new Invoice();
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        Assert.assertEquals(1, invoice.getProductNumber());
    }

    @Test
    public void testProductAmountAddedOnTheInvoiceWhenAddedTwice(){
        Invoice.resetId();
        Invoice invoice = new Invoice();
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 5);
        int amountActual = invoice.getProducts().get(invoice.getProductByName("Kozi Serek"));
        Assert.assertEquals(8, amountActual);
    }

    @Test
    public void testExciseProductHasCorrectPriceWithTax(){
        Product product = new BottleOfWine("Chateau", new BigDecimal("40"));
        Assert.assertEquals(new BigDecimal("54.76"), product.getPriceWithTax().round(new MathContext(4)));
    }

    @Test
    public void testFuelProductHasCorrectPriceOnRegularDay(){
        Product product = new FuelCanister("Olej rzepakowy", new BigDecimal("40"), LocalDate.of(2025, Month.APRIL, 15));
        Assert.assertEquals(new BigDecimal("54.76"), product.getPriceWithTax().round(new MathContext(4)));
    }

    @Test
    public void testFuelProductHasCorrectPriceOnMotherInLawDay(){
        Product product = new FuelCanister("Olej rzepakowy", new BigDecimal("40"), LocalDate.of(2025, Month.MARCH, 5));
        Assert.assertEquals(new BigDecimal("40"), product.getPriceWithTax().round(new MathContext(4)));
    }
}
