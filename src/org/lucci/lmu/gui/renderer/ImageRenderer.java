package org.lucci.lmu.gui.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;


import org.lucci.lmu.Entities;
import org.lucci.lmu.Model;
import org.lucci.lmu.gui.ClassDiagramViewer;
import org.lucci.lmu.output.GraphVizBasedViewFactory;
import org.lucci.lmu.output.WriterException;

import toools.io.FileUtilities;

/*
 * Created on Oct 4, 2004
 */

/**
 * @author luc.hogie
 */
public class ImageRenderer extends ClassDiagramViewer
{
	JButton button = new JButton()
	{
		 public void paintComponent(Graphics g) {
		      super.paintComponent(g);
		      g.drawImage(image, 0, 0, this);
		  }

	};
	private String format = "png";
	private Model model;
	private Image image;

	public ImageRenderer()
	{
		button.setForeground(Color.white);
		button.setOpaque(true);
	
		setLayout(new GridLayout(1, 1));
		add(button);
		button.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					setModel(model);
					redraw();
				}
				catch (WriterException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	@Override
	public String getFriendlyName()
	{
		return "Bitmap (not finished)";
	}

	@Override
	public synchronized void setModel(Model model) throws WriterException
	{
			this.model = model;
			GraphVizBasedViewFactory imgFactory = new GraphVizBasedViewFactory(format);
			byte[] bytes = imgFactory.writeModel(model);
			this.image = new ImageIcon(bytes).getImage();
			redraw();
	}

	@Override
	public void redraw()
	{
		button.repaint();
	}
}
