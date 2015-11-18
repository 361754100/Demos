/**
 * 本js函数库依赖于StringUtil.js函数库，请先引入StringUtil.js
 */
/**
 * 例子 : select_setSelected("BASETYPE",{value:2,innerHTML:"固定摄像枪"});
 * "BASETYPE"是一个select的id值,
 */
function select_setSelected(id, obj )
{
	var oSelect = document.getElementById(id);
	
	if(oSelect!=null){
		for(var i=0;i<oSelect.options.length;i++)
		{
			var isSelected = true;
			var pName;
			for(pName in obj)
			{
				if(oSelect.options[i][pName]!=obj[pName])
				{
					isSelected = false;
				}
			}
			if(isSelected==true)
			{
				oSelect.options[i].selected = true;
				break;//
			}
		}
	}
	else{
		alert("domUtil.js出错,不存在id为'"+id+"',对象");
	}
}


//根据index设置下拉列表
function select_setSelectedByIndex(id, index) {
	var oSelect = document.getElementById(id);
	if(oSelect!=null){
		for(var i=0;i<oSelect.options.length;i++) {
			if (i == index) {
				//alert("id=" + id + ",i=" + i + ";index=" + index);
				oSelect.options[i].selected = true;
				break;
			}
		}
	}
	else{
		alert("domUtil.js出错,不存在id为'"+id+"',对象");
	}
}

/*
* 例子 : select_indexOf("BASETYPE",{value:2,innerHTML:"固定摄像枪"});
* "BASETYPE"是一个select的id值,
* 返回各个属性和obj的各个属性一样的option的下标
*/
function select_indexOf(id, obj){
	var oSelect = document.getElementById(id);
	
	if(oSelect!=null){
		for(var i=0;i<oSelect.options.length;i++)
		{
			var isExist = true;
			var pName;
			for(pName in obj)
			{
				if(oSelect.options[i][pName]!=obj[pName])
				{
					isExist = false;
				}
			}
			if(isExist==true)
			{
				return i;
			}
		}
		return -1;
	}
	else{
		alert("domUtil.js出错,不存在id为'"+id+"',对象");
	}
}


/**
 * 传进来一个form对象,将这个form对象的元素转成一个bean
 */
function formToBean(form, ignoreButton){
	if( ignoreButton==null || ignoreButton==true) {
		ignoreButton = true;
	} else {
		ignoreButton = false;
	}
	var object = {};
	var elements = form.elements;
	for (var i = 0; i < elements.length; i++ ) {
		var element = elements[i];
		switch (element.type) {
		case "radio" : 
			if (!object[element.name]) {object[element.name] = new Array()};
			if (element.checked) {object[element.name].push(element.value);}
			break;
		case "checkbox" : 
			if (!object[element.name]) {object[element.name] = new Array()};
			if (element.checked) {object[element.name].push(element.value);}
			break;
		case "select-one" : 
			var value = '', opt, index = element.selectedIndex;
			if (index >= 0) {
				opt = element.options[index];
				value = opt.value;
				if (!value && !('value' in opt)) value = opt.text;
			}
			object[element.name] = value;
			break;
		case "select-multiple" :
			if (!object[element.name]) {object[element.name] = new Array()};
			for (var j = 0; j < element.options.length; j++) {
				var opt = element.options[j];
				if (opt.selected) {
					var optValue = opt.value;
					if (!optValue && !('value' in opt)) optValue = opt.text;
					object[element.name].push(optValue);
				}
		    }
		    break;
		default : 
			if (ignoreButton) {
				if (element.type!="submit" && element.type!="button" && element.type!="reset") {
					object[element.name] = element.value;
				}
			} else {
				object[element.name] = element.value;
			}
			break;
		}
	}
	return object;
}
/**
 * 将一个select的内容串起来
 * id：	select的id
 * arr： select当中要串的内容
 * str:	串起来时使用的分隔符
 * 
 * 例子：var rtStr = stringTogether("ciVideoID",["value","innerHTML"],",")
 */
function stringTogether(id,arr,str){
	var oSelect = document.getElementById(id);
	var rtStr = "";
	if(oSelect!=null && oSelect.tagName=="SELECT"){
		for(var i=0;i<oSelect.length;i++){
			for(var j=0;j<arr.length;j++){
				var s = ""+oSelect.options[i][arr[j]];
				if(s=="undefined")
					s = "";
				rtStr += s + str;
			}
		}
	}else{
		alert("不存在id为'"+id+"'的select对象");
		return "";
	}
	return rtStr;
}
/*
 * 
 */
function joinAttr(arr, p, sep){
	var tempArr = [];
	for( var i=0; i<arr.length; i++ ){
		var obj = arr[i];
		tempArr.push(obj[p]);
	}
	if( sep==null ){
		sep = ",";
	}
	return tempArr.join(sep)
}
/**
 * 当checkFun()函数返回true的时候,就执行doFun函数,若为false,过100ms再试,死循环直到成功,
 */
function whenTrueDo( checkFun, doFun, interval ){
	if( interval==null ){
		interval = 100;
	}
	if(checkFun()){
		doFun();
	}else{
		window.setTimeout(function(){
			whenTrueDo( checkFun, doFun );
		},interval);
	}
}

/*
* 清除tbody里面的所有tr
* pTbody: 是一个getElementById以后的对象
*/
function clearTbody(pTbody){
	if( pTbody != null ){
		for( var i=pTbody.childNodes.length-1; i>=0; i-- ){
			pTbody.removeChild(pTbody.childNodes[i]);
		}
	}
}

/*
* 给select加上title
* 参数pObj是能是一个select对象，不然需要第2个参数
*　例子：showTitle(this)　或　showTitle(this, this.value)
*/
function showTitle(pObj, pContent){
	var div = null;
	if(pObj.titleDiv!=null){
		var text = "";
		if(pContent!=null){
			text = pContent;
		}else{
			try{
				text = pObj.options[pObj.selectedIndex].innerHTML
			}catch(e){
				text = "无法获取内容";
			}
		}
		if(text==""){
			text = "&nbsp;"
		}
		div = pObj.titleDiv;
		div.style.display = "";
		document.getElementById("contentTd_"+div.id).innerHTML = text;
		var table = document.getElementById("contentTable_"+div.id);
		var iframe = document.getElementById("contentIframe_"+div.id);
		iframe.style.height = table.offsetHeight;
	}else{
		var text = "";
		if(pContent!=null){
			text = pContent;
		}else{
			try{
				text = pObj.options[pObj.selectedIndex].innerHTML
			}catch(e){
				text = "无法获取内容";
			}
		}
		if(text==""){
			text = "&nbsp;"
		}
		var id = "title_"+new Date().getTime();
		var html = ""
		+ "<div id='"+id+"' style='margin:0px; position:absolute; background-color:#FFFFE1; z-index:10000000;' onmouseover='this.style.display=\"none\"'>"
			+ "<table id='contentTable_"+id+"' border='0' cellpadding='0' cellspacing='0' style=' margin:0px;border:1px solid black;padding:3 10 3 10;'>"
				+ "<tr>"
					+ "<td id='contentTd_"+id+"' style='white-space:nowrap;'>"
						+ text 
					+ "</td>"
				+ "</tr>"
			+ "</table>"
			+ "<iframe id='contentIframe_"+id+"' src=\"javascript:false;\" style=\"position:absolute; visibility:inherit; top:0px; left:0px; width:100%; height:20px; z-index:-1; filter='alpha(style=0,opacity=0)';\"></iframe>"
		+ "</div>"
		;
		document.body.insertAdjacentHTML("beforeEnd",html);
		div = document.getElementById(id);
		var table = document.getElementById("contentTable_"+id);
		var iframe = document.getElementById("contentIframe_"+id);
		iframe.style.height = table.offsetHeight;
		pObj.titleDiv = div;
	}
	var x = window.event.clientX + document.body.scrollLeft;
	var y = window.event.clientY + document.body.scrollTop;
	
	if(  x + div.offsetWidth > document.body.scrollLeft + document.body.clientWidth - 30 ){
		x = document.body.scrollLeft + document.body.clientWidth - 30 - div.offsetWidth;
	}
	if(  y + div.offsetHeight > document.body.scrollTop + document.body.clientHeight - 30 ){
		y = document.body.scrollTop + document.body.clientHeight - 30 - div.offsetHeight;
	}
	
	div.style.left = x + 10;
	div.style.top = y + 10;
}

/*
* 跟showTitle函数配套
*/
function clearTitle(pObj){
	var div = pObj.titleDiv;
	if(div!=null){
		div.style.display = "none";
	}
}

/**
 * 清除一个对象的所有属性
 */
function removeAllProperties(obj){
	for(var p in obj){
		delete(obj[p]);
	}
}

/**
 * 复制一个对象属性完成一样
 */
function cloneObject(obj){
	var newObj = {};
	for(var p in obj){
		newObj[p] = obj[p];
	}
	return newObj;
}

/**
 * 将一个对象的属性copy到另一个对象
 */
function copyTo( fromObj,toObj ){
	for(var p in fromObj){
		toObj[p] = fromObj[p];
	}
}

/**
 * 给checkbox打勾或者去掉打勾
 */
function setCheckboxChecked( cbxName, checked ){
	var arr = document.getElementsByName(cbxName);
	if( arr==null ){
		return ;
	}
	for( var i=0; i<arr.length; i++ ){
		var cbx = arr[i];
		cbx.checked = checked;
	}
}

/**
 * 返回一个同名的checkbox中的打了勾的checkbox
 */
function getCheckedBox( pName ){
	var chkboxArr = document.getElementsByName(pName);
	var result = [];
	if( chkboxArr!=null ){
		for( var i=0; i<chkboxArr.length; i++ ){
			var chkbox = chkboxArr[i];
			if( chkbox.checked==true ){
				result.push(chkbox);
			}
		}
	}
	return result;
}
/**
 * 返回一组radio中打了勾的那个radio的value
 */
function getRadioValue( radioName, valueColumn ){
	if( valueColumn==null ){
		valueColumn = "value";
	}
	var radios = document.getElementsByName(radioName);
	if( radios==null ){
		return null;
	}
	for( var i=0; i<radios.length; i++ ){
		if( radios[i].checked==true ){
			return radios[i][valueColumn];
		}
	}
	return null;
}
/**
 * 返回一组checkbox中打了勾的那个checkbox的value
 */
function getCheckboxValue( checkboxName, valueColumn ){
	if( checkboxName==null ){
		checkboxName = "value";
	}
	var checkboxs = document.getElementsByName(checkboxName);
	if( checkboxs==null ){
		return null;
	}
	var arr = [];
	for( var i=0; i<checkboxs.length; i++ ){
		if( checkboxs[i].checked==true ){
			arr.push( checkboxs[i][valueColumn] );
		}
	}
	return arr.join("");
}
/**
 * 获取一个对象的绝对位置
 * @return 返回pos = { left:0, top:0 };
 */
function getAbsolutePosition(obj){
	var pos = { left:0, top:0 };
	var op = obj;
	while( op.offsetParent!=null ){
		pos.left += op.offsetLeft;
		pos.top += op.offsetTop;
		op = op.offsetParent;
	}
	return pos;
}
 
/**
 * 获取一个iframe在父窗体中的下标
 * @return 返回-1表示没有找到这个父窗体
 */
function getFrameIndexInParentWindow(win){
	var pWin = win.parent;
	var frameIndex = -1;
	var frames = pWin.frames;
	for( var i=0; i<frames.length; i++ ){
		if( frames[i]==win ){
			frameIndex = i;
		}
	}
	return frameIndex;
}

/**
 * 返回一个对象的所有属性串起来的字符串
 */
function getAllProperties(obj){
	var arr = [];
	for( var p in obj ){
		arr.push( p+" : "+obj[p] );
	}
	return arr.join("\n");
}

/**
 * 返回一个对象的所有属性键串起来的字符串
 */
function getAllKeys(obj,connStr){
	var arr = [];
	for( var p in obj ){
		arr.push( p );
	}
	return arr.join( (connStr==null) ? "," : connStr );
}

/**
 * 获取异常对象的信息
 */
function getErrorMessage(e){
	if( e==null ){
		return "null";
	} else if( e instanceof Error ) {
		return e.message;
	} else if( typeof(e)=="string" ) {
		return e;
	} else {
		return getAllProperties(e);
	}
}

/**
 * 打印错误信息
 */
function alertError(e){
	alert(getErrorMessage(e));
}

/**
 * 
 */
function getReturnMessage(str){
	if( str.startsWith("fail:") ){
		return str.substring( "fail:".length );
	} else {
		return str;
	}
}

/**
 * 
 */
function alertReturnMessage(str){
	alert( getReturnMessage(str) );
}
/**
 * 选中文本中的一个小域，如"123456",只在里面选中"2345"
 * 使用 ：selectRange("id", 1, 5)
 */
function selectRange(id, start, end)
{
	var text = document.getElementById(id);
	var range = text.createTextRange();
    range.moveStart("character", start);
    range.moveEnd("character", end - text.value.length);
    range.select();
    text.focus();
}
/**
 * 获取光标所在的位置,包括开始和结束的位置
 * @param textBox: 一个DOM对象
 */
function getTextCursorPosition(textBox) {
	var start = 0;
	var end = 0;
	var range = document.selection.createRange();
	var range_all=document.body.createTextRange();
	range_all.moveToElementText(textBox);
	for(start=0; range_all.compareEndPoints("StartToStart",range)<0; start++){
		range_all.moveStart('character',1);
	}
	for(var i=0;i<=start;i++) {
		if(textBox.value.charAt(i)=='\n') {
			start++;
		}
	}
	var range_all=document.body.createTextRange();
	range_all.moveToElementText(textBox);
	for(end=0;range_all.compareEndPoints('StartToEnd',range)<0;end++){
		range_all.moveStart('character',1);
	}
	for(var i=0;i<=end;i++) {
		if(textBox.value.charAt(i)=='\n'){
			end++;
		}
	}
	return {"start":start, "end":end}
}

/**
 * 增加一个半透明的div，显示出等待信息
 */
function showWaitBackground(title){
 	var div = document.getElementById("waitBackground_div");
 	var titlediv = document.getElementById("waitBackground_title_div");
 	if( div==null ){
 		div = document.createElement("div");
 		div.id = "waitBackground_div";
 		div.style.cssText = "position:absolute; z-index:1000000; top:0; left:0; cursor:wait; background-color:black; width:100%; height:100%; filter:alpha(opacity=30);";
 		document.body.appendChild(div);
 		
 		titlediv = document.createElement("div");
 		titlediv.id = "waitBackground_title_div";
 		titlediv.style.cssText = "position:absolute; z-index:1000001; top:0; left:0; width:1px; height:200px; text-align:center;";
 		titlediv.innerHTML = ""
 		+ "<table border='0'>"
 			+ "<tr>"
 				+ "<td align='center'>"
 					+ "<img src='/afResource/i_mages/loading.gif' />"
 				+ "</td>"
 			+ "</tr>"
 			+ "<tr>"
 				+ "<td align='center' nowrap style='font-size:20px; color:green; font-weight:bold;'>"
 					+ title
 				+ "</td>"
 			+ "</tr>"
 		+ "</table>"
 		;
 		document.body.appendChild(titlediv);
 	}
 	div.style.width = div.offsetWidth + 40;
 	div.style.display = "";
 	titlediv.style.display = "";
 	
 	var top = document.body.scrollTop + document.body.scrollHeight/2 - titlediv.offsetHeight/2;
 	var left = document.body.scrollLeft + document.body.scrollWidth/2 - titlediv.offsetWidth/2;
 	titlediv.style.top = top;
 	titlediv.style.left = left;
}

var CaptureUtil = new function(){
	this._obj;
	this.setCapture = function(obj){
		this._obj = obj;
		this._obj.setCapture();
	}
	this.releaseCapture = function(){
		if( this._obj!=null ){
			this._obj.releaseCapture();
		}
		this._obj = null;
	}
}

/**
 * 删除半透明的div
 */
function hideWaitBackground(){
	var div = document.getElementById("waitBackground_div");
 	var titlediv = document.getElementById("waitBackground_title_div");
 	if( div!=null ){
 		document.body.removeChild(div);
 	}
 	if( titlediv!=null ){
 		document.body.removeChild(titlediv);
 	}
}

/**
 * 获取页面所在的应用名
 */
function getAppName(pWid){
	var win = (pWid!=null) ? pWid : window;
	var path = win.location.pathname;
	if( path.indexOf("/")==-1 ){
		return path;
	}else{
		if( path.startsWith("/") ){
			var idx = path.indexOf("/",1);
			return path.substring(1,9);
		}else{
			var idx = path.indexOf("/");
			return path.substring(0,9);
		}
	}
}

/**
 * 获取页面所在的上下文
 */
function getContextPath(pWid){
	return "/"+getAppName(pWid);
}

/**
 * 打开一个对话框，显示指定的按钮和内容，点击后返回按钮的value
 * 使用例子：new CommonDialog(["是","否","返回"],"确定要保存到数据库吗?").show()
 */
function CommonDialog( pButtonArr, pMsg, pFeature ){
	var buttonArr = pButtonArr;
	var msg = pMsg;
	var feature = (pFeature!=null) ? pFeature : "dialogWidth:260px;dialogHeight:141px;center:yes;status:no;resizable:yes;scroll:no;";
	//
	this.show = function(){
  		var path = getContextPath()+"/public/dialog.jsp";
  		var param = {};
  		param.buttonArr = buttonArr;
  		param.msg = msg;
  	    return window.showModalDialog(path,param,feature);
	}
}

/**
 * 将字符串写入一个文件里面
 */
function writeToFile(str,path,param){
	var fso = new ActiveXObject("Scripting.FileSystemObject");
	path = (path==null) ? "g:\\testfile.txt" : path;
	param = (param==null) ? true : param;
	var tf = fso.CreateTextFile(path, param);
	tf.WriteLine(str) ;
	tf.Close();
}

/**
 * 利用IE来检查xml是不是有错
 */
function checkXml(strXml){
	window.showModalDialog("/afManage/test/xml.jsp?ts="+new Date().getTime(), strXml);
}
/**
 * 判断输入的是不是数字, 方向键, 回退键, Del键
 */
function isPressNumberKey(keyCode){
	if( (event.keyCode>=48 && event.keyCode<=57) || 	// 数字键
		(event.keyCode>=96 && event.keyCode<=105) || 	// 小键盘数字键
		(event.keyCode>=35 && event.keyCode<=40) ||		// end, home 方向 
		event.keyCode==8 || 							// 回退键
		event.keyCode==46 ) {							// Del键
		return true;
	} else {
		return false;
	}
}
/**
 * 设置弹出窗口的大小，传入宽度和高度，将居中显示窗口。（qxb）
 */
function setDialogSize( width, height, isCenter){
	//窗口大小，单位px
	var setW=width;
	var setH=height;
	//设置窗口大小
	if( window.dialogWidth != setW+"px" ){
		window.dialogWidth = setW+"px";
	}
	if( window.dialogHeight != setH+"px" ){
		window.dialogHeight = setH+"px";
	}
	//以下代码让弹出窗口居中
	if( isCenter==true ){
		var w = 1024;
	    var h = 768;
	    if (document.all || document.layers)
	    {
			w = screen.availWidth;
			h = screen.availHeight;
	    }
	    var leftPos = (w/2-setW/2);
	    var topPos = (h/2.3-setH/2.3);
		window.dialogLeft = leftPos+"px";
		window.dialogTop = topPos+"px";
	}
}


//设置不同IE版本下窗口的高度
function resetDialogHeight(){  
	//if( document.body.scrollWidth > (window.screen.availWidth-100) ){     
     //   window.dialogWidth = (window.screen.availWidth-100).toString() + "px";     
    //}else{     
     //   window.dialogWidth = (document.body.scrollWidth + 50).toString() + "px";     
    //}         
        
    if( document.body.scrollHeight > (window.screen.availHeight-70) ){     
        window.dialogHeight = (window.screen.availHeight - 50).toString() + "px";     
    }else{     
        window.dialogHeight = (document.body.scrollHeight + 40).toString() + "px";     
    }         

    window.dialogLeft = ((window.screen.availWidth - document.body.clientWidth) / 2).toString() + "px";     
    window.dialogTop = ((window.screen.availHeight - document.body.clientHeight) / 2).toString() + "px"; 
}

//************lc************************

    function resetDialogHightSize(){          
    if( document.body.scrollHeight > (window.screen.availHeight-70) ){  
        window.dialogHeight = (window.screen.availHeight - 50).toString() + "px";     
    }else{     
        window.dialogHeight = (document.body.scrollHeight + 30).toString() + "px";      
    }  
 
      var vision=getIEVision();
    
    if(vision.indexOf("7")>-1){
          window.dialogHeight = (document.body.scrollHeight).toString() + "px";    
    }
    window.dialogLeft = ((window.screen.availWidth - document.body.clientWidth) / 2).toString() + "px";     
    window.dialogTop = ((window.screen.availHeight - document.body.clientHeight) / 2).toString() + "px"; 
}

function appInfo(){
     var browserVision="";
    var browser = {
            msie: false, firefox: false, opera: false, safari: false, 
            chrome: false, netscape: false, appname: 'unknown', version: 0
        },
        userAgent = window.navigator.userAgent.toLowerCase();
    if ( /(msie|firefox|opera|chrome|netscape)\D+(\d[\d.]*)/.test( userAgent ) ){
        browser[RegExp.$1] = true;
        browser.appname = RegExp.$1;
        browser.version = RegExp.$2;
    } else if ( /version\D+(\d[\d.]*).*safari/.test( userAgent ) ){ // safari
        browser.safari = true;
        browser.appname = 'safari';
        browser.version = RegExp.$2;
    }
    return browser;
}


function getIEVision(){
  var vision="";
var myos = appInfo();
if (myos.msie){
    vision=myos.version;
} else {
    vision=myos.appname+myos.version;
}

 return vision;
}

//************lc*********************


/**
 * 自定义事件流机制
 */
var isCancelBubble = false;	// 是否事件冒泡

function onclickEventFlowHandler(){
	if( isCancelBubble==true ){		// 阻止事件冒泡
		isCancelBubble = false		// 恢复为默认值
		return ;
	}
	if( window.parent==null ){
		return ;
	}
	window.parent.document.body.click();
}
/**
 * 打日志用的工具类
 */
function LoggerUtilClass(pContainer){
	var self = this;
	this.enable = true;				// 如果不使用日志工具类的话,就将这个值设为false就行了
	this.id = ("var"+new Date().getTime()+Math.random()).replace(".","");
	this.dom = null;
	this.container = pContainer;	// 一个window对象,
	this.width = 500;
	this.height = 400;
	//
	this._hideBtn_ = null;
	this._smallBtn_ = null;
	this._context_ = null;
	this._contentTr_ = null;
	this._titleTd_ = null;
	//
	this._init_ = function(){
		if( this.enable==false ){
			return;
		}
		this.dom = this.container.document.createElement("<div>");
		this.dom.style.cssText = "position:absolute; left:10; top:10; width:"+this.width+"px; height:"+this.height+"px; z-index:99999999;";
		var html = [];
		html.push(	"<table width='100%' height='100%' border='0' cellpadding='0' cellspacing='0' style='border:2px solid #555555;'>" );
		html.push( 		"<tr id='titleTr_"+this.id+"'>" );
		html.push( 			"<td id='titleTd_"+this.id+"' align='right' style='height:20px; background-color:#956A33; text-align:right;'>" );
		html.push( 				"<span style='float:left; color:white;'>&nbsp;日志窗口</span>" );
		html.push( 				"<a id='small_"+this.id+"' href='#' style=' color:white; font-family:webdings;'>2</a>&nbsp;" );
		html.push( 				"<a id='hide_"+this.id+"' href='#' style=' color:white; font-family:webdings;'>r</a>&nbsp;" );
		html.push( 			"</td>" );
		html.push( 		"</tr>" );
		html.push( 		"<tr id='contentTr_"+this.id+"'>" );
		html.push( 			"<td><textarea id='logDiv_"+this.id+"' style='width:100%; height:100%; border:0px; background-color:#aaaaaa;'></textarea></td>" );
		html.push( 		"</tr>" );
		html.push(	"</table>" );
		html.push(	"<iframe src='' style='position:absolute; visibility:inherit; top:0px; left:0px; width:100%; height:100%; z-index:-1;'></iframe>" );
		this.dom.innerHTML = html.join("");
		this.container.document.body.appendChild(this.dom);
		//
		this._smallBtn_ = this.container.document.getElementById("small_"+this.id);
		this._hideBtn_ = this.container.document.getElementById("hide_"+this.id);
		this._titleTd_ = this.container.document.getElementById("titleTd_"+this.id);
		this._contentTr_ = this.container.document.getElementById("contentTr_"+this.id);
		this._context_ = this.container.document.getElementById("logDiv_"+this.id);
		//
		this._smallBtn_.attachEvent( "onclick", function(){self._small_();} );
		this._hideBtn_.attachEvent( "onclick", function(){self._hide_();} );
		//
		this._setMoveFn_();
	}
	//
	this.info = function(str){
		if( this.enable==false ){
			return;
		}
		this.show();
		this._context_.value += "["+dateToString(new Date())+"] "+str+"\n";
	}
	//
	this._small_ = function(){
		if( this._contentTr_.style.display=="" ){
			this._contentTr_.style.display = "none";
			this.dom.style.height = "1px";
		} else {
			this._contentTr_.style.display = "";
			this.dom.style.height = this.height;
		}
	}
	//
	this._hide_ = function(){
		this.dom.style.display = "none";
	}
	//
	this.show = function(){
		if( this.enable==false ){
			return;
		}
		this.dom.style.display = "";
	}
	//
	this._setMoveFn_ = function(){
		this._titleTd_.attachEvent("onmousedown",function () {
			self._moveMouseDown_(self._titleTd_);
		});
		this._titleTd_.attachEvent("onmouseup",function () {
			self._moveMouseUp_(self._titleTd_);
		});
		this._titleTd_.attachEvent("onmousemove",function(){
			self._moveMouseMove_();
		});
	}
	//
	this.moving;	
	this.ex;
	this.ey;
	this.l;
	this.t;
	//
	this._moveMouseDown_ = function(pObj){
		if( this.container.event.button==1 ) {
			this.moving = true;
			pObj.setCapture();
			this.ex = this.container.event.screenX;
			this.ey = this.container.event.screenY;
			this.l = this.dom.offsetLeft;
			this.t = this.dom.offsetTop;
		}
	}
	//
	this._moveMouseUp_ = function(pObj){
		if( this.container.event.button==1 && this.moving==true ) {
			this.moving=false;
			pObj.releaseCapture();
		}
	}
	//
	this._moveMouseMove_ = function(){
		if( this.moving ) {
			var dx = this.container.event.screenX - this.ex;
			var dy = this.container.event.screenY - this.ey;
			if( this.t + dy < 0 ){
				this.dom.style.top = 0
			} else {
				this.dom.style.top = this.t + dy
			}
			this.dom.style.left = this.l + dx
		}
	}
	//
	this.dispose = function(){
		if( this.dom!=null ){
			this.dom.parentElement.removeChild(this.dom);
		}
	}
	//
	this._init_();
}

















