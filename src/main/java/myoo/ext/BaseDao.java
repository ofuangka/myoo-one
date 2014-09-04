package myoo.ext;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

/**
 * This is the base dao class that dao objects should extend from. It contains
 * logic common to all dao objects
 * 
 * @author ofuangka
 *
 */
public class BaseDao {
	protected DatastoreService getDatastore() {
		return DatastoreServiceFactory.getDatastoreService();
	}
}
