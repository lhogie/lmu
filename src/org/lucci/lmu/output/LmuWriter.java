package org.lucci.lmu.output;

import org.lucci.lmu.AssociationRelation;
import org.lucci.lmu.Attribute;
import org.lucci.lmu.Entity;
import org.lucci.lmu.InheritanceRelation;
import org.lucci.lmu.Model;
import org.lucci.lmu.ModelElement;
import org.lucci.lmu.Operation;
import org.lucci.lmu.Relation;
import org.lucci.lmu.Relations;
import org.lucci.lmu.Visibility;

/*
 * Created on Oct 3, 2004
 */

/**
 * @author luc.hogie
 */
public class LmuWriter extends AbstractWriter
{
	public byte[] writeModel(Model model)
	{
		StringBuilder text = new StringBuilder();

		for (Entity entity : ModelElement.findVisibleModelElements(model.getEntities()))
		{
			if (entity.getComment() != null && !entity.getComment().trim().isEmpty())
			{
				text.append("\n# " + entity.getComment());
			}

			text.append("\nentity " + entity.getName());

			for (Attribute attribute : entity.getAttributes())
			{
				text.append("\n\tfeatures " + getLMUVisibilityFor(attribute.getVisibility()) + " attribute " + attribute.getName() + " of type " + attribute.getType().getName());
			}

			for (String stereoType : entity.getStereoTypeList())
			{
				text.append("\n\nstereotype " + stereoType);
			}

			for (Operation operation : entity.getOperations())
			{
				text.append("\n\tfeatures " + getLMUVisibilityFor(operation.getVisibility()) + " operation " + operation.getName() + " of type " + operation.getType().getName());

				if (!operation.getParameterList().isEmpty())
				{
					text.append(" expecting");

					for (Entity parm : operation.getParameterList())
					{
						text.append(" " + parm.getName());
					}
				}
			}

			for (Relation rel : Relations.findRelationsDeclaredBy(entity, model))
			{
				if (rel instanceof InheritanceRelation)
				{
					InheritanceRelation irel = (InheritanceRelation) rel;
					text.append("\n\textends " + irel.getSuperEntity().getName());
				}
				else if (rel instanceof AssociationRelation)
				{
					AssociationRelation arel = (AssociationRelation) rel;
					text.append("\n\thas " + arel.getCardinality() + " " + arel.getContainedEntity().getName() + " by ");

					if (arel.getType() == AssociationRelation.TYPE.AGGREGATION)
					{
						text.append("aggregation");
					}
					else if (arel.getType() == AssociationRelation.TYPE.COMPOSITION)
					{
						text.append("composition");
					}
					else if (arel.getType() == AssociationRelation.TYPE.ASSOCIATION)
					{
						text.append("association");
					}
					else
					{
						throw new IllegalStateException();
					}

					if (arel.getLabel() != null)
					{
						text.append(" " + arel.getLabel());
					}

				}
			}

			text.append("\n");
		}

		return text.toString().getBytes();
	}

	private String getLMUVisibilityFor(Visibility visibility)
	{
		if (visibility == Visibility.PRIVATE)
		{
			return "private";
		}
		else if (visibility == Visibility.PROTECTED)
		{
			return "protected";
		}
		else if (visibility == Visibility.PUBLIC)
		{
			return "public";
		}
		else
		{
			throw new IllegalArgumentException("unknown visibilty " + visibility);
		}
	}
}
