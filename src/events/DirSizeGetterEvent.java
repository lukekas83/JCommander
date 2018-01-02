package events;

import java.text.NumberFormat;
import java.util.EventObject;

public class DirSizeGetterEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private long size;

	public DirSizeGetterEvent(Object source, long size) {
		super(source);
		this.size = size;
	}
	
	public String getCalculatedDirSize() {
		return NumberFormat.getInstance().format(size);
	}
	
	public long getCalculatedDirBytes() {
		return size;
	}
}
