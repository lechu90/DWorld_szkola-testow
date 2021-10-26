import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BigDecimalBadPracticeProofTest {

    // explanation: https://blogs.oracle.com/javamagazine/post/four-common-pitfalls-of-the-bigdecimal-class-and-how-to-avoid-them
    @ParameterizedTest
    @CsvSource({"2021.13,2485.9899", "21.50,26.4450", "76767.66,94424.2218"})
    void testAsBigDecimalBadPracticeProof_WhenWantToSetPrecisionOfFractionPartOfBigDecimal_ShouldNotUseRoundWithMathContext(String priceNetto, String expectedBrutto) {
        BigDecimal vat = new BigDecimal("0.23");
        BigDecimal netto = new BigDecimal(priceNetto);
        MathContext mathContext = new MathContext(4);
        BigDecimal brutto = netto.multiply(vat.add(BigDecimal.ONE));

        // bad practice: .round(mathContext);
        BigDecimal bruttoRoundedBad = brutto.round(mathContext);
        // good practice: use .setScale(4, RoundingMode.HALF_UP);
        BigDecimal bruttoRoundedGood = brutto.setScale(4, RoundingMode.HALF_UP);

        assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(bruttoRoundedBad).isNotEqualByComparingTo(new BigDecimal(expectedBrutto));
                    softAssertions.assertThat(bruttoRoundedGood).isEqualByComparingTo(new BigDecimal(expectedBrutto));
                    softAssertions.assertThat(bruttoRoundedBad.toPlainString()).isNotEqualTo(new BigDecimal(expectedBrutto).toPlainString()); // only for better human readable of proof
                    softAssertions.assertThat(bruttoRoundedGood.toPlainString()).isEqualTo(new BigDecimal(expectedBrutto).toPlainString());
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
}