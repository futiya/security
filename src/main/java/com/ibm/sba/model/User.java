package com.ibm.sba.model;

import lombok.Data;

import java.sql.Date;

/**
 * @author Candise Li (jieqli@cn.ibm.com)
 * @create 2019-11-01 11:22
 */
@Data
public class User {
    private int id;

    private String username;

    private String email;

    private String password;
    private String createTime;

    private String firstName;

    private String lastName;

    private String contactNumber;

    private Boolean active;

    private String role;
}
