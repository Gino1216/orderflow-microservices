package dev.portfolio.order.application;

import dev.portfolio.order.api.OrderDtos;
import dev.portfolio.order.catalog.CatalogGateway;
import dev.portfolio.order.domain.OrderRepository;
import dev.portfolio.order.domain.PurchaseOrder;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock OrderRepository orders;
    @Mock CatalogGateway catalog;

    @Test
    void createReservesStockAndCalculatesTotal() {
        when(catalog.reserve(10L, 3)).thenReturn(new CatalogGateway.ReservedProduct(
                10L, "SKU-10", "Clean Code", new BigDecimal("250000.00"), 3, 7));
        when(orders.save(any(PurchaseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
        OrderService service = new OrderService(orders, catalog);

        OrderDtos.OrderResponse response = service.create(
                new OrderDtos.CreateOrderRequest(10L, 3, "buyer@example.com"));

        assertThat(response.totalAmount()).isEqualByComparingTo("750000.00");
        ArgumentCaptor<PurchaseOrder> captor = ArgumentCaptor.forClass(PurchaseOrder.class);
        verify(orders).save(captor.capture());
        assertThat(captor.getValue().getProductSku()).isEqualTo("SKU-10");
    }
}
