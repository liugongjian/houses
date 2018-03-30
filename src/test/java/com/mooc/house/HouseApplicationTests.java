package com.mooc.house;

import java.io.IOException;

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
@SpringBootTest
public class HouseApplicationTests {

	@Autowired
	private HttpClient HttpClient;
	
	@Test
	public void testHttpclient() throws ClientProtocolException,IOException{
		String s =EntityUtils.toString( HttpClient.execute(new HttpGet("http://www.baidu.com")).getEntity());
		
		System.out.println(s);
	}

}
