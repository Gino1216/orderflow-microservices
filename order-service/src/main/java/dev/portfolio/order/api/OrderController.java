package dev.portfolio.order.api;

import dev.portfolio.order.application.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDtos.OrderResponse create(@Valid @RequestBody OrderDtos.CreateOrderRequest request) {
        return orderService.create(request);
    }

    @GetMapping("/{id}")
    public OrderDtos.OrderResponse findOne(@PathVariable String id) {
        return orderService.findOne(id);
    }

    @GetMapping
    public List<OrderDtos.OrderResponse> findByCustomer(@RequestParam @Email String customerEmail) {
        return orderService.findByCustomer(customerEmail);
    }
}
