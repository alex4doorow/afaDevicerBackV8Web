package com.afa.devicer.web.dto.orders;

import com.afa.core.dto.orders.OrderDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormOrderDto extends OrderDto {
    private String formStatusCode;
    private Long formProductCategoryId;

}
