package myoo.web;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import myoo.ext.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController extends BaseController {
	private static final String URL_AFTER_LOGOUT = "/";

	@RequestMapping(value = { "/index.html" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String get(ModelMap model) {
		UserService userService = UserServiceFactory.getUserService();
		model.addAttribute("logoutUrl", userService.createLogoutURL(URL_AFTER_LOGOUT));

		model.addAttribute("currentUser", getCurrentUser());
		return "main";
	}
}
