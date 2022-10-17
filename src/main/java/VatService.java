import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class VatService {

    private static final Logger logger = LoggerFactory.getLogger(VatService.class);
    VatProvider vatProvider;

    public VatService(VatProvider vatProvider) {
        this.vatProvider = vatProvider;
    }

    public BigDecimal getGrossPriceForDefaultVat(Product product) throws IncorrectVatException {
        logger.info("Get gross price with default VAT (getGrossPriceForDefaultVat) for product: " + product);
        return calculateGrossPrice(product.getNetPrice(), vatProvider.getDefaultVat());
    }

    public BigDecimal getGrossPrice(BigDecimal netPrice, String productType) throws IncorrectVatException {
        logger.warn("Get gross price with VAT specific for product type (getGrossPrice() fired): " + productType + " and netto price = " + netPrice);
        BigDecimal vatValue = vatProvider.getVatForType(productType);
        return calculateGrossPrice(netPrice, vatValue);
    }

    private BigDecimal calculateGrossPrice(BigDecimal netPrice, BigDecimal vatValue) throws IncorrectVatException {
        logger.info("Calculating gross price (calculateGrossPrice)");
        if (isGreaterThanOne(vatValue)) {
            logger.error("VAT value is >= 1. vatValue = " + vatValue);
            throw new IncorrectVatException("VAT value have to be smaller than 1!");
        }
        BigDecimal grossPrice = netPrice.multiply(vatValue.add(BigDecimal.ONE)).setScale(4, RoundingMode.HALF_UP);
        logger.info("(calculateGrossPrice) Gross price = " + grossPrice);
        return grossPrice;
    }

    private boolean isGreaterThanOne(BigDecimal vatValue) {
        logger.info("(isGreaterThanOne) vatValue = " + vatValue);
        return vatValue.compareTo(BigDecimal.ONE) > 0;
    }
}