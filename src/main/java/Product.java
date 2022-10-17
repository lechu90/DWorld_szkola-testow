import java.math.BigDecimal;
import java.util.UUID;

public class Product {

    UUID id;

    BigDecimal netPrice;

    String type;

    public Product(UUID id, BigDecimal netPrice, String type) {
        this.id = id;
        this.netPrice = netPrice;
        this.type = type;
    }

    public BigDecimal getNetPrice() {
        return netPrice;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", netPrice=" + netPrice +
                ", type='" + type + '\'' +
                '}';
    }
}
