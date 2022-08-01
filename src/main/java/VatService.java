import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class VatService {
    BigDecimal vatValue;

    public VatService() {
        this.vatValue = new BigDecimal("0.23");
    }

    public BigDecimal getGrossPriceForDefaultVat(Product product) throws Exception {
        return getGrossPrice(product.getNetPrice(), vatValue);
    }

    public BigDecimal getGrossPrice(BigDecimal netPrice, BigDecimal vatValue) throws Exception {
        if (isGreaterThanOne(vatValue)) {
            throw new Exception("Vat value have to be smaller than 1!");
        }
        return netPrice.multiply(vatValue.add(BigDecimal.ONE)).setScale(4, RoundingMode.HALF_UP);
    }

    private boolean isGreaterThanOne(BigDecimal vatValue) {
        return vatValue.compareTo(BigDecimal.ONE) > 0;
    }
}