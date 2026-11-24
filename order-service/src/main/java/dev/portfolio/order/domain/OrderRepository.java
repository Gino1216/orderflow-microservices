package dev.portfolio.order.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<PurchaseOrder, String> {
    List<PurchaseOrder> findAllByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(String customerEmail);
}
