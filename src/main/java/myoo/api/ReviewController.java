package myoo.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myoo.dto.Comparison;
import myoo.ext.BaseController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

@Controller
public class ReviewController extends BaseController {

	@RequestMapping({ "/projects/{projectId}/review/comparison" })
	public View comparison(@PathVariable String projectId, @RequestParam("from") String from, @RequestParam("to") String to, ModelMap model) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		List<String> projectMemberIds = getProjectMemberIds(projectId, datastore);

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
						points.add(getPointsByProjectByUserByDate(projectId, memberId, date, datastore));
					}

					String memberEmail = null;
					try {
						memberEmail = getNicknameByUserId(memberId, datastore);
					} catch (EntityNotFoundException e) {
						// TODO: figure out logging
					}

					if (memberEmail != null) {

						users.put(memberEmail, points);
					}
				}

				comparison.setUsers(users);

				model.addAttribute("comparison", comparison);
			}
		}

		return new MappingJackson2JsonView();
	}

	private String getNicknameByUserId(String userId, DatastoreService datastore) throws EntityNotFoundException {
		return (String) datastore.get(KeyFactory.createKey("User", Long.valueOf(userId))).getProperty("nickname");
	}

	private Integer getPointsByProjectByUserByDate(String projectId, String userId, Date date, DatastoreService datastore) {
		Query query = new Query("Record");
		query.setFilter(getFilterByProjectByUserByDate(projectId, userId, date));
		List<Entity> recordEntities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		Integer ret = 0;
		if (recordEntities != null) {
			for (Entity recordEntity : recordEntities) {
				ret += ((Long) recordEntity.getProperty("points")).intValue();
			}
		}
		return ret;
	}

	private Filter getFilterByProjectByUserByDate(String projectId, String userId, Date date) {
		Calendar fromCal = Calendar.getInstance();
		Calendar toCal = Calendar.getInstance();
		fromCal.setTime(date);
		toCal.setTime(date);
		fromCal.set(Calendar.HOUR_OF_DAY, 0);
		fromCal.set(Calendar.MINUTE, 0);
		fromCal.set(Calendar.SECOND, 0);
		toCal.set(Calendar.HOUR_OF_DAY, 23);
		toCal.set(Calendar.MINUTE, 59);
		toCal.set(Calendar.SECOND, 59);
		Date from = fromCal.getTime();
		Date to = toCal.getTime();
		return CompositeFilterOperator.and(FilterOperator.EQUAL.of("projectId", projectId), FilterOperator.EQUAL.of("userId", userId),
				FilterOperator.GREATER_THAN_OR_EQUAL.of("createdTs", from), FilterOperator.LESS_THAN_OR_EQUAL.of("createdTs", to));
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

	private List<String> getProjectMemberIds(String projectId, DatastoreService datastore) {
		List<String> ret = new ArrayList<String>();
		ret.add(getUserId(datastore));
		return ret;
	}
}
