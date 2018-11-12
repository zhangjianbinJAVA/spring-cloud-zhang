package com.jd.test;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author： zhangjianbin <br/>
 * ===============================
 * Created with IDEA.
 * Date： 2018/10/23 17:44
 * ================================
 */
public class SecurityTest {
    @Test
    public void passpordTest() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String admin = bCryptPasswordEncoder.encode("admin");
        System.out.println(admin);
        //$2a$10$2/fOiLp2HZLzU7WSkEjgneCWLnAexLoIEjj6rtCBp3zts9ztoO/BK

    }
}
