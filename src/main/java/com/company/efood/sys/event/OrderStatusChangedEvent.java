package com.company.efood.sys.event;

import com.company.efood.sys.utils.OrderStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderStatusChangedEvent extends ApplicationEvent {
    private final Long orderId;
    private final OrderStatus previousStatus;
    private final OrderStatus newStatus;
    private final Long updatedByUserId;

    public OrderStatusChangedEvent(Object source, Long orderId, OrderStatus previousStatus, OrderStatus newStatus, Long updatedByUserId) {
        super(source);
        this.orderId = orderId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.updatedByUserId = updatedByUserId;
    }
}
