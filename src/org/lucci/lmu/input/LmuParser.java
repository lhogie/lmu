package org.lucci.lmu.input;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lucci.lmu.AssociationRelation;
import org.lucci.lmu.Attribute;
import org.lucci.lmu.Entities;
import org.lucci.lmu.Entity;
import org.lucci.lmu.Group;
import org.lucci.lmu.InheritanceRelation;
import org.lucci.lmu.Model;
import org.lucci.lmu.ModelElement;
import org.lucci.lmu.Models;
import org.lucci.lmu.Operation;
import org.lucci.lmu.TypedNamedModelElement;
import org.lucci.lmu.Visibility;

import toools.collections.Collections;
import toools.io.file.RegularFile;
import toools.text.TextUtilities;

public class LmuParser extends ModelFactory
{
	private final static LmuParser parser = new LmuParser();

	public static LmuParser getParser()
	{
		return parser;
	}

	private LmuParser()
	{
	}

	private final Map<RegularFile, Model> modelCache = new HashMap<RegularFile, Model>();
	private int lineNumber;
	private Model model;
	private Entity currentEntity = null;
	private String comment = "";

	@Override
	public Model createModel(byte[] data, AnalyzerListener al) throws ParseError,
			ModelException
	{
		return createModel(new String(data), al);
	}

	public Model createModel(String text, AnalyzerListener al) throws ParseError,
			ModelException
	{
		return createModel(Arrays.asList(text.split("\n")), al);
	}

	private Model createModel(List<String> lines, AnalyzerListener al) throws ParseError, ModelException
	{
		List<List<String>> tokens = new ArrayList<List<String>>();

		for (String thisLine : lines)
		{
			thisLine = thisLine.trim();

			if (thisLine.isEmpty())
			{
				tokens.add(new ArrayList<String>());
			}
			else
			{
				tokens.add(Arrays.asList(thisLine.split(" +")));
			}
		}

		return createMode(tokens, al);
	}

	public void _group(List<String> line) throws ParseError
	{
		if (TextUtilities.concatene(line, " ").equals("group by package"))
		{
			model.getGroups().addAll(Group.groupByPackage(model.getEntities()));
		}
		else if (line.size() > 3)
		{
			Group group = new Group();
			group.setLabel(line.get(1));
			group.setColorName(line.get(2));
			group.setCluster(line.get(3).equals("box"));

			for (int i = 4; i < line.size(); ++i)
			{
				String regexp = line.get(i);
				Set<Entity> entity = Entities.findEntitiesWhoseNameMatch(
						model.getEntities(), regexp);

				if (entity.isEmpty())
					syntax("entity is not found " + regexp);

				group.addAll(entity);
			}

			model.getGroups().add(group);
		}
		else
		{
			syntax("group title color box|nobox [entity1] [entity2] ... [entityN]");
		}
	}

	public void _stereotype(List<String> line) throws ParseError
	{
		if (line.size() > 1)
		{
			for (int i = 1; i < line.size(); ++i)
			{
				this.currentEntity.getStereoTypeList().add(line.get(i));
			}
		}
		else
		{
			syntax("stereotype [entity1] [entity2] ... [entityN]");
		}
	}

	public void _sameY(List<String> line) throws ParseError
	{
		if (line.size() > 1)
		{
			Collection<Entity> align = new HashSet<Entity>();

			for (int i = 1; i < line.size(); ++i)
			{
				String regexp = line.get(i);
				Set<Entity> entities = Entities.findEntitiesWhoseNameMatch(
						model.getEntities(), regexp);
				if (entities.isEmpty())
					syntax("entity is not found " + entities);

				align.addAll(entities);
			}

			model.getAlignments().add(align);
		}
		else
		{
			syntax("sameY [entity1] [entity2] ... [entityN]");
		}
	}

	public void _describe_namespace(List<String> line) throws ParseError
	{
		if (line.size() > 1)
		{
			Models.describeNamespace(model, line.get(1));
		}
		else
		{
			syntax("describe_namespace [namespace]");
		}
	}

	public void _remove_classes_in_namespace(List<String> line) throws ParseError
	{
		if (line.size() > 1)
		{
			for (int i = 1; i < line.size(); ++i)
			{
				for (Entity e : new ArrayList<Entity>(model.getEntities()))
				{
					if (e.getNamespace().matches(line.get(i)))
					{
						model.removeEntity(e);
					}
				}
			}
		}
		else
		{
			syntax(line.get(0) + " [namespace]");
		}
	}

	public void _namespace(List<String> line) throws ParseError
	{
		if (line.size() == 2)
		{
			currentEntity.setNamespace(line.get(1));
		}
		else
		{
			syntax("namespace name");
		}
	}

	private Model createMode(List<List<String>> tokens, AnalyzerListener al) throws ParseError, ModelException
	{
		this.model = new Model();
		createEntities(model, tokens);
		comment = "";
		currentEntity = null;

		for (lineNumber = 1; lineNumber <= tokens.size(); ++lineNumber)
		{
			List<String> line = tokens.get(lineNumber - 1);

			if (line.size() == 0)
			{
				// a blank line means that the current entity declaration ends
				// here
				currentEntity = null;
			}
			else if (line.size() > 0)
			{
				String statement = line.get(0);

				if (statement.startsWith("#"))
				{
					comment += statement.substring(1).trim();

					for (int word = 1; word < line.size(); ++word)
					{
						comment += " " + line.get(word);
					}
				}
				else
				{
					try
					{
						getClass().getMethod('_' + statement, List.class).invoke(this,
								line);
					}
					catch (IllegalArgumentException e)
					{
						e.printStackTrace();
					}
					catch (IllegalAccessException e)
					{
						e.printStackTrace();
					}
					catch (InvocationTargetException e)
					{
						if (e.getCause() instanceof ParseError)
						{
							throw (ParseError) e.getCause();
						}
						else if (e.getCause() instanceof ModelException)
						{
							throw (ModelException) e.getCause();
						}
						else
						{
							e.printStackTrace();
						}
					}
					catch (SecurityException e1)
					{
						e1.printStackTrace();
					}
					catch (NoSuchMethodException e1)
					{
						// show packages in .*
						if (statement.equalsIgnoreCase("show")
								|| statement.equalsIgnoreCase("hide"))
						{
							if (line.size() == 4)
							{
								boolean visible = statement.equalsIgnoreCase("show");
								String target = line.get(1);
								Set<Entity> entities = Entities
										.findEntitiesWhoseNameMatch(model.getEntities(),
												line.get(3));

								if (line.get(2).equals("in"))
								{
									if (target.equals("packages"))
									{
										try
										{
											Entities.removeJavaPackageNames(entities,
													model);
										}
										catch (ModelException e)
										{
											e.setLine(lineNumber);
											throw e;
										}
									}
									else if (target.equals("attributes"))
									{
										for (Entity entity : entities)
										{
											for (ModelElement modelElement : entity
													.getAttributes())
											{
												modelElement.setVisible(visible);
											}

										}
									}
									else if (target.equals("operations"))
									{
										for (Entity entity : entities)
										{
											for (ModelElement modelElement : entity
													.getOperations())
											{
												modelElement.setVisible(visible);
											}
										}
									}
									else
									{
										syntax("target '"
												+ target
												+ "' is unknown. Only 'attributes' 'operations' and 'packages' are allowed");
									}
								}
								else
								{
									syntax("show and hides command attributes|operations|packages regexp");

								}
							}
							else
							{
								syntax("Syntax: hide and show commands require 2 parameters: attributes|operations|packages in regexp");

							}
						}
						else if (TextUtilities.concatene(line, " ").equals(
								"remove_entities"))
						{
							if (line.size() < 2)
							{
								syntax("remove_entities [e]");
							}
							else
							{
								String regexp = line.get(1);
								Collection<Entity> entities = Entities
										.findEntitiesWhoseNameMatch(model.getEntities(),
												regexp);
								Entities.removeEntities(entities, model);
							}
						}
						else if (statement
								.equalsIgnoreCase("retain_entities_connected_to_namespace"))
						{
							if (line.size() < 2)
							{
								syntax("retain_entities_connected_to_namespace ns [distance]");
							}
							else
							{
								try
								{
									String regexp = line.get(1);
									int distance = line.size() > 2 ? Integer.valueOf(line
											.get(2)) : Integer.MAX_VALUE;
									Set<Entity> srcEntities = Entities
											.findEntityWhoseNameSpaceMatches(
													model.getEntities(), regexp);

									if (srcEntities.isEmpty())
									{
										syntax("no entities found!");
									}
									else
									{
										Set<Entity> entitiesToKeep = Entities
												.findEntitiesConnectedTo(srcEntities,
														distance, model);
										Set<Entity> entitiesToRemove = (Set<Entity>) Collections
												.difference(model.getEntities(),
														entitiesToKeep);
										Entities.removeEntities(entitiesToRemove, model);
									}
								}
								catch (NumberFormatException e)
								{
									syntax("'retain_entities_connected_to' entity [distance]");
								}
							}
						}
						else if (statement.equalsIgnoreCase("remove_package_names"))
						{
							if (line.size() != 1)
							{
								syntax("no argument allowed");
							}
							else
							{
								Entities.removeJavaPackageNames(model.getEntities(),
										model);
							}
						}
						else if (statement.equalsIgnoreCase("remove_isolated_entities"))
						{
							if (line.size() != 1)
							{
								syntax("no argument allowed");
							}
							else
							{
								Collection<Entity> isolatedEntities = Entities
										.findIsolatedEntities(model.getEntities(), model);
								Entities.removeEntities(isolatedEntities, model);
							}
						}
						else if (statement.equalsIgnoreCase("retain_isolated_entities"))
						{
							if (line.size() != 1)
							{
								syntax("no argument allowed");
							}
							else
							{
								Entities.retainEntities(Entities.findIsolatedEntities(
										model.getEntities(), model), model);
							}
						}
						else if (statement
								.equalsIgnoreCase("retain_largest_connected_component"))
						{
							if (line.size() != 1)
							{
								syntax("no argument allowed");
							}
							else
							{
								Set<Entity> s = Entities.findLargestConnectedComponent(
										model.getEntities(), model);
								Entities.retainEntities(s, model);
							}
						}
						else if (statement
								.equalsIgnoreCase("retain_entities_connected_to"))
						{
							if (line.size() != 2 && line.size() != 3)
							{
								syntax("entity [distance]");
							}
							else
							{
								try
								{
									String regexp = line.get(1);
									int distance = line.size() > 2 ? Integer.valueOf(line
											.get(2)) : Integer.MAX_VALUE;
									Set<Entity> srcEntities = Entities
											.findEntitiesWhoseNameMatch(
													model.getEntities(), regexp);

									if (srcEntities.isEmpty())
									{
										syntax("no entities found!");
									}
									else
									{
										Set<Entity> entitiesToKeep = Entities
												.findEntitiesConnectedTo(srcEntities,
														distance, model);
										Set<Entity> entitiesToRemove = (Set<Entity>) Collections
												.difference(model.getEntities(),
														entitiesToKeep);
										Entities.removeEntities(entitiesToRemove, model);
									}
								}
								catch (NumberFormatException e)
								{
									syntax("'retain_entities_connected_to' entity [distance]");
								}
							}
						}
						else if (statement.equalsIgnoreCase("hide_non_public_elements"))
						{
							if (line.size() != 1)
							{
								syntax("no argument allowed");
							}
							else
							{
								for (ModelElement me : Models
										.findAllModelElementsInModel(model))
								{
									if (me.getVisibility() != Visibility.PUBLIC)
									{
										me.setVisible(false);
									}
								}
							}
						}
						else if (statement.equalsIgnoreCase("entity"))
						{
							if (line.size() != 2)
							{
								syntax("'entity' name");
							}
							else
							{
								String entityName = line.get(1);
								currentEntity = Entities.findEntityByName(model,
										entityName);
								currentEntity.setComment(comment);
								comment = "";
							}
						}
						else if (statement.equalsIgnoreCase("color"))
						{
							if (currentEntity == null)
							{
								syntax("the <b>'"
										+ statement
										+ "'</b> statement doesn't belong to any entity declaration");
							}
							else if (line.size() != 2)
							{
								syntax("'color' color_name");
							}
							else
							{
								currentEntity.setColorName(line.get(1));
							}
						}
						else if (statement.equalsIgnoreCase("extends"))
						{
							if (currentEntity == null)
							{
								syntax("the <b>'"
										+ statement
										+ "'</b> statement doesn't belong to any entity declaration");
							}
							else if (line.size() < 2)
							{
								throw new ParseError(
										lineNumber,
										"the <b>'extends'</b> keyword must be followed by a whitespace separated list of entity names",
										"extends <i>entity_name</i>");
							}
							else
							{
								for (int i = 1; i < line.size(); ++i)
								{
									String superEntityName = line.get(i);
									Entity superEntity = Entities.findEntityByName(model,
											superEntityName);

									if (superEntity == null)
									{
										syntax("undeclared entity: <b>" + superEntityName
												+ "</b>");
									}
									else
									{
										InheritanceRelation rel = new InheritanceRelation(
												currentEntity, superEntity);
										model.addRelation(rel);
									}
								}
							}
						}
						else
						{
							syntax("don't know what to do with statement " + statement);
						}
					}
				}
			}
		}

		return model;
	}

	public void _has(List<String> line) throws ParseError, ModelException
	{

		if (currentEntity == null)
		{
			syntax("has statement doesn't belong to any entity declaration");
		}
		else if (line.size() < 5)
		{
			syntax("the <b>'has'</b> statement should consist of at least 5 elements",
					"has <i>cardinality entityName</i> by <i>aggregation|composition|association [relationName]</i>");
		}
		else
		{
			String entityName = line.get(2);
			Entity containedEntity = Entities.findEntityByName(model, entityName);

			if (containedEntity == null)
			{
				syntax("undeclared entity: <b>" + entityName + "</b>");
			}

			AssociationRelation rel = new AssociationRelation(containedEntity,
					currentEntity);
			rel.setCardinality(line.get(1));
			rel.setContainerEntity(currentEntity);
			rel.setContainedEntity(containedEntity);

			if ( ! ((String) line.get(3)).equalsIgnoreCase("by"))
			{
				syntax("the <b>'by'</b> keyword was expected but <b>'" + line.get(1)
						+ "'</b> was found");
			}

			String relationType = line.get(4);

			if (relationType.equals("aggregation"))
			{
				rel.setType(AssociationRelation.TYPE.AGGREGATION);
			}
			else if (relationType.equals("association"))
			{
				rel.setType(AssociationRelation.TYPE.ASSOCIATION);
			}
			else if (relationType.equals("composition"))
			{
				rel.setType(AssociationRelation.TYPE.COMPOSITION);
			}
			else if (relationType.equals("direction"))
			{
				rel.setType(AssociationRelation.TYPE.DIRECTION);
			}
			else
			{
				throw new ParseError(
						lineNumber,
						"the <b>'"
								+ relationType
								+ "'</b> relation type is unknown ; supported types are <b>'association'</b>, <b>'aggregation'</b> or <b>'composition'</b>");
			}

			if (line.size() > 5)
			{
				rel.setLabel("");

				for (int i = 5; i < line.size(); ++i)
				{
					rel.setLabel(rel.getLabel() + " " + line.get(i));
				}
			}

			model.addRelation(rel);
		}

	}

	public void _features(List<String> line) throws ParseError, ModelException
	{
		if (currentEntity == null)
		{
			syntax("feature statement doesn't belong to any entity declaration");
		}
		else if (line.size() < 7)
		{
			throw new ParseError(
					lineNumber,
					"the <b>'features'</b> statement should consist of 7 elements",
					"features public|protected|private attribute|operation <i>name</i> of type <i>entityName</i> [expecting <i>entityName1 entityName2...</i>]'");
		}
		else
		{
			TypedNamedModelElement entityElement = null;
			String entityElementType = line.get(2);

			if (entityElementType.equalsIgnoreCase("attribute"))
			{
				entityElement = new Attribute();
			}
			else if (entityElementType.equalsIgnoreCase("operation"))
			{
				entityElement = new Operation();
			}
			else
			{
				syntax("3rd element of the <b>'features'</b> statement must either <b>'attribute'</b> or <b>'operation'</b> ; <b>'"
						+ entityElementType + "'</b> is unknown");
			}

			String visibilityValue = line.get(1);

			if (visibilityValue.equalsIgnoreCase("public"))
				entityElement.setVisibility(Visibility.PUBLIC);
			else if (visibilityValue.equalsIgnoreCase("private"))
				entityElement.setVisibility(Visibility.PRIVATE);
			else if (visibilityValue.equalsIgnoreCase("protected"))
				entityElement.setVisibility(Visibility.PROTECTED);
			else
				throw new ParseError(
						lineNumber,
						"<b>'"
								+ visibilityValue
								+ "'</b> visibility is unknown ; supported visibilities are <b>'public'</b> or <b>'protected'</b> or <b>'private'</b>");

			String elementName = line.get(3);
			entityElement.setName(elementName);

			if ( ! line.get(4).equalsIgnoreCase("of")
					|| ! line.get(5).equalsIgnoreCase("type"))
				syntax("3rd and 4th elements of the <b>'features'</b> statement must be <b>'of type'</b>");

			String elementTypeName = line.get(6);
			Entity elementType = Entities.findEntityByName(model, elementTypeName);

			if (elementType == null)
			{
				syntax("undeclared entity: <b>" + elementTypeName + "</b>");
			}
			else
			{
				entityElement.setType(elementType);
			}

			if (entityElement instanceof Attribute)
			{
				if (line.size() != 7)
				{
					syntax("attribute declaration should contain exactly 7 elements");
				}
				else
				{
					currentEntity.getAttributes().add((Attribute) entityElement);
				}
			}
			else if (entityElement instanceof Operation)
			{
				Operation operation = (Operation) entityElement;

				if (line.size() > 7)
				{
					if ( ! line.get(7).equalsIgnoreCase("expecting"))
						syntax("the <b>'expecting'</b> keyword was expected but <b>'"
								+ line.get(7) + "'</b> was found");

					for (int i = 8; i < line.size(); ++i)
					{
						String argumentTypeName = line.get(i);
						Entity argumentType = Entities.findEntityByName(model,
								argumentTypeName);

						if (argumentType == null)
						{
							syntax("undeclared entity: <b>" + argumentTypeName + "</b>");
						}
						else
						{
							operation.getParameterList().add(argumentType);
						}
					}
				}

				currentEntity.getOperations().add(operation);
			}
		}
	}

	public void _load(List<String> line, AnalyzerListener al) throws ParseError, ModelException
	{
		if (line.size() < 2)
		{
			syntax("'load' [file1]");
		}
		else
		{
			for (int i = 1; i < line.size(); ++i)
			{
				RegularFile file = new RegularFile(line.get(i));

				if (file.exists())
				{
					String fileExtension = file.getExtension();

					if (fileExtension == null)
					{
						syntax("File name has not extension, which is required to guess its content type: "
								+ file.getPath());
					}
					else
					{
						ModelFactory modelFactory = ModelFactory
								.getModelFactory(fileExtension.toLowerCase());

						if (modelFactory == null)
						{
							syntax(file.getPath()
									+ ": dunno what to do with files extension "
									+ fileExtension);
						}
						else
						{
							Model newModel = this.modelCache.get(file.getPath());

							if (newModel == null)
							{
								try
								{
									newModel = modelFactory
											.createModel(file.getContent(), al);
									model.merge(newModel);
									modelCache.put(file, newModel);
								}
								catch (IOException ex)
								{
									syntax("I/O error while reading file "
											+ file.getPath());
								}
							}
						}
					}
				}
				else
				{
					syntax("File does not exist: " + file.getPath());
				}
			}
		}
	}

	private void syntax(String s) throws ParseError
	{
		throw new ParseError(lineNumber, s);
	}

	private void syntax(String s, String suggestion) throws ParseError
	{
		throw new ParseError(lineNumber, s, suggestion);
	}

	private void createEntities(Model model, List<List<String>> lines) throws ParseError
	{
		// first instantiate all explicitely declared entities
		// for this, only "entity" lines are considered
		for (int lineNumber = 1; lineNumber <= lines.size(); ++lineNumber)
		{
			List<String> line = lines.get(lineNumber - 1);

			if (line.size() > 0)
			{
				String statement = line.get(0);

				if (statement.equalsIgnoreCase("entity"))
				{
					if (line.size() == 2)
					{
						String name = line.get(1);

						if (Entities.findEntityByName(model, name) == null)
						{
							Entity entity = new Entity();
							entity.setName(name);
							model.addEntity(entity);
						}
						else
						{
							syntax("entity already declared: <b>" + name + "</b>");
						}
					}
					else
					{
						syntax("the '<b>entity</b>' keyword must be followed by the name of the entity",
								"entity <i>name</i>");
					}
				}
			}
		}
	}

}
