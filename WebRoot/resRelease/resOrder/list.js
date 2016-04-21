/**
  * js实现list
  *	@author xiehw  
  * @date 2015-04-15 
  */
function List() {
    this.value = [];
    /* 添加 */
    this.add = function(obj) {
		return this.value.push(obj);
    };

    /* 大小 */
    this.size = function() {
		return this.value.length;
    };

    /* 返回指定索引的值 */
    this.get = function(index) {
		return this.value[index];
    };

    /* 删除指定索引的值 */
    this.remove = function(index) {
		this.value.splice(index,1);
		return this.value;
    };

    /* 删除全部值 */
    this.removeAll = function() {
		return this.value = [];
    };

    /* 是否包含某个对象 */
    this.contains = function(obj) {
		for ( var i in this.value) {
			if (obj == this.value[i]) {
				return true;
			} else {
				continue;
			}
		}
		return false;
    };

    /* 是否包含某个对象 */
    this.getAll = function() {
		var allInfos = '';
		for ( var i in this.value) {
			if(i != (value.length-1)){
				allInfos += this.value[i]+",";
			}else{
				allInfos += this.value[i];
			}
		}
		alert(allInfos);
		return allInfos += this.value[i]+",";;
    };
    
    /*  重写toString  */  
    this.toString = function(){   
        var s = "{";   
        for(var i=0;i<this.size();i++,s+=','){   
            var k = this.get(i);   
            s += k;   
        } 
        if(s.length>1){
        	s = s.substring(0, s.length-1);
        }
        s+="}";   
        return s;   
    };  
    /*  获得两个list中相同的数据        */
    this.getEqualsValue = function(obj){
		var newValue = new List();
    	for(var i=0; i<this.size();i++){
    		var val = this.get(i);
    		for(var j=0;j<obj.size();j++){
    			if(obj.contains(val)){
    				newValue.add(val);
					break;
    			}
    		}
    	}
    	return newValue;
    };
    
    
    /*  静态方法：将字符串转化为list集合        */
    List.arrToList = function(arr, splitWord){
    	var list = new List();
    	var arrs = arr.split(splitWord);
    	for(var i=0;i<arrs.length;i++){
    		list.add(arrs[i]);
    	}
    	return list;
    };
}