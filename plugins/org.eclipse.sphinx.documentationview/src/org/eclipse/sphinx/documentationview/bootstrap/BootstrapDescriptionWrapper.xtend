package org.eclipse.sphinx.documentationview.bootstrap

import org.eclipse.sphinx.documentationview.IDescriptionWrapper

class BootstrapDescriptionWrapper implements IDescriptionWrapper{
	
	override textPost() '''
		 </div>
	'''
	
	override textPre() '''
		<div class="col-md-6">
	'''
	
	
}