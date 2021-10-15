class VatService {
    double vatValue;

    public VatService() {
        this.vatValue = 0.23;
    }

    public double getGrossPriceForDefaultVat(Product product) throws Exception {
        return getGrossPrice(product.getNetPrice(), vatValue);
    }

    public double getGrossPrice(double netPrice, double vatValue) throws Exception {
        if (vatValue > 1) {
            throw new Exception("Vat value have to be smaller than 1!");
        }
        return netPrice * (1 + vatValue);
    }
}