package jcommander;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import events.FileCopierRootDoneEvent;
import events.FileCopierRootDoneEventListener;

public class FileCopier extends Thread {
	private ArrayList<File> srcFiles;
	private ArrayList<File> roots;
	private File destRoot;
	private JProgressBar progressTotal;
	private JProgressBar progressFile;	
	private File root;
	private long calculatedTotalBytes;
	private long totalBytes;
	private int totalDivider;
	private FileCopierRootDoneEventListener fileCopierRootDoneEventListener;
	
	public FileCopier(ArrayList<File> roots, File destRoot, JProgressBar progressTotal, JProgressBar progressFile) {
		srcFiles = new ArrayList<File>();
		this.roots = roots;
		this.root = roots.get(0).getParentFile();
		this.destRoot = destRoot;
		this.progressTotal = progressTotal;
		this.progressFile = progressFile;
		this.progressTotal.setMinimum(0);
		totalDivider = 1;
	}
	
	public void addFileCopierRootDoneEventListener(FileCopierRootDoneEventListener fcdl) {
		fileCopierRootDoneEventListener = fcdl;
	}
	
	private void calcultateTotalSize(File root) {		
		if (root.isFile()) {			
			calculatedTotalBytes += root.length();
		} else {
			File[] subFiles = root.listFiles();

			for (File file : subFiles) {
				try {
					if (file.isFile()) {						
						calculatedTotalBytes += file.length();
					} else {
						calcultateTotalSize(file);
					}
				} catch (Exception ex) {
				}
			}
		}
	}
	
	private void prepareSrcFiles(File root) {		
		if (root.isFile()) {
			srcFiles.add(root);			
		} else {
			File[] subFiles = root.listFiles();

			for (File file : subFiles) {
				try {
					if (file.isFile()) {
						srcFiles.add(file);						
					} else {
						prepareSrcFiles(file);
					}
				} catch (Exception ex) {
				}
			}
		}		
	}
	
	private File buildDestFile(File srcFile) {
		File destFile = null;
		
		String absoluteRootPath = root.getAbsolutePath();
		
		String absoluteSrcFilePath = srcFile.getAbsolutePath();
		String destAbsolutePath = destRoot.getAbsolutePath() + "\\"
			+ absoluteSrcFilePath.substring(absoluteRootPath.length());
		
		destFile = new File(destAbsolutePath);
		
		return destFile;
	}
	
	private File builDestFolder(File destFile) {
		File destFolder = null;
		
		String absoluteDestFilePath = destFile.getAbsolutePath();
		String destAbsolutePath = absoluteDestFilePath.substring(0, absoluteDestFilePath.lastIndexOf("\\"));
		
		destFolder = new File(destAbsolutePath);
		
		return destFolder;
	}
	
    @Override
    public void run() {
        doIt();
    }
	
	private void doIt() {		
		for(File f : roots) {
			calcultateTotalSize(f);
		}
		if(calculatedTotalBytes > Integer.MAX_VALUE) {
			totalDivider = (int)(calculatedTotalBytes / Integer.MAX_VALUE);
			totalDivider += 1;
		}
		progressTotal.setMaximum((int)(calculatedTotalBytes / totalDivider));
		for(File f : roots) {
			srcFiles.clear();
			prepareSrcFiles(f);
		
			for(File g : srcFiles) {
				try {				
					copyFile(g, buildDestFile(g));
				} catch (IOException e) {
					new JcDialogs().ShowErrorDialog(e.getMessage(), g.getName());
				}
			}
			if(fileCopierRootDoneEventListener != null) {
				fileCopierRootDoneEventListener.FileCopierRootDoneEventSeen(new FileCopierRootDoneEvent(this,f.getAbsolutePath()));
			}			
		}
		if(fileCopierRootDoneEventListener != null) {
			fileCopierRootDoneEventListener.FileCopierRootDoneEventSeen(new FileCopierRootDoneEvent(this, null));
		}
	}

	private void copyFile(File source, File dest) throws IOException {
		File destFolder = builDestFolder(dest);
		if(!destFolder.exists())
			destFolder.mkdirs();
		
		if (!dest.exists()) {
			dest.createNewFile();
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			int progress = 0;
			int divider = 1;
			
			if(source.length() > Integer.MAX_VALUE) {
				divider = (int)(source.length() / Integer.MAX_VALUE);
				divider += 1;
			}
			
			progressFile.setMaximum((int)(source.length() / divider));
			progressFile.setMinimum(1);
			
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);
			
			byte[] buf = new byte[1024];		
			
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
				progress += len / divider;
				totalBytes += len / totalDivider;
				final int fprogress = progress;				
				final int ftprogress = (int)totalBytes;
				
				SwingUtilities.invokeLater(
						new Runnable() {
							public void run() {
								progressFile.setValue(fprogress);
								progressTotal.setValue(ftprogress);
							}
						}
				);
			}			
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}
}