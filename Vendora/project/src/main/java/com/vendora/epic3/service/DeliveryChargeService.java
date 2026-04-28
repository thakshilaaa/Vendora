package com.vendora.epic3.service;

import com.vendora.epic3.model.LocationType;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class DeliveryChargeService {

    public BigDecimal calculate(LocationType type) {
        if (type == null) return new BigDecimal("500");
        return new BigDecimal(type.getFee());
    }
}
