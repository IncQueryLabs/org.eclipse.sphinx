package org.eclipse.sphinx.emf.check.workflows;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.mwe.core.WorkflowContext;
import org.eclipse.emf.mwe.core.issues.Issues;
import org.eclipse.emf.mwe.core.monitor.ProgressMonitor;
import org.eclipse.sphinx.emf.check.ICheckValidator;
import org.eclipse.sphinx.emf.check.registry.CheckValidatorRegistry;
import org.eclipse.sphinx.emf.check.services.CheckProblemMarkerService;
import org.eclipse.sphinx.emf.mwe.dynamic.IWorkflowSlots;
import org.eclipse.sphinx.emf.mwe.dynamic.components.AbstractModelWorkflowComponent;
import org.eclipse.sphinx.emf.mwe.dynamic.components.IModelWorkflowComponent;

/**
 * An abstract workflow component which makes use of the check validation service.
 */
public abstract class AbstractCheckWorkflowComponent extends AbstractModelWorkflowComponent implements IModelWorkflowComponent {

	private ICheckValidator validator = null;

	protected Set<String> filters = new HashSet<String>();

	public ICheckValidator getValidator(EPackage ePackage) throws CoreException {
		if (validator == null) {
			CheckValidatorRegistry registry = CheckValidatorRegistry.getInstance();
			validator = registry.getValidator(ePackage);
		}
		return validator;
	}

	@Override
	protected void invokeInternal(WorkflowContext ctx, ProgressMonitor monitor, Issues issues) {
		@SuppressWarnings("unchecked")
		List<EObject> models = (List<EObject>) ctx.get(IWorkflowSlots.MODEL_SLOT_NAME);
		if (models != null && !models.isEmpty()) {
			for (EObject model : models) {
				try {
					// Get the epackage from the model input
					EPackage ePackage = model.eClass().getEPackage();

					// Get the validator specific to epackage
					ICheckValidator checkValidator = getValidator(ePackage);

					// Set manually a filter for categories of constraints to validate
					checkValidator.setFilter(filters);

					// Run validation (use standard validation entry point)
					Diagnostic diagnostic = Diagnostician.INSTANCE.validate(model);

					// Generate error markers and update check validation view
					CheckProblemMarkerService.INSTANCE.updateProblemMarkers(model, diagnostic);

				} catch (Exception ex) {
					issues.addError(this, ex.getMessage(), model, ex, null);
				}
			}
		}
	}
}
