package ui.component






import innerweb.WEB;
import innerweb.fund.WebFundActive;
import innerweb.fund.WebFundActiveType;
import innerweb.info.WebArticle;
import innerweb.info.WebArticleReply;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Response;

import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.impl.api.InputElement;
import ui.component.BaseWindow
import ui.component.combobox.AutocompleteMaker;
import util.condition.Condition;
import util.condition.C;

import org.zkoss.zk.ui.Component 
import ui.component.query.ComponentCondition;
import org.zkoss.zk.ui.Executions
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox 
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox
import org.zkoss.zul.Listbox
import org.zkoss.zul.Window;
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;

import platform.code.CodeItem;
import platform.code.CodeType
import platform.code.Codes;
import flow.FlowStepStatus;
import house.prop.Property;
import hr.HR
import hr.unit.Unit;
import hr.user.UserBaseManager;
import hr.user.User
import hr.team.Team
import hr.team.TeamManager

//通用的作为forEach显示列表的Window
class IncListPageWindow extends BaseWindow  implements AfterCompose{	
	
	List<Object> listPage
	Window parentWindow
	
	//初始化
	protected void prepareData() {
		setListPage()
		
		//父窗口的实例
		parentWindow = Executions.getCurrent().getAttribute("parentWindow")

	}

	void setListPage(){
		if(listPage==null){
			listPage = Executions.getCurrent().getAttribute("listPage")
		}		
	}

	List<Object> getListData(){	
		setListPage()

		return listPage
	}
		
}
