package com.mooc.house.biz.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Lists;
import com.mooc.house.biz.mapper.UserMapper;
import com.mooc.house.common.model.User;
import com.mooc.house.common.utils.BeanHelper;
import com.mooc.house.common.utils.HashUtils;

@Service
public class UserService {
	
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private MailService mailService;

	@Value("${file.prefix}")
	private String imgPrefix;
	
	public List<User> getUsers(){
		return userMapper.selectUsers();
	}
	
	/**
	 * 1.插入数据库，非激活；密码加盐md5；保存头像到本地
	 * 2.生成key，绑定email
	 * 3.发送邮件给用户
	 */
	
	@Transactional(rollbackFor=Exception.class)
	public boolean addAccount(User account){
		
		account.setPasswd(HashUtils.encryPassword(account.getPasswd()));
		List<String> imgList = fileService.getImgPath(Lists.newArrayList(account.getAvatarFile()));
		if(!imgList.isEmpty()){
			account.setAvatar(imgList.get(0));
		}
		BeanHelper.setDefaultProp(account, User.class);
		BeanHelper.onInsert(account);
		account.setEnable(0);
		userMapper.insert(account);
		mailService.registerNotify(account.getEmail());
		return true;
		
	}


	/**
	 * 用户名密码验证
	 * @param key
	 * @return
	 */
	public boolean enable(String key) {
		return mailService.enable(key);
	}

	public User auth(String username, String password) {
		User user = new User();
		user.setEmail(username);
		user.setPasswd(HashUtils.encryPassword(password));
		user.setEnable(1);
		List<User> list = getUserByQuery(user);
		if(!list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	public List<User> getUserByQuery(User user) {
		List<User> list = userMapper.selectUsersByQuery(user);
		list.forEach(u->{
			u.setAvatar(imgPrefix+u.getAvatar());
		});
		return list;
	}

	public void updateUser(User updateUser, String email) {
		updateUser.setEmail(email);
		BeanHelper.onUpdate(updateUser);
		userMapper.update(updateUser);
	}
}
