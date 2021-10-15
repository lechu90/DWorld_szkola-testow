import java.util.ArrayList;
import java.util.List;

class StringCalculator {
    int add(String input) {
        if (input.isEmpty()) {
            return 0;
        } else {
            String[] numbers = input.split(",");
            int result = 0;
            for (String number : numbers) {
                result += getIntFrom(number);
            }
            return result;
        }
    }

    private int getIntFrom(String number) {
        return Integer.parseInt(number);
    }
}