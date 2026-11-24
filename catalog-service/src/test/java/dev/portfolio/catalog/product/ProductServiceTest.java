package dev.portfolio.catalog.product;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock ProductRepository repository;

    @Test
    void reserveReducesAvailableStock() {
        Product product = new Product("SKU-1", "Keyboard", new BigDecimal("100.00"), 5);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        ProductService service = new ProductService(repository);

        ProductDtos.ReservationResponse result = service.reserve(1L, 2);

        assertThat(result.remainingStock()).isEqualTo(3);
        verify(repository).flush();
    }

    @Test
    void reserveRejectsInsufficientStock() {
        Product product = new Product("SKU-1", "Keyboard", new BigDecimal("100.00"), 1);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        ProductService service = new ProductService(repository);

        assertThatThrownBy(() -> service.reserve(1L, 2))
                .isInstanceOf(InsufficientStockException.class);
    }
}
