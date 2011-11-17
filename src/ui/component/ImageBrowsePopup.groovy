package ui.component;

import org.zkoss.zk.ui.event.OpenEvent
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.Components;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Vbox 

public class ImageBrowsePopup extends Popup implements AfterCompose {
	String abc
	
	def imgs = []
	
	Vbox noImgs
	
	@Override
	void afterCompose() {
		Components.wireVariables(this, this)
		
		def argMap = Executions.current.arg
		abc = argMap['abc']
		
		if(imgs)
			noImgs.visible = false
		else
			noImgs.visible = true
	}
	
	public void onOpen(OpenEvent event){
		if(!event.isOpen())
			this.detach()
	}
	
}
