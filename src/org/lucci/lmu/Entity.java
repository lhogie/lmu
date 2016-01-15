package org.lucci.lmu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import toools.collections.Collections;
import toools.util.assertion.Assertions;

/*
 * Created on Oct 2, 2004
 */

/**
 * @author luc.hogie
 */
public class Entity extends NamedModelElement
{
	private final Collection<Attribute> attributesList = new ArrayList<Attribute>();
	private final Collection<Operation> operationList = new ArrayList<Operation>();
	private boolean primitive = false;
	private boolean isInterface = false;
	private boolean isAbsract = false;
	private String color = null;
	private Model model;
	private String namespace = DEFAULT_NAMESPACE;
	private final List<String> stereoTypeList = new ArrayList();

	public static final String DEFAULT_NAMESPACE = "default namespace";

	public String getNamespace()
	{
		return namespace;
	}

	public List<String> getStereoTypeList()
	{
		return stereoTypeList;
	}

	public void setNamespace(String namespace)
	{
		if (namespace == null)
			throw new NullPointerException();

		this.namespace = namespace;
	}

	public Model getModel()
	{
		return model;
	}

	@Override
	public void setName(String name)
	{
		if (getModel() != null && Entities.findEntityByName(getModel(), name) != null)
		{
			throw new IllegalArgumentException(
					"an entity with the same name already exist");
		}

		super.setName(name);
	}

	public void setModel(Model model)
	{
		this.model = model;
	}

	public String getColorName()
	{
		return color;
	}

	public Collection<Attribute> getAttributes()
	{
		return attributesList;
	}

	public Collection<Operation> getOperations()
	{
		return operationList;
	}

	@Override
	public String toString()
	{
		return getName() + "(" + getNamespace() + ")";
	}

	public boolean declareRelation(Relation rel)
	{
		if (rel instanceof InheritanceRelation)
		{
			return ((InheritanceRelation) rel).getSubEntity() == this;
		}
		else if (rel instanceof AssociationRelation)
		{
			return ((AssociationRelation) rel).getContainerEntity() == this;
		}
		else
		{
			throw new IllegalStateException();
		}
	}

	public boolean isPrimitive()
	{
		return primitive;
	}

	public void setPrimitive(boolean primitive)
	{
		this.primitive = primitive;
	}

	public void setColorName(String color)
	{
		if (color.equals("random"))
		{
			color = Collections.pickRandomObject(
					Arrays.asList(new String[] { "red", "blue", "green", "yellow" }),
					new Random());
		}

		this.color = color;
	}

	public void merge(Entity e)
	{
		Assertions.ensure(e.getName().equals(getName()),
				"entities must have the same name");

		for (Attribute a : e.getAttributes())
		{
			Attribute correspondingLocalAttribute = Attributes.findAttributeByName(this,
					e.getName());

			if (correspondingLocalAttribute == null)
			{
				getAttributes().add(a);
			}
			else
			{
				throw new IllegalArgumentException("attribute already exists");
			}
		}

		for (Operation o : e.getOperations())
		{
			Operation correspondingLocalOperation = Operations.findOperationByName(this,
					e.getName());

			if (correspondingLocalOperation == null)
			{
				getOperations().add(o);
			}
			else
			{
				throw new IllegalArgumentException("attribute already exists");
			}
		}
	}

	@Override
	public int hashCode()
	{
		return (getNamespace() + '.' + getName()).hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj.getClass() == getClass() && obj.hashCode() == hashCode();
	}

	public boolean isAbsract()
	{
		return isAbsract;
	}

	public void setAbsract(boolean isAbsract)
	{
		this.isAbsract = isAbsract;
	}

	public boolean isInterface()
	{
		// return isInterface;
		return getAttributes().isEmpty();
	}

	public void setInterface(boolean isInterface)
	{
		this.isInterface = isInterface;
	}

	public Object clone(String newName) throws CloneNotSupportedException
	{
		Entity clone = new Entity();

		for (Attribute a : getAttributes())
		{
			clone.getAttributes().add((Attribute) a.clone());
		}

		for (Operation a : getOperations())
		{
			clone.getOperations().add((Operation) a.clone());
		}

		clone.color = this.color;
		clone.isAbsract = this.isAbsract;
		clone.isInterface = this.isInterface;
		clone.namespace = this.namespace;
		clone.primitive = this.primitive;
		clone.model = this.model;
		clone.model = this.model;
		clone.setName(newName);
		return clone;
	}
}
