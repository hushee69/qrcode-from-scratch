/*
 * Authors: CISSE Demba
 * 			JANDU Harry
*/

public class Data
{
	protected String stringInput;
	protected int stringInputLength;
	
	protected int[] polynomialInput;
	protected int polynomialInputLength;
	
	public Data(String data)
	{
		this.stringInput = data;
		this.stringInputLength = data.length();
	}
	
	public Data(int[] input)
	{
		this.polynomialInput = input;
		this.polynomialInputLength = polynomialInput.length;
	}
	
	@Override
	public String toString()
	{
		String s = new String();
		
		s += "[ ";
		for( int i = 0; i < polynomialInput.length; ++i )
		{
			s += polynomialInput[i] + " ";
		}
		s += "]";
		
		return s;
	}
	
	public void corruptPolynomialInput(int index, int val)
	{
		this.polynomialInput[index] = val;
	}
	
	public int[] getPolynomialInput()
	{
		return this.polynomialInput;
	}
	
	public int getPolynomialInputLength()
	{
		return this.polynomialInputLength;
	}
	
	public static String fromPolynomialArray(int[] polynomialArray)
	{
		String s = new String();
		
		//s += polynomialArray[0] + " ";
		for( int i = 1; i < polynomialArray.length; ++i )
		{
			s += Character.toString(polynomialArray[i]);
		}
		
		return s;
	}
}
