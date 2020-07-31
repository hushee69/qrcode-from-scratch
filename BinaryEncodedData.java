/*
 * Authors: CISSE Demba
 * 			JANDU Harry
*/

public class BinaryEncodedData extends Data
{
	private String encodedInput;
	private int encodedInputLength;
	
	public BinaryEncodedData(int[] polynomial)
	{
		super(polynomial);
		this.encodedInput = this.toBinary();
		this.encodedInputLength = this.encodedInput.length();
	}
	
	public String toBinary()
	{
		String s = new String();
		
		for( int i = 0; i < polynomialInputLength; ++i )
		{
			int c = polynomialInput[i];
			s += String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
		}
		
		return s;
	}
	
	public char get(int index)
	{
		return this.encodedInput.charAt(index);
	}
	
	public String encodedString()
	{
		return this.encodedInput;
	}
	
	public int encodedLength()
	{
		return this.encodedInputLength;
	}
}
