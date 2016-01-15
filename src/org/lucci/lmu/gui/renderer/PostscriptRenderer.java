package org.lucci.lmu.gui.renderer;

import org.lucci.lmu.output.AbstractWriter;
import org.lucci.lmu.output.GraphVizBasedViewFactory;

/*
 * Created on Oct 4, 2004
 */

/**
 * @author luc.hogie
 */
public class PostscriptRenderer extends TextRenderer
{

	@Override
	public AbstractWriter getWriter()
	{
		return new GraphVizBasedViewFactory("ps");
	}

	@Override
	public String getFriendlyName()
	{
		return "Postscript";
	}

}
