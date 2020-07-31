/*
 * Authors: CISSE Demba
 * 			JANDU Harry
*/

public interface EncoderInterface
{
	// initialize tables for Galois Field information
	public void initializeTables();
	public int multiply(int x, int y);
	public int divide(int dividend, int divisor);
	// x^y
	public int pow(int x, int y);
	// x^-1
	public int inv(int x);
	// multiply polynomial with scalar
	public int[] polynomialScale(int[] poly, int val);
	public int[] polynomialAdd(int[] p1, int[] p2);
	public int[] polynomialMult(int[] p1, int[] p2);
	public int[] polynomialDiv(int[] p1, int[] p2);
	public int polynomialEval(int[] poly, int val);
	public int[] polynomialGenerator(int num_symbols);
	// calculate syndromes - used for decoding
	public int[] calculateSyndromePolynomial(int[] msgPoly, int numSymbols);
	// for solving the equation, we could've used extended euclidean algorithm
	// but we chose to use Berlekamp-Massey Algorithm
	public int[] findErrorLocations(int[] syndPoly, int numSymbols);
	// find errors - using Chien search
	public int[] findErrors(int[] errLocs, int msgLength);
	public int[] encode();
	// decode message, check for errors and correct if any
	public boolean decode();
}
