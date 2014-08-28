package myoo.api;

import myoo.dao.AchievementDao;
import myoo.dao.ProjectDao;
import myoo.dao.SubscriptionDao;
import myoo.dao.UserDao;
import myoo.dto.Project;
import myoo.ext.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.User;

@Controller
public class ProjectController extends BaseController {

	@Autowired
	private UserDao userDao;

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private SubscriptionDao subscriptionDao;

	@Autowired
	private AchievementDao achievementDao;

	@RequestMapping(value = { "/projects" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public View projects(ModelMap model) {
		model.addAttribute("projects", projectDao.all());
		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/projects" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST }, consumes = { "application/json" })
	public View insertProject(@RequestBody Project projectIn, ModelMap model) {
		User currentUser = getCurrentUser();

		// insert the project
		Project projectOut = projectDao.put(projectIn.getName(), projectIn.getDescription(), currentUser.getNickname());

		// have the user automatically subscribe to the project
		subscriptionDao.put(currentUser.getUserId(), projectOut.getId());

		// add the resulting project to the model
		model.addAttribute("project", projectOut);

		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/projects" }, method = { org.springframework.web.bind.annotation.RequestMethod.PUT }, consumes = { "application/json" })
	public View updateProject(@RequestBody Project project, ModelMap model) throws NumberFormatException, EntityNotFoundException {
		User currentUser = getCurrentUser();

		model.put("project", projectDao.update(project.getId(), project.getName(), project.getDescription(), currentUser.getNickname()));

		return new MappingJackson2JsonView();
	}

	@RequestMapping(value = { "/projects/{projectId}" }, method = { org.springframework.web.bind.annotation.RequestMethod.DELETE })
	public View deleteProject(@PathVariable String projectId, ModelMap model) {
		subscriptionDao.deleteByProjectId(projectId);
		achievementDao.deleteByProjectId(projectId);
		projectDao.delete(projectId);
		return new MappingJackson2JsonView();
	}

}
