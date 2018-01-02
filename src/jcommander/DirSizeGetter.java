package jcommander;

import java.io.File;

import events.DirSizeGetterEvent;
import events.DirSizeGetterEventListener;

public class DirSizeGetter extends Thread {
	private DirSizeGetterEventListener dirSizeGetterEventListener;
	private File root;
	
	public DirSizeGetter(File root) {
		this.root = root; 
	}
	
    @Override
    public void run() {
    	long size = getDirSize(root);
    	if(dirSizeGetterEventListener != null) {    		
    		dirSizeGetterEventListener.DirSizeGetterDoneSeen(new DirSizeGetterEvent(this,size));
    	}
    }
    
    public void addDirSizeGetterEvent(DirSizeGetterEventListener dsel) {
    	dirSizeGetterEventListener = dsel;
    }	
	
	private long getDirSize(File dir) {
		long size = 0;
		if (dir.isFile()) {
			size = dir.length();
		} else {
			File[] subFiles = dir.listFiles();

			for (File file : subFiles) {
				try {
					if (file.isFile()) {
						size += file.length();
					} else {
						size += getDirSize(file);
					}
				} catch (Exception ex) {
				}
			}
		}
		return size;
	}
}
