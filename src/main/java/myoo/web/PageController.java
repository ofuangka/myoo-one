package myoo.web;

import myoo.dao.UserDao;
import myoo.ext.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * This controller forwards to the single page application view
 * 
 * @author ofuangka
 *
 */
@Controller
public class PageController extends BaseController {
	private static final String URL_AFTER_LOGOUT = "/";

	@Autowired
	private UserDao userDao;

	@RequestMapping(value = { "/index.html" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String get(ModelMap model) {
		UserService userService = UserServiceFactory.getUserService();
		model.addAttribute("logoutUrl", userService.createLogoutURL(URL_AFTER_LOGOUT));
		model.addAttribute("currentUserNickname", userDao.getCurrentUserNickname());
		model.addAttribute("isUserAdmin", userService.isUserAdmin());
		return "main";
	}
}
