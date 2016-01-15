package org.lucci.lmu.gui.renderer;

import org.lucci.lmu.Entities;
import org.lucci.lmu.Entity;
import org.lucci.lmu.Model;
import org.lucci.lmu.output.AbstractWriter;
import org.lucci.lmu.output.WriterException;

public class DetailWriter extends AbstractWriter
{

	@Override
	public byte[] writeModel(Model model) throws WriterException
	{
		StringBuilder b = new StringBuilder();

		b.append("Entities\n");
		for (Entity e : model.getEntities())
		{
			b.append(e.getName()+"\n");
		}

		b.append("Isolated entities\n");
		for (Entity e : Entities.findIsolatedEntities(model.getEntities(), model))
		{
			b.append(e.getName()+"\n");
		}
		
		
		return b.toString().getBytes();
	}

}
