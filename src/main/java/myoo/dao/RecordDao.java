package myoo.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import myoo.dto.Record;
import myoo.ext.BaseDao;

import org.springframework.stereotype.Repository;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

/**
 * This dao manages data access to the Record model
 * 
 * @author ofuangka
 *
 */
@Repository
public class RecordDao extends BaseDao {

	public Integer getPointsByProjectByUserByDate(String projectId, String userId, Date date) {
		Query query = new Query("Record");
		query.setFilter(getFilterByProjectByUserByDate(projectId, userId, date));
		List<Entity> recordEntities = getDatastore().prepare(query).asList(FetchOptions.Builder.withDefaults());
		Integer ret = 0;
		if (recordEntities != null) {
			for (Entity recordEntity : recordEntities) {
				ret += ((Long) recordEntity.getProperty("points")).intValue();
			}
		}
		return ret;
	}

	private Filter getFilterByProjectByUserByDate(String projectId, String userId, Date date) {
		Calendar toCal = Calendar.getInstance();
		toCal.setTime(date);
		toCal.set(Calendar.HOUR_OF_DAY, 23);
		toCal.set(Calendar.MINUTE, 59);
		toCal.set(Calendar.SECOND, 59);
		toCal.set(Calendar.MILLISECOND, 999);
		Date to = toCal.getTime();
		return CompositeFilterOperator.and(FilterOperator.EQUAL.of("projectId", projectId), FilterOperator.EQUAL.of("userId", userId),
				FilterOperator.LESS_THAN_OR_EQUAL.of("createdTs", to));
	}

	private List<Record> getAsRecords(List<Entity> recordEntities) {

		List<Record> ret = new ArrayList<Record>();
		if (recordEntities != null) {
			for (Entity entity : recordEntities) {
				Record record = new Record();
				record.setAchievementId((String) entity.getProperty("achievementId"));
				record.setCreatedTs((Date) entity.getProperty("createdTs"));
				record.setPoints(((Long) entity.getProperty("points")).intValue());
				ret.add(record);
			}
		}

		return ret;
	}

	public List<Record> getByUserId(String userId) {
		Query query = new Query("Record");
		query.setFilter(getFilterByUserId(userId));
		return getAsRecords(getDatastore().prepare(query).asList(FetchOptions.Builder.withDefaults()));
	}

	public List<Record> getByProjectIdByUserIdByDate(String projectId, String userId, Date from, Date to) {
		Query query = new Query("Record");
		query.setFilter(getFilterByProjectIdByUserId(projectId, userId, from, to));
		return getAsRecords(getDatastore().prepare(query).asList(FetchOptions.Builder.withDefaults()));
	}

	public List<Record> getByAchievementIdByUserId(String achievementId, String userId) {
		Query query = new Query("Record");
		query.setFilter(getFilterByAchievementIdByUserId(achievementId, userId));
		return getAsRecords(getDatastore().prepare(query).asList(FetchOptions.Builder.withDefaults()));
	}

	private Filter getFilterByUserId(String userId) {
		return FilterOperator.EQUAL.of("userId", userId);
	}

	private Filter getFilterByProjectIdByUserId(String projectId, String userId, Date from, Date to) {
		Calendar toCal = Calendar.getInstance();
		toCal.setTime(to);
		toCal.set(Calendar.HOUR_OF_DAY, 23);
		toCal.set(Calendar.MINUTE, 59);
		toCal.set(Calendar.SECOND, 59);
		toCal.set(Calendar.MILLISECOND, 999);
		return CompositeFilterOperator.and(FilterOperator.EQUAL.of("projectId", projectId), FilterOperator.EQUAL.of("userId", userId),
				FilterOperator.GREATER_THAN_OR_EQUAL.of("createdTs", from), FilterOperator.LESS_THAN_OR_EQUAL.of("createdTs", toCal.getTime()));
	}

	private Filter getFilterByAchievementIdByUserId(String achievementId, String userId) {
		return CompositeFilterOperator.and(FilterOperator.EQUAL.of("achievementId", achievementId), FilterOperator.EQUAL.of("userId", userId));
	}

	public Record put(String userId, String projectId, String achievementId, Integer points) {

		Date now = Calendar.getInstance().getTime();

		Entity entity = new Entity("Record");
		entity.setProperty("userId", userId);
		entity.setProperty("projectId", projectId);
		entity.setProperty("achievementId", achievementId);
		entity.setProperty("points", points);
		entity.setProperty("createdTs", now);

		getDatastore().put(entity);

		Record ret = new Record();
		ret.setAchievementId(achievementId);
		ret.setCreatedTs(now);
		ret.setPoints(points);

		return ret;
	}

	private Filter getFilterByProjectIdByUserId(String projectId, String userId) {
		return CompositeFilterOperator.and(getFilterByProjectId(projectId), FilterOperator.EQUAL.of("userId", userId));
	}

	public void deleteByProjectIdByUserId(String projectId, String userId) {
		deleteByFilter(getFilterByProjectIdByUserId(projectId, userId));
	}
	
	private Filter getFilterByProjectId(String projectId) {
		return FilterOperator.EQUAL.of("projectId", projectId);
	}
	
	private void deleteByFilter(Filter filter) {
		Query query = new Query("Record");
		query.setFilter(filter);
		List<Entity> recordEntities = getDatastore().prepare(query).asList(FetchOptions.Builder.withDefaults());
		if (recordEntities != null) {
			List<Key> recordKeys = new ArrayList<Key>();
			for (Entity entity : recordEntities) {
				recordKeys.add(entity.getKey());
			}
			getDatastore().delete(recordKeys);
		}
	}
	
	public void deleteByProjectId(String projectId) {
		deleteByFilter(getFilterByProjectId(projectId));
	}
	
	private Filter getFilterByAchievementId(String achievementId) {
		return FilterOperator.EQUAL.of("achievementId", achievementId);
	}
	
	public void deleteByAchievementId(String achievementId) {
		deleteByFilter(getFilterByAchievementId(achievementId));
	}
}
