import java.math.BigDecimal;
import java.math.RoundingMode;

public class VatService {

    VatProvider vatProvider;

    public VatService(VatProvider vatProvider) {
        this.vatProvider = vatProvider;
    }

    public BigDecimal getGrossPriceForDefaultVat(Product product) throws IncorrectVatException {
        return calculateGrossPrice(product.getNetPrice(), vatProvider.getDefaultVat());
    }

    public BigDecimal getGrossPrice(BigDecimal netPrice, String productType) throws IncorrectVatException {
        BigDecimal vatValue = vatProvider.getVatForType(productType);
        return calculateGrossPrice(netPrice, vatValue);
    }

    private BigDecimal calculateGrossPrice(BigDecimal netPrice, BigDecimal vatValue) throws IncorrectVatException {
        if (isGreaterThanOne(vatValue)) {
            throw new IncorrectVatException("Vat value have to be smaller than 1!");
        }
        return netPrice.multiply(vatValue.add(BigDecimal.ONE)).setScale(4, RoundingMode.HALF_UP);
    }

    private boolean isGreaterThanOne(BigDecimal vatValue) {
        return vatValue.compareTo(BigDecimal.ONE) > 0;
    }
}