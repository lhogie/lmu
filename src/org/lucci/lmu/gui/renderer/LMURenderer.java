package org.lucci.lmu.gui.renderer;

import org.lucci.lmu.output.AbstractWriter;
import org.lucci.lmu.output.LmuWriter;

/*
 * Created on Oct 4, 2004
 */

/**
 * @author luc.hogie
 */
public class LMURenderer extends TextRenderer
{

	@Override
	public AbstractWriter getWriter()
	{
		return new LmuWriter();
	}

	@Override
	public String getFriendlyName()
	{
		return "LMU text";
	}

}
