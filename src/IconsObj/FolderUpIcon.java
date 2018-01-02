package IconsObj;

import java.net.URL;

import javax.swing.ImageIcon;

public class FolderUpIcon {
	private static FolderUpIcon folderUpIcon;
	private javax.swing.Icon icon;
		
	protected FolderUpIcon(URL iconFileName) {
		icon = new ImageIcon(iconFileName);
	}
	
	public static FolderUpIcon getInstance(URL iconFileName) {
		if(folderUpIcon == null) {
			synchronized (FolderUpIcon.class) {
				if(folderUpIcon == null) {
					folderUpIcon = new FolderUpIcon(iconFileName);
				}				
			}			
		}
			return folderUpIcon;
	}
		
	public javax.swing.Icon getIcon() {
		return icon;
	}
}
