package org.example.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.producer.producer.FactEventProducer;
import org.example.domain.dto.DiscountDto;
import org.example.domain.service.DiscountService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    public final FactEventProducer factEventProducer;

    @Override
    public void save(DiscountDto code) {
        factEventProducer.sendCreateEvent(code);
    }
}
