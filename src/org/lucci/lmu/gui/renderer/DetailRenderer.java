package org.lucci.lmu.gui.renderer;

import org.lucci.lmu.output.AbstractWriter;

/*
 * Created on Oct 4, 2004
 */

/**
 * @author luc.hogie
 */
public class DetailRenderer extends TextRenderer
{

	@Override
	public AbstractWriter getWriter()
	{
		return new DetailWriter();
	}

	@Override
	public String getFriendlyName()
	{
		return "Details";
	}

}
