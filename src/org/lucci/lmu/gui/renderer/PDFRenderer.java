package org.lucci.lmu.gui.renderer;

import java.awt.GridLayout;
import java.nio.ByteBuffer;

import org.lucci.lmu.Model;
import org.lucci.lmu.gui.ClassDiagramViewer;
import org.lucci.lmu.output.GraphVizBasedViewFactory;
import org.lucci.lmu.output.WriterException;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

/*
 * Created on Oct 4, 2004
 */

/**
 * @author luc.hogie
 */
public class PDFRenderer extends ClassDiagramViewer
{
	PagePanel panel = new PagePanel();

	public PDFRenderer()
	{
		setLayout(new GridLayout(1, 1));
		add(panel);
	}

	@Override
	public String getFriendlyName()
	{
		return "PDF renderer";
	}

	@Override
	public synchronized void setModel(Model model) throws WriterException
	{
		GraphVizBasedViewFactory imgFactory = new GraphVizBasedViewFactory("pdf");
		byte[] bytes = imgFactory.writeModel(model);
		ByteBuffer buf = ByteBuffer.wrap(bytes);

		try
		{
			PDFFile pdffile = new PDFFile(buf);
			PDFPage page = pdffile.getPage(0);
			panel.showPage(page);
			panel.repaint();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void redraw()
	{
		panel.repaint();
	}
}
