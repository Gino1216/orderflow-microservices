package dev.portfolio.order.catalog;

import java.math.BigDecimal;

public interface CatalogGateway {
    ReservedProduct reserve(Long productId, int quantity);

    record ReservedProduct(Long productId, String sku, String name, BigDecimal unitPrice,
                           int quantity, int remainingStock) {
    }
}
