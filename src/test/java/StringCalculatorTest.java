import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringCalculatorTest {
    StringCalculator stringCalculator;

    @BeforeEach
    public void setup() {
        stringCalculator = new StringCalculator();
    }

    @Test
    void testAdd_WhenThreeNumbersGiven_ShouldSumThreeNumbers() {
        int sum = stringCalculator.add("13,3,4");
        assertEquals(20, sum);
    }

    @Test
    void testAdd_WhenNumbersHaveDifferentSeparator_ShouldThrowNumberFormatException() {
        Assertions.assertThrows(NumberFormatException.class, () -> stringCalculator.add("13;2;2"));
    }

    @Test
    void testAdd_WhenNotNumber_ShouldThrowNumberFormatException() {
        Assertions.assertThrows(NumberFormatException.class, () -> stringCalculator.add("YOLO!"));
    }
}