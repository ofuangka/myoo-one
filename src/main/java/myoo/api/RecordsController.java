package myoo.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import myoo.dto.Achievement;
import myoo.dto.Record;
import myoo.ext.BaseController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

@Controller
public class RecordsController extends BaseController {
	private static final String ID_ALL = "all";

	@RequestMapping(value = { "/projects/{projectId}/achievements/{achievementId}/records" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public View recordsByAchievementId(@PathVariable String projectId, @PathVariable String achievementId, ModelMap model) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		String userId = getUserId(datastore);

		Query query = new Query("Record");
		if (ID_ALL.equalsIgnoreCase(achievementId)) {
			if (ID_ALL.equalsIgnoreCase(projectId)) {
				query.setFilter(getRecordFilter(userId));
			} else {
				query.setFilter(getRecordFilterByProjectId(projectId, userId));
			}
		} else {
			query.setFilter(getRecordFilterByAchievementId(achievementId, userId));
		}
		List<Entity> recordEntities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

		List<Record> records = new ArrayList<Record>();
		if (recordEntities != null) {
			for (Entity entity : recordEntities) {
				Record record = new Record();
				record.setAchievementId((String) entity.getProperty("achievementId"));
				record.setCreatedTs((Date) entity.getProperty("createdTs"));
				record.setPoints(((Long) entity.getProperty("points")).intValue());
				records.add(record);
			}
		}
		model.addAttribute("records", records);

		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/projects/{projectId}/achievements/{achievementId}/records" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public View insertRecord(@PathVariable String projectId, @PathVariable String achievementId, @RequestBody Record record, ModelMap model)
			throws NumberFormatException, EntityNotFoundException {
		Date now = Calendar.getInstance().getTime();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		String userId = getUserId(datastore);

		Achievement achievement = getAchievement(achievementId, datastore);

		Entity entity = new Entity("Record");
		entity.setProperty("userId", userId);
		entity.setProperty("projectId", projectId);
		entity.setProperty("achievementId", achievementId);
		entity.setProperty("points", Integer.valueOf(achievement.getPoints()));
		entity.setProperty("createdTs", now);

		datastore.put(entity);

		record.setAchievementId(achievementId);
		record.setCreatedTs(now);
		record.setPoints(achievement.getPoints());

		model.put("record", record);

		return new MappingJackson2JsonView();
	}

	private Query.Filter getRecordFilter(String userId) {
		return Query.FilterOperator.EQUAL.of("userId", userId);
	}

	private Query.Filter getRecordFilterByProjectId(String projectId, String userId) {
		Calendar todayCal = Calendar.getInstance();

		todayCal.set(todayCal.get(1), todayCal.get(2), todayCal.get(5), 0, 0, 0);
		return Query.CompositeFilterOperator.and(new Query.Filter[] { Query.FilterOperator.EQUAL.of("projectId", projectId),
				Query.FilterOperator.EQUAL.of("userId", userId), Query.FilterOperator.GREATER_THAN_OR_EQUAL.of("createdTs", todayCal.getTime()) });
	}

	private Query.Filter getRecordFilterByAchievementId(String achievementId, String userId) {
		return Query.CompositeFilterOperator.and(new Query.Filter[] { Query.FilterOperator.EQUAL.of("achievementId", achievementId),
				Query.FilterOperator.EQUAL.of("userId", userId) });
	}

	private Achievement getAchievement(String achievementId, DatastoreService datastore) throws NumberFormatException, EntityNotFoundException {
		Entity achievementEntity = datastore.get(KeyFactory.createKey("Achievement", Long.valueOf(achievementId).longValue()));
		Achievement ret = new Achievement();
		ret.setId(achievementId);
		ret.setCreatedBy((String) achievementEntity.getProperty("createdBy"));
		ret.setDescription((String) achievementEntity.getProperty("description"));
		ret.setLastUpdatedBy((String) achievementEntity.getProperty("lastUpdatedBy"));
		ret.setLastUpdatedTs((Date) achievementEntity.getProperty("lastUpdatedTs"));
		ret.setName((String) achievementEntity.getProperty("name"));
		ret.setPoints(((Long) achievementEntity.getProperty("points")).intValue());
		ret.setProjectId((String) achievementEntity.getProperty("projectId"));
		return ret;
	}
}
