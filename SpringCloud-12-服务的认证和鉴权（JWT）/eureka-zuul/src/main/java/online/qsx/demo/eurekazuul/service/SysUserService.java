/********************************************
 * Copyright (c) , yuan yi xiong
 *
 * All rights reserved
 *
 *********************************************/
package online.qsx.demo.eurekazuul.service;



import online.qsx.demo.eurekazuul.dto.SysRole;
import online.qsx.demo.eurekazuul.dto.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName CustomUserService
 * @Description TODO
 * @Date 2018/07/11 10:41
 * @Author yuan yi xiong
 * @Version 1.0
 **/
@Service
public class SysUserService implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public SysUser loadUserByUsername(String username) throws UsernameNotFoundException {
        if (Objects.isNull(username) || !Objects.equals("admin", username)) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        List<SysRole> roles = new ArrayList<SysRole>() {{
            add(new SysRole(1l, "ROLE_ADMIN"));
            //add(new SysRole(2l, "ROLE_USER"));
        }};

        Date time = null;
        try {
            time = new SimpleDateFormat("yyyy-mm-dd").parse("2012-12-12");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SysUser(1001l, "admin", "123456", time, "666666", roles);
    }
}
