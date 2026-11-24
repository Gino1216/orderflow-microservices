package dev.portfolio.order.application;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String id) {
        super("Order " + id + " was not found");
    }
}
