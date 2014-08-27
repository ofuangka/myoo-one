package myoo.dao;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class BaseDao {
	protected DatastoreService getDatastore() {
		return DatastoreServiceFactory.getDatastoreService();
	}
}
