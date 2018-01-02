package jcommander;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class FrameNewFile extends javax.swing.JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField txtFileName;
	private JButton btnOk;
	private JButton btnCancel;
	private int modalResult;
	public static final int ModalOk = 1;
	public static final int ModalCancel = -1;
	private File root;
	
	@SuppressWarnings("static-access")
	public FrameNewFile(File root) {		
		this.root = root;
		initComponents();
		setModal(true);
		setMinimumSize(new Dimension(300, getSize().height));
		new JcDialogs().setCenterLocation(this);
	}
	
    @SuppressWarnings("serial")
	@Override
    protected JRootPane createRootPane() {
        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        AbstractAction actionListener = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
            	setVisible(false);	        	
            }
        };
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", actionListener);

        return rootPane;
    }

	
	private void createDirectory() {
		String newDirName = txtFileName.getText();
		new File(root.getAbsolutePath() + "\\" + newDirName).mkdir();		
	}
	
	private void initComponents() {
		txtFileName = new JTextField();		
		btnOk  		= new JButton("OK");		
		btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	createDirectory();
            	modalResult = ModalOk;
            	setVisible(false);
            }			
        });
		
		btnCancel  	= new JButton("Cancel");
		btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {            	
            	modalResult = ModalCancel;
            	setVisible(false);
            }			
        });
		
		JPanel pnlMain = new JPanel(new BorderLayout());
		pnlMain.add(txtFileName, BorderLayout.NORTH);
		
		JPanel pnlBtns = new JPanel(new FlowLayout());		
		pnlBtns.add(btnOk);
		pnlBtns.add(btnCancel);
		pnlMain.add(pnlBtns, BorderLayout.SOUTH);
		setContentPane(pnlMain);
		pack();
	}
	
	public int getModalResult() {
		return modalResult;
	}
}
