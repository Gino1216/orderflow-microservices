package dev.portfolio.order.application;

import dev.portfolio.order.api.OrderDtos;
import dev.portfolio.order.catalog.CatalogGateway;
import dev.portfolio.order.domain.OrderRepository;
import dev.portfolio.order.domain.PurchaseOrder;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orders;
    private final CatalogGateway catalog;

    public OrderService(OrderRepository orders, CatalogGateway catalog) {
        this.orders = orders;
        this.catalog = catalog;
    }

    @Transactional
    public OrderDtos.OrderResponse create(OrderDtos.CreateOrderRequest request) {
        CatalogGateway.ReservedProduct product = catalog.reserve(request.productId(), request.quantity());
        PurchaseOrder order = new PurchaseOrder(product.productId(), product.sku(), product.name(),
                request.quantity(), product.unitPrice(), request.customerEmail());
        return OrderDtos.OrderResponse.from(orders.save(order));
    }

    public OrderDtos.OrderResponse findOne(String id) {
        return orders.findById(id).map(OrderDtos.OrderResponse::from)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<OrderDtos.OrderResponse> findByCustomer(String customerEmail) {
        return orders.findAllByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(customerEmail).stream()
                .map(OrderDtos.OrderResponse::from)
                .toList();
    }
}
