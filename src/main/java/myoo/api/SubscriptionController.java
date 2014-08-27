package myoo.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import myoo.dao.SubscriptionDao;
import myoo.dao.UserDao;
import myoo.dto.Subscription;
import myoo.ext.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Controller
public class SubscriptionController extends BaseController {

	@Autowired
	private UserDao userDao;

	@Autowired
	private SubscriptionDao subscriptionDao;

	@RequestMapping(value = "/subscriptions", method = RequestMethod.GET)
	public View subscriptions(ModelMap model) {
		model.addAttribute("subscriptions", getAsMap(subscriptionDao.getByUserId(userDao.getUserId(getCurrentUser()))));
		return new MappingJackson2JsonView();
	}

	private Object getAsMap(List<Subscription> subscriptions) {
		Map<String, Boolean> ret = new HashMap<String, Boolean>();

		if (subscriptions != null) {
			for (Subscription subscription : subscriptions) {
				ret.put(subscription.getProjectId(), true);
			}
		}

		return ret;
	}

	/**
	 * Deletes all subscriptions and re-adds the new set for the user
	 * 
	 * @param subscriptions
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/subscriptions", method = RequestMethod.POST)
	public View updateSubscriptions(@RequestBody Map<String, Boolean> subscriptions, ModelMap model) {
		String userId = userDao.getUserId(getCurrentUser());
		subscriptionDao.deleteByUserId(userId);
		Set<String> newSubscriptions = subscriptions.keySet();
		if (newSubscriptions != null) {
			for (String projectId : newSubscriptions) {
				if (subscriptions.get(projectId)) {
					subscriptionDao.put(userId, projectId);
				}
			}
		}
		return new MappingJackson2JsonView();
	}
}
