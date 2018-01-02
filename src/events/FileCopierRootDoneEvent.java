package events;

import java.util.EventObject;

public class FileCopierRootDoneEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private String absolutePath;

	public FileCopierRootDoneEvent(Object source, String absolutePath) {
		super(source);
		this.absolutePath = absolutePath;
	}
	
	public String getAbsolutePath() {
		return absolutePath;
	}
}