package com.brainsoon.common.util.dofile.service.pc;


/**
 * 
 * @ClassName: Product 
 * @Description: 
 * Product：模拟生产的产品。现在这个类比较简单，只有一个属性ID编号，就是每次生产出来一个产品的时候，给它依次编个号。之所以写这个类来模拟生产的产品，而不是简单地推入数据，是因为以后可以方便地扩展该类，潜在的实用性还是很大的。 
 * @author tanghui 
 * @date 2014-5-26 下午2:42:46 
 *
 */
public class Product {
	public static int totalID=0;
	private int ID;
	
	public Product(int ID){
		this.ID=ID;
	}
	
	public int getID(){
		return ID;
	}
}
