package myoo.ext;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class BaseController {
	public String getUserId(DatastoreService datastore) {
		User currentUser = getCurrentUser();

		Key userKey = null;
		Query query = new Query("User");
		query.setFilter(Query.FilterOperator.EQUAL.of("email", currentUser.getEmail()));
		List<Entity> userEntities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		if ((userEntities == null) || (userEntities.size() == 0)) {
			Entity userEntity = new Entity("User");
			userEntity.setProperty("email", currentUser.getEmail());
			userEntity.setProperty("nickname", currentUser.getNickname());
			userKey = datastore.put(userEntity);
		} else {
			if (userEntities.size() > 1) {
			}
			userKey = ((Entity) userEntities.get(0)).getKey();
		}
		return String.valueOf(userKey.getId());
	}

	public User getCurrentUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}
}
