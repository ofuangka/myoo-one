package myoo.dto;

import java.util.List;
import java.util.Map;

public class Comparison {
	private List<String> dates;
	private Map<String, List<Integer>> users;

	public List<String> getDates() {
		return dates;
	}

	public void setDates(List<String> dates) {
		this.dates = dates;
	}

	public Map<String, List<Integer>> getUsers() {
		return users;
	}

	public void setUsers(Map<String, List<Integer>> users) {
		this.users = users;
	}
}
