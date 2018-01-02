package IconsObj;

import java.net.URL;

import javax.swing.ImageIcon;

public class FolderIcon {
	private static FolderIcon folderIcon;
	private javax.swing.Icon icon;
	
	protected FolderIcon(URL iconFileName) {
		icon = new ImageIcon(iconFileName);
	}
	
	public static FolderIcon getInstance(URL iconFileName) {
		if(folderIcon == null) {
			synchronized (FolderIcon.class) {
				if(folderIcon == null) {
					folderIcon = new FolderIcon(iconFileName);
				}				
			}			
		}
		return folderIcon;
	}
	
	public javax.swing.Icon getIcon() {
		return icon;
	}
}