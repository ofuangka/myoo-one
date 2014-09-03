package myoo.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import myoo.dao.AchievementDao;
import myoo.dao.RecordDao;
import myoo.dao.SubscriptionDao;
import myoo.dao.UserDao;
import myoo.dto.Achievement;
import myoo.dto.Comparison;
import myoo.dto.Record;
import myoo.dto.Subscription;
import myoo.ext.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.User;

@Controller
public class ReviewController extends BaseController {

	@Autowired
	private SubscriptionDao subscriptionDao;

	@Autowired
	private RecordDao recordDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AchievementDao achievementDao;

	@RequestMapping({ "/projects/{projectId}/review/achievements" })
	public View achievements(@PathVariable String projectId, @RequestParam("from") String from, @RequestParam("to") String to, ModelMap model)
			throws NumberFormatException, EntityNotFoundException {
		User currentUser = getCurrentUser();

		List<Date> dates = parseDates(from, to);

		if (dates.size() > 1) {
			List<Record> records = recordDao.getByProjectIdByUserIdByDate(projectId, userDao.getUserId(currentUser), dates.get(0), dates.get(dates.size() - 1));
			if (records != null) {

				// create a map of achievementIds to points within the time
				// frame provided
				Map<String, Integer> achievementIdToPointMap = new HashMap<String, Integer>();
				for (Record record : records) {
					String achievementId = record.getAchievementId();
					Integer currentValue = achievementIdToPointMap.get(achievementId);
					if (currentValue != null) {
						achievementIdToPointMap.put(achievementId, currentValue + record.getPoints());
					} else {
						achievementIdToPointMap.put(achievementId, record.getPoints());
					}
				}

				// translate from achievementId to achievementName
				Map<String, Integer> ret = new HashMap<String, Integer>();
				Set<String> keys = achievementIdToPointMap.keySet();
				for (String key : keys) {
					Achievement achievement = achievementDao.get(key);
					ret.put(achievement.getName(), achievementIdToPointMap.get(key));
				}
				model.addAttribute("achievements", ret);
			}

		}
		return new MappingJackson2JsonView();
	}

	@RequestMapping({ "/projects/{projectId}/review/comparison" })
	public View comparison(@PathVariable String projectId, @RequestParam("from") String from, @RequestParam("to") String to, ModelMap model) {

		List<Subscription> subscriptions = subscriptionDao.getByProjectId(projectId);

		Set<String> projectMemberIds = getUserIdsFromSubscriptions(subscriptions);

		// TODO: prevent projects from getting too large

		if (projectMemberIds != null) {

			List<Date> dates = parseDates(from, to);

			List<String> dateStrings = new ArrayList<String>();

			// TODO: prevent users from putting in too long a date range

			if (dates != null) {

				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

				for (Date date : dates) {
					dateStrings.add(df.format(date));
				}

				Comparison comparison = new Comparison();

				comparison.setDates(dateStrings);

				Map<String, List<Integer>> users = new HashMap<String, List<Integer>>();

				for (String memberId : projectMemberIds) {

					List<Integer> points = new ArrayList<Integer>();

					for (Date date : dates) {
						points.add(recordDao.getPointsByProjectByUserByDate(projectId, memberId, date));
					}

					String memberNickname = null;
					try {
						memberNickname = userDao.getNicknameByUserId(memberId);
					} catch (EntityNotFoundException e) {
						// TODO: figure out logging
					}

					if (memberNickname != null) {

						users.put(memberNickname, points);
					}
				}

				comparison.setUsers(users);

				model.addAttribute("comparison", comparison);
			}
		}

		return new MappingJackson2JsonView();
	}

	private Set<String> getUserIdsFromSubscriptions(List<Subscription> subscriptions) {
		Set<String> ret = new HashSet<String>();

		if (subscriptions != null) {
			for (Subscription subscription : subscriptions) {
				ret.add(subscription.getUserId());
			}
		}

		return ret;
	}

	private List<Date> parseDates(String fromString, String toString) {
		List<Date> ret = new ArrayList<Date>();

		Calendar fromCal = Calendar.getInstance();
		Calendar toCal = Calendar.getInstance();

		fromCal.set(getYearFromString(fromString), getMonthFromString(fromString), getDateFromString(fromString), 0, 0, 0);
		toCal.set(getYearFromString(toString), getMonthFromString(toString), getDateFromString(toString), 0, 0, 0);

		Date from = fromCal.getTime();
		Date to = toCal.getTime();

		while (from.before(to) || from.equals(to)) {
			ret.add(from);
			fromCal.set(Calendar.DATE, fromCal.get(Calendar.DATE) + 1);
			from = fromCal.getTime();
		}

		return ret;
	}

	private int getYearFromString(String str) {
		return Integer.valueOf(str.substring(0, 4));
	}

	private int getMonthFromString(String str) {
		return Integer.valueOf(str.substring(5, 7)) - 1;
	}

	private int getDateFromString(String str) {
		return Integer.valueOf(str.substring(8, 10));
	}
}
