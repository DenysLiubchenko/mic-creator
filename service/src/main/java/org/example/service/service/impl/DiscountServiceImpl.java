package org.example.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.domain.dto.DiscountDto;
import org.example.domain.repository.DiscountRepository;
import org.example.domain.service.DiscountService;
import org.example.producer.producer.DiscountFactEventProducerImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    public final DiscountRepository discountRepository;
    public final DiscountFactEventProducerImpl factEventProducer;

    @Override
    public void save(DiscountDto discount) {
        DiscountDto savedDiscount = discountRepository.save(discount);
        factEventProducer.sendCreateEvent(savedDiscount);
    }
}
