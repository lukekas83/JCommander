package IconsObj;

import java.net.URL;

import javax.swing.ImageIcon;

public class FileIcon {
	private static FileIcon fileIcon;
	private javax.swing.Icon icon;
	
	protected FileIcon(URL iconFileName) {
		icon = new ImageIcon(iconFileName);
	}
	
	public static FileIcon getInstance(URL iconFileName) {
		if(fileIcon == null) {
			synchronized (FileIcon.class) {
				if(fileIcon == null) {
					fileIcon = new FileIcon(iconFileName);
				}				
			}			
		}
		return fileIcon;
	}
	
	public javax.swing.Icon getIcon() {
		return icon;
	}

}
