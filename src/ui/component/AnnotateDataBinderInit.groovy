package ui.component

import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Components
import org.zkoss.zk.ui.Page

public class AnnotateDataBinderInit extends org.zkoss.zkplus.databind.AnnotateDataBinderInit {
	
	private Page page
	
	private Map args
	
	@Override
	public void doInit(Page page, java.util.Map args) {
		this.page = page
		this.args = args  
		super.doInit(page, args)
	}

	private List<BaseWindow> getWindow(Component[] comps) {
		return comps.findAll { comp -> return comp instanceof BaseWindow }
	}
	
	@Override
	protected void beforeFirstLoadAll(Page page, Component[] comps) {
		List<BaseWindow> windows = getWindow(comps)
		if (windows.isEmpty())
			return
			
		def bindings = []
		bindings.addAll(_binder.allBindings)
		bindings.each { binding ->
			BaseWindow window = findBaseWindow(windows, binding.component)
			if(window==null)
				return
				
			def save_when = []
			if(window.saveDataButton!=null)
				save_when << "${window.saveDataButton.id}.onClick".toString()
			if(window.saveAndNewDataButton!=null)
				save_when << "${window.saveAndNewDataButton.id}.onClick".toString()
			if(window.switchDataButtons)
				window.switchDataButtons.each{save_when << "${it.id}.onClick".toString()}
			if(save_when.isEmpty())
				return		
				
			String access = null
			if(binding.loadable && binding.savable)
				access = 'both'
			else if(binding.loadable)
				access = 'load'
			else if(binding.savable)
				access = 'save'
			
			_binder.removeBinding(binding.component, binding.attr)
			_binder.addBinding(binding.component, // component
					binding.attr, // attr
					binding.expression, // expr
					binding.loadWhenEvents?.toList(), // load when events
					save_when, // save when events
					access, // access
					binding.converter?.class?.name, // converter
					binding.args, // args
					binding.loadAfterEvents?.toList(), // load after events
					binding.afterWhenEvents?.toList()) // save after events
		}
	}
	
	private BaseWindow findBaseWindow(List<BaseWindow> windows, Component comp) {
		return windows.find { window -> return Components.isAncestor(window, comp) }
	}
}