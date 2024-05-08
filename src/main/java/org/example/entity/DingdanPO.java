package org.example.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 订单
 */
@Getter
@Setter
@ToString
public class DingdanPO {

    private Integer id;

    private String zt;

    private List<Caidan> caidan;

    private BigDecimal price;

    private String createdBy;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private String acl;

    private String position;

    private String phone;

    private String  action;

    private Integer shopId;

    private String name;

}
