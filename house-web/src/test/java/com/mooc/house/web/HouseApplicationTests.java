package com.mooc.house.web;

import java.io.IOException;

import com.mooc.house.biz.service.UserService;
import com.mooc.house.common.model.User;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HouseApplicationTests {

	@Autowired
	private UserService userService;
	
	@Test
	public void testAuth(){
        User user = userService.auth("liugongjianxin3232@163.com","111111");
        assert user !=null;
        System.out.println(user.getAboutme());
	}

}
