package IconsObj;

import java.net.URL;

import javax.swing.ImageIcon;

public class FolderIconHidden {
	private static FolderIconHidden folderIcon;
	private javax.swing.Icon icon;
	
	protected FolderIconHidden(URL iconFileName) {
		icon = new ImageIcon(iconFileName);
	}
	
	public static FolderIconHidden getInstance(URL iconFileName) {
		if(folderIcon == null) {
			 synchronized(FolderIconHidden.class) {
				 if(folderIcon == null)
					 folderIcon = new FolderIconHidden(iconFileName);
			 }
		}
		return folderIcon;
	}
	
	public javax.swing.Icon getIcon() {
		return icon;
	}
}
