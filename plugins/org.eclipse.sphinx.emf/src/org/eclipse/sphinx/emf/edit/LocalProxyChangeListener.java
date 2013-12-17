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

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.sphinx.emf.util.EObjectUtil;

/**
 * Detects {@link EObject model object}s that have been removed from their
 * {@link org.eclipse.emf.ecore.EObject#eResource() containing resource} and/or their {@link EObject#eContainer
 * containing object} and turns them as well as all their directly and indirectly contained objects into proxies. This
 * offers the following benefits:
 * <ul>
 * <li>After removal of an {@link EObject model object} from its container, all other {@link EObject model object}s that
 * are still referencing the removed {@link EObject model object} will know that the latter is no longer available but
 * can still figure out its type and {@link URI}.</li>
 * <li>If a new {@link EObject model object} with the same type as the removed one is added to the same container again
 * later on, the proxies which the other {@link EObject model object}s are still referencing will be resolved as usual
 * and therefore get automatically replaced by the newly added {@link EObject model object}.</li>
 * <li>In big models, this approach can yield significant advantages in terms of performance because it helps avoiding
 * full deletions of {@link EObject model object}s involving expensive searches for their cross-references and those of
 * all their directly and indirectly contained objects. It does all the same not lead to references pointing at
 * "floating" {@link EObject model object}s, i.e., {@link EObject model object}s that are not directly or indirectly
 * contained in a resource.</li>
 * </ul>
 */

public class LocalProxyChangeListener extends ResourceSetListenerImpl {
	/**
	 * Default constructor.
	 */
	public LocalProxyChangeListener() {
		super(NotificationFilter.createEventTypeFilter(Notification.ADD_MANY).or(
				NotificationFilter.createEventTypeFilter(Notification.REMOVE_MANY).or(
						NotificationFilter.createEventTypeFilter(Notification.ADD).or(
								NotificationFilter.createEventTypeFilter(Notification.REMOVE).or(
										NotificationFilter.createEventTypeFilter(Notification.SET).or(
												NotificationFilter.createEventTypeFilter(Notification.UNSET)))))));
	}

	@Override
	public void resourceSetChanged(ResourceSetChangeEvent event) {
		for (Notification notification : event.getNotifications()) {
			if (notification.getNotifier() instanceof EObject) {
				EObject object = (EObject) notification.getNotifier();
				if (notification.getFeature() instanceof EReference) {
					EReference reference = (EReference) notification.getFeature();

					// Object being removed from model (i.e. from its owner)?
					/*
					 * !! Important Note !! Don't use notification.getNewValue() == null to test if object has been
					 * removed from the model. The reason is that plenty of successive modifications and notifications
					 * for the same object and feature might have happened before we get called here. Therefore the new
					 * value of an arbitrary notification which we are dealing with here is the new value which was
					 * applicable by the time the notification was created but does not necessarily correspond to the
					 * current object's state.
					 */
					switch (notification.getEventType()) {
					case Notification.REMOVE:
						// Convert removed object plus its directly and indirectly contained children into
						// proxies
						if (reference.isContainment()) {
							if (reference.isMany()) {
								@SuppressWarnings("unchecked")
								List<Object> values = (List<Object>) object.eGet(reference);
								if (!values.contains(notification.getOldValue())) {
									EObjectUtil.proxify(object, reference, (EObject) notification.getOldValue());
								}
							} else {
								if (object.eGet(reference) == null) {
									EObjectUtil.proxify(object, reference, (EObject) notification.getOldValue());
								}
							}
						}
						break;
					case Notification.REMOVE_MANY:
						// Convert removed objects plus its directly and indirectly contained children into
						// proxies
						if (reference.isContainment()) {
							@SuppressWarnings("unchecked")
							List<Object> values = (List<Object>) object.eGet(reference);
							@SuppressWarnings("unchecked")
							List<Object> oldValues = (List<Object>) notification.getOldValue();
							for (Object oldValue : oldValues) {
								if (!values.contains(oldValue)) {
									EObjectUtil.proxify(object, reference, (EObject) oldValue);
								}
							}
						}
						break;
					case Notification.ADD:
						if (reference.isContainment()) {
							if (reference.isMany()) {
								@SuppressWarnings("unchecked")
								List<Object> references = (List<Object>) object.eGet(reference);
								if (references.contains(notification.getNewValue())) {
									EObjectUtil.deproxify((EObject) notification.getNewValue());
								}
							} else {
								if (object.eGet(reference) != null) {
									EObjectUtil.deproxify((EObject) notification.getNewValue());
								}
							}
						}
						break;
					case Notification.ADD_MANY:
						if (reference.isContainment()) {
							@SuppressWarnings("unchecked")
							List<Object> values = (List<Object>) object.eGet(reference);
							@SuppressWarnings("unchecked")
							List<Object> newValues = (List<Object>) notification.getNewValue();
							for (Object newValue : newValues) {
								if (values.contains(newValue)) {
									EObjectUtil.deproxify((EObject) newValue);
								}
							}
						}
						break;
					case Notification.SET:
						// Object being added to model (i.e. from its owner)?
						/*
						 * !! Important Note !! Don't use notification.getOldValue() == null to test if object has been
						 * added to the model. The reason is that plenty of successive modifications and notifications
						 * for the same object and feature might have happened before we get called here. Therefore the
						 * old value of an arbitrary notification which we are dealing with here is the old value which
						 * was applicable by the time the notification was created but does not necessarily correspond
						 * to the current object's state.
						 */
						if (reference.isContainer() && object.eContainer() != null) {
							// Convert added object plus its directly and indirectly contained children
							// back into regular EObjects
							if (object.eIsProxy()) {
								EObjectUtil.deproxify(object);
							}
						}
						break;
					case Notification.UNSET:
						if (reference.isContainer() && object.eContainer() == null) {
							if (notification.getOldValue() != null) {
								if (!object.eIsProxy()) {
									EObjectUtil.proxify((EObject) notification.getOldValue(), reference, object);
								}
							}
						}
						break;
					}
				}
			}
		}
	}

	@Override
	public boolean isPostcommitOnly() {
		return true;
	}

}
