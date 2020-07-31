/*
 * Authors: CISSE Demba
 * 			JANDU Harry
*/

import java.util.ArrayList;
import java.util.List;

public class RSDecoder extends ReedSolomon
{
	private List<Integer> rsPolynomial;
	private int[] decodedArray;
	private int[] errorPositions;
	
	public RSDecoder(String polynomial)
	{
		this.rsPolynomial = new ArrayList<Integer>();
		String[] arr = polynomial.split(",");
		for( String a : arr )
		{
			// extract int
			String temp = a.replaceAll("[^0-9]", "");
			this.rsPolynomial.add(Integer.parseInt(temp));
		}
	}
	
	public static int listMaxValue(int[] in)
	{
		int greatest = in[0];
		for( int i = 0; i < in.length; ++i )
		{
			if( in[i] > greatest )
			{
				greatest = in[i];
			}
		}
		
		return greatest;
	}
	
	@Override
	public boolean decode()
	{
		int[] copyMsg = RSEncoder.integerListToArray(rsPolynomial);
		int[] syndPoly = this.calculateSyndromePolynomial(copyMsg, RSEncoder.NUM_SYMBOLS);
		
		System.err.println("Syndrome polynomial: " + RSEncoder.intArrayToList(syndPoly));
		if( listMaxValue(syndPoly) == 0 )
		{
			this.decodedArray = RSEncoder.slice(copyMsg, (copyMsg.length - RSEncoder.NUM_SYMBOLS));
			
			return true;
		}
		
		int[] errLocs = this.findErrorLocations(syndPoly, RSEncoder.NUM_SYMBOLS);
		System.err.println("Localization polynomial: " + RSEncoder.intArrayToList(errLocs));
		
		this.errorPositions = this.findErrors(ReedSolomon.reverseArray(errLocs), copyMsg.length);
		
		System.err.println("errors' positions: " + RSEncoder.intArrayToList(errorPositions));
		
		return false;
	}
	
	public int[] getErrorPositions()
	{
		return this.errorPositions;
	}
	
	public int[] getDecodedArray()
	{
		return this.decodedArray;
	}
}
