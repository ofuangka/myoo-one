package myoo.dto;

import java.util.Date;

public class Achievement {
	private String id;
	private String projectId;
	private String name;
	private String description;
	private int points;
	private String createdBy;
	private String lastUpdatedBy;
	private Date lastUpdatedTs;
	private int backgroundPositionX;
	private int backgroundPositionY;

	public void setId(String id) {
		this.id = id;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public void setLastUpdatedTs(Date lastUpdatedTs) {
		this.lastUpdatedTs = lastUpdatedTs;
	}
	
	public void setBackgroundPositionX(int backgroundPositionX) {
		this.backgroundPositionX = backgroundPositionX;
	}
	
	public void setBackgroundPositionY(int backgroundPositionY) {
		this.backgroundPositionY = backgroundPositionY;
	}

	public String getId() {
		return this.id;
	}

	public String getProjectId() {
		return this.projectId;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public int getPoints() {
		return this.points;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public String getLastUpdatedBy() {
		return this.lastUpdatedBy;
	}

	public Date getLastUpdatedTs() {
		return this.lastUpdatedTs;
	}
	
	public int getBackgroundPositionX() {
		return backgroundPositionX;
	}
	
	public int getBackgroundPositionY() {
		return backgroundPositionY;
	}
}
