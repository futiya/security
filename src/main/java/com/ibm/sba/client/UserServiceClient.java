package com.ibm.sba.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Candise Li (jieqli@cn.ibm.com)
 * @create 2019-11-01 09:57
 */

@FeignClient(name = "sba-user")
public interface UserServiceClient {

    @RequestMapping(value="/user/api/v1/getUser/",method = RequestMethod.GET)
    ResponseEntity<Object> getUser(@RequestParam("email") String email);
}
