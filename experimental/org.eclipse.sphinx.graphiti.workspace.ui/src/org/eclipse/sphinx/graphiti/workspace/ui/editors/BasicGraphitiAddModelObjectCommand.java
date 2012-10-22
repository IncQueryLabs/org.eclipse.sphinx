/**
 * <copyright>
 * 
 * Copyright (c) 2012 itemis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     itemis - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.graphiti.workspace.ui.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureAndContext;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.internal.DefaultFeatureAndContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.ui.internal.command.AbstractCommand;
import org.eclipse.graphiti.ui.internal.config.IConfigurationProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * Extended Graphiti command to manage the initial set of business objects to be transfered by drag & drop feature
 */
@SuppressWarnings("restriction")
public class BasicGraphitiAddModelObjectCommand extends AbstractCommand {

	/**
	 * Added to extendedContextList : all objects to drop, including objects selected and dragged from tree viewer to
	 * diagram editor objects contained in selected objects objects referenced and which are in the selection
	 */
	private List<AddContext> extendedContextList; // contains AddContext(s) and AddConnectionContext(s)

	/**
	 * Issues a command, each command is executed later within an EMF transaction.
	 * 
	 * @param configurationProvider
	 * @param parent
	 * @param sel
	 * @param rectangle
	 */
	public BasicGraphitiAddModelObjectCommand(IConfigurationProvider configurationProvider, ContainerShape parent, ISelection sel, Rectangle rectangle) {
		this(configurationProvider, parent, sel, rectangle, null);
	}

	/**
	 * Creates the context list.
	 * 
	 * @param configurationProvider
	 * @param parent
	 * @param selection
	 * @param rectangle
	 * @param connection
	 */
	public BasicGraphitiAddModelObjectCommand(IConfigurationProvider configurationProvider, ContainerShape parent, ISelection selection,
			Rectangle rectangle, Connection connection) {
		super(configurationProvider);

		IStructuredSelection s = (IStructuredSelection) selection;
		if (s == null) {
			s = StructuredSelection.EMPTY;
		}

		extendedContextList = new ArrayList<AddContext>();
		int x = rectangle.x;
		int y = rectangle.y;
		AddContext ctx = null;

		// Collect currently selected objects
		for (Iterator<?> iter = s.iterator(); iter.hasNext();) {
			Object next = iter.next();

			if (next instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) next;
				Object adapter = adaptable.getAdapter(EObject.class);
				if (adapter instanceof EObject) {
					next = adapter;
				}
			}
			ctx = new AddContext();
			ctx.setNewObject(next);
			ctx.setTargetContainer(parent);
			ctx.setLocation(x, y);
			ctx.setTargetConnection(connection);
			extendedContextList.add(ctx);
			x += 10;
			y += 10;
		}

		// Add contained objects
		for (Iterator<?> iter = s.iterator(); iter.hasNext();) {
			Object next = iter.next();
			// Traverse containment references
			EObject candidateObject = (EObject) next;
			EList<EReference> eAllReferences = candidateObject.eClass().getEAllReferences();
			for (EReference candidateRef : eAllReferences) {
				// Feature is reference
				if (candidateRef.isContainment()) {
					// Reference is a containment
					@SuppressWarnings("unchecked")
					EList<EObject> childs = (EList<EObject>) candidateObject.eGet(candidateRef);
					if (childs != null) {
						// add new objects
						for (EObject containedObject : childs) {
							ctx = new AddContext();
							ctx.setNewObject(containedObject);
							ctx.setTargetContainer(parent);
							ctx.setLocation(x, y);
							ctx.setTargetConnection(connection);
							ctx.putProperty("container", candidateObject); //$NON-NLS-1$ 
							extendedContextList.add(ctx);
						}
					}
				}
			}
			x += 10;
			y += 10;
		}

		// Add referenced objects that are in the selection
		for (Iterator<?> iter = s.iterator(); iter.hasNext();) {
			Object next = iter.next();
			// Traverse containment references
			EObject candidateObject = (EObject) next;
			EList<EReference> eAllReferences = candidateObject.eClass().getEAllReferences();
			for (EReference candidateRef : eAllReferences) {
				// Feature is reference
				if (!candidateRef.isContainment()) {
					@SuppressWarnings("unchecked")
					EList<EObject> referencedObjects = (EList<EObject>) candidateObject.eGet(candidateRef);
					if (referencedObjects != null) {
						// Now check references between objects already added, if a reference exist, add a
						// AddConnectionContext
						for (EObject referenced : referencedObjects) {
							if (inExtendedContextList(referenced)) {
								// Shapes are not known yet, because will be created later, so business objects are
								// stored
								ctx = new AddConnectionContext(null, null);
								ctx.putProperty(candidateRef.getName(), candidateObject);
								ctx.putProperty("sourceAnchor", candidateObject); //$NON-NLS-1$
								ctx.putProperty("targetAnchor", referenced); //$NON-NLS-1$
								ctx.setNewObject(candidateObject);
								ctx.setTargetContainer(parent);
								ctx.setLocation(x, y);
								ctx.setTargetConnection(connection);
								extendedContextList.add(ctx);
							}
						}
					}
				}
			}

			x += 10;
			y += 10;
		}
	}

	/**
	 * Checks if a referenced object is contained in the context list.
	 * 
	 * @param referenced
	 * @return
	 */
	Boolean inExtendedContextList(EObject referenced) {
		for (AddContext addContext : extendedContextList) {
			IAddContext ctx = addContext;
			EObject newObject = (EObject) ctx.getNewObject();
			if (newObject.equals(referenced)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canExecute() {
		IFeatureProvider featureProvider = getFeatureProvider();
		if (featureProvider != null && extendedContextList.size() > 0) {
			// try to find an add-feature for each object in the selection
			for (AddContext addContext : extendedContextList) {
				IAddContext ctx = addContext;
				IAddFeature f = featureProvider.getAddFeature(ctx);
				if (f == null) {
					return false;
				} else {
					boolean canAdd = f.canAdd(ctx);
					if (canAdd == true) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void execute() {
		for (AddContext addContext : extendedContextList) {
			IAddContext ctx = addContext;
			getFeatureProvider().addIfPossible(ctx);
		}
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	public IFeatureAndContext[] getFeaturesAndContexts() {
		List<IFeatureAndContext> features = new ArrayList<IFeatureAndContext>();
		IFeatureProvider featureProvider = getFeatureProvider();
		if (featureProvider != null && extendedContextList.size() > 0) {
			// try to find an add-feature for each object in the selection
			for (AddContext addContext : extendedContextList) {
				IAddContext ctx = addContext;
				IAddFeature f = featureProvider.getAddFeature(ctx);
				if (f != null && f.canAdd(ctx)) {
					DefaultFeatureAndContext dfac = new DefaultFeatureAndContext(f, ctx);
					features.add(dfac);
				}
			}
		}
		return features.toArray(new IFeatureAndContext[features.size()]);
	}

}