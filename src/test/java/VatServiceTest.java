import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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
    void testCalculateGrossPriceWithDefaultVat_WhenProductWithNettoPriceWithFractionPart_ShouldReturnGrossPriceWithPrecisionTo4NumbersInFractionPart() throws Exception {
        Product product = generateProductWithPrice("133.32");

        BigDecimal grossPrice = vatService.getGrossPriceForDefaultVat(product);

        assertThat(grossPrice).isEqualByComparingTo(new BigDecimal("163.9836"));
    }

    @Test
    void testCalculateGrossPrice_WhenProductAndVatAreValid_ShouldReturnGrossPrice() throws Exception {
        Product product = generateProductWithPrice("10.00");

        BigDecimal grossPrice = vatService.getGrossPrice(product.getNetPrice(), new BigDecimal("0.20"));

        assertThat(grossPrice).isEqualByComparingTo(new BigDecimal("12.00"));
    }

    @Test
    void testCalculateGrossPrice_WhenNegativeNettoPrice_ShouldReturnNegativeGrossPrice() throws Exception {
        Product product = generateProductWithPrice("-1500.00");

        BigDecimal grossPrice = vatService.getGrossPrice(product.getNetPrice(), new BigDecimal("0.10"));

        assertThat(grossPrice).isEqualByComparingTo(new BigDecimal("-1650.00"));
    }

    @Test
    void testCalculateGrossPrice_WhenVatIsZero_ShouldReturnGrossPriceEqualsToNettoPrice() throws Exception {
        Product product = generateProductWithPrice("150.00");

        BigDecimal grossPrice = vatService.getGrossPrice(product.getNetPrice(), new BigDecimal("0"));

        assertThat(grossPrice).isEqualByComparingTo(product.getNetPrice());
    }

    @Test
    void testCalculateGrossPrice_WhenVatIsTooHigh_ShouldThrowException() {
        Product product = generateProductWithPrice("99.99");

        assertThatExceptionOfType(Exception.class).isThrownBy(() -> {
            vatService.getGrossPrice(product.getNetPrice(), BigDecimal.TEN);
        });
    }

    // explanation: https://blogs.oracle.com/javamagazine/post/four-common-pitfalls-of-the-bigdecimal-class-and-how-to-avoid-them
    @ParameterizedTest
    @CsvSource({"2021.13,2485.9899", "21.50,26.445", "76767.66,94424.2218"})
    void testAsBigDecimalBadPracticeProof_WhenWantToSetPrecisionOfFractionPartOfBigDecimal_ShouldNotUseRoundWithMathContext(String priceNetto, String expectedBrutto) {
        BigDecimal vat = new BigDecimal("0.23");
        BigDecimal netto = new BigDecimal(priceNetto);
        MathContext mathContext = new MathContext(4);

        BigDecimal brutto = netto.multiply(vat.add(BigDecimal.ONE)).round(mathContext);

        assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(brutto).isNotEqualByComparingTo(new BigDecimal(expectedBrutto));
                    softAssertions.assertThat(brutto.toPlainString()).isNotEqualTo(new BigDecimal(expectedBrutto).toPlainString()); // only for better human readable of proof
                }
        );
    }

    // explanation: https://blogs.oracle.com/javamagazine/post/four-common-pitfalls-of-the-bigdecimal-class-and-how-to-avoid-them
    @ParameterizedTest
    @CsvSource({"100.00,100", "10.0,10.00"})
    void testAsBigDecimalBadPracticeProof_WhenWantToCompareNumbersByValueWithoutFormattingCheck_ShouldNotUseIsEqualTo(String numberInFirstPrecision, String numberInSecondPrecision) {
        BigDecimal firstNumber = new BigDecimal(numberInFirstPrecision);
        BigDecimal secondNumber = new BigDecimal(numberInSecondPrecision);

        // boolean badPractice = firstNumber.equals(secondNumber);
        assertThat(firstNumber).isNotEqualTo(secondNumber);
        // boolean goodPractice = firstNumber.compareTo(secondNumber);
        assertThat(firstNumber).isEqualByComparingTo(secondNumber);
    }

    // TODO: move to class BigDecimalBadPracticeProofTest

    private Product generateProductWithPrice(String price) {
        return new Product(UUID.randomUUID(), new BigDecimal(price));
    }
}