package com.mooc.house.biz.service;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.mooc.house.biz.mapper.UserMapper;
import com.mooc.house.common.model.User;

@Service
public class MailService {


	private final Cache<String,String> registerCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(15, TimeUnit.MINUTES)
			.removalListener(new RemovalListener<String,String>(){

				@Override
				public void onRemoval(RemovalNotification<String, String> notification) {
					// TODO Auto-generated method stub
					
					userMapper.delete(notification.getValue());
					
				}
				
			}).build();
	
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private UserMapper userMapper;
	
	
	@Value("${domain.name}")
	private String domainName;
	@Value("${spring.mail.username}")
	private String from;
	
	public void sendMail(String title,String url,String email){
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(email);
		message.setText(url);
		mailSender.send(message);
	}
	
	/**
	 * 1.缓存key-email的关系
	 * 2.借助spring mail 发送邮件
	 * 3.借助异步框架进行异步操作
	 * @param email
	 */
	
	@Async
	public void registerNotify(String email) {
		
		String randomKey = RandomStringUtils.randomAlphabetic(10);
		registerCache.put(randomKey, email);
		String url = "http://"+domainName+"/accounts/verify?key="+randomKey;
		sendMail("房产激活邮件",url,email);
	}

	public boolean enable(String key) {
		String email = registerCache.getIfPresent(key);
		if(StringUtils.isBlank(email)){
			return false;
		}
		User updateUser = new User();
		updateUser.setEmail(email);
		updateUser.setEnable(1);
		userMapper.update(updateUser);
		registerCache.invalidate(key);
		return true;
	}
}
