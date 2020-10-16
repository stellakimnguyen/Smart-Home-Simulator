package models;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class SHS extends Module {
	private LocalDateTime currentTime;
	private boolean isRunning;
	private User activeUser;

	private List<User> userList;
	private List<Module> moduleList;
	private List<Location> home;

	public SHS(String name) {
		super(name);
		this.userList = new LinkedList<>();
		this.moduleList = new LinkedList<>();
		this.home = new LinkedList<>();
		this.isRunning = false;
	}

	public LocalDateTime getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(LocalDateTime currentTime) {
		this.currentTime = currentTime;
	}

	public boolean isRunning() {
		return isRunning;
	}
	public void setRunning(boolean running) {
		isRunning = running;
	}

	public User getActiveUser() {
		return activeUser;
	}
	public void setActiveUser(User activeUser) {
		if (userList.contains(activeUser)) {
			this.activeUser = activeUser;
		}
	}

	public List<User> getUserList() {
		return userList;
	}
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public List<Module> getModuleList() {
		return moduleList;
	}
	public void setModuleList(List<Module> moduleList) {
		this.moduleList = moduleList;
	}

	public List<Location> getHome() {
		return home;
	}
	public void setHome(List<Location> home) {
		this.home = home;
	}
}
