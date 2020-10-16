package models;

public class Window extends Device {
	private boolean isBlocked;

	public static final String actionBlock = "block";
	public static final String actionUnblock = "unblock";
	public static final String statusBlocked = "blocked";

	public Window(String name, Location location, boolean isBlocked) {
		super(name, location);
		this.isBlocked = isBlocked;
		super.setStatus(Device.statusClosed);
	}

	public boolean isBlocked() {
		return isBlocked;
	}
	public void setBlocked(boolean blocked) {
		isBlocked = blocked;
	}

	@Override
	public String getStatus() {
		String status = super.getStatus();
		if (isBlocked) {
			status = status + "," + statusBlocked;
		}
		return status;
	}
	@Override
	public void setStatus(String status) {
		if (status.equals(Device.statusOpen) || status.equals(Device.statusClosed)) {
			super.setStatus(status);
		}
	}

	@Override
	public boolean doAction(String action) {
		if (action.equals(Device.actionOpen)) {
			super.setStatus(Device.statusOpen);
			return true;
		} else if (action.equals(Device.actionClose)) {
			super.setStatus(Device.statusClosed);
			return true;
		} else if (action.equals(actionBlock)) {
			setBlocked(true);
		} else if (action.equals(actionUnblock)) {
			setBlocked(false);
		}
		return false;
	}
}
