package myoo.dao;

import java.util.List;

import myoo.ext.BaseDao;

import org.springframework.stereotype.Repository;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;

@Repository
public class UserDao extends BaseDao {

	public String getUserId(User currentUser) {

		Key userKey = null;
		Query query = new Query("User");
		query.setFilter(Query.FilterOperator.EQUAL.of("email", currentUser.getEmail()));
		List<Entity> userEntities = getDatastore().prepare(query).asList(FetchOptions.Builder.withDefaults());
		if ((userEntities == null) || (userEntities.size() == 0)) {
			Entity userEntity = new Entity("User");
			userEntity.setProperty("email", currentUser.getEmail());
			userEntity.setProperty("nickname", currentUser.getNickname());
			userKey = getDatastore().put(userEntity);
		} else {
			userKey = ((Entity) userEntities.get(0)).getKey();

			// delete any entries beyond the first one
			if (userEntities.size() > 1) {
				for (int i = 1; i < userEntities.size(); i++) {
					getDatastore().delete(userEntities.get(i).getKey());
				}
			}
		}
		return String.valueOf(userKey.getId());
	}

	public String getNicknameByUserId(String userId) throws EntityNotFoundException {
		return (String) getDatastore().get(KeyFactory.createKey("User", Long.valueOf(userId))).getProperty("nickname");
	}
}
