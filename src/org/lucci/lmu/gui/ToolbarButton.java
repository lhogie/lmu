package org.lucci.lmu.gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;

/*
 * Created on Oct 5, 2004
 */

/**
 * @author luc.hogie
 */
public class ToolbarButton extends JButton
{
	public ToolbarButton(Icon icon, String tip, String shortCut)
	{
		if (shortCut != null)
		{
			tip += " (" + shortCut + ")";
		}
		
		setToolTipText(tip);
		setIcon(icon);
		setFocusable(false);
		setFocusPainted(false);
		addMouseListener(new MouseHandler());
	}
	
	private class MouseHandler implements MouseListener
	{

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent arg0)
		{
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent arg0)
		{
//			setBorderPainted(true);
			setBackground(Color.white);
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent arg0)
		{
//			setBorderPainted(false);
			setBackground(Color.lightGray);
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent arg0)
		{
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent arg0)
		{
		}
		
	}
}
