package myoo.api;

import java.util.Calendar;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

	@RequestMapping(value = { "/projects/{projectId}/achievements/all/records" }, method = RequestMethod.DELETE)
	public View deleteByProjectId(@PathVariable String projectId) {
		recordDao.deleteByProjectIdByUserId(projectId, userDao.getUserId(getCurrentUser()));
		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/projects/{projectId}/achievements/{achievementId}/records" }, method = { RequestMethod.GET })
	public View getByProjectIdByAchievementId(@PathVariable String projectId, @PathVariable String achievementId, @RequestParam(required = false) String from,
			@RequestParam(required = false) String to, ModelMap model) {

		String userId = userDao.getUserId(getCurrentUser());

		if (ID_ALL.equalsIgnoreCase(achievementId)) {
			if (ID_ALL.equalsIgnoreCase(projectId)) {

				// gets all the records for all projects for all time
				model.addAttribute("records", recordDao.getByUserId(userId));
			} else {

				// gets all the records for this project for today
				Calendar todayStartCal = Calendar.getInstance();
				Calendar todayEndCal = Calendar.getInstance();
				todayStartCal.set(Calendar.HOUR_OF_DAY, 0);
				todayStartCal.set(Calendar.MINUTE, 0);
				todayStartCal.set(Calendar.SECOND, 0);
				todayStartCal.set(Calendar.MILLISECOND, 0);
				todayEndCal.set(Calendar.HOUR_OF_DAY, 23);
				todayEndCal.set(Calendar.MINUTE, 59);
				todayEndCal.set(Calendar.SECOND, 59);
				todayEndCal.set(Calendar.MILLISECOND, 999);
				model.addAttribute("records", recordDao.getByProjectIdByUserIdByDate(projectId, userId, todayStartCal.getTime(), todayEndCal.getTime()));
			}
		} else {
			model.addAttribute("records", recordDao.getByAchievementIdByUserId(achievementId, userId));
		}

		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/projects/{projectId}/achievements/{achievementId}/records" }, method = { RequestMethod.POST })
	public View insert(@PathVariable String projectId, @PathVariable String achievementId, @RequestBody Record record, ModelMap model)
			throws NumberFormatException, EntityNotFoundException {

		String userId = userDao.getUserId(getCurrentUser());

		Achievement achievement = achievementDao.get(achievementId);

		model.addAttribute("record", recordDao.put(userId, projectId, achievementId, Integer.valueOf(achievement.getPoints())));

		return new MappingJackson2JsonView();
	}
}
