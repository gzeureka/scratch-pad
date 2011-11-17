package ui.component

import org.zkoss.zul.Timer
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events

class MessageWindow extends BaseWindow {
	def parent
	
	String message
	
	String style
	
	Timer timer
	
	@Override
	protected void prepareData() {
		super.prepareData()
		parent=argMap['parent']
		title=argMap['title']?:''
		message=argMap['message']?:''
		style="padding:12px 20px;background-color:${argMap['color']?:'palegreen'};"
		Integer closeAfter=argMap['duration']
		if(closeAfter!=null && closeAfter>0) {
			timer.delay=closeAfter
			timer.start()
		}
	}
	
	void onClose() {
		timer?.stop()
		parent?.detachMessageWindow(this)
	}
	
	void onTimer$timer(Event e) {
		Events.postEvent(Events.ON_CLOSE, this, null)
	}
	
}