package com.programmers.springbootjpa.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table
@Entity
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public OrderItem(int quantity, Item item) {
        super(LocalDateTime.now());

        checkQuantity(quantity, item);

        this.price = item.getPrice();
        this.quantity = quantity;
        this.item = item;

        item.subtractStockQuantity(quantity);
    }

    private void checkPrice(int price, Item item) {
        if (price != item.getPrice()) {
            throw new IllegalArgumentException("금액은 선택한 Item의 금액과 다를 수 없습니다.");
        }
    }

    private void checkQuantity(int quantity, Item item) {
        if (quantity < 1 || quantity > item.getStockQuantity()) {
            throw new IllegalArgumentException("수량은 1개보다 적거나 재고 수량보다 많을 수 없습니다.");
        }
    }

    public void updateOrder(Order order) {
        if (Objects.nonNull(this.order)) {
            List<OrderItem> orderItems = this.order.getOrderItems();
            orderItems.remove(this);
        }

        this.order = order;
        List<OrderItem> orderItems = order.getOrderItems();
        orderItems.add(this);
    }

    public void updateItem(Item item) {
        this.item = item;
    }

    public void updatePrice(int price) {
        checkPrice(price, item);
        this.price = price;
    }

    public void updateQuantity(int quantity) {
        checkQuantity(quantity, item);
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Order getOrder() {
        return order;
    }

    public Item getItem() {
        return item;
    }
}
