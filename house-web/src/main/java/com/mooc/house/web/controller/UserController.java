package com.mooc.house.web.controller;

import java.util.List;

import com.mooc.house.common.constants.CommonConstants;
import com.mooc.house.common.utils.HashUtils;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mooc.house.biz.service.UserService;
import com.mooc.house.common.model.User;
import com.mooc.house.common.result.ResultMsg;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;

@Controller
public class UserController {

	@Autowired
	private UserService userService;


	/**
	 * 注册提交：1.注册验证 2.发送邮件 3.验证失败重定向到注册页面
	 * 注册页获取：根据account对象
	 *
	 * @param account
	 * @param modelMap
	 * @return
	 */

	@RequestMapping("accounts/register")
	public String accountsRegister(User account, ModelMap modelMap) {

		if (account == null || account.getName() == null) {
			return "/user/accounts/register";
		}

		//用户验证

		ResultMsg resultMsg = UserHelper.validate(account);

		if (resultMsg.isSuccess() && userService.addAccount(account)) {
			modelMap.put("email", account.getEmail());
			return "/user/accounts/registerSubmit";

		} else {

			return "redirect:/accounts/register?" + resultMsg.asUrlParams();
		}
	}

	@RequestMapping("accounts/verify")
	public String verify(String key) {
		boolean result = userService.enable(key);
		if (result) {
			return "redirect:/index?" + ResultMsg.successMsg("激活成功").asUrlParams();
		} else {
			return "redirect:/accounts/register?" + ResultMsg.errorMsg("激活失败，请确认连接是否过期");
		}
	}
	//-----------------------登录流程-------------------------------

	/**
	 * 登入操作
	 * @param req
	 * @return
	 */

	@RequestMapping("/accounts/signin")
	public String signin(HttpServletRequest req){
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String target = req.getParameter("target");
		if(username == null||password == null){
			req.setAttribute("target",target);
			return "/user/accounts/signin";
		}

		User user = userService.auth(username,password);
		if(user == null){
			return "redirect:/account/singin?"+"target="+target+"&username="+username+"&"+ ResultMsg.errorMsg("用户名或密码错误").asUrlParams();
		}else{
			HttpSession session = req.getSession(true);
			session.setAttribute(CommonConstants.USER_ATTRIBUTE,user);
			session.setAttribute(CommonConstants.PLAIN_USER_ATTRIBUTE,user);
			return StringUtils.isNoneBlank(target)?"redirect:"+target:"redirect:/index";
		}
	}

	/**
	 * 登出操作
	 * @param request
	 * @return
	 */
	@RequestMapping("accounts/logout")
	public String logout(HttpServletRequest request){
		HttpSession session = request.getSession(true);
		session.invalidate();
		return "redirect:/index";
	}

	//---------------个人信息页-------------

	/**
	 * 1.能够提供页面信息
	 * 2.更新用户信息
	 * @param updateUser
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "accounts/profile",method={RequestMethod.GET,RequestMethod.POST})
	public String profile(HttpServletRequest req,User updateUser, ModelMap model){
		if(updateUser.getEmail()== null){
			return "/user/accounts/profile";
		}
		userService.updateUser(updateUser,updateUser.getEmail());
		User query = new User();
		query.setEmail(updateUser.getEmail());
		List<User> users = userService.getUserByQuery(query);
		req.getSession(true).setAttribute(CommonConstants.USER_ATTRIBUTE,users.get(0));
		return "redirect:/accounts/profile?"+ResultMsg.successMsg("更新成功").asUrlParams();
	}

	/**
	 * 修改密码操作
	 * @param email
	 * @param password
	 * @param newPassword
	 * @param confirmPassword
	 * @param model
	 * @return
	 */
		@RequestMapping("accounts/changePassword")
		public String changePassword(String email,String password,String newPassword,String confirmPassword,ModelMap model){
			User user = userService.auth(email,password);
			if(user==null||!confirmPassword.equals(newPassword)){
				return "redirect:/accounts/profile?"+ResultMsg.errorMsg("密码错误").asUrlParams();
			}
		User updateUser = new User();
		updateUser.setPasswd(HashUtils.encryPassword(newPassword));
		userService.updateUser(updateUser,email);
		return "redirect:/accounts/profile?"+ResultMsg.successMsg("更新成功").asUrlParams();
	}
}