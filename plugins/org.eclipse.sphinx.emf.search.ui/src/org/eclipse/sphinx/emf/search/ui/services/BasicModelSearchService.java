package org.eclipse.sphinx.emf.search.ui.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sphinx.emf.model.IModelDescriptor;
import org.eclipse.sphinx.emf.search.ui.ModelSearchMatch;
import org.eclipse.sphinx.emf.search.ui.QuerySpecification;
import org.eclipse.sphinx.emf.ui.util.RetrieveNameAttributeHelper;

public class BasicModelSearchService implements IModelSearchService {

	private RetrieveNameAttributeHelper helper = new RetrieveNameAttributeHelper();

	@Override
	public List<ModelSearchMatch> getMatches(IModelDescriptor modelDescriptor, QuerySpecification spec) {
		return getMatches(modelDescriptor.getLoadedResources(true), spec);
	}

	@Override
	public List<ModelSearchMatch> getMatches(Collection<Resource> resources, QuerySpecification spec) {
		List<ModelSearchMatch> result = new ArrayList<ModelSearchMatch>();
		for (Resource resource : resources) {
			TreeIterator<EObject> allContents = resource.getAllContents();
			while (allContents.hasNext()) {
				EObject eObject = allContents.next();
				EAttribute nameAttribute = helper.getNameAttribute(eObject);
				if (nameAttribute != null) {
					Object name = eObject.eGet(nameAttribute);
					if (name != null) {
						// TODO Add support for *, ?, \*, \?, \\
						if (spec.isCaseSensitive()) {
							if (name.toString().equals(spec.getPattern())) {
								result.add(createModelSearchMatch(eObject));
							}
						} else if (name.toString().equalsIgnoreCase(spec.getPattern())) {
							result.add(createModelSearchMatch(eObject));
						}
					}
				}
			}
		}
		return result;
	}

	private ModelSearchMatch createModelSearchMatch(EObject eObject) {
		ModelSearchMatch match = new ModelSearchMatch(eObject);
		return match;
	}
}
