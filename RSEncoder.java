/*
 * Authors: CISSE Demba
 * 			JANDU Harry
*/

public class RSEncoder extends ReedSolomon
{
	public static final int NUM_SYMBOLS = 11;
	
	public RSEncoder(String text)
	{
		super();
		
		// convert data into polynomial array
		String in = new String(text);

		// add the length of the data in the polynomial
		// add plus one for the size of the input
		this.polynomialDataLength = in.length() + 1;
		this.polynomialData = this.textToPolynomialArray(in);
	}
	
	public int[] textToPolynomialArray(String text)
	{
		int[] ret = new int[text.length() + 1];
		
		ret[0] = text.length();
		for( int i = 0; i < text.length(); ++i )
		{
			ret[i + 1] = (int) text.charAt(i);
		}
		
		return ret;
	}
	
	private int[] zeroPadding(int num_zeros)
	{
		int[] ret = new int[this.polynomialDataLength + num_zeros];
		
		System.arraycopy(this.polynomialData, 0, ret, 0, this.polynomialDataLength);
		
		return ret;
	}
	
	@Override
	public int[] encode()
	{
		int[] genPoly = this.polynomialGenerator(NUM_SYMBOLS);
		
		// add zeros to the message polynomial
		int[] encodedPoly = this.zeroPadding(genPoly.length - 1);
		
		System.err.println("msg len: " + encodedPoly.length);
		
		int[] remnant = this.polynomialDiv(encodedPoly, genPoly);
		
		// append remnant to msgPoly
		System.arraycopy(remnant, 0, encodedPoly, encodedPoly.length - remnant.length, remnant.length);
		
		return encodedPoly;
	}
}
