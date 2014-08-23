package myoo.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import myoo.ext.BaseController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

@Controller
public class SelectedProjectsController extends BaseController {

	@RequestMapping(value = "/selected-projects", method = RequestMethod.GET)
	public View selectedProjects(ModelMap model) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String userId = getUserId(datastore);
		Query query = new Query("SelectedProject");
		query.setFilter(FilterOperator.EQUAL.of("userId", userId));
		List<Entity> selectedProjectEntities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		Map<String, Boolean> selectedProjects = new HashMap<String, Boolean>();
		if (selectedProjectEntities != null) {
			for (Entity entity : selectedProjectEntities) {
				selectedProjects.put((String) entity.getProperty("projectId"), true);
			}
		}
		model.addAttribute("selectedProjects", selectedProjects);
		return new MappingJackson2JsonView();
	}

	/**
	 * Deletes all selected project entities and re-adds the new set for the
	 * user
	 * 
	 * @param selectedProjects
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/selected-projects", method = RequestMethod.POST)
	public View updateSelectedProjects(@RequestBody Map<String, Boolean> selectedProjects, ModelMap model) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String userId = getUserId(datastore);
		Query query = new Query("SelectedProject");
		query.setFilter(FilterOperator.EQUAL.of("userId", userId));
		List<Entity> oldProjectEntities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		if (oldProjectEntities != null) {
			List<Key> selectedProjectEntityKeys = new ArrayList<Key>();
			for (Entity entity : oldProjectEntities) {
				selectedProjectEntityKeys.add(entity.getKey());
			}
			datastore.delete(selectedProjectEntityKeys);
		}
		Set<String> newSelectedProjects = selectedProjects.keySet();
		if (newSelectedProjects != null) {
			List<Entity> newProjectEntities = new ArrayList<Entity>();
			for (String projectId : newSelectedProjects) {
				Entity entity = new Entity("SelectedProject");
				entity.setProperty("userId", userId);
				entity.setProperty("projectId", projectId);
				newProjectEntities.add(entity);
			}
			datastore.put(newProjectEntities);
		}
		return new MappingJackson2JsonView();
	}
}
