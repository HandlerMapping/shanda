package org.example.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 商家
 */
@Getter
@Setter
@ToString
public class Shop {


    private String id;

    private String shopname;

    private String shopava;

    private String shopex;

    private String type;

    private String foods;

    private String createdBy;

    private String createdAt;

    private String updatedAt;

    private String acl;

}
