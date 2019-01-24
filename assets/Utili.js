var xAxisTimes = ['00:00',
'00:15','00:30','00:45','01:00',
'01:15','01:30','01:45','02:00',
'02:15','02:30','02:45','03:00',
'03:15','03:30','03:45','04:00',
'04:15','04:30','04:45','05:00',
'05:15','05:30','05:45','06:00',
'06:15','06:30','06:45','07:00',
'07:15','07:30','07:45','08:00',
'08:15','08:30','08:45','09:00',
'09:15','09:30','09:45','10:00',
'10:15','10:30','10:45','11:00',
'11:15','11:30','11:45','12:00',
'12:15','12:30','12:45','13:00',
'13:15','13:30','13:45','14:00',
'14:15','14:30','14:45','15:00',
'15:15','15:30','15:45','16:00',
'16:15','16:30','16:45','17:00',
'17:15','17:30','17:45','18:00',
'18:15','18:30','18:45','19:00',
'19:15','19:30','19:45','20:00',
'20:15','20:30','20:45','21:00',
'21:15','21:30','21:45','22:00',
'22:15','22:30','22:45','23:00',
'23:15','23:30','23:45'];
var opt_date={
	preset:'date',
	theme:'jqm',
	mode:'clickpick',
	yearRange: "c-2:c+10",
	dateFormat:'yyy-mm-dd',
	setText:'确定',
	cancelText:'取消',
	dateOrder:'yyyymmdd',
	yearText:'年',monthText:'月',dayText:'日',
	monthNamesShort:['01','02','03','04','05','06','07','08','09','10','11','12'],
	showNow:true
};
template.defaults.imports.artSubstr = function(value){
    return value[0].substr(value[1],value[2]);
}

//显示加载器  
function showLoader() {  
  //显示加载器.for jQuery Mobile 1.2.0  
  $.mobile.loading('show', {  
      text: '加载中...', //加载器中显示的文字  
      textVisible: true, //是否显示文字  
      theme: 'a',        //加载器主题样式a-e  
      textonly: false,   //是否只显示文字  
      html: ""           //要显示的html内容，如图片等  
  });  
}  

//隐藏加载器.for jQuery Mobile 1.2.0  
function hideLoader()  
{  
  //隐藏加载器  
  $.mobile.loading('hide');  
} 


function initDateControll(){
	var todayDate = new Date();
			
	opt_date.dateFormat = 'yyy-mm-dd';
	opt_date.startYear = todayDate.getFullYear()-2;
	opt_date.endYear = todayDate.getFullYear()+2;
	$("#strDate").mobiscroll(opt_date);
	var month = todayDate.getMonth()+1;
	if(month<10)
		month="0"+month;
	var date = todayDate.getDate();
	if(date<10)
		date = "0"+date;
	$("#strDate").val(todayDate.getFullYear()+"-"+month+"-"+date);
}