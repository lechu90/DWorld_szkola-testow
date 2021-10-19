import java.math.BigDecimal;
import java.math.MathContext;

class VatService {
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
        MathContext mathContext = new MathContext(4);
        return netPrice.multiply(vatValue.add(BigDecimal.ONE)).round(mathContext);
    }

    private boolean isGreaterThanOne(BigDecimal vatValue) {
        return vatValue.compareTo(BigDecimal.ONE) == 1;
    }
}