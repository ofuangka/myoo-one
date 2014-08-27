package myoo.dao;

import java.util.Date;

import myoo.dto.Achievement;

import org.springframework.stereotype.Repository;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

@Repository
public class AchievementDao extends BaseDao {

	public Achievement get(String achievementId) throws NumberFormatException, EntityNotFoundException {
		Entity achievementEntity = getDatastore().get(KeyFactory.createKey("Achievement", Long.valueOf(achievementId).longValue()));
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
