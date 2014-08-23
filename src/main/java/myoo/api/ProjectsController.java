package myoo.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import myoo.dto.Achievement;
import myoo.dto.Project;
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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.users.User;

@Controller
@RequestMapping({ "/projects" })
public class ProjectsController extends BaseController {
	@RequestMapping(value = { "", "/" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public View projects(ModelMap model) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Query query = new Query("Project");
		List<Entity> projectEntities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

		List<Project> projects = new ArrayList<Project>();
		if (projectEntities != null) {
			for (Entity entity : projectEntities) {
				Project project = new Project();
				project.setId(String.valueOf(entity.getKey().getId()));
				project.setName((String) entity.getProperty("name"));
				project.setDescription((String) entity.getProperty("description"));

				project.setCreatedBy((String) entity.getProperty("createdBy"));
				project.setLastUpdatedBy((String) entity.getProperty("lastUpdatedBy"));

				project.setLastUpdatedTs((Date) entity.getProperty("lastUpdatedTs"));

				projects.add(project);
			}
		}
		model.addAttribute("projects", projects);
		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "", "/" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST }, consumes = { "application/json" })
	public View insertProject(@RequestBody Project project, ModelMap model) {
		Date now = Calendar.getInstance().getTime();
		User currentUser = getCurrentUser();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity projectEntity = new Entity("Project");
		projectEntity.setProperty("name", project.getName());
		projectEntity.setProperty("description", project.getDescription());
		projectEntity.setProperty("createdBy", currentUser.getNickname());
		projectEntity.setProperty("lastUpdatedBy", currentUser.getNickname());
		projectEntity.setProperty("lastUpdatedTs", now);

		Key key = datastore.put(projectEntity);
		
		Entity selectedProjectEntity = new Entity("SelectedProject");
		selectedProjectEntity.setProperty("projectId", String.valueOf(key.getId()));
		selectedProjectEntity.setProperty("userId", getUserId(datastore));
		datastore.put(selectedProjectEntity);
		
		project.setId(String.valueOf(key.getId()));
		project.setCreatedBy(currentUser.getNickname());
		project.setLastUpdatedBy(currentUser.getNickname());
		project.setLastUpdatedTs(now);

		model.addAttribute("project", project);

		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "", "/" }, method = { org.springframework.web.bind.annotation.RequestMethod.PUT }, consumes = { "application/json" })
	public View updateProject(@RequestBody Project project, ModelMap model) {
		Date now = Calendar.getInstance().getTime();
		User currentUser = getCurrentUser();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity projectEntity = new Entity("Project", Long.valueOf(project.getId()).longValue());

		projectEntity.setProperty("name", project.getName());
		projectEntity.setProperty("description", project.getDescription());
		projectEntity.setProperty("lastUpdatedBy", currentUser.getNickname());
		projectEntity.setProperty("lastUpdatedTs", now);

		datastore.put(projectEntity);

		project.setLastUpdatedBy(currentUser.getNickname());
		project.setLastUpdatedTs(now);

		model.put("project", project);

		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/{projectId}" }, method = { org.springframework.web.bind.annotation.RequestMethod.DELETE })
	public View deleteProject(@PathVariable String projectId, ModelMap model) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		List<Entity> selectedProjectEntities = getSelectedProjectEntitiesByProjectId(projectId, datastore);
		if (selectedProjectEntities != null) {
			List<Key> selectedProjectKeys = new ArrayList<Key>();
			for (Entity entity : selectedProjectEntities) {
				selectedProjectKeys.add(entity.getKey());
			}
			datastore.delete(selectedProjectKeys);
		}

		List<Entity> achievementEntities = getAchievementsByProjectId(projectId, datastore);
		if (achievementEntities != null) {
			List<Key> achievementKeys = new ArrayList<Key>();
			for (Entity entity : achievementEntities) {
				achievementKeys.add(entity.getKey());
			}
			datastore.delete(achievementKeys);
		}
		datastore.delete(new Key[] { KeyFactory.createKey("Project", Long.valueOf(projectId).longValue()) });

		return new MappingJackson2JsonView();
	}

	private List<Entity> getSelectedProjectEntitiesByProjectId(String projectId, DatastoreService datastore) {
		String userId = getUserId(datastore);
		Query query = new Query("SelectedProject");
		query.setFilter(FilterOperator.EQUAL.of("userId", userId));
		return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
	}

	@RequestMapping(value = { "/{projectId}/achievements" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public View achievements(@PathVariable String projectId, ModelMap model) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		List<Entity> achievementEntities = getAchievementsByProjectId(projectId, datastore);

		List<Achievement> achievements = new ArrayList<Achievement>();
		if (achievementEntities != null) {
			for (Entity entity : achievementEntities) {
				Achievement achievement = new Achievement();
				achievement.setProjectId((String) entity.getProperty("projectId"));

				achievement.setId(String.valueOf(entity.getKey().getId()));
				achievement.setName((String) entity.getProperty("name"));
				achievement.setDescription((String) entity.getProperty("description"));

				achievement.setPoints(((Long) entity.getProperty("points")).intValue());

				achievement.setCreatedBy((String) entity.getProperty("createdBy"));

				achievement.setLastUpdatedBy((String) entity.getProperty("lastUpdatedBy"));

				achievement.setLastUpdatedTs((Date) entity.getProperty("lastUpdatedTs"));

				achievements.add(achievement);
			}
		}
		model.addAttribute("achievements", achievements);
		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/{projectId}/achievements" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public View insertAchievement(@PathVariable String projectId, @RequestBody Achievement achievement, ModelMap model) {
		Date now = Calendar.getInstance().getTime();
		User currentUser = getCurrentUser();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity achievementEntity = new Entity("Achievement");
		achievementEntity.setProperty("projectId", projectId);
		achievementEntity.setProperty("name", achievement.getName());
		achievementEntity.setProperty("description", achievement.getDescription());

		achievementEntity.setProperty("points", Integer.valueOf(achievement.getPoints()));
		achievementEntity.setProperty("createdBy", currentUser.getNickname());
		achievementEntity.setProperty("lastUpdatedBy", currentUser.getNickname());

		achievementEntity.setProperty("lastUpdatedTs", now);

		Key key = datastore.put(achievementEntity);
		achievement.setId(String.valueOf(key.getId()));
		achievement.setProjectId(projectId);
		achievement.setCreatedBy(currentUser.getNickname());
		achievement.setLastUpdatedBy(currentUser.getNickname());
		achievement.setLastUpdatedTs(now);

		model.addAttribute("achievement", achievement);

		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/{projectId}/achievements" }, method = { org.springframework.web.bind.annotation.RequestMethod.PUT })
	public View updateAchievement(@PathVariable String projectId, @RequestBody Achievement achievement, ModelMap model) {
		Date now = Calendar.getInstance().getTime();
		User currentUser = getCurrentUser();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity achievementEntity = new Entity("Achievement", Long.valueOf(achievement.getId()).longValue());

		achievementEntity.setProperty("projectId", achievement.getProjectId());
		achievementEntity.setProperty("name", achievement.getName());
		achievementEntity.setProperty("description", achievement.getDescription());

		achievementEntity.setProperty("points", Integer.valueOf(achievement.getPoints()));
		achievementEntity.setProperty("lastUpdatedBy", currentUser.getNickname());

		achievementEntity.setProperty("lastUpdatedTs", now);

		datastore.put(achievementEntity);

		achievement.setLastUpdatedBy(currentUser.getNickname());
		achievement.setLastUpdatedTs(now);

		model.addAttribute("achievement", achievement);

		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/{projectId}/achievements/{achievementId}" }, method = { org.springframework.web.bind.annotation.RequestMethod.DELETE })
	public View deleteAchievement(@PathVariable String projectId, @PathVariable String achievementId, ModelMap model) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		datastore.delete(KeyFactory.createKey("Achievement", Long.valueOf(achievementId).longValue()));

		return new MappingJackson2JsonView();
	}

	protected List<Entity> getAchievementsByProjectId(String projectId, DatastoreService datastore) {
		Query query = new Query("Achievement");
		query.setFilter(FilterOperator.EQUAL.of("projectId", projectId));
		return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
	}
}
