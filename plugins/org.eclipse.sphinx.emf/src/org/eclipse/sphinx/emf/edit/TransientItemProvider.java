/**
 * <copyright>
 * 
 * Copyright (c) 2008-2010 See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandWrapper;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.DelegatingWrapperItemProvider;
import org.eclipse.emf.edit.provider.Disposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.sphinx.emf.Activator;

/**
 * This class provides transient item provider adapter that allows introducing non-model view objects between an object
 * and its children.
 */
public class TransientItemProvider extends ExtendedItemProviderAdapter implements IEditingDomainItemProvider, IStructuredItemContentProvider,
		ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource {

	/**
	 * A constructor allowing to create transient item providers. Transient item providers are not created in the usual
	 * way (i.e., by calling {@link org.eclipse.emf.common.notify.impl.AdapterFactoryImpl#adapt(Notifier, Object))} for
	 * the object that content non-model elements), their constructors explicitly add them to the eAdapters.
	 * 
	 * @param adapterFactory
	 *            an adapter factory.
	 * @param parent
	 *            the parent of the element.
	 */
	public TransientItemProvider(AdapterFactory adapterFactory, Notifier parent) {
		super(adapterFactory);
		parent.eAdapters().add(this);
	}

	/**
	 * Returns whether this item provider may need to use wrappers for some or all of the values it returns as
	 * {@link #getChildren children}. This is used to determine whether to use a store to keep track of children and
	 * whether to use command wrappers that re-wrap results and affected objects. The default implementation of
	 * {@link #createWrapper createWrapper} also tests this method and will not create any wrappers if it returns
	 * <code>false</code>.
	 * <p>
	 * This implementation consults {@link #getChildrenFeatures getChildrenFeatures}, returning true if any feature map
	 * or simple attributes contribute children.
	 */
	@Override
	protected boolean isWrappingNeeded(Object object) {
		if (wrappingNeeded == null) {
			wrappingNeeded = Boolean.FALSE;

			for (EStructuralFeature feature : getChildrenFeatures(object)) {
				if (feature instanceof EAttribute || feature instanceof EReference && !((EReference) feature).isContainment()) {
					wrappingNeeded = Boolean.TRUE;
					break;
				}
			}
		}
		return wrappingNeeded;
	}

	/**
	 * Updates any cached children based on the given notification. If a {@link ChildrenStore} exists for the given
	 * transient item provider, then the children of the specified feature are updated.
	 * 
	 * @param notification
	 *            a description of a feature change that has occurred for some notifier.
	 * @param transientItemProvider
	 *            a transient item provider for which cached children were updated
	 */
	protected void updateTransientItemProviderChildren(Notification notification, TransientItemProvider transientItemProvider) {
		EObject object = (EObject) notification.getNotifier();
		ChildrenStore childrenStore = getChildrenStore(transientItemProvider);

		if (childrenStore != null) {
			EStructuralFeature feature = (EStructuralFeature) notification.getFeature();
			EList<Object> children = childrenStore.getList(feature);
			if (children != null) {
				int index = notification.getPosition();

				switch (notification.getEventType()) {
				case Notification.UNSET: {
					// Ignore the unset notification for an isMany feature; the value is boolean in this case.
					//
					if (feature.isMany()) {
						break;
					}

					// continue to next case
				}
				case Notification.SET: {
					Object oldChild = childrenStore.get(feature, index);
					Object newValue = notification.getNewValue();

					if (unwrap(oldChild) != newValue) {
						if (feature.isMany() && index == Notification.NO_INDEX) {
							disposeWrappers((List<?>) oldChild);
						} else {
							disposeWrapper(oldChild);
						}
						Object newChild = newValue == null && index == Notification.NO_INDEX ? null : wrap(transientItemProvider, feature, newValue,
								index);
						childrenStore.set(feature, index, newChild);
					}
					break;
				}
				case Notification.ADD: {
					EList<?> values = (EList<?>) object.eGet(feature);

					if (children.size() != values.size()) {
						Object newValue = notification.getNewValue();
						adjustWrapperIndices(children, index, 1);
						children.add(index, wrap(transientItemProvider, feature, newValue, index));
					}
					break;
				}
				case Notification.REMOVE: {
					EList<?> values = (EList<?>) object.eGet(feature);

					if (children.size() != values.size()) {
						disposeWrapper(children.remove(index));
						adjustWrapperIndices(children, index, -1);
					}
					break;
				}
				case Notification.ADD_MANY: {
					EList<?> values = (EList<?>) object.eGet(feature);

					if (children.size() != values.size()) {
						if (notification.getOldValue() != null) {
							throw new IllegalArgumentException("No old value expected"); //$NON-NLS-1$
						}
						List<?> newValues = (List<?>) notification.getNewValue();
						List<Object> newChildren = new ArrayList<Object>(newValues.size());
						int offset = 0;
						for (Object newValue : newValues) {
							newChildren.add(wrap(transientItemProvider, feature, newValue, index + offset++));
						}
						adjustWrapperIndices(children, index, offset);
						children.addAll(index, newChildren);
					}
					break;
				}
				case Notification.REMOVE_MANY: {
					// No index specified when removing all elements.
					//
					if (index == Notification.NO_INDEX) {
						index = 0;
					}
					EList<?> values = (EList<?>) object.eGet(feature);

					if (children.size() != values.size()) {
						if (notification.getNewValue() instanceof int[]) {
							int[] indices = (int[]) notification.getNewValue();
							for (int i = indices.length - 1; i >= 0; i--) {
								disposeWrapper(children.remove(indices[i]));
								adjustWrapperIndices(children, indices[i], -1);
							}
						} else {
							int len = ((List<?>) notification.getOldValue()).size();
							List<?> sl = children.subList(index, index + len);
							disposeWrappers(sl);
							sl.clear();
							adjustWrapperIndices(children, index, -len);
						}
					}
					break;
				}
				case Notification.MOVE: {
					int oldIndex = (Integer) notification.getOldValue();
					EList<?> values = (EList<?>) object.eGet(feature);
					boolean didMove = true;

					for (int i = Math.min(oldIndex, index), end = Math.max(oldIndex, index); didMove && i <= end; i++) {
						didMove = unwrap(children.get(i)) == values.get(i);
					}

					if (!didMove) {
						int delta = index - oldIndex;
						if (delta < 0) {
							adjustWrapperIndices(children, index, oldIndex, 1);
						}
						children.move(index, oldIndex);
						adjustWrapperIndex(children.get(index), delta);
						if (delta > 0) {
							adjustWrapperIndices(children, oldIndex, index, -1);
						}
					}
					break;
				}
				}
			}
		}
	}

	/**
	 * This returns the children of its parent i.e. a transient item provider. This implementation use
	 * <code>target</code> for obtaining children of the corresponding features. The target instance variable comes from
	 * the adapter base class {@link org.eclipse.emf.common.notify.impl.AdapterImpl}.
	 */
	@Override
	public Collection<?> getChildren(Object object) {
		ChildrenStore store = getChildrenStore(object);
		if (store != null) {
			return store.getChildren();
		}

		store = createChildrenStore(object);
		List<Object> result = store != null ? null : new ArrayList<Object>();
		EObject eObject = (EObject) target;

		for (EStructuralFeature feature : getChildrenFeatures(object)) {
			if (feature.isMany()) {
				List<?> children = (List<?>) eObject.eGet(feature);
				int index = 0;
				for (Object unwrappedChild : children) {
					Object child = wrap(object, feature, unwrappedChild, index);
					if (store != null) {
						store.getList(feature).add(child);
					} else {
						result.add(child);
					}
					index++;
				}
			} else {
				Object child = eObject.eGet(feature);
				if (child != null) {
					child = wrap(object, feature, child, CommandParameter.NO_INDEX);
					if (store != null) {
						store.setValue(feature, child);
					} else {
						result.add(child);
					}
				}
			}
		}
		return store != null ? store.getChildren() : result;
	}

	/**
	 * Wraps a value, if needed, and keeps the wrapper for disposal along with the item provider. This method actually
	 * calls {@link #createWrapper createWrapper} to determine if the given value, at the given index in the given
	 * feature of the given object, should be wrapped and to obtain the wrapper. If a wrapper is obtained, it is
	 * recorded and returned. Otherwise, the original value is returned. Subclasses may override {@link #createWrapper
	 * createWrapper} to specify when and with what to wrap values.
	 */
	protected Object wrap(Object object, EStructuralFeature feature, Object value, int index) {
		if (!feature.isMany() && index != CommandParameter.NO_INDEX) {
			System.out.println("Bad wrap index."); //$NON-NLS-1$
			System.out.println("  object: " + object); //$NON-NLS-1$
			System.out.println("  feature: " + feature); //$NON-NLS-1$
			System.out.println("  value: " + value); //$NON-NLS-1$
			System.out.println("  index: " + index); //$NON-NLS-1$
			new IllegalArgumentException("Bad wrap index.").printStackTrace(); //$NON-NLS-1$
		}

		Object wrapper = createWrapper(object, feature, value, index);
		if (wrapper == null) {
			wrapper = value;
		} else if (wrapper != value) {
			if (wrappers == null) {
				wrappers = new Disposable();
			}
			wrappers.add(wrapper);
		}
		return wrapper;
	}

	protected Object createWrapper(Object object, EStructuralFeature feature, Object value, int index) {
		if (!isWrappingNeeded(object)) {
			return value;
		}
		if (!((EReference) feature).isContainment()) {
			value = new DelegatingWrapperItemProvider(value, object, feature, index, adapterFactory);
		}
		return value;
	}

	/**
	 * This sets the parent of the transient item provider to <code>target</code> value. The target instance variable
	 * comes from the adapter base class {@link org.eclipse.emf.common.notify.impl.AdapterImpl}.
	 */
	@Override
	public Object getParent(Object object) {
		return target;
	}

	/**
	 * This creates a primitive {@link org.eclipse.emf.edit.command.RemoveCommand}.
	 */
	@Override
	protected Command createRemoveCommand(EditingDomain domain, EObject owner, EStructuralFeature feature, Collection<?> collection) {
		return createWrappedCommand(super.createRemoveCommand(domain, owner, feature, collection), owner);
	}

	/**
	 * This creates a primitive {@link org.eclipse.emf.edit.command.AddCommand}.
	 */
	@Override
	protected Command createAddCommand(EditingDomain domain, EObject owner, EStructuralFeature feature, Collection<?> collection, int index) {
		return createWrappedCommand(super.createAddCommand(domain, owner, feature, collection, index), owner);
	}

	/**
	 * This allows creating a command and overriding the {@link
	 * org.eclipse.emf.common.command.CommandWrapper.getAffectedObjects()#getAffectedObjects()} method to return the
	 * appropriate transient item provider whenever the “real” affected object is the owner.
	 * 
	 * @param command
	 *            an command.
	 * @param owner
	 *            the owner of the object.
	 * @return a {@link CommandWrapper}
	 */
	protected Command createWrappedCommand(Command command, final EObject owner) {
		return new CommandWrapper(command) {
			@Override
			public Collection<?> getAffectedObjects() {
				Collection<?> affected = super.getAffectedObjects();
				if (affected.contains(owner)) {
					affected = Collections.singleton(TransientItemProvider.this);
				}
				return affected;
			}
		};
	}

	/**
	 * This implements {@link IEditingDomainItemProvider#getNewChildDescriptors
	 * IEditingDomainItemProvider.getNewChildDescriptors}, returning descriptors for all the possible children that can
	 * be added to the specified <code>target</code>. The target instance variable comes from the adapter base class
	 * {@link org.eclipse.emf.common.notify.impl.AdapterImpl}.
	 */
	@Override
	public Collection<?> getNewChildDescriptors(Object object, EditingDomain editingDomain, Object sibling) {
		return super.getNewChildDescriptors(target, editingDomain, sibling);
	}

	/**
	 * This implements delegated command creation for the given transient item provider and sets its owner to
	 * <code>target</code>.
	 */
	@Override
	public Command createCommand(Object object, EditingDomain domain, Class<? extends Command> commandClass, CommandParameter commandParameter) {
		if (commandClass == CreateChildCommand.class) {
			commandParameter.setOwner(target);
		}
		return super.createCommand(object, domain, commandClass, commandParameter);
	}

	/**
	 * This returns the image for non-model view objects.
	 */
	@Override
	public Object getImage(Object object) {
		if (object != null) {
			return overlayImage(object, Activator.INSTANCE.getImage("full/obj16/folder_closed")); //$NON-NLS-1$
		}
		return null;
	}

}
