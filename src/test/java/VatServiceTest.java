import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class VatServiceTest {
    private VatService vatService;

    @BeforeEach
    void prepareVatService() {
        vatService = new VatService();
    }

    @Test
    void testCalculateGrossPriceWithDefaultVat_WhenProductWithNettoPrice_ShouldReturnGrossPrice() throws Exception {
        Product product = generateProductWithPrice("100.00");

        BigDecimal grossPrice = vatService.getGrossPriceForDefaultVat(product);

        assertThat(grossPrice).isEqualByComparingTo(new BigDecimal("123.00"));
    }

    @Test
    void testCalculateGrossPrice_WhenProductAndVatAreValid_ShouldReturnGrossPrice() throws Exception {
        Product product = generateProductWithPrice("10.00");

        BigDecimal grossPrice = vatService.getGrossPrice(product.getNetPrice(), new BigDecimal("0.20"));

        assertThat(grossPrice).isEqualByComparingTo(new BigDecimal("12.00"));
        assertThat(grossPrice).isEqualTo(new BigDecimal("12.00"));
    }

    @Test
    void testCalculateGrossPrice_WhenVatIsTooHigh_ShouldThrowException() throws Exception {
        Product product = generateProductWithPrice("99.99");

        assertThatExceptionOfType(Exception.class).isThrownBy(() -> {
            vatService.getGrossPrice(product.getNetPrice(), BigDecimal.TEN);
        });
    }

    // TODO: Pitfall #4 - .round(mathContext) - add test proof
    // TODO: assertThat(grossPrice).isEqualTo(new BigDecimal("123.00")); // FAIL

    private Product generateProductWithPrice(String price) {
        return new Product(UUID.randomUUID(), new BigDecimal(price));
    }
}