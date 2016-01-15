package org.lucci.lmu.gui.renderer;

import java.awt.GridLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;


import org.lucci.lmu.Model;
import org.lucci.lmu.gui.ClassDiagramViewer;
import org.lucci.lmu.output.AbstractWriter;
import org.lucci.lmu.output.WriterException;

/*
 * Created on Oct 4, 2004
 */

/**
 * @author luc.hogie
 */
public abstract class TextRenderer extends ClassDiagramViewer
{
	JTextArea  area = new JTextArea();
	
	public TextRenderer()
	{
		setLayout(new GridLayout(1, 1));
		add(new JScrollPane(area));
	}

	@Override
    public synchronized void setModel(Model model) throws WriterException
	{
		AbstractWriter factory = getWriter();
		area.setText(new String(factory.writeModel(model)));
	}

	@Override
	public void redraw()
	{
		area.repaint();
	}
	
	public abstract AbstractWriter getWriter();
	
}
