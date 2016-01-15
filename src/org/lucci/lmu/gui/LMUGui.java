package org.lucci.lmu.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.lucci.lmu.Model;
import org.lucci.lmu.gui.renderer.DotRenderer;
import org.lucci.lmu.gui.renderer.JFIGRenderer;
import org.lucci.lmu.gui.renderer.PDFRenderer;
import org.lucci.lmu.input.LmuException;
import org.lucci.lmu.input.LmuParser;
import org.lucci.lmu.input.StdOutAnalyserLog;
import org.lucci.lmu.output.AbstractWriter;
import org.lucci.lmu.output.LmuWriter;
import org.lucci.lmu.output.WriterException;

import toools.io.JavaResource;

/*
 * Created on Oct 3, 2004
 */

/**
 * @author luc.hogie
 */
public class LMUGui extends JFrame
{
	public static void main(String[] args)
	{
		new LMUGui("load /Users/lhogie/lib/java/commons-logging-api.jar");
	}

	JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	JLabel assistantLabel = new ErrorMessageLabel();

	LmuTextArea editor = new LmuTextArea();

	JScrollPane editorScrollPane;

	JPanel editorPanel;

	Model model = new Model();

	List<ClassDiagramViewer> renderers = new ArrayList<ClassDiagramViewer>();

	JMenuItem fileOpenButton = new JMenuItem("Open a new file", new ImageIcon(getClass()
			.getResource("Open.png")));
	JMenuItem fileSaveButton = new JMenuItem("Save the current file", new ImageIcon(
			getClass().getResource("Save.png")));
	JMenuItem fileSaveAsButton = new JMenuItem(
			"Save the current file using a different name", new ImageIcon(getClass()
					.getResource("SaveAs.png")));
	JMenuItem fileCloseButton = new JMenuItem(
			"Close (potentially save) the current file", new ImageIcon(getClass()
					.getResource("Delete.png")));
	JMenuItem fileExportButton = new JMenuItem(
			"Export the current model file to various file formats", new ImageIcon(
					getClass().getResource("SaveDB.png")));
	JMenuItem fileExitButton = new JMenuItem("Exit", new ImageIcon(getClass()
			.getResource("SaveDB.png")));
	JMenuItem srcReformat = new JMenuItem("Reformat");
	JMenuItem srcExample = new JMenuItem("Create example");
	JMenu codeTemplatesMenu = new JMenu("Code templates");
	JTabbedPane tabbedPane = new JTabbedPane();
	File file;

	int modificationCount = 0;

	private Map<String, String> codeTemplates = new HashMap<String, String>();

	public LMUGui(String lmu)
	{
		super();

		renderers.add(new PDFRenderer());
		renderers.add(new DotRenderer());
		// renderers.add(new LMURenderer());
		// renderers.add(new PostscriptRenderer());
		// renderers.add(new DetailRenderer());
		renderers.add(new JFIGRenderer());
		// renderers.add(new ImageRenderer());

		defineCodeGenerationRules();
		buildWidgets();
		initShortCuts();
		initEvents();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(3 * screenSize.width / 4, 3 * screenSize.height / 4);
		setLocation(screenSize.width / 8, screenSize.height / 8);
		setVisible(true);

		// the frame has to be visible for the following statement to work
		splitPane.setDividerLocation(0.35);

		if (lmu == null)
		{
			user_createExampleModel();
		}
		else
		{
			load(lmu);
		}
	}

	private void defineCodeGenerationRules()
	{
		this.codeTemplates.put("Create new entity", "entity new_entity");
		this.codeTemplates.put("Retain entities matching a given regular expression",
				"retain_entities .*");
		this.codeTemplates.put("Remove entities matching a given regular expression",
				"remove_entities .*");
		this.codeTemplates.put("Remove namespaces", "hide packages in .*");
		this.codeTemplates.put("Hide attributes", "hide attributes in .*");
		this.codeTemplates.put("Hide operations", "hide operations in .*");
		this.codeTemplates.put("Group entities", "group A B C");
		this.codeTemplates.put("Remove isolated entities", "remove_isolated_entities");
		this.codeTemplates.put("Retain entities connected to",
				"retain_entities_connected_to .* 1");
		this.codeTemplates.put("Hide non public elements", "hide_non_public_elements");
		this.codeTemplates.put("Group entities", "group ");
		this.codeTemplates.put("Load a JAR or LMU file", "load file.jar");
		this.codeTemplates.put("Retain isolated entities", "retain_isolated_entities");
		this.codeTemplates.put("Retain the largest connected component",
				"retain_largest_connected_component");
		this.codeTemplates.put("Describe namespace", "describe_namespace");
		this.codeTemplates.put("Group by namespace", "group by namespace");

	}

	private void initEvents()
	{
		addWindowListener(new WindowHandler());
		// diagramRenderer.addComponentListener(new DiagramRendererHandler());
		editor.getDocument().addDocumentListener(new DocumentHandler());
	}

	private void buildWidgets()
	{

		for (ClassDiagramViewer v : this.renderers)
		{
			tabbedPane.addTab(v.getFriendlyName(), v);
			// v.setOpaque(true);
			// v.setBackground(Color.black);
		}

		tabbedPane.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				updateView();
			}
		});

		this.editorPanel = new JPanel(new BorderLayout());

		this.splitPane.setContinuousLayout(true);

		this.assistantLabel.setForeground(Color.black);
		this.assistantLabel.setIcon(new ImageIcon(getClass().getResource(
				"smileys/grin.gif")));
		this.assistantLabel.setFont(new Font("Helvetica", Font.PLAIN, 12));
		this.assistantLabel.setIconTextGap(30);
		this.assistantLabel
				.setText("Hi! I'm the LMU code analyser. I'm gonna <b>help</b> you!<br>You can start describing your object-oriented model using the LMU language. Enjoy!");

		updateFrameTitle();

		editorScrollPane = new JScrollPane(editor);
		editorPanel.add(editorScrollPane, BorderLayout.CENTER);
		editorPanel.add(assistantLabel, BorderLayout.SOUTH);

		splitPane.setLeftComponent(editorPanel);
		splitPane.setRightComponent(tabbedPane);

		setContentPane(splitPane);

		setJMenuBar(createMenuBar());
	}

	private JMenuBar createMenuBar()
	{

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.addSeparator();
		fileMenu.add(fileOpenButton);
		fileMenu.add(fileSaveButton);
		fileMenu.add(fileSaveAsButton);
		fileMenu.add(fileCloseButton);
		fileMenu.addSeparator();
		fileMenu.add(fileExportButton);
		fileMenu.addSeparator();
		fileMenu.add(fileExitButton);
		menuBar.add(fileMenu);

		JMenu editMenu = new JMenu("Edit");
		editMenu.add(srcExample);
		editMenu.add(srcReformat);
		menuBar.add(editMenu);

		List<String> keys = new ArrayList<String>(codeTemplates.keySet());
		Collections.sort(keys);

		for (String key : keys)
		{
			codeTemplatesMenu.add(new JMenuItem(key));
		}

		menuBar.add(codeTemplatesMenu);

		declareMenuItemTrigger(fileMenu, new ButtonHandler());
		declareMenuItemTrigger(editMenu, new ButtonHandler());
		declareMenuItemTrigger(codeTemplatesMenu, new ButtonHandler());
		return menuBar;
	}

	private void declareMenuItemTrigger(JMenu menu, ActionListener al)
	{
		for (Component component : menu.getMenuComponents())
		{
			if (component instanceof JMenuItem)
			{
				((JMenuItem) component).addActionListener(al);
			}
		}
	}

	private void initShortCuts()
	{
		editor.getActionMap().put("Update view", new AbstractAction()
		{
			public void actionPerformed(ActionEvent evt)
			{
				updateView();
			}
		});

		editor.getInputMap().put(KeyStroke.getKeyStroke("F5"), "Update view");

		editor.getActionMap().put("Save", new AbstractAction()
		{
			public void actionPerformed(ActionEvent evt)
			{
				user_save();
			}
		});

		editor.getInputMap().put(KeyStroke.getKeyStroke("control S"), "Save");

		editor.getActionMap().put("Open", new AbstractAction("Open")
		{
			public void actionPerformed(ActionEvent evt)
			{
				user_open();
			}
		});

		editor.getInputMap().put(KeyStroke.getKeyStroke("control O"), "Open");

		editor.getActionMap().put("Reformat", new AbstractAction("Reformat")
		{
			public void actionPerformed(ActionEvent evt)
			{
				user_reformat();
			}
		});

		editor.getInputMap().put(KeyStroke.getKeyStroke("control R"), "Reformat");

		editor.getActionMap().put("Close", new AbstractAction("Close")
		{
			public void actionPerformed(ActionEvent evt)
			{
				user_close();
			}
		});

		editor.getInputMap().put(KeyStroke.getKeyStroke("control W"), "Close");

		editor.getActionMap().put("Exit", new AbstractAction("Exit")
		{
			public void actionPerformed(ActionEvent evt)
			{
				user_exit();
			}
		});

		editor.getInputMap().put(KeyStroke.getKeyStroke("control Q"), "Exit");

		editor.getInputMap().put(KeyStroke.getKeyStroke("control T"), "Config");
	}

	private void updateFrameTitle()
	{
		String s = "LMU";

		if (file == null)
		{
			if (modificationCount > 0)
			{
				s += " (modified)";
			}
			else
			{
				s += " - the UML class diagram generator";
			}
		}
		else
		{
			s += " - " + file.getAbsolutePath();

			if (modificationCount > 0)
			{
				s += " (modified)";
			}
		}

		setTitle(s);
	}

	private boolean parse()
	{
		try
		{
			LmuParser parser = LmuParser.getParser();
			this.model = parser.createModel(editor.getText(), new StdOutAnalyserLog());
			assistantLabel.setIcon(new ImageIcon(getClass().getResource(
					"smileys/smiley.gif")));
			assistantLabel.setText("Good job!");
			return true;
		}
		catch (LmuException ex)
		{
			assistantLabel.setIcon(new ImageIcon(getClass().getResource(
					"smileys/angry.gif")));
			assistantLabel.setText(ex.getMessage());
			return false;
		}
	}

	final Collection<Thread> runningThreads = new Vector<Thread>();

	private void updateView()
	{
		if (this.model != null)
		{
			if (runningThreads.isEmpty())
			{
				new Thread()
				{
					public void run()
					{
						runningThreads.add(this);

						try
						{
							ClassDiagramViewer v = getCurrentViewer();
							v.setModel(model);
							v.redraw();
						}
						catch (WriterException ex)
						{
							assistantLabel.setForeground(Color.red);
							assistantLabel.setText(ex.getMessage());
							ex.printStackTrace();
						}

						runningThreads.remove(this);
					}
				}.run();
			}
		}
	}

	private void save(File f) throws IOException
	{
		FileOutputStream fis = new FileOutputStream(f);
		fis.write(editor.getText().getBytes());
		fis.flush();
		fis.close();
		LMUGui.this.file = f;
		modificationCount = 0;
		updateFrameTitle();
	}

	private void export(Model diagram, File f)
	{
		try
		{
			AbstractWriter factory = AbstractWriter.getTextFactory(FileChooser
					.getFileExtension(f.getName()));
			FileOutputStream fis = new FileOutputStream(f);
			fis.write(factory.writeModel(diagram));
			fis.flush();
			fis.close();
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(
					LMUGui.this,
					"I/O error while writing " + f.getAbsolutePath() + ": "
							+ ex.getMessage());
		}
		catch (WriterException ex)
		{
			JOptionPane.showMessageDialog(LMUGui.this, "Error: " + ex.getMessage());
		}

	}

	private void user_close()
	{
		if (modificationCount > 0)
		{
			int response = JOptionPane.showConfirmDialog(LMUGui.this,
					"Model has not been saved. Do you want to save it before closing?");

			if (response == 0)
			{
				user_save();

				if (modificationCount == 0)
				{
					user_close();
				}
			}
			else if (response == 1)
			{
				editor.setText("");
				file = null;
				modificationCount = 0;
				updateFrameTitle();
			}
			else
			{
			}
		}
		else
		{
			editor.setText("");
			file = null;
			modificationCount = 0;
			updateFrameTitle();
		}
	}

	private void user_saveAs()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Save LMU model file...");
		chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		chooser.addChoosableFileFilter(new FileChooser("lmu", "LMU model files"));

		if (chooser.showSaveDialog(LMUGui.this) == JFileChooser.APPROVE_OPTION)
		{
			File chosenFile = chooser.getSelectedFile();

			if (chosenFile.exists())
			{
				int response = JOptionPane.showConfirmDialog(LMUGui.this,
						"File already exist. Do you want to overwrite it?");

				if (response == 0)
				{
					try
					{
						save(chosenFile);
					}
					catch (IOException ex)
					{
						JOptionPane
								.showMessageDialog(
										LMUGui.this,
										"I/O error while writing "
												+ chosenFile.getAbsolutePath());
					}
				}
				else if (response == 1)
				{
					user_saveAs();
				}
				else
				{
				}
			}
			else
			{
				try
				{
					save(chosenFile);
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(LMUGui.this, "I/O error while writing "
							+ chosenFile.getAbsolutePath());
				}
			}
		}
	}

	private void user_export()
	{
		try
		{
			LmuParser parser = LmuParser.getParser();
			Model model = parser.createModel(editor.getText(), new StdOutAnalyserLog());

			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Export LMU model file...");
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			chooser.addChoosableFileFilter(new FileChooser("dot",
					"GraphViz DOT graph description"));
			chooser.addChoosableFileFilter(new FileChooser("ps", "Adobe PostScript text"));
			chooser.addChoosableFileFilter(new FileChooser("png",
					"Portable Network Graphics bitmap image"));
			chooser.addChoosableFileFilter(new FileChooser("fig",
					"FIG vector-based image"));
			chooser.addChoosableFileFilter(new FileChooser("svg",
					"Scalable Vector Graphics document"));

			if (chooser.showSaveDialog(LMUGui.this) == JFileChooser.APPROVE_OPTION)
			{
				File f = chooser.getSelectedFile();

				if (f.exists())
				{
					int response = JOptionPane.showConfirmDialog(LMUGui.this,
							"File already exist. Do you want to overwrite it?");

					if (response == 0)
					{
						export(model, f);
					}
					else if (response == 1)
					{
						user_export();
					}
					else
					{
					}
				}
				else
				{
					export(model, f);
				}
			}
		}
		catch (LmuException ex)
		{
			JOptionPane.showMessageDialog(LMUGui.this,
					"You can't export the model since it contains parse errors");
		}
	}

	private void user_save()
	{
		if (this.file == null)
		{
			user_saveAs();
		}
		else
		{
			try
			{
				save(file);
			}
			catch (IOException ex)
			{
				JOptionPane.showMessageDialog(LMUGui.this, "I/O error while writing "
						+ file.getAbsolutePath());
			}
		}
	}

	private void user_open()
	{
		user_close();

		// if the user did close the model file
		if (file == null && modificationCount == 0)
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Load LMU model file...");
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			chooser.addChoosableFileFilter(new FileChooser("lmu", "LMU model files"));

			if (chooser.showOpenDialog(LMUGui.this) == JFileChooser.APPROVE_OPTION)
			{
				File f = chooser.getSelectedFile();

				try
				{
					FileInputStream fis = new FileInputStream(f);
					byte[] bytes = new byte[(int) f.length()];
					fis.read(bytes);
					fis.close();
					editor.setText(new String(bytes));
					editor.setCaretPosition(0);
					LMUGui.this.file = f;
					modificationCount = 0;
					updateFrameTitle();
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(LMUGui.this, "I/O error while reading "
							+ f.getAbsolutePath());
				}
			}
		}
	}

	private void user_reformat()
	{
		try
		{
			Model diagram = LmuParser.getParser().createModel(editor.getText(),
					new StdOutAnalyserLog());
			AbstractWriter factory = new LmuWriter();

			try
			{
				String newText = new String(factory.writeModel(diagram));
				editor.setText(newText);
			}
			catch (WriterException ex)
			{
				JOptionPane.showMessageDialog(LMUGui.this,
						"Reformat error: " + ex.getMessage());
			}
		}
		catch (LmuException ex)
		{
			JOptionPane.showMessageDialog(LMUGui.this,
					"You can't reformat the text since it contains parse errors");
		}
	}

	public void load(String lmu)
	{
		editor.setText(lmu);
	}

	private void user_createExampleModel()
	{
		try
		{
			JavaResource resource = new JavaResource(getClass(), "car.lmu");
			load(new String(resource.getByteArray()));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void user_exit()
	{
		while (file != null || modificationCount > 0)
		{
			JOptionPane.showMessageDialog(LMUGui.this,
					"You must close the current model before exiting");
			user_close();
		}

		dispose();
		System.exit(0);
	}

	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			Object source = event.getSource();

			if (source instanceof JMenuItem)
			{
				JMenuItem sourceMenuItem = (JMenuItem) source;

				if (codeTemplates.get(sourceMenuItem.getText()) != null)
				{
					LMUGui.this.editor.insertText(codeTemplates.get(sourceMenuItem
							.getText()) + '\n');
				}
				else
				{
					if (source == fileSaveAsButton)
					{
						user_saveAs();
					}
					else if (source == fileExportButton)
					{
						user_export();
					}
					else if (source == fileCloseButton)
					{
						user_close();
					}
					else if (source == fileSaveButton)
					{
						user_save();
					}
					else if (source == fileOpenButton)
					{
						user_open();
					}
					else if (source == srcReformat)
					{
						user_reformat();
					}
					else if (source == srcExample)
					{
						user_createExampleModel();
					}
					else if (source == fileExitButton)
					{
						user_exit();
					}
					else
					{
						throw new IllegalStateException();
					}
				}
			}
		}
	}

	private class WindowHandler implements WindowListener
	{
		public void windowActivated(WindowEvent arg0)
		{
		}

		public void windowClosed(WindowEvent arg0)
		{
		}

		public void windowClosing(WindowEvent arg0)
		{
			user_exit();
		}

		public void windowDeactivated(WindowEvent arg0)
		{
		}

		public void windowDeiconified(WindowEvent arg0)
		{
		}

		public void windowIconified(WindowEvent arg0)
		{
		}

		public void windowOpened(WindowEvent arg0)
		{
		}
	}

	private class DiagramRendererHandler implements ComponentListener
	{
		public void componentHidden(ComponentEvent arg0)
		{
		}

		public void componentMoved(ComponentEvent arg0)
		{
		}

		public void componentResized(ComponentEvent arg0)
		{
			getCurrentViewer().redraw();
		}

		public void componentShown(ComponentEvent arg0)
		{
		}
	}

	public ClassDiagramViewer getCurrentViewer()
	{
		return (ClassDiagramViewer) tabbedPane
				.getComponent(tabbedPane.getSelectedIndex());
	}

	private class DocumentHandler implements DocumentListener
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.event.DocumentListener#changedUpdate(javax.swing.event
		 * .DocumentEvent)
		 */
		public void changedUpdate(DocumentEvent arg0)
		{
			textModified();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.event.DocumentListener#insertUpdate(javax.swing.event
		 * .DocumentEvent)
		 */
		public void insertUpdate(DocumentEvent arg0)
		{
			textModified();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.event.DocumentListener#removeUpdate(javax.swing.event
		 * .DocumentEvent)
		 */
		public void removeUpdate(DocumentEvent arg0)
		{
			textModified();
		}

		private void textModified()
		{
			++modificationCount;

			if (Math.random() < modificationCount / 10000d)
			{
				JOptionPane.showMessageDialog(LMUGui.this, "You've made "
						+ modificationCount
						+ " modifications on your model. You should save your work!");
			}

			updateFrameTitle();

			new Thread()
			{

				@Override
				public void run()
				{
					if (parse())
					{
						updateView();
					}
				}

			}.run();

		}
	}
}
