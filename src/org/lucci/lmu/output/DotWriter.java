package org.lucci.lmu.output;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.lucci.lmu.AssociationRelation;
import org.lucci.lmu.Attribute;
import org.lucci.lmu.Entity;
import org.lucci.lmu.Group;
import org.lucci.lmu.InheritanceRelation;
import org.lucci.lmu.Model;
import org.lucci.lmu.ModelElement;
import org.lucci.lmu.Operation;
import org.lucci.lmu.Relation;
import org.lucci.lmu.Visibility;

/*
 * Created on Oct 2, 2004
 */

/**
 * @author luc.hogie
 */
public class DotWriter extends AbstractWriter
{
    private String fontName = "Times";

    @Override
    public byte[] writeModel(Model model) throws WriterException
    {
	StringBuffer buf = new StringBuffer();
	buf.append("digraph ClassDiagram\n{");
	buf.append("\n\tgraph [rankdir=TD,ranksep=0.75];\n\tedge [fontname=\"" + fontName
		+ "\", fontsize=10,labelfontname=\"" + fontName + "\", labelfontsize=10];\n\tnode [fontname=\""
		+ fontName + "\", fontsize=10];\n");

	buf.append("\n");

	for (Collection<Entity> align : model.getAlignments())
	{
	    buf.append("\t{rank=same");

	    for (Entity e : align)
	    {
		buf.append(";\"" + e.getName().hashCode() + "\"");
	    }

	    buf.append("}\n");
	}

	Collection<Entity> visibleEntities = (Collection<Entity>) ModelElement.findVisibleModelElements(model
		.getEntities());

	for (Entity entity : visibleEntities)
	{
	    Collection<Attribute> visibleAttributes = (List<Attribute>) ModelElement.findVisibleModelElements(entity
		    .getAttributes());
	    Collection<Operation> visibleOperations = (List<Operation>) ModelElement.findVisibleModelElements(entity
		    .getOperations());
	    boolean isRecord = true;// visibleAttributes.size() +
				    // visibleOperations.size() > 0;

	    buf.append("\n\t");

	    buf.append(quoteNodeNameIfNecessary(String.valueOf(entity.getName().hashCode())));
	    buf.append(" [");
	    buf.append("shape=\"" + (isRecord ? "record" : "box") + "\"");

	    if (entity.getColorName() != null)
	    {
		buf.append(", fillcolor=" + entity.getColorName());
		buf.append(", style=filled");
	    }

	    buf.append(", fontcolor=black");
	    buf.append(", fontsize=10.0");

	    if (isRecord)
	    {
		buf.append(", label=\"" + "{");

		for (String st : entity.getStereoTypeList())
		{
		    buf.append("&lt;&lt;" + st + "&gt;&gt;\\n");
		}
		
		if (!entity.getStereoTypeList().isEmpty())
		{
		    buf.append("\\n");
		}

		buf.append(entity.getName());

		if (!visibleAttributes.isEmpty())
		{
		    buf.append("|");

		    for (Attribute attribute : visibleAttributes)
		    {
			if (attribute.getVisibility() != null)
			{
			    buf.append(getUMLVisibility(attribute.getVisibility()) + " ");
			}

			buf.append(attribute.getName());

			if (attribute.getType() != null)
			{
			    buf.append(" : " + escapeStringIfNecessary(attribute.getType().getName()));
			}

			buf.append("\\l");
		    }
		}

		if (!visibleOperations.isEmpty())
		{
		    buf.append("|");

		    for (Operation operation : visibleOperations)
		    {
			if (operation.isVisible())
			{
			    if (operation.getVisibility() != null)
			    {
				buf.append(getUMLVisibility(operation.getVisibility()) + " ");
			    }

			    buf.append(operation.getName() + "(");
			    Iterator<Entity> parameterIterator = operation.getParameterList().iterator();

			    while (parameterIterator.hasNext())
			    {
				Entity parameterType = parameterIterator.next();
				buf.append(escapeStringIfNecessary(parameterType.getName()));

				if (parameterIterator.hasNext())
				{
				    buf.append(", ");
				}
			    }

			    buf.append(")");

			    if (operation.getType() != null)
			    {
				buf.append(" : " + escapeStringIfNecessary(operation.getType().getName()));
			    }

			    buf.append("\\l");
			}
		    }
		}

		buf.append((isRecord ? "}" : "") + "\"];");
	    }
	    else
	    {
		buf.append(", label=\"" + (entity.isAbsract() ? "&lt;&lt;abstract&gt;&gt;\\n" : "") + entity.getName());

		buf.append("\"];");
	    }

	}

	for (Relation relation : model.getRelations())
	{
	    // c0 -> c1 [taillabel="1", label="come from", headlabel="1",
	    // fontname="Helvetica", fontcolor="black", fontsize=10.0,
	    // color="black", , arrowtail=ediamond];
	    // System.out.println(relation);

	    if (relation.getTailEntity().isVisible() && relation.getHeadEntity().isVisible())
	    {
		if (relation instanceof AssociationRelation)
		{
		    AssociationRelation assoc = (AssociationRelation) relation;
		    buf.append("\n\t");
		    buf.append(quoteNodeNameIfNecessary(String.valueOf(assoc.getContainedEntity().getName().hashCode())));
		    buf.append(" -> ");
		    buf.append(quoteNodeNameIfNecessary(String.valueOf(assoc.getContainerEntity().getName().hashCode())));

		    if (assoc.getType() == AssociationRelation.TYPE.ASSOCIATION)
		    {
			buf.append(" [arrowhead=none");
		    }
		    else if (assoc.getType() == AssociationRelation.TYPE.AGGREGATION)
		    {
			buf.append(" [arrowhead=odiamond");
		    }
		    else if (assoc.getType() == AssociationRelation.TYPE.COMPOSITION)
		    {
			buf.append(" [arrowhead=diamond");
		    }
		    else if (assoc.getType() == AssociationRelation.TYPE.DIRECTION)
		    {
			buf.append(" [arrowtail=vee");
		    }
		    else
		    {
			throw new IllegalStateException("unknow relation type");
		    }

		    if (assoc.getCardinality() != null && !assoc.getCardinality().equals("1"))
		    {
			buf.append(", taillabel=\"" + assoc.getCardinality() + "\"");
		    }

		    if (assoc.getLabel() != null)
		    {
			buf.append(", label=\"" + assoc.getLabel() + "\"");
		    }

		    buf.append("];");
		}
		else
		{
		    InheritanceRelation heritage = (InheritanceRelation) relation;
		    buf.append("\n\t");
		    buf.append(quoteNodeNameIfNecessary(String.valueOf(heritage.getSubEntity().getName().hashCode())));
		    buf.append(" -> ");
		    buf.append(quoteNodeNameIfNecessary(String.valueOf(heritage.getSuperEntity().getName().hashCode())));
		    buf.append(" [arrowhead=onormal");

		    if (heritage.getSuperEntity().isInterface())
		    {
			buf.append(",style=dashed");
		    }

		    buf.append("];");
		}
	    }
	}

	int gid = 0;

	for (Group group : model.getGroups())
	{
	    buf.append("\n\n\tsubgraph cluster_" + gid++ + " {");
	    buf.append("\n\t\tcolor = " + group.getColorName() + ";");
	    buf.append("\n\t\tlabel = \"" + group.getLabel() + "\";");

	    for (Entity e : group)
	    {
		buf.append("\n\t\t" + quoteNodeNameIfNecessary(String.valueOf(e.getName().hashCode())) + ";");
	    }

	    buf.append("\n\t}");
	}

	buf.append("\n}");
	String dotText = buf.toString();
	return dotText.getBytes();
    }

    private String getUMLVisibility(Visibility v)
    {
	if (v == Visibility.PUBLIC)
	{
	    return "+";
	}
	else if (v == Visibility.PROTECTED)
	{
	    return "#";
	}
	else if (v == Visibility.PRIVATE)
	{
	    return "-";
	}
	else
	{
	    throw new IllegalArgumentException("unknow visilibity " + v);
	}
    }

    private String quoteNodeNameIfNecessary(String s)
    {
	if (!s.matches("[0-9a-zA-Z]+"))
	{
	    return '"' + s + '"';
	}
	else
	{
	    return s;
	}
    }

    private String escapeStringIfNecessary(String s)
    {
	return s.replaceAll("\\.", "\\.");
    }
}
