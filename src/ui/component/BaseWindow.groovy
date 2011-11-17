package ui.component



import groovy.lang.Closure
import hr.HR
import hr.privilege.NoPrivException
import hr.privilege.Priv

import org.apache.poi.hssf.record.formula.functions.T
import org.joda.time.DateTime
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Components
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.WrongValueException
import org.zkoss.zk.ui.WrongValuesException
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.ForwardEvent
import org.zkoss.zk.ui.ext.AfterCompose
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zkplus.databind.DataBinder
import org.zkoss.zul.A
import org.zkoss.zul.Bandbox
import org.zkoss.zul.Button
import org.zkoss.zul.Checkbox
import org.zkoss.zul.Combobox
import org.zkoss.zul.Comboitem
import org.zkoss.zul.Datebox
import org.zkoss.zul.Decimalbox
import org.zkoss.zul.Doublebox
import org.zkoss.zul.Include
import org.zkoss.zul.Intbox
import org.zkoss.zul.Listbox
import org.zkoss.zul.Listitem
import org.zkoss.zul.Messagebox
import org.zkoss.zul.Paging
import org.zkoss.zul.Radio
import org.zkoss.zul.Textbox
import org.zkoss.zul.Toolbarbutton
import org.zkoss.zul.Window
import org.zkoss.zul.event.PagingEvent

import platform.code.CodeType
import platform.code.Codes
import platform.param.ParamType
import platform.param.Params
import ui.component.combobox.LookupComboboxRenderer
import ui.component.validator.*
import util.DateTimeUtils
import util.ObjUtils
import util.UIUtils
import util.Utils
import util.condition.Condition

public class BaseWindow extends Window implements AfterCompose {

	private List<ValidatorGroup> validatorGroups = [new ValidatorGroup()]

	private Closure showModalCallback

	private Window messageWindow

	private Object currentData

	private boolean isNewData

	def tmpCurrentData//克隆currentData,用于比较界面的数据是否被修改

	// 用于传递数据的Map
	protected Map argMap

	protected boolean closeOnEsc

	// 用于新建数据对象的按钮
	protected Button createDataButton

	// 用于保存数据到数据库的按钮
	protected Button saveDataButton

	// 用于保存数据并新建数据对象的按钮
	protected Button saveAndNewDataButton

	// 用于从数据库删除数据的按钮
	protected Button deleteDataButton

	// 用于离开当前编辑的对象的按钮
	protected List<Button> switchDataButtons=[]

	void afterCompose() {
		// wire variables
		Components.wireVariables(this, this)

		// NO need to register onXxx event listeners

		// auto forward
		Components.addForwards(this, this)

		argMap = Executions.current.arg

		prepareData()

		isNewData = !(currentData?.id!=Utils.NULL_ID)

		Events.postEvent('onPrepareWindow', this, null)
	}

	protected DataBinder getDataBinder() {
		return getAttribute('binder')
	}

	public Object getCurrentData() {
		return currentData
	}

	protected void setCurrentData(Object data) {
		currentData = data
	}

	protected void resetTmpCurrentData() {
		tmpCurrentData = Utils.cloneObject(currentData)
	}

	// 不要覆盖此方法
	public void onPrepareWindow(Event e) {
		prepareWindow()
	}

	//================================================================
	// 子类可覆盖此方法，返回data是否为新数据
	//================================================================
	protected boolean isNewData(Object data) {
		isNewData
	}

	//================================================================
	// 子类覆盖此方法，声明Validator和初始化数据
	//================================================================
	protected void prepareData() {
	}

	//================================================================
	// 子类覆盖此方法，用于在完成数据绑定后初始化界面
	//================================================================
	protected void prepareWindow() {
	}

	//================================================================
	// 子类覆盖此方法，新建数据对象
	//================================================================
	protected Object newData() {
		return null
	}

	//================================================================
	// 子类覆盖此方法，用于新建数据对象后，准备界面
	//================================================================
	protected Object afterNewData() {
		return null
	}

	// 不用覆盖此方法
	protected Object doAfterNewData() {
		def obj = afterNewData()
		isNewData = true
		return obj
	}

	//================================================================
	// 子类覆盖此方法，添加数据到数据库中
	//================================================================
	protected void createData(Object data) {

	}

	// 不用覆盖此方法
	protected void doCreateData(Object data) {
		createData(data)
		isNewData = false
	}

	//================================================================
	// 子类覆盖此方法，保存数据到数据库中
	//================================================================
	protected void saveData(Object data) {

	}

	// 不用覆盖此方法
	protected void doSaveData(Object data) {
		saveData(data)
		isNewData = false
	}

	//================================================================
	// 子类覆盖此方法，从数据库中删除数据
	//================================================================
	protected void deleteData(Object data) {

	}

	//================================================================
	// 子类覆盖此方法，离开当前编辑的对象前会回调此方法
	//================================================================
	protected void beforeSwitchData(Object data,boolean exit) {
		def listener = {Event e->
			if(e.name == 'onYes'){
				//Events.postEvent('onClick', saveDataButton, null)
				//Events.postEvent('onClose', this, null)
				saveAndClose()
				return Messagebox.OK
			}
			if(e.name == 'onNo'){
				switchDataButtons=[]
				if(exit)
					Events.postEvent('onClose', this, null)
				return Messagebox.NO
			}
		} as org.zkoss.zk.ui.event.EventListener
		Messagebox.show('您修改了记录但没有保存, 需要保存吗?','', Messagebox.YES | Messagebox.NO | Messagebox.ABORT, Messagebox.QUESTION, listener)

	}

	protected void confirmAction(String message,Closure actionForYes){
		def listener = {Event e->
			if(e.name == 'onYes'){
				actionForYes.call()
				return Messagebox.OK
			}
			if(e.name == 'onNo'){
				return Messagebox.NO
			}
		} as org.zkoss.zk.ui.event.EventListener
		Messagebox.show(message,'', Messagebox.YES | Messagebox.NO | Messagebox.ABORT, Messagebox.QUESTION, listener)
	}

	//================================================================
	// 子类覆盖此方法，判断currentData内容是否已被用户更改
	//================================================================
	protected boolean isDataDirty(Object data) {
		return !ObjUtils.compareObject(data,tmpCurrentData)
	}

	protected void loadData() {
		getDataBinder().loadAll()
	}

	protected void loadData(String componentId) {
		Component component = getComponent(componentId)
		getDataBinder().loadComponent(component)
	}

	protected void loadData(Component component) {
		getDataBinder().loadComponent(component)
	}

	protected Component getComponent(String componentId) {
		Component comp = getFellowIfAny(componentId)
		assert comp:"component of id ${componentId} not found"
		return comp
	}

	List getBaseWinFellows(){
		return UIUtils.getDescendants(this)
	}

	protected List<Component> getComponentsWithAttribute(String attributeName) {
		return baseWinFellows.findAll{it.hasAttribute(attributeName)}
	}

	protected List<Component> getComponentsWithAttribute(String attributeName, String attributeValue) {
		return baseWinFellows.findAll{it.getAttribute(attributeName)==attributeValue}
	}


	/**
	 * 获取页面listbox,grid控件中的所有checkbox选项选取中的属性
	 * 调用方法:
	 * 如页面有listbox控件中有
	 * <listcell>
	 * 	<checkbox>  
	 *	  <custom-attributes name="cellCbx" chkValue="@{item.id}"/>
	 * 	</checkbox>
	 * </listcell>
	 * 则调用时:
	 * def ids = getCheckboxsComponentsWithCustomAttribute('name,'cellCbx','chkValue')
	 * @param attributeName
	 * @param attributeValue
	 * @param checkAttributeName
	 * @return
	 */
	protected List<Integer> getCheckboxsComponentsWithCustomAttribute(String attributeName,String attributeValue,def checkAttributeName ='chkValue'){
		List<Integer> selectedCheckBoxIds = new ArrayList<Integer>()
		getComponentsWithAttribute(attributeName,attributeValue).each{
			if(it instanceof Checkbox && it.checked){
				selectedCheckBoxIds.add(it.attributes."${checkAttributeName}"?.toInteger())
			}
		}
		return selectedCheckBoxIds
	}

	protected void setAllCheckboxsComponentsWithCustomAttribute(boolean selected,String attributeName,String attributeValue,def checkAttributeName ='chkValue'){
		List<Integer> selectedCheckBoxIds = new ArrayList<Integer>()
		getComponentsWithAttribute(attributeName,attributeValue).each{
			if(it instanceof Checkbox){
				if(selected)
					it.checked = true
				else
					it.checked = false
			}
		}

	}

	protected void clearComponents(String... compIds) {
		clearComponents(compIds.collect{getComponent(it)})
	}

	protected void clearComponents(Component... comps) {
		comps.asList()
	}

	protected void clearComponents(List<Component> comps) {
		comps.each{comp ->
			switch(comp) {
				case Textbox:
					comp.value = ''
					break
				case Intbox:
				case Decimalbox:
				case Datebox:
					comp.value = null
					break
				case Checkbox:
					comp.checked = false
					break
				default:
					break
			}
		}
	}

	private QueryBuilder getQueryBuilder() {
		return new QueryBuilder()
	}

	protected Condition and(Closure closure) {
		QueryBuilder builder = getQueryBuilder()
		Condition condition = builder.and(closure)
		return condition
	}

	protected Condition or(Closure closure) {
		QueryBuilder builder = getQueryBuilder()
		Condition condition = builder.or(closure)
		return condition
	}

	protected selectItem(Combobox combobox, int index) {
		def renderer = combobox.itemRenderer

		if(renderer instanceof LookupComboboxRenderer) {
			if(combobox.model.size() <= index)
				return
			renderer.selected=combobox.model.getElementAt(index)
			if(index < combobox.items.size())
				combobox.selectedItem=combobox.items[index]
		} else {
			if(combobox.itemCount <= index)
				return
			combobox.selectedIndex = index
		}
	}

	protected selectItem(int index, Combobox... comboboxes) {
		comboboxes.each{selectItem(it, index)}
	}

	protected void lookup(Combobox combobox, List list, String displayMemberName, String valueMemberName, boolean withoutBinding=false) {
		def items = combobox.items
		if(withoutBinding || items.size()>0) {
			// 用于查询
			list.each {
				Comboitem item = combobox.appendItem(it."$displayMemberName")
				item.value = it."${valueMemberName}"
			}
		}
		else {
			// 用于数据绑定
			combobox.itemRenderer = new LookupComboboxRenderer(displayMemberName, valueMemberName)
			combobox.model = new BindingListModelList(list, true)
		}
		selectItem(combobox, 0)
	}

	/*
	 * 两个Combobox联动
	 * cbb1    父Combobox
	 * cbb2    子Combobox
	 * dn2       子Combobox显示
	 * vn1      子Combobox value 
	 * withAll 是否包括"全部"
	 * c 两个Combobox联动执行闭包
	 */
	protected void lookupDoubleCombobox(Combobox cbb1, Combobox cbb2, String dn2, String vn2, boolean withAll, Closure c){
		//根据第一个下拉值，级联初始化第二个Combobox
		lookupSecondCombobox(cbb1.selectedItem, cbb2, dn2, vn2, withAll, c)
		//添加监听器
		cbb1.addEventListener("onChange", {
			lookupSecondCombobox(cbb1.selectedItem, cbb2, dn2, vn2, withAll, c)
		} as EventListener)
	}

	protected void lookupDoubleComboboxWithoutBinding(Combobox cbb1, Combobox cbb2, String dn2, String vn2, boolean withAll, Closure c){
		//根据第一个下拉值，级联初始化第二个Combobox
		lookupSecondComboboxWithoutBinding(cbb1.selectedItem, cbb2, dn2, vn2, withAll, c)
		//添加监听器
		cbb1.addEventListener("onChange", {
			lookupSecondComboboxWithoutBinding(cbb1.selectedItem, cbb2, dn2, vn2, withAll, c)
		} as EventListener)
	}

	/*
	 * 两个Combobox联动
	 * cbb1    父Combobox
	 * cbb2    子Combobox
	 * list1  父Combobox的list
	 * dn1        父Combobox显示
	 * vn1        父Combobox value
	 * dn2       子Combobox显示
	 * vn1      子Combobox value 
	 * withAll 是否包括"全部"
	 * c     两个Combobox联动执行闭包
	 */
	protected void lookupDoubleCombobox(Combobox cbb1,Combobox cbb2, List list, String dn1, String vn1, String dn2, String vn2,boolean withAll,Closure c){
		if(withAll){
			lookupWithAllItem(cbb1,list,dn1,vn1)
		}
		else
			lookup(cbb1,list,dn1,vn1)
		//根据第一个下拉值，级联初始化第二个Combobo
		if(list?.size()>0)
			lookupSecondCombobox(list[0]."$vn1",cbb2,dn2,vn2,withAll,c)
		//添加监听器
		cbb1.addEventListener("onChange", {
			def _value = cbb1.selectedItem?.value
			if(_value != null){
				if(_value != Utils.NULL_ID_STR && _value != Utils.ALL_ID_STR)
					lookupSecondCombobox(_value,cbb2,dn2,vn2,withAll,c)
				else{
					cbb2.items?.clear()
					cbb2.value=""
				}
			}
		} as EventListener)
	}

	protected void lookupSecondComboboxWithoutBinding(def selValue, Combobox cbb, String displayMemberName, String valueMemberName, boolean withAll, Closure c){
		if(selValue){
			def list = c.call(selValue)
			cbb.items?.clear()
			cbb.value=""
			if(list?.size() > 0){
				if(withAll)
					addAllItem(cbb)
					lookup(cbb, list, displayMemberName, valueMemberName, true)
			}
		}
	}

	protected void lookupSecondCombobox(def selValue, Combobox cbb, String displayMemberName, String valueMemberName, boolean withAll, Closure c){
		if(selValue){
			def list = c.call(selValue)
			cbb.items?.clear()
			cbb.value=""
			if(list?.size() > 0){
				if(withAll)
					lookupWithAllItem(cbb, list, displayMemberName, valueMemberName)
				else
					lookup(cbb, list, displayMemberName, valueMemberName)
			}
		}
	}

	//combobox数据绑定
	protected void lookupWithAllItem(Combobox combobox, List list, String displayMemberName, String valueMemberName) {
		addAllItem(combobox)
		lookup(combobox, list, displayMemberName, valueMemberName)
	}

	protected void lookupWithNullItem(Combobox combobox, List list, String displayMemberName, String valueMemberName) {
		addNullItem(combobox)
		lookup(combobox, list, displayMemberName, valueMemberName)
	}

	protected void lookupWithSelectItem(Combobox combobox, List list, String displayMemberName, String valueMemberName) {
		addSelectItem(combobox)
		lookup(combobox, list, displayMemberName, valueMemberName)
	}

	protected void lookupEnum(Combobox combobox, Class<Enum> type) {
		def list = type.values().collect{[val:it.name(), desc:it.desc]}
		combobox.setAttribute('enum', type)
		lookup(combobox, list, 'desc', 'val')
	}

	/***
	 * 绑定下拉框时,过滤掉exceptionArray数组的值
	 * @param combobox
	 * @param type 枚举类
	 * @param exceptionArray 数组
	 */
	protected void lookupEnumException(Combobox combobox, Class<Enum> type, def exceptionArray,boolean isAddNull = false){
		def allvalues = type.values()
		def _array = allvalues - allvalues.findAll{it.toStr() in exceptionArray}
		def list = _array.collect{[val:it.name(), desc:it.desc]}
		combobox.setAttribute('enum', type)
		if(isAddNull)
			addAllItem(combobox)
		lookup(combobox, list, 'desc', 'val')
	}


	protected void lookupEnumWithAllItem(Combobox combobox, Class<Enum> type) {
		addAllItem(combobox)
		lookupEnum(combobox, type)
	}

	protected void lookupEnumWithNullItem(Combobox combobox, Class<Enum> type) {
		addNullItem(combobox)
		lookupEnum(combobox, type)
	}

	protected void lookupCode(Combobox combobox, CodeType codeType) {
		def list = Codes.getList(codeType).collect{[code:it.code, codeValue:it.codeValue]}
		lookup(combobox, list, 'codeValue', 'code')
	}
	
	protected void lookupCodeWithoutBinding(Combobox combobox, CodeType codeType) {
		def list = Codes.getList(codeType).collect{[code:it.code, codeValue:it.codeValue]}
		lookup(combobox, list, 'codeValue', 'code',true)
	}

	protected void lookupCodeWithAllItem(Combobox combobox, CodeType codeType) {
		addAllItem(combobox)
		lookupCode(combobox, codeType)
	}

	protected void lookupCodeWithNullItem(Combobox combobox, CodeType codeType) {
		addNullItem(combobox)
		lookupCode(combobox, codeType)
	}

	//流程专用
	protected void lookupFlowCodeWithAllItem(Combobox combobox){
		def list = Codes.getList(CodeType.FLOW_DEF_CATEGORY)
		def innerCategoryId = Params.get(ParamType.FLOW_INNER_CATEGORY_ID)
		def faxCategoryId = Params.get(ParamType.FLOW_ELECTRONIC_FAX_CATEGORY_ID)
		def flowcategorys = list.findAll{it.code == innerCategoryId || it.code == faxCategoryId}
		addAllItem(combobox)
		lookup(combobox, flowcategorys, 'codeValue','code')
	}

	public static boolean isEmpty(Component comp) {
		if(comp instanceof Textbox || comp instanceof Intbox || comp instanceof Decimalbox)
			return ((comp?.value)?:'').toString().trim().length()==0
		else if(comp instanceof Datebox)
			return (comp?.value==null)

		assert false
	}

	public static boolean isNotEmpty(Component comp) {
		return !isEmpty(comp)
	}

	public static boolean isAllItemSelected(Combobox combobox) {
		return combobox?.selectedItem?.value.equals(Utils.ALL_ID_STR)
	}

	public static boolean isNullItemSelected(Combobox combobox) {
		return combobox?.selectedItem?.value.equals(Utils.NULL_ID_STR)
	}

	protected void CLOSE_ON_ESC() {
		closeOnEsc = true
	}

	protected void CREATE_DATA_ON_CLICK(String buttonId) {
		CREATE_DATA_ON_CLICK(getButton(buttonId))
	}

	protected void SAVE_DATA_ON_CLICK(String buttonId) {
		SAVE_DATA_ON_CLICK(getButton(buttonId))
	}

	protected void SAVE_AND_NEW_DATA_ON_CLICK(String buttonId) {
		SAVE_AND_NEW_DATA_ON_CLICK(getButton(buttonId))
	}

	protected void SAVE_AND_CLOSE_ON_CLICK(String buttonId) {
		SAVE_AND_CLOSE_ON_CLICK(getButton(buttonId))
	}

	protected void DELETE_DATA_ON_CLICK(String buttonId, String message) {
		DELETE_DATA_ON_CLICK(getButton(buttonId), message)
	}

	protected void DELETE_DATA_ON_CLICK(String buttonId, Closure getMessage) {
		DELETE_DATA_ON_CLICK(getButton(buttonId), getMessage)
	}


	//为'新增'button添加Listener
	protected void CREATE_DATA_ON_CLICK(Button button) {
		if(!button)
			throw new IllegalArgumentException("'button' cannot be null")

		button.addEventListener('onClick', {
			currentData = newData()
			loadData()
			doAfterNewData()
		} as EventListener)

		this.createDataButton = button
	}

	//为'保存'button添加Listener
	protected void SAVE_DATA_ON_CLICK(Button button) {
		bindSaveButton(button, false,false)
		this.saveDataButton = button
	}

	//为'保存并新建'button添加Listener
	protected void SAVE_AND_NEW_DATA_ON_CLICK(Button button) {
		bindSaveButton(button, true,false)
		this.saveAndNewDataButton = button
	}

	//为'保存并关闭窗口'button添加Listener
	protected void SAVE_AND_CLOSE_ON_CLICK(Button button) {
		bindSaveButton(button,false,true)
		this.saveDataButton = button
	}

	private void bindSaveButton(Button button, boolean newDataAfterSave,boolean closeAfterSave) {
		if(!button)
			throw new IllegalArgumentException("'button' cannot be null")
		//添加'校验页面输入内容'的Listener
		button.addEventListener('onBindingValidate', { validateData() } as EventListener)
		//添加'保存'button添加Listener
		button.addEventListener('onClick', {
			if(currentData==null)
				return

			if(isNewData(currentData)){
				if(closeAfterSave)
					Messagebox.show('是否要添加数据', '添加数据',
							Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, 1, { e ->
								if(e.data!=Messagebox.YES || currentData==null)
									return
								doCreateData(currentData)
								close(ModalResult.RESULT_OK)
							} as EventListener);
				else
					doCreateData(currentData)
			}
			else if(closeAfterSave){
				Messagebox.show('是否要修改数据', '修改数据',
						Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, 1, { e ->
							if(e.data!=Messagebox.YES || currentData==null)
								return
							doSaveData(currentData)
							close(ModalResult.RESULT_OK)
						} as EventListener);
			}
			else
				doSaveData(currentData)

			if(newDataAfterSave) {
				currentData = newData()
			}
			loadData()
			if(newDataAfterSave)
				doAfterNewData()
		} as EventListener)
	}

	protected void DELETE_DATA_ON_CLICK(Button button, String message) {
		DELETE_DATA_ON_CLICK(button, {message})
	}

	protected void DELETE_DATA_ON_CLICK(Button button, Closure getMessage) {
		if(!button)
			throw new IllegalArgumentException("'button' cannot be null")

		button.addEventListener('onClick', {
			if(currentData==null)
				return
			Messagebox.show("${getMessage.call()?:'是否要删除数据？'}", '删除数据',
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, 1, { e ->
						if(e.data!=Messagebox.YES || currentData==null)
							return
						deleteData(currentData)
					} as EventListener);
		} as EventListener)

		deleteDataButton = button
	}


	//点击按钮后判断是否修改过记录 如果没有修改并不退出
	protected void SWITCH_DATA_ON_CLICK_NOTEXIT(String... buttonIds) {
		SWITCH_DATA_ON_CLICK(false,*(buttonIds.collect{getComponent(it)}))
	}

	//点击按钮后判断是否修改过记录 如果没有修改 自动退出
	protected void SWITCH_DATA_ON_CLICK(String... buttonIds) {
		SWITCH_DATA_ON_CLICK(true,*(buttonIds.collect{getComponent(it)}))
	}

	//在setCurrentData后调用,方法内会调用resetTmpCurrentData()自动设置序列号的临时数据
	//buttons不需要在页面中写任何onClick方法,窗口会自行关闭
	protected void SWITCH_DATA_ON_CLICK(boolean exit,Button... buttons) {
		buttons.each{button ->
			button.addEventListener('onClick', {
				if(currentData==null){
					this.close()
				}

				if(isDataDirty(currentData)){
					beforeSwitchData(currentData,exit)
				}
				else{
					if(exit) {
						Events.postEvent('onClose', this, null)
					}
				}
			} as EventListener)
			switchDataButtons<<button
		}

		resetTmpCurrentData()
	}


	//符合闭包条件,则直接关闭,不检查currentData是否发生变化
	//否则则SWITCH_DATA_ON_CLICK
	protected void setCloseWindowButton(boolean closeFlag,String buttonId){
		if(closeFlag){
			getComponent(buttonId).addEventListener('onClick', {
				this.close()
			}as EventListener)
		}
		else{
			SWITCH_DATA_ON_CLICK(buttonId)
		}
	}

	private Button getButton(String buttonId) {
		Component comp = getComponent(buttonId)
		if (comp==null || !(comp instanceof Button))
			throw new IllegalArgumentException("'${buttonId}' is not a button")
		return comp as Button
	}

	//================================================================
	// Validators
	//================================================================
	protected Validator NOT_NULL(String compId, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return NOT_NULL(getComponent(compId), label, g)
	}

	protected Validator NOT_NULL(Component comp, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		NotNullValidator v = new NotNullValidator(comp, label)
		addValidator(v, g)
		return v
	}

	protected Validator MUST_NULL(String compId, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return MUST_NULL(getComponent(compId), label, g)
	}

	protected Validator MUST_NULL(Component comp, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		NullValidator v = new NullValidator(comp, label)
		addValidator(v, g)
		return v
	}

	protected Validator MUST_SELECT(String compId, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return MUST_SELECT(getComponent(compId), label, g)
	}

	protected Validator MUST_SELECT(Component comp, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		MustSelectValidator v = new MustSelectValidator(comp, label)
		addValidator(v, g)
		return v
	}

	protected Validator NULL_OR_SELECT(String compId, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return NULL_OR_SELECT(getComponent(compId), label, g)
	}

	protected Validator NULL_OR_SELECT(Component comp, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		NullOrSelectValidator v = new NullOrSelectValidator(comp, label)
		addValidator(v, g)
		return v
	}

	protected Validator LENGTH(String compId, String label, int minLength, int maxLength, ValidatorGroup g=getDefaultValidatorGroup()) {
		return LENGTH(getComponent(compId), label, minLength, maxLength, g)
	}

	protected Validator LENGTH(Component comp, String label, int minLength, int maxLength, ValidatorGroup g=getDefaultValidatorGroup()) {
		LengthValidator v = new LengthValidator(comp, label, minLength, maxLength)
		addValidator(v, g)
		return v
	}

	protected Validator IS_INT(String compId, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return IS_INT(getComponent(compId), label, g)
	}

	protected Validator IS_INT(Component comp, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		IntValidator v = new IntValidator(comp, label)
		addValidator(v, g)
		return v
	}

	protected Validator IS_POSITIVE_INT(String compId, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return IS_POSITIVE_INT(getComponent(compId), label, g)
	}

	protected Validator IS_POSITIVE_INT(Component comp, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		PositiveIntValidator v = new PositiveIntValidator(comp, label)
		addValidator(v, g)
		return v
	}

	protected Validator IS_DECIMAL(String compId, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return IS_DECIMAL(getComponent(compId), label, g)
	}

	protected Validator IS_DECIMAL(Component comp, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		DecimalValidator v = new DecimalValidator(comp, label)
		addValidator(v, g)
		return v
	}

	protected Validator IS_POSITIVE_DECIMAL(String compId, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return IS_POSITIVE_DECIMAL(getComponent(compId), label, g)
	}

	protected Validator IS_POSITIVE_DECIMAL(Component comp, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		PositiveDecimalValidator v = new PositiveDecimalValidator(comp, label)
		addValidator(v, g)
		return v
	}

	protected Validator RANGE(String compId, String label, int minValue, int maxValue, ValidatorGroup g=getDefaultValidatorGroup()) {
		return RANGE(getComponent(compId), label, minValue, maxValue, g)
	}

	protected Validator RANGE(Component comp, String label, int minValue, int maxValue, ValidatorGroup g=getDefaultValidatorGroup()) {
		RangeValidator v = new RangeValidator(comp, label, minValue, maxValue)
		addValidator(v, g)
		return v
	}

	protected Validator RANGE(String compId, String label, BigDecimal minValue, BigDecimal maxValue, ValidatorGroup g=getDefaultValidatorGroup()) {
		return RANGE(getComponent(compId), label, minValue, maxValue, g)
	}

	protected Validator RANGE(Component comp, String label, BigDecimal minValue, BigDecimal maxValue, ValidatorGroup g=getDefaultValidatorGroup()) {
		RangeValidator v = new RangeValidator(comp, label, minValue, maxValue)
		addValidator(v, g)
		return v
	}

	protected Validator IS_DATE(String compId, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return IS_DATE(getComponent(compId), label, g)
	}

	protected Validator IS_DATE(Component comp, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		DateValidator v = new DateValidator(comp, label)
		addValidator(v, g)
		return v
	}

	protected Validator IS_YEARMONTH(String compId, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return IS_YEARMONTH(getComponent(compId), label, g)
	}

	protected Validator IS_YEARMONTH(Component comp, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		YearMonthValidator v = new YearMonthValidator(comp, label)
		addValidator(v, g)
		return v
	}

	protected Validator PATTERN(String compId, String label, String pattern, String errMsg, ValidatorGroup g=getDefaultValidatorGroup()) {
		return PATTERN(getComponent(compId), label, pattern, errMsg, g)
	}

	protected Validator PATTERN(Component comp, String label, String pattern, String errMsg, ValidatorGroup g=getDefaultValidatorGroup()) {
		RegexValidator v = new RegexValidator(comp, label, errMsg, pattern)
		addValidator(v, g)
		return v
	}

	// closure 为接受一个参数Validator的闭包，返回boolean
	protected Validator VALIDATE(String compId, String label, String errMsg, ValidatorGroup g=getDefaultValidatorGroup(), Closure closure) {
		return VALIDATE(getComponent(compId), label, errMsg, g, closure)
	}

	protected Validator VALIDATE(Component comp, String label, String errMsg, ValidatorGroup g=getDefaultValidatorGroup(), Closure closure) {
		ClosureValidator v = new ClosureValidator(comp, label, errMsg, closure)
		addValidator(v, g)
		return v
	}

	protected Validator VALIDATE(String compId, String label, String errMsg, ComponentValidator validator, ValidatorGroup g=getDefaultValidatorGroup()) {
		return VALIDATE(getComponent(compId), label, errMsg, validator, g)
	}

	protected Validator VALIDATE(Component comp, String label, String errMsg, ComponentValidator validator, ValidatorGroup g=getDefaultValidatorGroup()) {
		ClosureValidator v = new ClosureValidator(comp, label, errMsg, validator)
		addValidator(v, g)
		return v
	}

	protected void ENABLE_VALIDATORS(Component... comps) {
		enableValitators(true, comps)
	}

	protected void DISABLE_VALIDATORS(Component... comps) {
		enableValitators(false, comps)
	}

	protected void ENABLE_VALIDATORS(Validator... validators) {
		enableValitators(true, validators)
	}

	protected void DISABLE_VALIDATORS(Validator... validators) {
		enableValitators(false, validators)
	}

	protected void enableValitators(boolean enabled, Component... comps) {
		validatorGroups.each { ValidatorGroup g ->
			g.enableValitators(enabled, comps)
		}
	}

	protected void enableValitators(boolean enabled, Validator... validators) {
		validatorGroups.each { ValidatorGroup g ->
			g.enableValitators(enabled, validators)
		}
	}

	protected void addValidatorGroup(ValidatorGroup validatorGroup) {
		validatorGroups.add(validatorGroup)
	}

	protected void removeValidatorGroup(ValidatorGroup validatorGroup) {
		validatorGroups.remove(validatorGroup)
	}

	protected void enableValidatorGroup(boolean enabled, ValidatorGroup validatorGroup) {
		validatorGroups.find { v -> v = validatorGroup} ?.enable(enabled)
	}

	protected ValidatorGroup getDefaultValidatorGroup() {
		return validatorGroups.isEmpty()?null:validatorGroups[0]
	}

	protected ValidatorGroup addValidator(Validator validator, ValidatorGroup g=getDefaultValidatorGroup()) {
		return g.add(validator)
	}

	protected ValidatorGroup removeValidator(Validator validator) {
		return this.getDefaultValidatorGroup().remove(validator)
	}

	protected List<ValidatorGroup> getValiatorGroups() {
		return validatorGroups
	}

	//	protected void validateData(String... compIds) {
	//		validateData(*(compIds.collect{getComponent(it)}))
	//	}
	//
	//	protected void validateData(Component... comps) {
	//		def falseValidators = []
	//
	//		validatorGroups.each { g ->
	//			falseValidators.addAll(g.validate(comps))
	//		}
	//
	//		def errMsgMap = [:]
	//	        falseValidators.unique().each { v ->
	//		    String msg = errMsgMap[v.comp]
	//		    errMsgMap[v.comp] = msg!=null?(msg+';'+v.errMsg):v.errMsg
	//		}
	//		if (!errMsgMap.isEmpty()) {
	//			List<WrongValueException> exs = errMsgMap.collect { comp, msg -> return new WrongValueException(comp, msg) }
	//			throw new WrongValuesException(exs.toArray(new WrongValueException[0]))
	//		}
	//	}

	protected void validateData(ValidatorGroup... groups) {
		def falseValidators = []

		(groups.length==0?validatorGroups:groups).each { g ->
			falseValidators.addAll(g.validate())
		}

		def errMsgMap = [:]
		falseValidators.unique().each { v ->
			String msg = errMsgMap[v.comp]
			errMsgMap[v.comp] = msg!=null?(msg+';'+v.errMsg):v.errMsg
		}
		if (!errMsgMap.isEmpty()) {
			List<WrongValueException> exs = errMsgMap.collect { comp, msg -> return new WrongValueException(comp, msg) }
			throw new WrongValuesException(exs.toArray(new WrongValueException[0]))
		}
	}

	boolean isInModal
	protected boolean canCreateSubWindow(){
		def fellows = baseWinFellows
		def subWins = fellows.findAll{fellow -> fellow instanceof BaseWindow}
		if(!subWins)
			return true

		def ret = true
		subWins.each{ Window win ->
			if(win.isInModal)
				ret = false
		}

		return ret
	}

	protected BaseWindow createWindow(String pagePath, Map argMap) {
		if(!canCreateSubWindow())
			return new NullWindow()
		return Executions.createComponents(pagePath, this, argMap)
	}

	protected BaseWindow createWindow(String pagePath, String parentComponentId,  Map argMap) {
		if(!canCreateSubWindow())
			return new NullWindow()
		return Executions.createComponents(pagePath, getComponent(componentId), argMap)
	}

	protected BaseWindow createWindow(String pagePath, Component parentComponent,  Map argMap) {
		if(!canCreateSubWindow())
			return new NullWindow()
		return Executions.createComponents(pagePath, parentComponent, argMap)
	}

	//====================================================================
	// MessageWindow
	// 显示消息提示窗，duration>0，表示若干毫秒后自动关闭，duration<=0不自动关闭
	// color 的警告值为 'salmon'
	//====================================================================

	//显示错误信息(包括警告)
	protected void showErrorMessage(String message) {
		showMessage(message, 'salmon')
	}
	protected void showMessage(String message, String color=null, int duration=0) {
		showMessage(null, message, color, duration)
	}

	protected void showMessage(String title, String message, String color, int duration=0) {
		showMessage([title:title, message:message, parent:this, color:color, duration:duration])
	}

	protected void showMessage(Map argMap) {
		if(messageWindow!=null)
			messageWindow.detach()
		messageWindow = Executions.createComponents('/message_window.zul', this, argMap)
		messageWindow.doPopup()
	}

	protected void detachMessageWindow(Window window) {
		window.detach()
		messageWindow = null
	}

	void showModal() {
		if(!canCreateSubWindow())
			return

		isInModal = true
		doModal()
	}

	// callback 为接受一个参数 ModalResult的闭包
	void showModal(Closure callback) {
		if(!canCreateSubWindow())
			return

		this.showModalCallback = callback
		isInModal = true
		doModal()
	}

	void showModal(ShowModalCallback callback) {
		showModal({ result -> callback.onClose(result) })
	}

	void close() {
		close(ModalResult.RESULT_CANCEL)
	}

	void close(ModalResult modalResult) {
		if (showModalCallback!=null)
			showModalCallback.call(modalResult)

		detach()
	}

	void onCancel() {
		if(closeOnEsc)
			Events.postEvent(Events.ON_CLOSE, this, null)
	}

	void onClose() {
		if(switchDataButtons && isDataDirty(currentData)){
			beforeSwitchData(currentData,true)
		}
		close()
	}

	protected saveAndClose() {
		Events.postEvent('onSaveAndClose', this, null)
	}

	public void onSaveAndClose(Event event) {
		Button button = [saveDataButton, saveAndNewDataButton].find{it!=null}
		if(button) {
			Events.sendEvent(new Event(Events.ON_CLICK, button))
		}
		close()
	}

	protected void addAllItem(Combobox combobox) {
		Comboitem item = combobox.appendItem(Utils.ALL_STR)
		item.value = Utils.ALL_ID_STR
	}

	protected void addNullItem(Combobox combobox) {
		Comboitem item = combobox.appendItem(Utils.NULL_STR)
		item.value = Utils.NULL_ID_STR
	}

	protected void addSelectItem(Combobox combobox) {
		Comboitem item = combobox.appendItem(Utils.NULL_SELECT_STR)
		item.value = Utils.NULL_ID_STR
	}

	// 模拟点击按钮
	protected void clickButton(String buttonId) {
		clickButton(getComponent(buttonId))
	}

	protected void clickButton(Button button) {
		if(button)
			Events.postEvent(Events.ON_CLICK, button, null)
	}

	//======================================================
	// Listbox相关
	//======================================================
	//取得鼠标点击ListItem中的按钮对应的行Index
	int getListItemClickIndex(ForwardEvent event){
		def button = event.origin.target

		def cell = button.parent
		Listitem item = cell.parent

		return item.index
	}

	Object getCurrentItem(Listbox listbox) {
		if(listbox.selectedIndex == -1)
			return null

		return listbox?.model?.getElementAt(listbox.selectedIndex)
	}

	//必须从下列选项中选择
	//getInitValue传入页面显示值,形式如{currentData.parent.unitName}
	//checkText:根据下拉框显示值获取数据库的值,返回布尔类型,形式如{ CheckExistData.oneUserByUserName(it)}
	protected Validator makeSelectValidator(def comp, String label, ValidatorGroup g=getDefaultValidatorGroup(), Closure getInitValue, Closure checkText) {
		return VALIDATE(comp, label, '必须从下列选项中选择', g) {
			def initValue = getInitValue.call()?:''//如果为null则视为combobox的value=''

			if(comp.value==initValue){
				return true
			}
			def showText=comp.value
			if(showText==''){
				return true
			}

			return checkText.call(showText)
		}
	}


	//起止时间的校验
	protected Validator makeStartEndTimeValidator(Datebox compStartDate,Datebox compEndDate, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return VALIDATE(compEndDate, label, '结束时间必须大于开始时间', g) {
			if(compStartDate.value==null || compEndDate.value==null ){
				return true
			}

			DateTime start = new DateTime(compStartDate.value)
			DateTime end = new DateTime(compEndDate.value)

			if(start<end){
				return true
			}

			return false
		}
	}


	//起止日期的校验
	protected Validator makeStartEndDateValidator(Datebox compStartDate,Datebox compEndDate, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return VALIDATE(compEndDate, label, '结束日期必须大于开始日期', g) {
			if(compStartDate.value==null || compEndDate.value==null ){
				return true
			}

			DateTime start = new DateTime(compStartDate.value)
			DateTime end = new DateTime(compEndDate.value)

			if(DateTimeUtils.isFromdateBeforeTodate(start,end)){
				return true
			}

			return false
		}
	}

	//起止日期的校验(结束日期必须不小于开始日期)
	protected Validator makeStartEndDateEQValidator(Datebox compStartDate,Datebox compEndDate, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return VALIDATE(compEndDate, label, '结束日期必须不小于开始日期', g) {
			if(compStartDate.value==null || compEndDate.value==null ){
				return true
			}

			DateTime start = new DateTime(compStartDate.value)
			DateTime end = new DateTime(compEndDate.value)

			if(DateTimeUtils.isFromdateBeforeEQTodate(start,end)){
				return true
			}

			return false
		}
	}

	//日期必须在指定日期之后
	protected Validator makeDateAfterDateValidator(Datebox compDate,Date startDate, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return VALIDATE(compDate, label, '必须在'+Utils.date2DateString(startDate)+'以后', g) {
			if(compDate.value==null ){
				return true
			}

			DateTime start = new DateTime(startDate)
			DateTime end = new DateTime(compDate.value)

			if(DateTimeUtils.isFromdateBeforeTodate(start,end)){
				return true
			}

			return false
		}
	}

	//日期必须在今天以前的校验
	protected Validator makeDateBeforeTodayValidator(Datebox compDate, String label, ValidatorGroup g=getDefaultValidatorGroup()) {
		return VALIDATE(compDate, label, '必须在今天以前', g) {
			if(compDate.value==null ){
				return true
			}

			DateTime start = new DateTime(compDate.value)
			DateTime end = DateTimeUtils.now

			if(DateTimeUtils.isFromdateBeforeTodate(start,end)){
				return true
			}

			return false
		}
	}

	//设置控件是否可见
	protected void setComponentsVisible(boolean flag,String... compIds) {
		setComponentsVisible(flag,compIds.collect{getComponent(it)})
	}

	protected void setComponentsVisible(boolean flag,Component... comps) {
		setComponentsVisible(flag,comps.asList())
	}

	protected void setComponentsVisible(boolean flag,List<Component> comps) {
		comps.each{comp ->
			comp.visible = flag
		}
	}

	//权限控制控件隐藏(如果没有权限，则隐藏控件)
	protected void privHideComponents(Priv priv,String... compIds) {
		privHideComponents(priv,compIds.collect{getComponent(it)})
	}

	protected void privHideComponents(Priv priv,Component... comps) {
		privHideComponents(priv,comps.asList())
	}
	protected void privHideComponents(Priv priv,List<Component> comps){
		if(!HR.me.hasPriv(priv)){
			setComponentsVisible(false,comps)
		}
	}

	//权限控制控件的异或显示(有权限显示控件1,没有权限则显示控件2)
	protected void privXORComponent(Priv priv,String comp1,String comp2){
		privXORComponent(priv,getComponent(comp1),getComponent(comp2))

	}
	protected void privXORComponent(Priv priv,Component comp1,Component comp2){
		privXORComponent(priv,[comp1],[comp2])
	}
	protected void privXORComponentByID(Priv priv,List<String> comps1,List<String> comps2){
		privXORComponent(priv,comps1.collect{getComponent(it)},comps2.collect{getComponent(it)})
	}
	//权限控制控件的异或显示(有权限显示控件列表1,没有权限则显示控件列表2)
	protected void privXORComponent(Priv priv,List<Component> comps1,List<Component> comps2){
		if(!HR.me.hasPriv(priv)){
			setComponentsVisible(false,comps1)
			setComponentsVisible(true,comps2)
		}
		else{
			setComponentsVisible(true,comps1)
			setComponentsVisible(false,comps2)
		}
	}

	//如果没有权限,则跳转到提示页面
	protected void privViewWindow(Priv priv){
		if(!HR.me.hasPriv(priv) ){
			Executions.current.sendRedirect('/no_viewpriv.zul')
		}
	}

	//如果没有权限，则不能下一步操作(抛出throw提示)
	protected void privCanGoOn(Priv priv){
		if(!HR.me.hasPriv(priv) ){
			throw new NoPrivException("没有权限")
		}
	}

	//权限控制控件不可编辑(包括子控件)
	protected void privDisableComponents(Priv priv,String... compIds) {
		privDisableComponents(priv,compIds.collect{getComponent(it)})
	}

	protected void privDisableComponents(Priv priv,Component... comps) {
		privDisableComponents(priv,comps.asList())
	}
	protected void privDisableComponents(Priv priv,List<Component> comps){
		if(!HR.me.hasPriv(priv)){
			comps.each{comp ->
				setComponentReadonly(comp)
				setChildComponentReadonly(comp)
			}
		}
	}

	/*
	 * 只要当前登录用户拥有其中任意一个权限就可以控制组件的显示属性
	 * privs 权限
	 * compIds 要控制的组件
	 * type	组件的属性
	 */
	protected void privAnyComponents(List<Priv> privs, List<String> compIds, String type) {
		List<Component> comps = compIds.collect{ getComponent(it) }

		if (type == 'visible') {
			comps*."${type}" = HR.me.hasAnyPriv(*privs)
		} else if (type == 'readonly' || type == 'disabled') {
			comps*."${type}" = !HR.me.hasAnyPriv(*privs)
		} else {
			throw new IllegalArgumentException("此方法暂时不支持属性$type,但是你可以添加!")
		}
	}

	/*
	 * 当前登录用户要拥有全部权限才可以控制组件的显示属性
	 * privs 权限
	 * compIds 要控制的组件
	 * type	组件的属性
	 */
	protected void privAllComponents(List<Priv> privs, List<String> compIds, String type) {
		List<Component> comps = compIds.collect{ getComponent(it) }

		if (type == 'visible') {
			comps*."${type}" = !(privs.any { !HR.me.hasPriv(it) })
		} else if (type == 'readonly' || type == 'disabled') {
			comps*."${type}" = privs.any { !HR.me.hasPriv(it) }
		} else {
			throw new IllegalArgumentException("此方法暂时不支持属性$type,但是你可以添加!")
		}
	}

	/*
	 * 当前登录用户要拥有全部权限才可以控制组件的显示属性
	 * privs 权限
	 * compIds 要控制的组件
	 * visible	是否显示
	 * uidList 要控制的人员列表
	 */
	protected void privAllComponentsWithDataPriv(List<Priv> privs, List<String> compIds, boolean visible ,def uidList) {
		List<Component> comps = compIds.collect{ getComponent(it) }
		if (visible==true)
			comps*.visible = !(privs.any { !HR.me.hasPriv(it) }) && uidList.contains(HR.me.id)
		else
			comps*.visible = privs.any { !HR.me.hasPriv(it) } || !uidList.contains(HR.me.id)
	}

	/*
	 * 当前登录用户要拥有全部权限才可以控制组件的显示属性
	 * privs 权限
	 * compIds 要控制的组件
	 * visible	是否显示
	 */
	protected void privAllComponents(List<Priv> privs, List<String> compIds, boolean visible){
		List<Component> comps = compIds.collect{ getComponent(it) }
		if (visible==true)
			comps*.visible = !(privs.any { !HR.me.hasPriv(it) })
		else
			comps*.visible = privs.any { !HR.me.hasPriv(it) }
	}

	//设置选中第一个可见的Tab
	protected void setSelectedTab0(String compId){
		getComponent(compId).setSelectedTab(
				getComponent(compId).tabs.find{it.visible==true}.children.find{it.visible==true}
				)

	}

	//取得控件的所有子控件,并加入到list中
	protected void getAllChild(Component comp,List<Component> list){
		comp.getChildren().each{
			list<<it
			getAllChild(it,list)
		}
	}

	protected List<Component> getAllChild(Component comp){
		List<Component> list = []
		getAllChild(comp,list)
		return list
	}


	//设置控件是否禁用
	protected void setComponentsForbid(boolean flag,String... compIds) {
		setComponentsForbid(flag,compIds.collect{getComponent(it)})
	}

	protected void setComponentsForbid(boolean flag,Component... comps) {
		setComponentsForbid(flag,comps.asList())
	}

	protected void setComponentsForbid(boolean flag,List<Component> comps) {
		comps.each{comp ->
			setComponentForbid(comp,flag)
		}
	}


	//设置控件的子控件是否为禁用(传入true代表禁用)
	protected void setChildComponentForbid(Component c,boolean flag=true){
		List<Component> comps =getAllChild(c)
		comps.each{comp ->
			setComponentForbid(comp,flag)
		}
	}

	//设置控件是否为禁用(传入true代表禁用)
	protected void setComponentForbid(Component comp,boolean flag=true){
		setComponentReadonly(comp,flag)
		setComponentDisabled(comp,flag)
		setButtonVisible(comp,!flag)
	}

	//设置控件的子控件是否为只读(传入true代表只读)
	protected void setChildComponentReadonly(Component c,boolean flag=true){
		List<Component> comps =getAllChild(c)
		comps.each{comp ->
			setComponentReadonly(comp,flag)
		}
	}

	//设置控件是否为只读(传入true代表只读)
	protected void setComponentReadonly(Component comp,boolean flag=true){
		switch(comp) {
			case Textbox:
			case Decimalbox:
			case Doublebox:
			case Intbox:
				comp.readonly = flag
				break
			case Datebox:
			case Bandbox:
			case Checkbox:
			case Combobox:
			case Radio:
				comp.disabled = flag
				break
			default:
				break
		}
	}


	//设置控件的子控件是否为不可用(传入true代表不可用)
	protected void setChildComponentDisabled(Component c,boolean flag=true){
		List<Component> comps =getAllChild(c)
		comps.each{comp ->
			setComponentDisabled(comp,flag)
		}
	}

	//设置控件是否为不可用(传入true代表不可用)
	protected void setComponentDisabled(Component comp,boolean flag=true){
		switch(comp) {
			case Datebox:
			case Combobox:
			case Checkbox:
			case Bandbox:
			case Radio:
				comp.disabled = flag
				break
			default:
				break
		}
	}


	//设置控件的子按钮是否为可见(传入true为可见)
	protected void setChildButtonVisible(Component c,boolean flag=true){
		List<Component> comps =getAllChild(c)
		comps.each{comp ->
			setButtonVisible(comp,flag)
		}
	}

	//设置按钮是否为是否为可见(传入true为可见)
	protected void setButtonVisible(Component comp,boolean flag=true){
		switch(comp) {
			case Toolbarbutton:
			case Button:
			case A:
				comp.visible = flag
				break
			default:
				break
		}
	}

	void searchResultMessage(def list){
		if (list.size() <= 0){
			showMessage('没有找到符合条件的记录,请更改查询条件!')
		} else {
			showMessage("查询出${list.size()}条记录!")
		}
	}

	void searchResultMessage(Listbox list) {
		searchResultMessage(list.model)

		list.setActivePage(0)
	}


	/*
	 * 设置分页控件(通常在prepareData设置)--此方法会加载第一页数据
	 * pg 分页控件
	 * pg_PAGESIZE 每页显示条数
	 * pgContentParent每个重复单元的父控件
	 * getAllList取得所有页数据的方法
	 * srcPath 每个重复单元对应的Include控件zul地址
	 */
	void SET_PAGING(Paging pg,int pg_PAGESIZE,Component pgContentParent,Closure getAllList,String srcPath){
		pg.pageSize = pg_PAGESIZE;
		RESET_PAGING(pg,pgContentParent,getAllList,srcPath)
		pg.addEventListener("onPaging", new EventListener() {
					public void onEvent(Event event) {
						PagingEvent pe = (PagingEvent) event;
						int pgno = pe.getActivePage();
						int ofs = pgno * pg_PAGESIZE;

						reDrawPage(ofs, pg_PAGESIZE,pgContentParent,getAllList,srcPath);
					}
				});
	}

	/*
	 * 设置分页控件(通常在prepareData设置)--此方法不加载第一页数据
	 * pg 分页控件
	 * pg_PAGESIZE 每页显示条数
	 * pgContentParent每个重复单元的父控件
	 * getAllList取得所有页数据的方法
	 * srcPath 每个重复单元对应的Include控件zul地址
	 */
	void SET_PAGING_NOT_LOAD(Paging pg,int pg_PAGESIZE,Component pgContentParent,Closure getAllList,String srcPath){
		pg.pageSize = pg_PAGESIZE;
		pg.addEventListener("onPaging", new EventListener() {
					public void onEvent(Event event) {
						PagingEvent pe = (PagingEvent) event;
						int pgno = pe.getActivePage();
						int ofs = pgno * pg_PAGESIZE;

						reDrawPage(ofs, pg_PAGESIZE,pgContentParent,getAllList,srcPath);
					}
				});
	}

	void RESET_PAGING(Paging pg,Component pgContentParent,Closure getAllList,String srcPath){
		pg.setTotalSize(getAllList.call().size());   //设置pging组件的总记录数；
		pg.setActivePage(0)
		reDrawPage(0,pg.pageSize,pgContentParent,getAllList,srcPath)
	}
	/*
	 * 重绘分页的具体内容
	 * firstResult本页的开始记录
	 * maxResults每页的显示条数
	 * pgContentParent每个重复单元的父控件
	 * getAllList取得所有页数据的方法
	 * srcPath 每个重复单元对应的Include控件zul地址
	 */
	private void reDrawPage(int firstResult, int maxResults,Component pgContentParent,Closure getAllList,String srcPath) {
		def list = getAllList.call()
		//当前页的数据
		def listPage = []

		for(int i=firstResult;i<list.size() && i<firstResult+maxResults;i++){
			listPage<< list[i]
		}

		pgContentParent.getChildren().clear();

		Include inc = new Include();
		inc.setDynamicProperty('listPage',listPage)
		inc.src = srcPath+'?'+new Date()
		pgContentParent.appendChild(inc)
	}


	//绘制列表
	void DrawList(Component listParent,Map mapPara,String srcPath) {
		listParent.getChildren().clear();

		Include inc = new Include();
		mapPara.each{
			inc.setDynamicProperty(it.key,it.value)
		}
		inc.setDynamicProperty('parentWindow',this)
		inc.src = srcPath+'?currenttime='+Utils.date2String(new Date(), "MMddHHmmss")

		listParent.appendChild(inc)

	}

	//绘制列表
	void DrawList(Component listParent,List<Object> listPage,String srcPath) {
		Map mapPara = [:]
		mapPara['listPage'] = listPage

		DrawList(listParent,mapPara,srcPath)
	}

	//================================================================
	// 重绘子列表
	// 用于在子列表中,调用父窗口的这个方法来实现页面子列表重绘
	//================================================================
	void reDrawIncludeList() {

	}

	//
	//
	void setQueryDateBoxValue(Datebox fromDtbox,Datebox endDtbox,int field = Calendar.DATE,int value = -7){
		Calendar calendar = Calendar.getInstance()
		Date now = new Date()
		calendar.setTime(now)
		calendar.add(field,value)
		fromDtbox.value = calendar.getTime()
		endDtbox.value = now
	}

	//================================================================
	// 以下常用于页面直接显示
	//================================================================
	//本年份
	String getYearOfPreMonth(){
		return DateTimeUtils.getNow().plusMonths(-1).year().get()
	}

	//本年份
	String getYearOfCurrentMonth(){
		return DateTimeUtils.getNow().year().get()
	}

	//前一月份
	String getPrePreMonth(){
		return DateTimeUtils.getNow().plusMonths(-2).monthOfYear().get()
	}

	//上月份
	String getPreMonth(){
		return DateTimeUtils.getNow().plusMonths(-1).monthOfYear().get()
	}

	//本月份
	String getCurrentMonth(){
		return DateTimeUtils.getNow().monthOfYear().get()
	}

	//前月统计区间开始日期
	Date getPrePreMonthStartDate(){
		return DateTimeUtils.getFirstDayOfMonth(DateTimeUtils.getNow().plusMonths(-2)).toDate()
	}
	//前月结束日期
	Date getPrePreMonthEndDate(){
		return DateTimeUtils.getLastDayOfMonth(DateTimeUtils.getNow().plusMonths(-2)).toDate()
	}
	//上月统计区间开始日期
	Date getPreMonthStartDate(){
		return DateTimeUtils.getFirstDayOfMonth(DateTimeUtils.getNow().plusMonths(-1)).toDate()
	}
	//上月结束日期
	Date getPreMonthEndDate(){
		return DateTimeUtils.getLastDayOfMonth(DateTimeUtils.getNow().plusMonths(-1)).toDate()
	}
	//本月统计区间开始日期
	Date getCurrentMonthStartDate(){
		return DateTimeUtils.getFirstDayOfMonth(DateTimeUtils.getNow()).toDate()
	}
	//本月结束日期
	Date getCurrentMonthEndDate(){
		return DateTimeUtils.getLastDayOfMonth(DateTimeUtils.getNow()).toDate()
	}

	//上季度的开始月份
	String getPreQuarterStartMonth(){
		return DateTimeUtils.getPreQuarterStartMonth()
	}
	//上季度的结束月份
	String getPreQuarterEndMonth(){
		return DateTimeUtils.getPreQuarterEndMonth()
	}
	//本季度的开始月份
	String getCurrentQuarterStartMonth(){
		return DateTimeUtils.getQuarterStartMonth()
	}
	//本季度的结束月份
	String getCurrentQuarterEndMonth(){
		return DateTimeUtils.getQuarterEndMonth()
	}

	//上一季度区间开始日期
	Date getPreQuarterStartDate(){
		return DateTimeUtils.getStartDateOfPreQuarter().toDate()
	}
	//上一季度区间结束日期
	Date getPreQuarterEndDate(){
		return DateTimeUtils.getEndDateOfPreQuarter().toDate()
	}

	//本季度区间开始日期
	Date getCurrentQuarterStartDate(){
		return DateTimeUtils.getStartDateOfQuarter().toDate()
	}
	//本季度区间结束日期
	Date getCurrentQuarterEndDate(){
		return DateTimeUtils.getEndDateOfQuarter().toDate()
	}

	//本年度区间开始日期
	Date getCurrentYearStartDate(){
		return DateTimeUtils.get(DateTimeUtils.now.year().get(),1,1).toDate()
	}
	//本年度区间结束日期
	Date getCurrentYearEndDate(){
		return DateTimeUtils.get(DateTimeUtils.now.year().get(),12,31).toDate()
	}


	/***
	 * 设置年月Combobox控件且绑定数据
	 * @param yearBox 
	 * @param monthBox
	 * @param yearDiff 年份的相差数，如3 则表示下拉框里(2008,2009,2010,2011)
	 * @param isAddAll
	 */
	void bindDateCombobox(Combobox yearBox,Combobox monthBox,int yearDiff,boolean isAddAll = false){
		if(yearBox){
			Calendar calendar = Calendar.getInstance()
			int nowYear = calendar.get(Calendar.YEAR)
			int preYear = nowYear - yearDiff
			def yearList = []

			while(preYear <= nowYear){
				yearList << nowYear
				nowYear --
			}

			def list = yearList.collect{[val:it, desc:"${it}年"]}
			if(isAddAll){
				addAllItem(yearBox)
				lookup(yearBox, list, 'desc', 'val')
			}else{
				lookup(yearBox, list, 'desc', 'val')
			}
		}

		if(monthBox){
			def monthList = []
			[1,2,3,4,5,6,7,8,9,10,11,12].each{
				monthList << it
			}
			def list = monthList.collect{[val:it, desc:"${it}月"]}
			if(isAddAll){
				addAllItem(monthBox)
				lookup(monthBox, list, 'desc', 'val')
			}else{
				lookup(monthBox, list, 'desc', 'val')
			}
		}

	}

	void bindQuarterCombobox(Combobox cbbQuarter,boolean isAddAll = false){
		if(cbbQuarter){
			def quarterList = [];
			quarterList << [val:'第一季度',desc:'第一季度']
			quarterList << [val:'第二季度',desc:'第二季度']
			quarterList << [val:'第三季度',desc:'第三季度']
			quarterList << [val:'第四季度',desc:'第四季度']
			if(isAddAll){
				addAllItem(cbbQuarter)
				lookup(cbbQuarter, quarterList, 'desc', 'val')
			}else{
				lookup(cbbQuarter, quarterList, 'desc', 'val')
			}
		}
	}

	protected void messageBoxTips(String labelTips, Closure doAfterYes) {
		def listener = doAfterYes as org.zkoss.zk.ui.event.EventListener
		Messagebox.show(labelTips, "提示", Messagebox.OK, Messagebox.NONE, listener)
	}
}
