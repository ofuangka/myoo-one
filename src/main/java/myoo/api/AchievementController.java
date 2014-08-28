package myoo.api;

import myoo.dao.AchievementDao;
import myoo.dto.Achievement;
import myoo.ext.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.User;

@Controller
public class AchievementController extends BaseController {

	@Autowired
	private AchievementDao achievementDao;

	@RequestMapping(value = { "/projects/{projectId}/achievements" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public View achievements(@PathVariable String projectId, ModelMap model) {
		model.addAttribute("achievements", achievementDao.getByProjectId(projectId));
		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/projects/{projectId}/achievements" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public View insertAchievement(@PathVariable String projectId, @RequestBody Achievement achievement, ModelMap model) {
		User currentUser = getCurrentUser();

		model.addAttribute("achievement",
				achievementDao.put(projectId, achievement.getName(), achievement.getDescription(), achievement.getPoints(), currentUser.getNickname()));

		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/projects/{projectId}/achievements" }, method = { org.springframework.web.bind.annotation.RequestMethod.PUT })
	public View updateAchievement(@PathVariable String projectId, @RequestBody Achievement achievement, ModelMap model) throws NumberFormatException,
			EntityNotFoundException {

		User currentUser = getCurrentUser();

		model.addAttribute(
				"achievement",
				achievementDao.update(achievement.getId(), achievement.getProjectId(), achievement.getName(), achievement.getDescription(),
						achievement.getPoints(), currentUser.getNickname()));

		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/projects/{projectId}/achievements/{achievementId}" }, method = { org.springframework.web.bind.annotation.RequestMethod.DELETE })
	public View deleteAchievement(@PathVariable String projectId, @PathVariable String achievementId, ModelMap model) {
		achievementDao.delete(achievementId);
		return new MappingJackson2JsonView();
	}
}
