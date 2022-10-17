import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VatServiceTest {
    private VatService vatService;

    private VatProvider vatProvider;

    private LogCaptor logCaptor;

    @BeforeEach
    public void setup() {
        vatProvider = mock(VatProvider.class);
        vatService = new VatService(vatProvider);

        logCaptor = LogCaptor.forClass(VatService.class);
    }

    @Test
    public void testCalculateGrossPriceWithDefaultVat_WhenProductWithNettoPrice_ShouldReturnGrossPrice() throws IncorrectVatException {
        Product product = generateProduct("100.00", "Clothes");

        when(vatProvider.getDefaultVat()).thenReturn(new BigDecimal("0.23"));
        BigDecimal grossPrice = vatService.getGrossPriceForDefaultVat(product);

        assertThat(grossPrice).isEqualByComparingTo(new BigDecimal("123.00"));
    }

    @Test
    void testCalculateGrossPriceWithDefaultVat_WhenProductWithNettoPriceWithFractionPart_ShouldReturnGrossPriceWithPrecisionTo4NumbersInFractionPart() throws IncorrectVatException {
        Product product = generateProduct("133.32", "Books");

        when(vatProvider.getDefaultVat()).thenReturn(new BigDecimal("0.23"));
        BigDecimal grossPrice = vatService.getGrossPriceForDefaultVat(product);

        assertThat(grossPrice).isEqualByComparingTo(new BigDecimal("163.9836"));
    }

    @Test
    void testCalculateGrossPrice_WhenProductAndVatAreValid_ShouldReturnGrossPrice() throws IncorrectVatException {
        String type = "Clothes";
        Product product = generateProduct("10.00", type);

        when(vatProvider.getVatForType(type)).thenReturn(new BigDecimal("0.20"));
        BigDecimal grossPrice = vatService.getGrossPrice(product.getNetPrice(), type);

        assertThat(grossPrice).isEqualByComparingTo(new BigDecimal("12.00"));
    }

    @Test
    void testCalculateGrossPrice_WhenNegativeNettoPrice_ShouldReturnNegativeGrossPrice() throws IncorrectVatException {
        String type = "Books";
        Product product = generateProduct("-1500.00", type);

        when(vatProvider.getVatForType(type)).thenReturn(new BigDecimal("0.10"));
        BigDecimal grossPrice = vatService.getGrossPrice(product.getNetPrice(), type);

        assertThat(grossPrice).isEqualByComparingTo(new BigDecimal("-1650.00"));
    }

    @Test
    void testCalculateGrossPrice_WhenVatIsZero_ShouldReturnGrossPriceEqualsToNettoPrice() throws IncorrectVatException {
        String type = "Food0VatByGovPromo";
        Product product = generateProduct("150.00", type);

        when(vatProvider.getVatForType(type)).thenReturn(new BigDecimal("0"));
        BigDecimal grossPrice = vatService.getGrossPrice(product.getNetPrice(), type);

        assertThat(grossPrice).isEqualByComparingTo(product.getNetPrice());
    }

    @Test
    void testCalculateGrossPrice_WhenVatIsZero_ShouldReturnGrossPriceEqualsToNettoPriceAndSpecificLogs() throws IncorrectVatException {
        String type = "Coffee";
        Product product = generateProduct("150.00", type);

        when(vatProvider.getVatForType(type)).thenReturn(new BigDecimal("0"));
        BigDecimal grossPrice = vatService.getGrossPrice(product.getNetPrice(), type);

        assertThat(grossPrice).isEqualByComparingTo(product.getNetPrice());
        assertThat(logCaptor.getInfoLogs()).hasSize(2).containsExactly("Calculating gross price (calculateGrossPrice)",
                "(calculateGrossPrice) Gross price = 150.0000");
        assertThat(logCaptor.getWarnLogs()).hasSize(1).containsExactly("Get gross price with VAT specific for " +
                "product type (getGrossPrice): Coffee and netto price = 150.00");
    }

    @Test
    void testCalculateGrossPrice_WhenVatIsTooHigh_ShouldThrowException() {
        String type = "Books";
        Product product = generateProduct("99.99", type);

        when(vatProvider.getVatForType(type)).thenReturn(BigDecimal.TEN);
        assertThatExceptionOfType(IncorrectVatException.class).isThrownBy(() -> vatService.getGrossPrice(product.getNetPrice(), type));
    }

    private Product generateProduct(String price, String type) {
        return new Product(UUID.randomUUID(), new BigDecimal(price), type);
    }
}