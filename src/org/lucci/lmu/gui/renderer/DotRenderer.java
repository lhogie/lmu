package org.lucci.lmu.gui.renderer;

import org.lucci.lmu.output.AbstractWriter;
import org.lucci.lmu.output.DotWriter;

/*
 * Created on Oct 4, 2004
 */

/**
 * @author luc.hogie
 */
public class DotRenderer extends TextRenderer
{

	@Override
	public AbstractWriter getWriter()
	{
		return new DotWriter();
	}

	@Override
	public String getFriendlyName()
	{
		return "DOT text";
	}

}
