package com.xlk.takeout.dto;

import com.xlk.takeout.entity.OrderDetail;
import com.xlk.takeout.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private List<OrderDetail> orderDetails;


}
