/*
 * Authors: CISSE Demba
 * 			JANDU Harry
*/

import java.util.ArrayList;
import java.util.List;

public class BinaryDecodedData extends Data
{
	private String decodedInput;
	private int[] decodedPolynomial;
	private int decodedPolynomialLength;
	
	// takes in binary input
	public BinaryDecodedData(String s)
	{
		super(s);
		this.decodedInput = this.toRawString();
		this.decodedPolynomial = this.toRawPolynomial();
		this.decodedPolynomialLength = this.decodedPolynomial.length;
	}
	
	public int[] toRawPolynomial()
	{
		// use list to store the data dynamically then later convert to array
		List<Integer> polynomialList = new ArrayList<Integer>();
		
		for( int i = 0; i < decodedInput.length(); ++i )
		{
			polynomialList.add((int) decodedInput.charAt(i));
		}
		
		int[] ret = ReedSolomon.integerListToArray(polynomialList);
		
		return ret;
	}

	public String toRawString()
	{
		String s = new String();
		String temp = new String();
		int ascii;
		
		for( int i = 0; i < stringInputLength; i += 8 )
		{
			temp = stringInput.substring(i, i + 8);
			ascii = Integer.parseInt(temp, 2);
			s += (char) ascii;
		}
		
		return s;
	}
	
	public char get(int index)
	{
		return this.decodedInput.charAt(index);
	}
	
	public String decodedString()
	{
		return this.decodedInput;
	}
	
	public int[] getDecodedPolynomial()
	{
		return this.decodedPolynomial;
	}

	public int getDecodedPolynomialLength()
	{
		return this.decodedPolynomialLength;
	}
}
