package com.afa.devicer.web.dto;

import com.afa.core.dto.dictionaries.OrderStatusTypeDto;
import com.afa.core.dto.orders.OrderDto;
import com.afa.core.enums.OrderStatusTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FormOrderDto extends OrderDto
{
    public FormOrderDto() {
        super();
    }

    public FormOrderDto(final OrderDto source) {
        super(source);
    }

    public OrderStatusTypes getOrderStatusType() {
        return OrderStatusTypes.valueOf(this.getStatus().getCode());
    }

    public void setOrderStatusType(final OrderStatusTypes orderStatusTypes) {
        final OrderStatusTypeDto orderStatusTypeDto = new OrderStatusTypeDto();
        orderStatusTypeDto.setId(orderStatusTypes.getId());
        orderStatusTypeDto.setCode(orderStatusTypes.getCode());
        orderStatusTypeDto.setAnnotation(orderStatusTypes.getAnnotation());
        this.setStatus(orderStatusTypeDto);
    }
}
