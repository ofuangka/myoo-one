package myoo.api;

import myoo.ext.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Controller
@RequestMapping({ "/review" })
public class ReviewController extends BaseController {
	@RequestMapping({ "/comparison" })
	public View comparison(@RequestParam("from") String from, @RequestParam("to") String to, ModelMap model) {
		return new MappingJackson2JsonView();
	}
}
