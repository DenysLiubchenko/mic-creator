package org.example.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.producer.producer.DeltaEventProducer;
import org.example.producer.producer.FactEventProducer;
import org.example.serviceapi.service.DiscountService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    public final FactEventProducer factEventProducer;
    public final DeltaEventProducer deltaEventProducer;

    @Override
    public void save(String code) {

    }
}
