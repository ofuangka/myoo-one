package myoo.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import myoo.dto.Project;
import myoo.ext.BaseDao;

import org.springframework.stereotype.Repository;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

@Repository
public class ProjectDao extends BaseDao {

	public Project get(String projectId) throws NumberFormatException, EntityNotFoundException {
		return getAsProject(getDatastore().get(KeyFactory.createKey("Project", Long.valueOf(projectId))));
	}

	public List<Project> all() {
		return getAsProjects(getDatastore().prepare(new Query("Project")).asList(FetchOptions.Builder.withDefaults()));
	}

	public Project put(String name, String description, String createdBy) {
		Date now = Calendar.getInstance().getTime();

		Entity projectEntity = new Entity("Project");
		projectEntity.setProperty("name", name);
		projectEntity.setProperty("description", description);
		projectEntity.setProperty("createdBy", createdBy);
		projectEntity.setProperty("lastUpdatedBy", createdBy);
		projectEntity.setProperty("lastUpdatedTs", now);

		Key key = getDatastore().put(projectEntity);

		Project ret = new Project();
		ret.setId(String.valueOf(key.getId()));
		ret.setName(name);
		ret.setDescription(description);
		ret.setCreatedBy(createdBy);
		ret.setLastUpdatedBy(createdBy);
		ret.setLastUpdatedTs(now);
		return ret;
	}

	public Project update(String projectId, String name, String description, String lastUpdatedBy) throws NumberFormatException, EntityNotFoundException {
		Date now = Calendar.getInstance().getTime();

		// get the original project
		Project ret = get(projectId);

		// create a new project with the changed attributes
		Entity projectEntity = new Entity("Project", Long.valueOf(projectId));

		projectEntity.setProperty("name", name);
		projectEntity.setProperty("description", description);
		projectEntity.setProperty("createdBy", ret.getCreatedBy());
		projectEntity.setProperty("lastUpdatedBy", lastUpdatedBy);
		projectEntity.setProperty("lastUpdatedTs", now);

		// store the new project
		getDatastore().put(projectEntity);

		// update the project in memory with the changed attributes
		ret.setName(name);
		ret.setDescription(description);
		ret.setLastUpdatedBy(lastUpdatedBy);
		ret.setLastUpdatedTs(now);
		return ret;
	}

	public void delete(String projectId) {
		getDatastore().delete(KeyFactory.createKey("Project", Long.valueOf(projectId).longValue()));
	}

	private List<Project> getAsProjects(List<Entity> projectEntities) {
		List<Project> ret = new ArrayList<Project>();
		if (projectEntities != null) {
			for (Entity entity : projectEntities) {
				ret.add(getAsProject(entity));
			}
		}
		return ret;
	}

	private Project getAsProject(Entity projectEntity) {
		Project ret = new Project();
		ret.setId(String.valueOf(projectEntity.getKey().getId()));
		ret.setName((String) projectEntity.getProperty("name"));
		ret.setDescription((String) projectEntity.getProperty("description"));

		ret.setCreatedBy((String) projectEntity.getProperty("createdBy"));
		ret.setLastUpdatedBy((String) projectEntity.getProperty("lastUpdatedBy"));

		ret.setLastUpdatedTs((Date) projectEntity.getProperty("lastUpdatedTs"));
		return ret;
	}
}
