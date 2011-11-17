package ui.component

import util.Utils
import util.Bool
import util.condition.C
import util.condition.Condition
import org.zkoss.zul.Decimalbox 
import org.zkoss.zul.Intbox
import org.zkoss.zul.Textbox
import org.zkoss.zul.Combobox
import org.zkoss.zul.Comboitem
import org.zkoss.zul.Datebox
import org.zkoss.zul.Checkbox

class QueryBuilder extends BuilderSupport{
	
	private Condition root
	
	public Condition getCondition() {
		return root
	}		
	
	@Override
	protected Object createNode(Object name) {
		return createNode(name, [:], null)
	}
	
	@Override
	protected Object createNode(Object name, Map attributes) {
		return createNode(name, attributes, null)
	}
	
	@Override
	protected Object createNode(Object name, Object control) {
		return createNode(name, [:], control)
	}
	
	//==========================================================
	// 支持的attributes：value:'属性名'
	//==========================================================
	@Override
	protected Object createNode(Object name, Map attributes, Object control) {
		Condition cond = null
		if(name.toUpperCase() in ['AND', 'OR'])
			cond = C."${name.toUpperCase()}"()
		else if(control!=null && !control.isDisabled()) {
			String value = null
			String propName = attributes['value']?:'value'
			switch(control)
			{
				case Combobox:
					if(attributes.isEmpty())
						value = control.value
					else if(!BaseWindow.isAllItemSelected(control)) {
						Comboitem item = control.selectedItem
						if(control.getAttribute('enum')!=null)
							value = Enum.valueOf(control.getAttribute('enum'), item?.value).toStr()
						else
							value = item?."${propName}"
					}
					break
				case Datebox:
					if(!BaseWindow.isEmpty(control)) {
						if(control.format =~ /[Hms]/) {
							value = Utils.date2DateTimeString(control."${propName}")
							cond = C."${name}"(value)
						}
						else {
							value = Utils.date2DateString(control."${propName}")
							cond = C."${name}"(value)
						}
					}
					break
				case Intbox:
				case Decimalbox:
				case Textbox:
					if(!BaseWindow.isEmpty(control)) {
						value = control."${propName}".toString()
					}
					break
				case Checkbox:
						value = Bool.fromBoolean(control.checked).toStr()
					break
				default:
					throw new IllegalArgumentException("${name} control type ${control.class} not supported")
					break
			}
			if(Utils.isNullString(value))
				return null
			if(!(control instanceof Datebox))
				cond = C."${name}"(value.toString().trim())
		}
		
		if (!current)
			root = cond
		return cond
	}
	
	@Override
	protected void nodeCompleted(Object parent, Object node) {
		super.nodeCompleted(parent, node);
	}
	
	@Override
	protected void setParent(Object parent, Object child) {
		if (!parent)
			return
			
		if(child==null)
			return
		parent.append(child)
	}
}
