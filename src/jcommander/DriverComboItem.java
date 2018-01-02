package jcommander;

import java.io.File;

public class DriverComboItem {
	private File file;	
	
	public DriverComboItem(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}	
	
	public String toString() {
		return "[-" +  file.toString().substring(0,file.toString().indexOf(":")) + "-]";
	}
}
