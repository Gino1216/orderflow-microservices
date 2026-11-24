package dev.portfolio.catalog.product;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public final class ProductDtos {
    private ProductDtos() {
    }

    public record ProductResponse(Long id, String sku, String name, BigDecimal price, int availableStock) {
        static ProductResponse from(Product product) {
            return new ProductResponse(product.getId(), product.getSku(), product.getName(),
                    product.getPrice(), product.getStock());
        }
    }

    public record ReservationRequest(@Positive int quantity) {
    }

    public record ReservationResponse(Long productId, String sku, String name, BigDecimal unitPrice,
                                      int quantity, int remainingStock) {
        static ReservationResponse from(Product product, int quantity) {
            return new ReservationResponse(product.getId(), product.getSku(), product.getName(),
                    product.getPrice(), quantity, product.getStock());
        }
    }
}
