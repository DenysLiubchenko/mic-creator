package org.example.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.domain.dto.DiscountDto;
import org.example.domain.producer.DiscountDeltaEventProducer;
import org.example.domain.repository.DiscountRepository;
import org.example.domain.service.DiscountService;
import org.example.domain.producer.DiscountFactEventProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    public final DiscountRepository discountRepository;
    public final DiscountFactEventProducer factEventProducer;
    public final DiscountDeltaEventProducer deltaEventProducer;

    @Override
    public void save(DiscountDto discount) {
        DiscountDto savedDiscount = discountRepository.save(discount);
        factEventProducer.sendCreateEvent(savedDiscount);
        deltaEventProducer.sendCreateEvent(savedDiscount);
    }
}
