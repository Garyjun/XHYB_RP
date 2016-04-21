//setting
var layoutSetting = {
	north__size : 65,
	west__size  : 360,
	east__size  : 360,
	south__size : 40,
	north__spacing_open: 0,
	south__spacing_open: 0,
	west__spacing_open: 4,
	east__spacing_open: 4,
	enableCursorHotkey:true,//开启快捷键设置
	customHotkeyModifier:"ctrl",//开启快捷组合键（上下左右）  此处只能为 ctrl shift不能为alt（此处无视大小写）
	stateManagement__enabled : true,
	west__childOptions : {
		minSize : 50,
		south__size : 200
	},
	east__childOptions : {
		minSize : 50,
		south__size : 200
	},
	onresize:onresize
};
function onresize(){
	setLiWidth();
}