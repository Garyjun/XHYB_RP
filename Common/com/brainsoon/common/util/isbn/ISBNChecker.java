package com.brainsoon.common.util.isbn;


/**
 * ISBN wraps a String interpreted as an ISBN. It checks the checksum, too. supports both 10 digit and 13 digit ISBN
 * @author Eric Hellman
 *
 *  Copyright 2000-2005  by Openly Informatics, Inc.
 *  http://www.openly.com/
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free
 *  Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA 02111-1307, USA
 *@version 
 *<ul>
 *<li>2005-10-25 checkDigit13 and checkDigit were getting it completely wrong! Added a constructor with a parameter that sets whether or not the class tries to fix the isbn.
 *</ul>
 *
 */
public class ISBNChecker implements StandardID {
	private static final String NUMVALUES = "0123456789X";

	private String value;
	private String value13;
	
	/**
	* the constructor requires a valid isbn string; dashes and spaces are ignored.
	*/
	public ISBNChecker(String isbn)
		throws InvalidStandardIDException{
		init (isbn,false);
		//ISBN.checkISBN(isbn);
	}
	/**
	* the constructor requires a valid isbn string; dashes and spaces are ignored.
	*@param fix if true, constructor will try to fix bad check digits, otherwise will throw exception for bad isbn
	*/
	public ISBNChecker(String isbn, boolean fix)
		throws InvalidStandardIDException{
		init (isbn,fix);
		//ISBN.checkISBN(isbn);
	}
	
	//校验ISBN前后是否有特殊字符
	public boolean checkIsbnSE(String isbn){
		boolean boo = true;
		String startStr = isbn.substring(0,1);
		String endStr = isbn.substring(isbn.length()-1,isbn.length());
		//第一步：校验首是否是数字
		if(NUMVALUES.indexOf(startStr) < 0){
			throw new InvalidStandardIDException("ISBN frist char is not a number");
		}
		//第二步：校验尾是否是数字
		if(NUMVALUES.indexOf(endStr) < 0){
			throw new InvalidStandardIDException("ISBN end char is not a number");
		}
//		//第三步：校验尾是否包含"-"
//		if(isbn.indexOf("-") < 0){
//			throw new InvalidStandardIDException("ISBN must contain '-'");
//		}
		
		//第三步：校验数字之前是否有特殊字符
//		char[] theChars = isbn.toCharArray();
//		int charNum = theChars.length; //总长度
//		int startNumIndex = 0; //数字开始的位置
//		int endNumIndex = charNum; //数字及X结束的位置
//		for (int i=0; i< charNum; i++){
//			
//			//第一步：校验首是否是数字
//			if(i==0){ 
//				if(NUMVALUE.indexOf(theChars[i]) < 0){//不是数字
//					throw new InvalidStandardIDException("ISBN Frist Char is not A Number");
//				}else{ //是数字
//					startNumIndex++;
//				}
//			}
//			
//			
//			//第二步：校验尾是否是数字及X
//			if(i == (charNum-1)){
//				if(NUMVALUES.indexOf(theChars[i]) < 0){//不是数字和X
//					throw new InvalidStandardIDException("ISBN End Char is not A Number Or X");
//				}else{//是数字和X
//					endNumIndex++;
//				}
//			}
//			
//			//第三步：校验尾是否是数字及X
////			if(){
////				
////			}
//			
//		}
		
		return boo;
	}
	
	private boolean init(String isbn, boolean fix)
		throws InvalidStandardIDException{ //	
		if (isbn.length()<10) throw new InvalidStandardIDException("ISBN length < 10");//小于10
		if (isbn.length()>17) throw new InvalidStandardIDException("ISBN length > 17");//大于17
		checkIsbnSE(isbn);
		isbn=isbn.toUpperCase().trim();
		//remove all but numbers, X
		char[] theChars = isbn.toCharArray();
		int i; 
		int val=0;
		StringBuffer sb= new StringBuffer();
		for ( i=0; i<theChars.length; i++){
			val=NUMVALUES.indexOf(theChars[i]);
			
			if (val >=0){
				//valid character
				sb.append(theChars[i]);
			} else {
				//invalid character
//				if (!fix && theChars[i] != '-'){
//					throw new InvalidStandardIDException("invalid char");
//				}
			}
		}
		isbn=sb.toString();

		theChars = isbn.toCharArray();
		if (theChars.length<9) throw new InvalidStandardIDException("ISBN length<9");
		if (theChars.length>13) throw new InvalidStandardIDException("ISBN length>13");
		if (theChars.length==11) throw new InvalidStandardIDException("ISBN length=11");
		if (theChars.length==10) {
			//10 digit isbn
			int checksum=0;
			int weight=10;
			//compute checksum
			val=0;
			for ( i=0; weight>0 ; i++){
				val=NUMVALUES.indexOf(theChars[i]);
				if (val >=0 ){
					//valid character
					if (val==10 && weight!=1) throw new InvalidStandardIDException("X in a bad place");

					checksum=checksum+weight*val;
					weight--;
				} else {
					//invalid char
					// should never happen
					throw new InvalidStandardIDException("invalid char");
				}
			}
			if ((checksum%11)!=0){
				//bad checksum
				if (fix){
					value =isbn.substring(0,9)+checkDigit(isbn.substring(0,9));
				} else 
					throw new InvalidStandardIDException("bad checksum-10");
			} else 
				value=isbn;
				
			//make 13 digit isbn
			value13="978"+value.substring(0,9)+checkDigit13("978"+value.substring(0,9));
			
			
		} else if (theChars.length==13) {
			if (!isbn.startsWith("978") && !isbn.startsWith("979") ){
				throw new InvalidStandardIDException("13 digit isbn must start with 978 or 979");
			}
			int checksum13=0;
			int weight13=1;
			//compute checksum
			val=0;
			for ( i=0; i<13; i++){
				val=NUMVALUES.indexOf(theChars[i]);
				if (val >=0 ){
					//valid character
					if (val==10 ) throw new InvalidStandardIDException("X not valid in ISBN 13");
					checksum13=checksum13+weight13*val;
					weight13=(weight13+2)%4;
				} else {
					//invalid char
					//should never occur
					throw new InvalidStandardIDException("invalid char");
				}
			}
			if ((checksum13%10)!=0) {
				//bad checksum
				if (fix){
					value13 =isbn.substring(0,12)+checkDigit13(isbn.substring(0,12));
				} else 
					throw new InvalidStandardIDException("bad checksum-13");
			} else {
				value13=isbn;
			}
			if (value13.startsWith("978")){
				//make 10 digit version
				
				value =value13.substring(3,12)+checkDigit(value13.substring(3,12));
			} else {
				value=null;
			}
		} else if  (theChars.length==9) {
			if (fix){
				value =isbn.substring(0,9)+checkDigit(isbn.substring(0,9));
				value13="978"+value.substring(0,9)+checkDigit13("978"+value.substring(0,9));
			} else 
				throw new InvalidStandardIDException("9-digit isbn");
		} else if  (theChars.length==12) {
			if (!isbn.startsWith("978") && !isbn.startsWith("979") ){
				throw new InvalidStandardIDException("13 digit isbn must start with 978 or 979");
			}
			if (fix){
				value =isbn.substring(3,12)+checkDigit(isbn.substring(3,12));
				value13=value.substring(0,12)+checkDigit13(value.substring(0,12));
			} else 
				throw new InvalidStandardIDException("12-digit isbn");
		} 
			
		return true;	
	}
	
	/** return 10-digit ISBN unless 979
	*/
	public String toString(){
		return toString(false);
	}
	
	/** return 13-digit ISBN unless 979 or isbn13 is false
	*/
	public String toString(boolean isbn13){
		if( isbn13 || value == null){
			return value13;
		} else {
		// try to return isbn-10
			return value;
		}
	}
	/** returns a isbn checkdigit for the first 9 digits in a string
	*/
	public static String checkDigit(String isbn){

		char[] theChars =isbn.toCharArray();
		int checksum=0;
		int weight=10;
		//clean the string, compute checksum
		int i; int val;
		for ( i=0; (i<theChars.length) && (weight>1 ); i++){
			val=NUMVALUES.indexOf(theChars[i]);
			if (val >=0 ){
				//valid character
				if (val<10){ //not a dash
					checksum=checksum+weight*val;
					weight--;
				}
			} 
		}
		if (checksum%11==0) return "0";
		return NUMVALUES.substring(11-checksum%11,12-checksum%11);
	}
	
	/** returns a isbn checkdigit for the first 12 digits in a string
	*/
	public static String checkDigit13(String isbn){

		char[] theChars =isbn.toCharArray();
		int checksum13=0;
		int weight13=1;
		//clean the string, compute checksum
		int i; int val;
		for ( i=0; (i<theChars.length && i<12); i++){
			val=NUMVALUES.indexOf(theChars[i]);
			if (val >=0 ){
				//valid character
				if (val<10){ //not a dash
					checksum13=checksum13+weight13*val;
					weight13=(weight13+2)%4;
				}
			} 
		}
		if (checksum13%10==0) return "0";
		return NUMVALUES.substring(10-checksum13%10,11-checksum13%10);
	}
	
	/**
	* @return "ISBN"
	*/
	public String IDName(){
		return "ISBN";
	}


    /** test for equality
    */
    public boolean equals(ISBNChecker isbn)
    {
        return (value13.equals(isbn.value13));
    }
    
    /** test Strings for equality
    */
    public boolean equals(String isbnString)
    {
        try
        {
            ISBNChecker isbn = new ISBNChecker(isbnString);
            return equals(isbn);
        }
        catch (InvalidStandardIDException e)
        {
            return false;
        }
    }
    
    
    public boolean digitCheck(String input) {
        for(int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if( (c < '0' || c > '9') ) {
            	return false;
            }
        }
        return true;
    }
    
    public static void main(String[] args)  {	
			try{
				ISBNChecker good = new ISBNChecker("978-7-5115-0589-7");
//				ISBNChecker good13=new ISBNChecker(good.toString(true));
//				System.out.println("13->10:" );
//				System.out.println("10位：" + good13);
//				System.out.println("13位：" + good13.toString(true) );
			}catch(Exception e){
				System.out.print(e.getMessage());
			}
		}

}

