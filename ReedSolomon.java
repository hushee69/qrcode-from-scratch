/*
 * Authors: CISSE Demba
 * 			JANDU Harry
*/

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ReedSolomon implements EncoderInterface
{
	protected int[] polynomialData;
	protected int polynomialDataLength;
	
	// finite field alphas for Reed Solomon
	protected static final int EXP_SIZE = 512;
	protected static final int LOG_SIZE = 256;
	protected static final int IRR_POLY = 285;		// irreducible polynomial
	
	protected int expSize, logSize, irrPoly;
	
	protected int[] exponentials;
	protected int[] logarithms;
	
	public ReedSolomon()
	{
		this(LOG_SIZE, IRR_POLY);
	}
	
	// polynomial must be a prime number between [2^size, 2^(size+1)]
	// e.g. size = 3 => poly = {11, 13}
	public ReedSolomon(int size, int poly)
	{
		this.expSize = size * 2;
		this.logSize = size;
		this.irrPoly = poly;
		
		this.exponentials = new int[this.expSize];
		this.logarithms = new int[this.logSize];
		
		this.initializeTables();
	}
	
	@Override
	public void initializeTables()
	{
		int alpha = 1;
		
		for( int i = 0; i < this.logSize; ++i )
		{
			this.exponentials[i] = alpha;
			this.logarithms[alpha] = i;
			// multiply by two
			alpha <<= 1;
			if( alpha >= this.logSize )
			{
				// alpha should be between [0, 2^(logSize - 1)]
				alpha ^= this.irrPoly;
			}
		}
		
		// over sizing exponential array to optimize
		for( int i = this.logSize - 1; i < this.expSize; ++i )
		{
			this.exponentials[i] = this.exponentials[i - this.logSize + 1];
		}
	}
	
	public void showLogs()
	{
		String s = new String();
		
		s += "[ ";
		for( int i = 0; i < LOG_SIZE; ++i )
		{
			s += this.logarithms[i] + " ";
		}
		s += "]";
		
		System.err.println("alphas: " + s);
	}
	
	public void showAlphas()
	{
		String s = new String();
		
		s += "[ ";
		for( int i = 0; i < LOG_SIZE; ++i )
		{
			s += this.exponentials[i] + " ";
		}
		s += "]";
		
		System.err.println("alphas: " + s);
	}
	
	@Override
	public int multiply(int x, int y)
	{
		if( x == 0 || y == 0 )
		{
			return 0;
		}
		
		return this.exponentials[this.logarithms[x] + this.logarithms[y]];
	}

	@Override
	public int divide(int dividend, int divisor)
	{
		if( divisor == 0 )
		{
			throw new IllegalArgumentException("Argument 'divisor' is 0");
		}
		if( dividend == 0 )
		{
			return 0;
		}
		
		try
		{
			return this.exponentials[(this.logarithms[dividend] + this.logSize - this.logarithms[divisor]) % this.logSize];
		}
		catch( IndexOutOfBoundsException e )
		{
			System.err.println("Index should be within GF bounds: [0, " + LOG_SIZE + "]");
			
			return -1;
		}
	}
	
	@Override
	public int pow(int x, int y)
	{
		return this.exponentials[(this.logarithms[x] * y) % LOG_SIZE];
	}
	
	@Override
	public int inv(int x)
	{
		return this.divide(1, x);
	}
	
	@Override
	public int[] polynomialScale(int[] poly, int val)
	{
		int ret[] = new int[poly.length];
		
		for( int i = 0; i < poly.length; ++i )
		{
			ret[i] = this.multiply(poly[i], val);
		}
		
		return ret;
	}
	
	@Override
	public int[] polynomialAdd(int[] p1, int[] p2)
	{
		int[] ret = new int[Math.max(p1.length, p2.length)];
		
		for( int i = 0; i < p1.length; ++i )
		{
			ret[i + ret.length - p1.length] = p1[i];
		}
		
		for( int i = 0; i < p2.length; ++ i)
		{
			ret[i + ret.length - p2.length] ^= p2[i];
		}
		
		return ret;
	}

	@Override
	public int[] polynomialMult(int[] p1, int[] p2)
	{
		int[] ret = new int[p1.length + p2.length - 1];
		
		for( int i = 0; i < p2.length; ++i )
		{
			for( int j = 0; j < p1.length; ++j )
			{
				ret[i + j] ^= this.multiply(p1[j], p2[i]);
			}
		}
		
		return ret;
	}

	@Override
	public int[] polynomialDiv(int[] p1, int[] p2)
	{
		int p1_len = p1.length;
		int p2_len = p2.length;
		int until = p1_len - (p2_len - 1);
		
		// using extended synthetic division
		int[] division_res = p1.clone();
		
		for( int i = 0; i < until; ++i )
		{
			int coef = division_res[i];
			if( coef != 0 )
			{
				for( int j = 1; j < p2_len; ++j )
				{
					if( p2[j] != 0 )
					{
						division_res[i + j] ^= this.multiply(p2[j], coef);
					}
				}
			}
		}
		
		// remainder has necessarily the same degree as the divisor
		// return only the remainder
		int cut = -(p2_len - 1);
		int[] remainder = new int[p2_len - 1];
		
		System.arraycopy(division_res, (p1_len + cut), remainder, 0, remainder.length);
		
		return remainder;
	}
	
	@Override
	public int polynomialEval(int[] poly, int val)
	{
		// using Horner's algorithm
		int ret = poly[0];
		for( int i = 1; i < poly.length; ++i )
		{
			ret = this.multiply(ret, val) ^ poly[i];
		}
		
		return ret;
	}
	
	@Override
	public int[] polynomialGenerator(int num_symbols)
	{
		// (x-a^0)(x-a^1)(x-a^2)...(x-a^n) => (n+1) length for polynomials
		int[] ret = new int[1];
		ret[0] = 1;
		
		for( int i = 0; i < num_symbols; ++i )
		{
			// temporary (x - a^i)
			int[] temp = new int[]{1, this.pow(2, i)};
			ret = this.polynomialMult(ret, temp);
		}
		
		return ret;
	}
	
	@Override
	public String toString()
	{
		String s = new String();
		
		s = "[ ";
		for( int i = 0; i < polynomialDataLength; ++i )
		{
			s += polynomialData[i] + " ";
		}
		s += "]";
		
		return s;
	}
	
	@Override
	public int[] calculateSyndromePolynomial(int[] msgPoly, int numSymbols)
	{
		int[] syndPoly = new int[numSymbols + 1];
		for( int i = 1; i <= numSymbols; ++i )
		{
			syndPoly[i] = this.polynomialEval(msgPoly, this.pow(2, (i - 1)));
		}
		
		return syndPoly;
	}
	
	@Override
	public int[] findErrorLocations(int[] syndPoly, int numSymbols)
	{
		int syndShift = 0;
		List<Integer> errLoc = new ArrayList<Integer>();
		int[] primitiveErrorLoc = null;
		List<Integer> oldLoc = new ArrayList<Integer>();
		int[] primitiveOldLoc = null;
		
		errLoc.add(1);
		oldLoc.add(1);
		
		if( syndPoly.length > numSymbols )
		{
			syndShift = syndPoly.length - numSymbols;
		}
		
		// degree
		// discrepancy check
		int deg, discrepancy, index;
		for( int i = 0; i < numSymbols; ++i )
		{
			deg = i + syndShift;
			discrepancy = syndPoly[deg];
			
			//System.err.println("discrepancy: " + discrepancy);
			for( int j = 1; j < errLoc.size(); ++j )
			{
				index = errLoc.size() - j - 1;		// -1+1, -2+1, ...
//				System.err.println("errloc: " + errLoc);
				discrepancy ^= this.multiply(errLoc.get(index), syndPoly[deg - j]);
//				System.err.println("discrepancy: " + discrepancy);
			}
			
			// calculate next degree and shift polynomial
			oldLoc.add(0);
//			System.err.println("oldloc " + oldLoc);
			// mis-a-jour if discrepancy is found
//			System.err.println("delta: " + discrepancy);
			if( discrepancy != 0 )
			{
				if( oldLoc.size() > errLoc.size() )
				{
					primitiveOldLoc = ReedSolomon.integerListToArray(oldLoc);
					int[] newLoc = this.polynomialScale(primitiveOldLoc, discrepancy);
					
					primitiveErrorLoc = ReedSolomon.integerListToArray(errLoc);
					primitiveOldLoc = this.polynomialScale(primitiveErrorLoc, this.inv(discrepancy));
					
					primitiveErrorLoc = newLoc;
					errLoc = ReedSolomon.intArrayToList(primitiveErrorLoc);
					oldLoc = ReedSolomon.intArrayToList(primitiveOldLoc);
					
//					System.err.println("errloc: " + errLoc);
//					System.err.println("oldLoc: " + oldLoc);
//					System.err.println("newloc:" + this.intArrayToList(newLoc));
				}
//				System.err.println("before " + errLoc);
				primitiveErrorLoc = ReedSolomon.integerListToArray(errLoc);
//				System.err.println("prim " + primitiveErrorLoc[0] + " " + primitiveErrorLoc[1]);
				primitiveOldLoc = ReedSolomon.integerListToArray(oldLoc);
				primitiveErrorLoc = this.polynomialAdd(primitiveErrorLoc, this.polynomialScale(primitiveOldLoc, discrepancy));
				errLoc = ReedSolomon.intArrayToList(primitiveErrorLoc);
//				System.err.println("inside " + errLoc);
			}
		}
		
//		System.err.println("errloc: " + errLoc.toString());
		Iterator<Integer> iter = errLoc.iterator();
		while( iter.hasNext() && iter.next() == 0 )
		{
			iter.remove();
		}
		
		int errLocSize = errLoc.size() - 1;
		if( (errLocSize * 2) > numSymbols )
		{
			throw new IllegalArgumentException("The errors are too much to be corrected");
		}
		
		primitiveErrorLoc = ReedSolomon.integerListToArray(errLoc);
		
		return primitiveErrorLoc;
	}
	
	public static List<Integer> intArrayToList(int[] in)
	{
		List<Integer> ret = new ArrayList<Integer>();
		
		for( int i : in )
		{
			ret.add(i);
		}
		
		return ret;
	}
	
	public static int[] integerListToArray(List<Integer> in)
	{
		int[] ret = new int[in.size()];
		
		Iterator<Integer> iter = in.iterator();
		for( int i = 0; i < in.size(); ++i )
		{
			ret[i] = iter.next().intValue();
		}
		
		return ret;
	}
	
	public static int[] reverseArray(int[] arr)
	{
		int[] ret = new int[arr.length];
		int len = arr.length;
		
		for( int i = 0; i < len; ++i)
		{
			ret[i] = arr[len - i - 1];
		}
		
		return ret;
	}
	
	@Override
	public int[] findErrors(int[] errLocs, int msgLength)
	{
		// use chien search to find errors
		int locsLen = errLocs.length - 1;
		List<Integer> errPos = new ArrayList<Integer>();
		int[] primitiveErrPos = null;
		
		for( int i = 0; i < msgLength; ++i )
		{
			// evaluate the polynomial at 2^i and if it is zero, then we have a root
			// and this implies that there is an error in the i-th location
			if( this.polynomialEval(errLocs, this.pow(2, i)) == 0 )
			{
				errPos.add(msgLength - i - 1);
			}
		}
		
		// error size should be equivalent to the error locator size
		if( errPos.size() != locsLen )
		{
			throw new IllegalArgumentException("Failed to assert the correct number of errors");
		}
		
		primitiveErrPos = ReedSolomon.integerListToArray(errPos);
		
		return primitiveErrPos;
	}
	
	@Override
	public int[] encode()
	{
		try
		{
			throw new Exception("Call this function with " + RSEncoder.class + " object");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public boolean decode()
	{
		try
		{
			throw new Exception("Call this function with " + RSDecoder.class + " object");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static int[] slice(int[] in, int num)
	{
		int[] ret = new int[num];
		
		for( int i = 0; i < num; ++i )
		{
			ret[i] = in[i];
		}
		
		return ret;
	}
}
