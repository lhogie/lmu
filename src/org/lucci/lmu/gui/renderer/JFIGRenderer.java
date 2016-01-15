package org.lucci.lmu.gui.renderer;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.lucci.lmu.Model;
import org.lucci.lmu.gui.ClassDiagramViewer;
import org.lucci.lmu.output.GraphVizBasedViewFactory;
import org.lucci.lmu.output.WriterException;

import jfig.gui.JFigViewerBean;

/*
 * Created on Oct 4, 2004
 */

/**
 * @author luc.hogie
 */
public class JFIGRenderer extends ClassDiagramViewer
{
	JFigViewerBean figbean = new JFigViewerBean();
	private String figText;

	public JFIGRenderer()
	{
		setLayout(new GridLayout(1, 1));
		add(figbean);
		figbean.setPreferredSize(new Dimension(600, 600));
		figbean.createDefaultKeyHandler(); // useful shortcut keys
		figbean.createDefaultPopupMenu(); // zoom, panning, options
		figbean.createDefaultDragHandler(); // panning via mouse-drag
		figbean.createPositionAndZoomPanel(); // show cursor position

	}

	@Override
	public String getFriendlyName()
	{
		return "JFigBean (buggy)";
	}

	@Override
	public synchronized void setModel(Model model) throws WriterException
	{
		GraphVizBasedViewFactory imgFactory = new GraphVizBasedViewFactory("fig");
		byte[] bytes = imgFactory.writeModel(model);
		this.figText = new String(bytes);
		figbean.setAntiAlias(true);

		try
		{
			File tempFile = File.createTempFile("lmu", "fig");
			tempFile.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tempFile);
			fos.write(figText.getBytes());
			fos.flush();
			fos.close();
			figbean.setURL(tempFile.toURI().toURL());
		}
		catch (IOException ex)
		{
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public void redraw()
	{
		figbean.doFullRedraw();
		figbean.doZoomFit();
	}
}
