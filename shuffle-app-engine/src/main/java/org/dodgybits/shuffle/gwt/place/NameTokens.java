package org.dodgybits.shuffle.gwt.place;

public class NameTokens {

	public static final String error = "!error";
	public static final String help = "!help";
	public static final String taskList = "!actions";
	public static final String projects = "!projects";
	public static final String restoreFromBackup = "!restore";
	public static final String editAction = "!editAction";

	public static String getError() {
		return error;
	}

	public static String getHelp() {
		return help;
	}
	
	public static String getInbox() {
		return taskList;
	}

	public static String getDueActions() {
		return taskList + ";q=dueActions";
	}

	public static String getNextActions() {
		return taskList + ";q=nextActions";
	}

	public static String getProjects() {
		return projects;
	}

	public static String getTickler() {
        return taskList + ";q=ticker";
	}

    public static String getRestoreFromBackup() {
        return restoreFromBackup;
    }

	public static String getEditAction() {
		return editAction;
	}
	
}
