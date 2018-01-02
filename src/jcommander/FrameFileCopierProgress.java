package jcommander;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class FrameFileCopierProgress extends javax.swing.JDialog {
	private static final long serialVersionUID = 1L;
	private ArrayList<File> roots;
	private File to;
	private FileCopier fc;
	private JProgressBar progressTotal;
	private JProgressBar progressFile;
	
	private void initComponents() {
		this.progressTotal = new JProgressBar();
		this.progressFile = new JProgressBar();
		this.progressTotal.setStringPainted(true);
		this.progressFile.setStringPainted(true);
		JPanel pnlMain = new JPanel(new BorderLayout());
		pnlMain.add(progressTotal, BorderLayout.NORTH);
		pnlMain.add(progressFile, BorderLayout.SOUTH);	
		setContentPane(pnlMain);
		pack();

		addWindowListener(new java.awt.event.WindowAdapter() {
	        @SuppressWarnings("deprecation")
			public void windowClosing(java.awt.event.WindowEvent evt) {
	        	if(fc != null && fc.isAlive()) {
	        		fc.stop();
	        		fc = null;
	        	}
	        }
	    });
		setSize(new Dimension(400, getHeight()));		
	}
	
    @SuppressWarnings("serial")
	@Override
    protected JRootPane createRootPane() {
        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        AbstractAction actionListener = new AbstractAction() {
            @SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent actionEvent) {
            	if(fc != null && fc.isAlive()) {
	        		fc.stop();
	        		fc = null;
	        		setVisible(false);
	        	}
            }
        };
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", actionListener);

        return rootPane;
    }
	
	public FileCopier getFileCopier() {
		return this.fc;
	}
	
	public FrameFileCopierProgress(javax.swing.JFrame parent, ArrayList<File> roots, File to) {
		super(parent, "Trwa kopiowanie", true);
		initComponents();		
		this.roots = roots;
		this.to = to;
		fc = new FileCopier(this.roots, this.to, this.progressTotal, this.progressFile);
	}
	
	public void fireUp() {		
		fc.start();
	}	
}
