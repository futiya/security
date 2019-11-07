package com.ibm.sba.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.sba.client.UserServiceClient;
import com.ibm.sba.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Candise Li (jieqli@cn.ibm.com)
 * @create 2019-11-01 11:02
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserServiceClient client;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
         ResponseEntity<Object> user = client.getUser(email);
        JsonObject result = getResult(user);
        if (result.get("code").getAsInt() == 404) {
            throw new UsernameNotFoundException("User " + email + " not exsit");
        } else {
            User u = getAccount(result);

            List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                    .commaSeparatedStringToAuthorityList("ROLE_"+u.getRole());
            return new org.springframework.security.core.userdetails.User(u.getEmail(), u.getPassword(), grantedAuthorities);
        }
    }

    public JsonObject getResult(ResponseEntity<Object> result) {
        Gson gson = new Gson();
        String jsonResultStr = gson.toJson(result.getBody());
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(jsonResultStr);

        return object;

    }

    public User getAccount(JsonObject result) {
        Gson gson = new Gson();

        User user = gson.fromJson(result.get("data").toString(), User.class);

        return user;

    }
}
