package com.furniture.erp.ecommerce.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.ecommerce.domain.entity.OnlineOrder;
import com.furniture.erp.ecommerce.domain.entity.CartItem;
import com.furniture.erp.ecommerce.domain.event.PaymentProcessedEvent;
import com.furniture.erp.ecommerce.infrastructure.repository.OnlineOrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OnlineOrderService {

    private final OnlineOrderRepository repository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public OnlineOrderService(OnlineOrderRepository repository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public OnlineOrder createOnlineOrder(String referenceCode, Double totalAmount, List<?> requestedItems) {
        OnlineOrder agg = new OnlineOrder(referenceCode, totalAmount);
        
        java.util.List<com.furniture.erp.ecommerce.domain.event.B2CPaymentReceivedEvent.ItemDto> eventItems = new java.util.ArrayList<>();

        if (requestedItems != null && !requestedItems.isEmpty()) {
            for (Object itemObj : requestedItems) {
                if (itemObj instanceof com.furniture.erp.ecommerce.infrastructure.rest.OnlineOrderController.ItemRequest itemReq) {
                    agg.addItem(new CartItem(itemReq.sku(), itemReq.name(), itemReq.quantity(), itemReq.price()));
                    eventItems.add(new com.furniture.erp.ecommerce.domain.event.B2CPaymentReceivedEvent.ItemDto(
                            itemReq.sku(), itemReq.name(), itemReq.quantity(), itemReq.price()
                    ));
                }
            }
        } else {
            agg.addItem(new CartItem("BED-KING", "Luxury King Size Bed", 1, 45000.00));
            eventItems.add(new com.furniture.erp.ecommerce.domain.event.B2CPaymentReceivedEvent.ItemDto(
                    "BED-KING", "Luxury King Size Bed", 1, 45000.00
            ));
        }

        OnlineOrder saved = repository.save(agg);
        
        // Publish single unified B2CPaymentReceivedEvent with rich details
        eventPublisher.publish(com.furniture.erp.ecommerce.domain.event.B2CPaymentReceivedEvent.create(
                saved.getId(), saved.getReferenceCode(), saved.getTotalAmount(), eventItems
        ));
        return saved;
    }

    @Transactional
    public OnlineOrder createOnlineOrder(String referenceCode) {
        return createOnlineOrder(referenceCode, 45000.00, null);
    }

    @Transactional(readOnly = true)
    public OnlineOrder getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }

    public List<OnlineOrder> getAll() {
        return repository.findAll();
    }
}
