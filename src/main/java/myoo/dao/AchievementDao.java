package myoo.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import myoo.dto.Achievement;
import myoo.ext.BaseDao;

import org.springframework.stereotype.Repository;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

/**
 * This dao manages data access to Achievement model
 * 
 * @author ofuangka
 *
 */
@Repository
public class AchievementDao extends BaseDao {

	public Achievement get(String achievementId) throws NumberFormatException, EntityNotFoundException {
		Entity achievementEntity = getDatastore().get(KeyFactory.createKey("Achievement", Long.valueOf(achievementId)));
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

	public Achievement put(String projectId, String name, String description, Integer points, String createdBy) {
		Date now = Calendar.getInstance().getTime();

		Entity achievementEntity = new Entity("Achievement");
		achievementEntity.setProperty("projectId", projectId);
		achievementEntity.setProperty("name", name);
		achievementEntity.setProperty("description", description);

		achievementEntity.setProperty("points", points);
		achievementEntity.setProperty("createdBy", createdBy);
		achievementEntity.setProperty("lastUpdatedBy", createdBy);

		achievementEntity.setProperty("lastUpdatedTs", now);

		Key key = getDatastore().put(achievementEntity);

		Achievement ret = new Achievement();

		ret.setId(String.valueOf(key.getId()));
		ret.setProjectId(projectId);
		ret.setName(name);
		ret.setDescription(description);
		ret.setCreatedBy(createdBy);
		ret.setLastUpdatedBy(createdBy);
		ret.setPoints(points);
		ret.setLastUpdatedTs(now);

		return ret;

	}

	public Achievement update(String achievementId, String projectId, String name, String description, Integer points, String lastUpdatedBy)
			throws NumberFormatException, EntityNotFoundException {
		Date now = Calendar.getInstance().getTime();

		Achievement ret = get(achievementId);

		Entity achievementEntity = new Entity("Achievement", Long.valueOf(achievementId));

		achievementEntity.setProperty("projectId", projectId);
		achievementEntity.setProperty("name", name);
		achievementEntity.setProperty("description", description);

		achievementEntity.setProperty("points", points);
		achievementEntity.setProperty("createdBy", ret.getCreatedBy());
		achievementEntity.setProperty("lastUpdatedBy", lastUpdatedBy);

		achievementEntity.setProperty("lastUpdatedTs", now);

		getDatastore().put(achievementEntity);

		ret.setDescription(description);
		ret.setLastUpdatedBy(lastUpdatedBy);
		ret.setLastUpdatedTs(now);
		ret.setName(name);
		ret.setPoints(points);

		return ret;
	}

	public void delete(String achievementId) {
		getDatastore().delete(KeyFactory.createKey("Achievement", Long.valueOf(achievementId)));
	}

	public List<Achievement> getByProjectId(String projectId) {
		Query query = new Query("Achievement");
		query.setFilter(FilterOperator.EQUAL.of("projectId", projectId));
		List<Entity> achievementEntities = getDatastore().prepare(query).asList(FetchOptions.Builder.withDefaults());
		return getAsAchievements(achievementEntities);
	}

	public void deleteByProjectId(String projectId) {
		Query query = new Query("Achievement");
		query.setFilter(FilterOperator.EQUAL.of("projectId", projectId));
		List<Entity> achievementEntities = getDatastore().prepare(query).asList(FetchOptions.Builder.withDefaults());
		if (achievementEntities != null) {
			List<Key> achievementKeys = new ArrayList<Key>();
			for (Entity entity : achievementEntities) {
				achievementKeys.add(entity.getKey());
			}
			getDatastore().delete(achievementKeys);
		}
	}

	private List<Achievement> getAsAchievements(List<Entity> achievementEntities) {
		List<Achievement> ret = new ArrayList<Achievement>();
		if (achievementEntities != null) {
			for (Entity entity : achievementEntities) {
				Achievement achievement = new Achievement();
				achievement.setCreatedBy((String) entity.getProperty("createdBy"));
				achievement.setDescription((String) entity.getProperty("description"));
				achievement.setId(String.valueOf(entity.getKey().getId()));
				achievement.setLastUpdatedBy((String) entity.getProperty("lastUpdatedBy"));
				achievement.setLastUpdatedTs((Date) entity.getProperty("lastUpdatedTs"));
				achievement.setName((String) entity.getProperty("name"));
				achievement.setPoints(((Long) entity.getProperty("points")).intValue());
				achievement.setProjectId((String) entity.getProperty("projectId"));
				ret.add(achievement);
			}
		}
		return ret;
	}
}
