package dev.portfolio.catalog.product;

import java.util.List;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository products;

    public ProductService(ProductRepository products) {
        this.products = products;
    }

    public List<ProductDtos.ProductResponse> findAll() {
        return products.findAll().stream().map(ProductDtos.ProductResponse::from).toList();
    }

    public ProductDtos.ProductResponse findOne(Long id) {
        return ProductDtos.ProductResponse.from(requireProduct(id));
    }

    @Transactional
    public ProductDtos.ReservationResponse reserve(Long id, int quantity) {
        Product product = requireProduct(id);
        product.reserve(quantity);
        try {
            products.flush();
        } catch (OptimisticLockingFailureException exception) {
            throw new InsufficientStockException("Stock changed while processing the reservation; please retry");
        }
        return ProductDtos.ReservationResponse.from(product, quantity);
    }

    private Product requireProduct(Long id) {
        return products.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
