package org.lucci.lmu.gui;

import javax.swing.JPanel;

import org.lucci.lmu.Model;
import org.lucci.lmu.output.WriterException;


/*
 * Created on Oct 4, 2004
 */

/**
 * @author luc.hogie
 */
public abstract class ClassDiagramViewer extends JPanel
{

	public abstract void setModel(Model model) throws WriterException;

	public abstract void redraw();
	public abstract String getFriendlyName();
}
