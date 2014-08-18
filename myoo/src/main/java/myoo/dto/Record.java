package myoo.dto;

import java.util.Date;

public class Record {
	private String achievementId;
	private Date createdTs;
	private int points;

	public void setAchievementId(String achievementId) {
		this.achievementId = achievementId;
	}

	public void setCreatedTs(Date createdTs) {
		this.createdTs = createdTs;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getAchievementId() {
		return this.achievementId;
	}

	public Date getCreatedTs() {
		return this.createdTs;
	}

	public int getPoints() {
		return this.points;
	}
}
