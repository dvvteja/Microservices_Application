package com.tejadvv.os.api.common;

import com.tejadvv.os.api.entity.Order;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    private Order order;
    private Payment payment;
}
