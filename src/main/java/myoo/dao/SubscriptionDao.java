package myoo.dao;

import java.util.ArrayList;
import java.util.List;

import myoo.dto.Subscription;
import myoo.ext.BaseDao;

import org.springframework.stereotype.Repository;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

@Repository
public class SubscriptionDao extends BaseDao {

	public List<Subscription> getByProjectId(String projectId) {
		DatastoreService datastore = getDatastore();
		Query query = new Query("Subscription");
		query.setFilter(FilterOperator.EQUAL.of("projectId", projectId));
		return getAsSubscriptions(datastore.prepare(query).asList(FetchOptions.Builder.withDefaults()));
	}

	public List<Subscription> getByUserId(String userId) {
		DatastoreService datastore = getDatastore();
		Query query = new Query("Subscription");
		query.setFilter(FilterOperator.EQUAL.of("userId", userId));
		return getAsSubscriptions(datastore.prepare(query).asList(FetchOptions.Builder.withDefaults()));
	}

	private List<Subscription> getAsSubscriptions(List<Entity> subscriptionEntities) {
		List<Subscription> ret = new ArrayList<Subscription>();

		if (subscriptionEntities != null) {
			for (Entity entity : subscriptionEntities) {
				Subscription subscription = new Subscription();
				subscription.setProjectId((String) entity.getProperty("projectId"));
				subscription.setUserId((String) entity.getProperty("userId"));
				ret.add(subscription);
			}
		}

		return ret;
	}

	public void deleteByProjectId(String projectId) {
		Query query = new Query("Subscription");
		query.setFilter(FilterOperator.EQUAL.of("projectId", projectId));
		List<Entity> oldProjectEntities = getDatastore().prepare(query).asList(FetchOptions.Builder.withDefaults());
		if (oldProjectEntities != null) {
			List<Key> subscriptionEntityKeys = new ArrayList<Key>();
			for (Entity entity : oldProjectEntities) {
				subscriptionEntityKeys.add(entity.getKey());
			}
			getDatastore().delete(subscriptionEntityKeys);
		}
	}

	public void deleteByUserId(String userId) {
		Query query = new Query("Subscription");
		query.setFilter(FilterOperator.EQUAL.of("userId", userId));
		List<Entity> oldProjectEntities = getDatastore().prepare(query).asList(FetchOptions.Builder.withDefaults());
		if (oldProjectEntities != null) {
			List<Key> subscriptionEntityKeys = new ArrayList<Key>();
			for (Entity entity : oldProjectEntities) {
				subscriptionEntityKeys.add(entity.getKey());
			}
			getDatastore().delete(subscriptionEntityKeys);
		}
	}

	public Subscription put(String userId, String projectId) {
		Entity entity = new Entity("Subscription");
		entity.setProperty("userId", userId);
		entity.setProperty("projectId", projectId);
		getDatastore().put(entity);

		Subscription ret = new Subscription();
		ret.setProjectId(projectId);
		ret.setUserId(userId);
		return ret;
	}

}
