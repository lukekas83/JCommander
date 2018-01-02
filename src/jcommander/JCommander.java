package jcommander;

import icons.FooIcon;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import events.DirSizeGetterEvent;
import events.DirSizeGetterEventListener;
import events.FileCopierRootDoneEvent;
import events.FileCopierRootDoneEventListener;

public class JCommander extends JFrame {
	private static final long serialVersionUID = 1L;
	private static int LEFT = -1;
	private static int RIGHT = 1;
	private JPanel pnlMain = null;	
	private JPanel pnlCenter = null;
	private JPanel pnlBottom = null;
	private JMenuBar mbMain = null;	
	
	private JTable tableLeft;
	private JTable tableRight;
	private JLabel lblPathLeft = null;
	private JLabel lblPathRight = null;
	private JLabel lblDriverInfoLeft;
	private JLabel lblDriverInfoRight;
	private JLabel lblItemLeft;
	private JLabel lblItemRight;
	private JLabel lblFolder = null;	
	
	private int activeSide = 0;
	private int[] sizes = null;
	
	private DirSizeGetter dg = null;	
	
	final static JCommanderSettingsMng settings = new JCommanderSettingsMng();
	final static JCommanderSettings conf = settings.getConfiguration();
	
	final static Locale currentLocale = new Locale(conf.getLocaleArg1(), conf.getLocaleArg2());
	public static java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("jcommander.MessageBundle", currentLocale);
	final public static Font globalfont = new Font(conf.getFontName(),conf.getFontStyle(), conf.getFontSize());
	
	public JCommander() {
		initComponents();
		setTitle("JCommander");
		setJMenuBar(createMainMenu());
		setContentPane(createContentPanel());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();	
		setCursorSide(conf.getSide());		
	}
	
	private void setCursorSide(int side) {
		if(dg != null && dg.isAlive())
			return;
		
		activeSide = side;
		if(side == LEFT) {
			tableLeft.setRowSelectionAllowed(true);
			tableRight.setRowSelectionAllowed(false);
			tableLeft.requestFocus();		
		}
		else {
			tableLeft.setRowSelectionAllowed(false);
			tableRight.setRowSelectionAllowed(true);
			tableRight.requestFocus();			
		}
	}
	
	private void doSelectionRow(JTable table, boolean moveToNext) {
		int selIdx = 0;
		((FilesTableModel)table.getModel()).setFileSelected(selIdx = table.getSelectedRow(),
				!((FilesTableModel)table.getModel()).isFileSelected(table.getSelectedRow()));	
		
		if(moveToNext && (selIdx + 1 < table.getRowCount())) {
			table.setRowSelectionInterval(++selIdx, selIdx);
			table.scrollRectToVisible(table.getCellRect(selIdx, 0, true));						
		}					
		table.repaint();		
	}
	
	private void addListenersToTable(final JTable table, final int side) {
		table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	setCursorSide(side);
            	if(evt.getClickCount() == 2) {
            		doBindTable(table, side);
            	}
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
            	setCursorSide(side);
            }
        });
		
		table.addKeyListener(new java.awt.event.KeyAdapter() {			
			public void keyPressed(KeyEvent evt) {
				if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_INSERT) {
					doSelectionRow(table, true);
					updateItemLabel(side);
				}
				if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
            		evt.consume();
				if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
					evt.consume();
					setCursorSide(side * -1);
				}				
			}
			
            @SuppressWarnings("deprecation")
			public void keyReleased(java.awt.event.KeyEvent evt) {
            	if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            		evt.consume();
            		doBindTable(table, side);
            	}
            	if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F5){
            		handleCopyRequest();
            	}
            	if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F7) {
            		doCreateNewDirectory();
            	}
            	if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE) {
            		if(((TextIcon)table.getModel().getValueAt(0, 0)).getText().equals("...")) {
            			table.setRowSelectionInterval(0, 0);
            			doBindTable(table, side);
            		}
            	}
            	if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
            		doSelectionRow(table, false);            		
            		updateItemLabel(side);
            		if(table.getModel().getValueAt(table.getSelectedRow(), 2).toString().equals("<DIR>")) {
            			final File root = (File)table.getModel().getValueAt(table.getSelectedRow(), 5);
            			dg = new DirSizeGetter(root);
            			dg.addDirSizeGetterEvent(new DirSizeGetterEventListener() {
            				public void DirSizeGetterDoneSeen(final DirSizeGetterEvent ude) {
            					SwingUtilities.invokeLater(
            							new Runnable() {
            								public void run() {
            									table.getModel().setValueAt(ude.getCalculatedDirSize(),
            	            							((FilesTableModel)table.getModel()).getFileIndex(root.getAbsolutePath()) , 2);
            									table.getModel().setValueAt(ude.getCalculatedDirBytes() ,
            	            							((FilesTableModel)table.getModel()).getFileIndex(root.getAbsolutePath()) , 6);
            	            					table.repaint();
            	            					table.setEnabled(true);            	            					
            	            					setCursor(Cursor.DEFAULT_CURSOR);
            	            					table.requestFocus();
            	            					updateItemLabel(side);
            								}
            							}
            					);
            				}
            			});
            			setCursor(Cursor.WAIT_CURSOR);            			            			
            			table.setEnabled(false);
            			new JcGuiTools().setEnabled(this, false);
            			dg.start();
            		}
            	}
            }
        });
	}
	
    @SuppressWarnings("serial")
	@Override
    protected JRootPane createRootPane() {
        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        AbstractAction actionListener = new AbstractAction() {
            @SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent actionEvent) {
                if(dg != null && dg.isAlive()) {
                	dg.stop();
                	dg = null;
                	if(activeSide == LEFT) {
                		tableLeft.setEnabled(true);
                		tableLeft.requestFocus();
                	}
                	else {
                		tableRight.setEnabled(true);
                		tableRight.requestFocus();
                	}
                	setCursor(Cursor.DEFAULT_CURSOR);
                }
            }
        };
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", actionListener);

        return rootPane;
    }
	
	private void initComponents() {
		tableLeft = new JTable();		
		tableRight = new JTable();
		tableLeft.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableRight.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lblPathLeft = new JLabel(" ", JLabel.LEFT);
		lblPathRight = new JLabel(" ", JLabel.LEFT);
		lblDriverInfoLeft = new JLabel();
		lblDriverInfoRight = new JLabel();
		lblFolder = new JLabel(" ", JLabel.RIGHT);
		
		lblItemLeft = new JLabel(".L " , JLabel.LEFT);
		lblItemRight = new JLabel(".R " , JLabel.LEFT);
		
		addListenersToTable(tableLeft, LEFT);
		addListenersToTable(tableRight, RIGHT);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
	        public void windowClosing(java.awt.event.WindowEvent evt) {
	        	conf.setSide(activeSide);
	            settings.saveConfiguration(conf);
	        }
	    });
		
	}
	
	private void doBindTable(JTable table, int side) {
		File selectedFile = ((File)table.getModel().getValueAt(table.getSelectedRow(), 5));
		
		if(selectedFile != null && selectedFile.isDirectory()) {
			bindTable(table, selectedFile, side);			
		}		
	}
	
	private JPanel createContentPanel() {
		pnlMain = new JPanel(new BorderLayout());		
		pnlMain.add(createTopPanel(), BorderLayout.NORTH);
		pnlMain.add(createCenterPanel(), BorderLayout.CENTER);
		pnlMain.add(createBottomPanel(), BorderLayout.SOUTH);
		return pnlMain;
	}
	
	private JPanel createTopPanel() {	
		FooIcon fi = new FooIcon();
		JPanel pnlTools = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
		JToolBar tbActions = new JToolBar();
		tbActions.setFloatable(false);
		tbActions.setRollover(true);
		
		JButton btnRefresh = new JButton(bundle.getString("JCommander.refresh"), new ImageIcon(fi.getClass().getResource("Refresh.png")));
		btnRefresh.setName("JCommander.refresh");
		tbActions.add(btnRefresh);
		btnRefresh.addActionListener(new java.awt.event.ActionListener() {			
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	if(activeSide == LEFT) {            		
            		bindTable(tableLeft, new File(lblPathLeft.getText()) , -1);            		
            	}
            	else if(activeSide == RIGHT) {
            		bindTable(tableRight, new File(lblPathRight.getText()) , 1);            		
            	}
            }            			
        });
		
		tbActions.add(new JSeparator(1));		
		
		JButton btnGoHome = new JButton(bundle.getString("JCommander.home_folder"), new ImageIcon(fi.getClass().getResource("UserHome.png")));
		btnGoHome.setName("JCommander.home_folder");
		btnGoHome.addActionListener(new java.awt.event.ActionListener() {
			final File homeFile = new File(System.getProperty("user.home"));
            public void actionPerformed(java.awt.event.ActionEvent evt) {            	
            	if(activeSide == LEFT) {
            		bindTable(tableLeft, homeFile, -1);
            		lblPathLeft.setText(homeFile.getAbsolutePath());
            	}
            	else if(activeSide == RIGHT) {
            		bindTable(tableRight, homeFile, -1);
            		lblPathRight.setText(homeFile.getAbsolutePath());
            	}
            }			
        });
		tbActions.add(btnGoHome);
		JButton btnGoDesktop = new JButton(bundle.getString("JCommander.pulpit_folder"));
		btnGoDesktop.setName("JCommander.pulpit_folder");
		btnGoDesktop.addActionListener(new java.awt.event.ActionListener() {
			final File homeFile = new File(System.getProperty("user.home"));
            public void actionPerformed(java.awt.event.ActionEvent evt) {            	
            	if(activeSide == LEFT) {
            		bindTable(tableLeft, homeFile, -1);	
            	}
            	else if(activeSide == RIGHT) {
            		bindTable(tableRight, homeFile, -1);            		       		
            	}
            }			
        });
		JButton btnStartNotePad = new JButton(bundle.getString("JCommander.btnStartNotePad"), new ImageIcon(fi.getClass().getResource("Notepad.png")));
		btnStartNotePad.setName("JCommander.btnStartNotePad");
		btnStartNotePad.addActionListener(new java.awt.event.ActionListener() {			
            public void actionPerformed(java.awt.event.ActionEvent evt) {   
            	try {
                    new ProcessBuilder("notepad").start();
                } catch (IOException ex) {
                    
                }            	
            }			
        });
		tbActions.add(btnStartNotePad);		
		
		JComboBox cbxLangs = new JComboBox();
		cbxLangs.addItem(new LangsComboItem(new String[] { "pl", "PL"}, "Polski        "));
		cbxLangs.addItem(new LangsComboItem(new String[] { "en", "Us"}, "Angielski     "));

		cbxLangs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] newLang = ((LangsComboItem)((JComboBox)e.getSource()).getSelectedItem()).getLang();
				conf.setLocaleArg1(newLang[0]);
				conf.setLocaleArg2(newLang[1]);
				setLanguage(newLang);
			}
		});
		
		tbActions.add(cbxLangs);		
		pnlTools.add(tbActions);
		
		return pnlTools;
	}
	
	private void setLanguage(String[] newLang) {
		Locale newLocale = new Locale(newLang[0], newLang[1]); 
		bundle = java.util.ResourceBundle.getBundle("jcommander.MessageBundle", newLocale);
		setLang(this.getRootPane());
	}
	
    public void setLang(Object object) {    	
    	//System.out.println(tableLeft.getColumnModel().getColumn(0).set
        if (object instanceof JComponent) {
        	System.out.println(object);
        	if(object instanceof JButton) {
        		JButton btnTmp = (JButton)object;
        		String tmpName = btnTmp.getName();
        		if(tmpName != null) {
        			try {
        				String newName = null;
        				if((newName = bundle.getString(tmpName)) != null) {
        					btnTmp.setText(newName);
        					//System.out.println(bundle.getString(tmpName));
        				}
        			}
        			catch(Exception ex) {}
        		}
        	}
        	if(object instanceof TableColumn) {
        		System.out.println("table column");
        		((TableColumn)object).
        		setHeaderValue(bundle.getString("FilesTableModel." + ((TableColumn)object).getIdentifier().toString()));
        	}
            for (int i = 0; i < ((JComponent) object).getComponentCount(); i++) {            	
            	setLang(((JComponent) object).getComponent(i));
            }
        }
    }
	
	private JPanel createCenterPanel() {
		pnlCenter = new JPanel(new GridBagLayout());
		JPanel pnlLeft = createSidePanel(LEFT);
		JPanel pnlRight = createSidePanel(1);		
		JSplitPane pnlSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlLeft, pnlRight);
		GridBagConstraints const_pnl = new GridBagConstraints();
		const_pnl.fill = GridBagConstraints.BOTH;
		const_pnl.weightx = 100;
		const_pnl.weighty = 100;		
		pnlSplit.setContinuousLayout(true);		
		pnlSplit.setResizeWeight(0.5);		
		pnlCenter.add(pnlSplit,const_pnl);		
		return pnlCenter;
	}
	
    private int[] getCurrentColumnSizes(JTable table) {
        int[] ColSizes = null;
        if(table.getColumnModel() != null) {            
            int colsCount = table.getColumnModel().getColumnCount();
            ColSizes = new int[colsCount];
            for(int i = 0 ; i < colsCount ; i++)
                ColSizes[i] = table.getColumnModel().getColumn(i).getWidth();
        }
        return ColSizes;
    }    
    
    private void setCurrentColumnSizes(JTable table, int[] sizes) {        
        if(table.getColumnModel() != null) {
            for(int i = 0 ; i < sizes.length ; i++)
            	table.getColumnModel().getColumn(i).setPreferredWidth(sizes[i]);                
        }        
    }
	
	@SuppressWarnings("deprecation")
	private void bindTable(JTable table, File root, int side) {
		setCursor(Cursor.WAIT_CURSOR);
		sizes = getCurrentColumnSizes(table);
		
		int rowIdx = 0;
		
		File[] files = root.listFiles();
		if(files == null) {
			table.setModel(new DefaultTableModel());
			setCursor(Cursor.DEFAULT_CURSOR);
			return;
		}
		
		table.setModel(new FilesTableModel(files, root));
		
		if(sizes != null)
			setCurrentColumnSizes(table, sizes);
		
		table.getColumnModel().getColumn(0).setCellRenderer(new IconTableCellRenderer());
		for(int i = 1 ; i < table.getColumnCount() ; i++)
			table.getColumnModel().getColumn(i).setCellRenderer(new JCommanderTableCellRenderer());		
		table.setShowGrid(false);
		table.getTableHeader().setReorderingAllowed(false);		
		table.setMinimumSize(new Dimension(200, 400));
		
		if(table.getModel() instanceof  FilesTableModel)
			rowIdx = ((FilesTableModel)table.getModel()).getFileIndex(side == LEFT ? lblPathLeft.getText() : lblPathRight.getText());
		
		if(side == LEFT)
			lblPathLeft.setText(root.getAbsolutePath());
		else
			lblPathRight.setText(root.getAbsolutePath());
		
		table.setRowSelectionInterval(rowIdx, rowIdx);
		table.scrollRectToVisible(table.getCellRect(rowIdx + 3, 0, true));		
		
		table.repaint();
		table.requestFocus();
		updateItemLabel(side);
		setCursor(Cursor.DEFAULT_CURSOR);
	}
	
	private JComboBox buildComboBoxDrives(final int side) {
		File[] drives = File.listRoots();
		JComboBox cbx = new JComboBox();
		for(int d = 0 ; d < drives.length ; d++) {
			cbx.addItem(new DriverComboItem(drives[d]));
		}
		
		cbx.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				File drive = null; 
				try {
					drive = ((DriverComboItem)((JComboBox)e.getSource()).getSelectedItem()).getFile();
					String free = NumberFormat.getInstance().format(drive.getFreeSpace() / 1024);
					String total = NumberFormat.getInstance().format(drive.getTotalSpace() / 1024);
					String driveInfo = String.format(bundle.getString("JCommander.driver_info"), free , total);
					if(side == LEFT)
						lblDriverInfoLeft.setText(driveInfo);
					else
						lblDriverInfoRight.setText(driveInfo);
				
					bindTable(side == LEFT ? tableLeft : tableRight, drive, side);
				}
				catch(Exception ex) { }
				finally {
					setCursor(Cursor.DEFAULT_CURSOR);
					if(side == LEFT) {
						lblPathLeft.setText(drive.getAbsolutePath());
						tableLeft.requestFocus();
					}
					else {
						lblPathRight.setText(drive.getAbsolutePath());
						tableRight.requestFocus();
					}
				}
			}
		});
		cbx.setSelectedIndex(0);
		return cbx;
	}
	

	// side left: -1 ; right: 1
	private JPanel createSidePanel(int side) {
		JPanel pnlSide = new JPanel(new BorderLayout());
		JPanel pnlDrv = new JPanel(new GridBagLayout());
		GridBagConstraints const_cbo = new GridBagConstraints();
		const_cbo.gridx = 0;
		const_cbo.gridwidth = 1;
		const_cbo.anchor = GridBagConstraints.WEST;
		const_cbo.weightx = 0;
		const_cbo.weighty = 100;
		const_cbo.insets = new Insets(2, 2, 2, 2);
		pnlDrv.add(buildComboBoxDrives(side), const_cbo);
		
		GridBagConstraints gbConstraints = new GridBagConstraints();
		gbConstraints.gridx = 1;
		gbConstraints.gridwidth = 2;
		gbConstraints.fill = GridBagConstraints.HORIZONTAL;
		gbConstraints.anchor = GridBagConstraints.WEST;
		gbConstraints.weightx = 100;
		gbConstraints.weighty = 100;
		gbConstraints.insets = new Insets(2, 2, 2, 2);
		pnlDrv.add(side == LEFT ? lblDriverInfoLeft : lblDriverInfoRight, gbConstraints);
		
		pnlSide.add(pnlDrv, BorderLayout.NORTH);		
		pnlSide.add(createContentPanel(side), BorderLayout.CENTER);
		
		return pnlSide;
	}
	
	private void updateItemLabel(final int side) {
		String txt = "%s k / %s k in %s / %s files, %d / %s dir(s)";
		long selectedBytesInFiles = 0, 
			 totalBytesInFiles = 0,
			 selectedFilesCounted = 0,
			 totalFilesCounted = 0,
			 selectedDirs = 0,
			 totalDirs = 0;
		
		JTable table = (side == LEFT ? tableLeft : tableRight);
		totalBytesInFiles = ((FilesTableModel)table.getModel()).getTotalBytesInFiles();
		totalFilesCounted = ((FilesTableModel)table.getModel()).getTotalFilesCounted();
		long[] selectedFilesCnt = ((FilesTableModel)table.getModel()).getSelectedFilesAndBytesCounted();
		selectedFilesCounted = selectedFilesCnt[0];
		selectedBytesInFiles = selectedFilesCnt[1];
		selectedDirs = ((FilesTableModel)table.getModel()).getSelectedDirsCounted();
		totalDirs = ((FilesTableModel)table.getModel()).getTotalDirsCounted();
		
		txt = String.format(txt,
				NumberFormat.getInstance().format(selectedBytesInFiles / 1024),
				NumberFormat.getInstance().format(totalBytesInFiles / 1024),
				NumberFormat.getInstance().format(selectedFilesCounted),
				NumberFormat.getInstance().format(totalFilesCounted),
				selectedDirs, totalDirs);
		
		if(side == LEFT) {
			lblItemLeft.setText(txt);
		}
		else {
			lblItemRight.setText(txt);
		}
	}

	private JPanel createContentPanel(int side) {
		JPanel pnlCnt = new JPanel(new BorderLayout());	
		JPanel pnlPath = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlPath.add(side == LEFT ? lblPathLeft : lblPathRight  );		
		pnlCnt.add(pnlPath, BorderLayout.NORTH);
		
		JScrollPane spTableFiles = new JScrollPane();
		spTableFiles.getViewport().setBackground(Color.WHITE);
		final int _side = side;
		spTableFiles.getViewport().addMouseListener(new java.awt.event.MouseAdapter() {            
            public void mousePressed(java.awt.event.MouseEvent evt) {
            	setCursorSide(_side);            	
            }
        });
		spTableFiles.setViewportView(side == LEFT ? tableLeft : tableRight);		
		pnlCnt.add(spTableFiles, BorderLayout.CENTER);			

		JPanel pnlInfo = new JPanel(new GridBagLayout());
		GridBagConstraints const_lbl_item = new GridBagConstraints();
		const_lbl_item.gridx = 0;
		const_lbl_item.gridwidth = 1;
		const_lbl_item.anchor = GridBagConstraints.WEST;
		const_lbl_item.weightx = 0;
		const_lbl_item.weighty = 100;		
		pnlInfo.add(side == LEFT ? lblItemLeft : lblItemRight, const_lbl_item);
		
		GridBagConstraints const_lbl_size = new GridBagConstraints();
		const_lbl_size.gridx = 1;
		const_lbl_size.gridwidth = 2;
		const_lbl_size.anchor = GridBagConstraints.EAST;
		const_lbl_size.weightx = 100;
		const_lbl_size.weighty = 100;
		JLabel lblSize = new JLabel(" " , JLabel.RIGHT);
		pnlInfo.add(lblSize, const_lbl_size);
		
		pnlCnt.add(pnlInfo, BorderLayout.SOUTH);
		
		return pnlCnt;
	}
	
	private void doCopy(final JTable fromTable, final JTable toTable, final int srcSide) {
		ArrayList<File> roots = new ArrayList<File>();
		for(int r = 0 ; r < fromTable.getRowCount() ; r++) {
			if(((FilesTableModel)fromTable.getModel()).isFileSelected(r)) {
				roots.add((File)fromTable.getModel().getValueAt(r, 5));
			}
		}
		boolean doselction = false;
		if(roots.size() < 1) {
			if(fromTable.getSelectedRow() == -1)
				return;
			doSelectionRow(fromTable, false);
			roots.add((File)fromTable.getModel().getValueAt(fromTable.getSelectedRow(), 5));
			doselction = true;
		}
		final File to = new File(srcSide == -1 ? lblPathRight.getText() : lblPathLeft.getText());
		String question = String.format(bundle.getString("JCommander.copy_question"), roots.size(), to.getAbsolutePath());
		if(!new JcDialogs().ShowYesNoDialog(question)) {			
			if(doselction)
				doSelectionRow(fromTable, false);
			return;
		}
		
		final FrameFileCopierProgress fcp = new FrameFileCopierProgress(this, roots, to);
		fcp.getFileCopier().addFileCopierRootDoneEventListener(new FileCopierRootDoneEventListener() {
			public void FileCopierRootDoneEventSeen(final FileCopierRootDoneEvent fcde) {
				SwingUtilities.invokeLater(
						new Runnable() {
							public void run() {
								if(fcde.getAbsolutePath() != null) {
									((FilesTableModel)fromTable.getModel()).setFileSelected(((FilesTableModel)fromTable.getModel()).getFileIndex(fcde.getAbsolutePath()), false);
									fromTable.repaint();
								}
								else if(fcde.getAbsolutePath() == null) {
									fcp.setVisible(false); //kopiowanie skonczone
									bindTable(toTable, to, srcSide * -1);								
								}
							}
						}
				);
			}
		});
		fcp.fireUp();
		JDialogUtils.setCenterLocation(fcp);
		fcp.setVisible(true);
	}
	
	private void attachNotYet(JButton button) {
		button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	new JcDialogs().ShowInfoDialog("Not yet implemented.");
            }			
        });		
	}
	
	private void handleCopyRequest() {
		if(activeSide == LEFT) {
    		doCopy(tableLeft, tableRight, activeSide);
    	}
    	else if(activeSide == RIGHT) {
    		doCopy(tableRight, tableLeft, activeSide);            		
    	}		
	}
	
	private JPanel createBottomPanel() {
		pnlBottom = new JPanel(new GridLayout(2, 1));
		
		JPanel pnlCmd = new JPanel(new GridBagLayout());
		GridBagConstraints const_lbl = new GridBagConstraints();
		const_lbl.gridx = 0;
		const_lbl.gridwidth = 1;
		const_lbl.anchor = GridBagConstraints.EAST;
		const_lbl.weightx = 100;
		const_lbl.weighty = 100;
		pnlCmd.add(lblFolder, const_lbl);
		
		JComboBox cboCmd = new JComboBox();
		cboCmd.setEditable(true);
		GridBagConstraints const_cbo = new GridBagConstraints();
		const_cbo.gridx = 1;
		const_cbo.gridwidth = 2;
		const_cbo.fill = GridBagConstraints.HORIZONTAL;		
		const_cbo.weightx = 100;
		const_cbo.weighty = 100;		
		pnlCmd.add(cboCmd, const_cbo);
		
		pnlBottom.add(pnlCmd);		
		
		JPanel pnlBtn = new JPanel(new GridLayout(1, 5));		
		
		JButton btnView = new JButton(bundle.getString("JCommander.f3"));
		btnView.setName("JCommander.f3");
		attachNotYet(btnView);
		pnlBtn.add(btnView);
		
		JButton btnEdit = new JButton(bundle.getString("JCommander.f4"));
		btnEdit.setName("JCommander.f4");
		attachNotYet(btnEdit);
		pnlBtn.add(btnEdit);
		
		JButton btnCopy = new JButton(bundle.getString("JCommander.f5"));
		btnCopy.setName("JCommander.f5");		
		btnCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	handleCopyRequest();
            }			
        });
		pnlBtn.add(btnCopy);
		
		JButton btnMove = new JButton(bundle.getString("JCommander.f6"));
		btnMove.setName("JCommander.f6");
		attachNotYet(btnMove);
		pnlBtn.add(btnMove);
		
		JButton btnNewFolder = new JButton(bundle.getString("JCommander.f7"));
		btnNewFolder.setName("JCommander.f7");
		
		btnNewFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	doCreateNewDirectory();
            }			
        });
		pnlBtn.add(btnNewFolder);
		
		JButton btnDelete = new JButton(bundle.getString("JCommander.f8"));
		btnDelete.setName("JCommander.f8");
		attachNotYet(btnDelete);
		pnlBtn.add(btnDelete);		
		
		JButton btnExit = new JButton(bundle.getString("JCommander.alt_f4"));
		btnExit.setName("JCommander.alt_f4");
		pnlBtn.add(btnExit);
		btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	System.exit(0);
            }			
        });
		
		pnlBottom.add(pnlBtn);
		
		return pnlBottom;
	}
	
	private void doCreateNewDirectory() {
    	File root = null;
    	FrameNewFile fnf = new FrameNewFile(root = new File(activeSide == LEFT ? lblPathLeft.getText() : lblPathRight.getText()));
    	fnf.setVisible(true);
    	if(fnf.getModalResult() == FrameNewFile.ModalOk) {
        	if(activeSide == LEFT) {            		
        		bindTable(tableLeft, root , -1);            		
        	}
        	else if(activeSide == RIGHT) {
        		bindTable(tableRight, root , 1);            		
        	}
    	}
	}
	
	private JMenuBar createMainMenu() {
		JMenu menFile = new JMenu(bundle.getString("JCommander.menu_file"));
		JMenu menHelp = new JMenu(bundle.getString("JCommander.menu_help"));
		
		JMenuItem miExit = new JMenuItem(bundle.getString("JCommander.mi_exit"));
		miExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	System.exit(0);
            }			
        });
		
		JMenuItem miAbout = new JMenuItem(bundle.getString("JCommander.mi_about"));
		miAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	new JcDialogs().ShowInfoDialog(bundle.getString("JCommander.about_txt"));
            }			
        });
		
		menFile.add(miExit);
		menHelp.add(miAbout);
			
		mbMain = new JMenuBar();
		mbMain.add(menFile);
		mbMain.add(menHelp);
		
		return mbMain;
	}
	
    private static void setLookAndFeel() throws Exception {
        try {
            boolean looked_and_feeled = false;
            String style_name = conf.getStyleName();
            UIManager.LookAndFeelInfo[] installed_lookandfeel = UIManager.getInstalledLookAndFeels();
            for (int i = 0; i < installed_lookandfeel.length; i++) {
            	//System.out.println(installed_lookandfeel[i].getName());
                if (installed_lookandfeel[i].getName().equals(style_name)) {                	
                    UIManager.setLookAndFeel(installed_lookandfeel[i].getClassName());
                    looked_and_feeled = true;
                    break;
                }
            }
            if (!looked_and_feeled) {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {			
			@Override
			public void run() {
				try {
					//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					setLookAndFeel();
				} 
				catch (Exception e) { }		    	
		    	JCommander jc = new JCommander();
		    	new JcGuiTools(globalfont).setGUI(jc);
				jc.setVisible(true);
			}
		});		
	}
}
