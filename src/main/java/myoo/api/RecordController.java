package myoo.api;

import myoo.dao.AchievementDao;
import myoo.dao.RecordDao;
import myoo.dao.UserDao;
import myoo.dto.Achievement;
import myoo.dto.Record;
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

@Controller
public class RecordController extends BaseController {
	private static final String ID_ALL = "all";

	@Autowired
	private RecordDao recordDao;

	@Autowired
	private AchievementDao achievementDao;

	@Autowired
	private UserDao userDao;

	@RequestMapping(value = { "/projects/{projectId}/achievements/{achievementId}/records" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public View recordsByAchievementId(@PathVariable String projectId, @PathVariable String achievementId, ModelMap model) {

		String userId = userDao.getUserId(getCurrentUser());

		if (ID_ALL.equalsIgnoreCase(achievementId)) {
			if (ID_ALL.equalsIgnoreCase(projectId)) {
				model.addAttribute("records", recordDao.getByUserId(userId));
			} else {
				model.addAttribute("records", recordDao.getByProjectIdByUserId(projectId, userId));
			}
		} else {
			model.addAttribute("records", recordDao.getByAchievementIdByUserId(achievementId, userId));
		}

		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/projects/{projectId}/achievements/{achievementId}/records" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public View insertRecord(@PathVariable String projectId, @PathVariable String achievementId, @RequestBody Record record, ModelMap model)
			throws NumberFormatException, EntityNotFoundException {

		String userId = userDao.getUserId(getCurrentUser());

		Achievement achievement = achievementDao.get(achievementId);

		model.addAttribute("record", recordDao.put(userId, projectId, achievementId, Integer.valueOf(achievement.getPoints())));

		return new MappingJackson2JsonView();
	}
}
