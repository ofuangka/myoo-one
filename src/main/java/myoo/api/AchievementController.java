package myoo.api;

import myoo.dao.AchievementDao;
import myoo.dao.RecordDao;
import myoo.dao.UserDao;
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

/**
 * This controller handles Achievement requests
 * 
 * @author ofuangka
 *
 */
@Controller
public class AchievementController extends BaseController {

	@Autowired
	private AchievementDao achievementDao;
	
	@Autowired
	private RecordDao recordDao;

	@Autowired
	private UserDao userDao;

	@RequestMapping(value = { "/projects/{projectId}/achievements" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public View achievements(@PathVariable String projectId, ModelMap model) {
		model.addAttribute("achievements", achievementDao.getByProjectId(projectId));
		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/projects/{projectId}/achievements" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public View insertAchievement(@PathVariable String projectId, @RequestBody Achievement achievement, ModelMap model) {

		model.addAttribute("achievement", achievementDao.put(projectId, achievement.getName(), achievement.getDescription(), achievement.getPoints(),
				userDao.getCurrentUserNickname(), achievement.getBackgroundPositionX(), achievement.getBackgroundPositionY()));

		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/projects/{projectId}/achievements" }, method = { org.springframework.web.bind.annotation.RequestMethod.PUT })
	public View updateAchievement(@PathVariable String projectId, @RequestBody Achievement achievement, ModelMap model) throws NumberFormatException,
			EntityNotFoundException {

		model.addAttribute(
				"achievement",
				achievementDao.update(achievement.getId(), achievement.getProjectId(), achievement.getName(), achievement.getDescription(),
						achievement.getPoints(), userDao.getCurrentUserNickname(), achievement.getBackgroundPositionX(), achievement.getBackgroundPositionY()));

		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/projects/{projectId}/achievements/{achievementId}" }, method = { org.springframework.web.bind.annotation.RequestMethod.DELETE })
	public View deleteAchievement(@PathVariable String projectId, @PathVariable String achievementId, ModelMap model) {
		recordDao.deleteByAchievementId(achievementId);
		achievementDao.delete(achievementId);
		return new MappingJackson2JsonView();
	}
}
