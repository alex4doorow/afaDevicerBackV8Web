package com.afa.devicer.web.dto.orders;

import com.afa.core.dto.dictionaries.OrderStatusTypeDto;
import com.afa.core.dto.orders.OrderDto;
import com.afa.core.dto.products.ProductCategoryDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormOrderDto extends OrderDto {

    @NotNull(message = "{order.form.fields.invalidFeedback.no}")
    private Long formOrderNum;

    private String formStatusCode;
    private Long formProductCategoryId;

    public void convertForm() {
        // order
        this.setOrderNum(formOrderNum);
        this.setStatus(OrderStatusTypeDto.builder()
                        .code(formStatusCode)
                .build());
        this.setProductCategory(ProductCategoryDto.builder()
                .id(formProductCategoryId)
                .build());
    }
}
