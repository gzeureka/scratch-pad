package ui.component

import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Components
import org.zkoss.zk.ui.Page
import org.zkoss.zk.ui.metainfo.ComponentInfo
import org.zkoss.zk.ui.util.GenericForwardComposer

class BaseComposer extends GenericForwardComposer {

	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		
		Components.wireVariables(parent, this)
		prepareData()
		
		return super.doBeforeCompose(page, parent, compInfo);
	}
	
	protected void prepareData() {
	}
	
}