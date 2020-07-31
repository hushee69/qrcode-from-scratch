/*
 * Authors: CISSE Demba
 * 			JANDU Harry
*/

public class Matrix
{
	// matrix minimum size
	// all characters handled exclude 1 byte for the size of the string
	// and the 11 bytes for the error correction code
	// matrix containing data cells always in form ax(a-1)
	public static final int VERSION_ONE_SIZE = 21;		// handle 28 characters
	public static final int VERSION_TWO_SIZE = 27;		// 60 characters
	public static final int VERSION_THREE_SIZE = 31;	// 86 characters
	public static final int VERSION_FOUR_SIZE = 35;		// 116 characters
	public static final int VERSION_FIVE_SIZE = 43;		// 188 characters
	public static final int VERSION_SIX_SIZE = 47;		// 230 characters

	private int matrixDataSize;
	private Cell[][] cells;
	private BinaryEncodedData encodedData;
	private int index;
	
	private int matrixSize;
	
	private boolean isMasked;
	
	public Matrix(Data input)
	{
		this.encodedData = (BinaryEncodedData) input;
		this.determineVersion(this.encodedData.getPolynomialInputLength());
		
		System.err.println("version: " + this.matrixSize + ", " + this.matrixDataSize);
		this.cells = new Cell[matrixSize][matrixSize];
		
		for( int i = 0; i < this.matrixSize; ++i )
		{
			for( int j = 0; j < this.matrixSize; ++j )
			{
				this.cells[i][j] = new Cell();
			}
		}
		
		// finder pattern
		this.finderPattern();
		
		for( int j = 2; j < this.matrixDataSize; j += 2 )
		{
			for( int i = 2; i < this.matrixDataSize; i += 4 )
			{
				if( (j + 2) >= this.matrixDataSize )
				{
					this.fillOneColumn(i, j);
				}
				else
				{
					this.fillTwoColumns(i, j, j + 1);
				}
			}
		}
	}
	
	private void fillOneColumn(int row, int col)
	{
		int k;
		for( int i = row; i < row + 4; ++i )
		{
			if( index < this.encodedData.encodedLength() )
			{
				if( (row + 4) >= this.matrixDataSize )
				{
					k = i;
					while( k < this.matrixDataSize && index < this.encodedData.encodedLength() )
					{
						this.fill(k, col, this.encodedData.get(index));
						index++;
						k++;
					}
					break;
				}
				else
				{
					this.fill(i, col, this.encodedData.get(index));
					index++;
				}
			}
		}
	}

	private void fillTwoColumns(int row, int col1, int col2)
	{
		int k;
		
		for( int i = row; i < row + 4; ++i )
		{
			if( index < this.encodedData.encodedLength() )
			{
				if( (row + 4) >= this.matrixDataSize )
				{
					k = i;
					while( k < this.matrixDataSize && index < this.encodedData.encodedLength() )
					{
						this.fill(k, col1, this.encodedData.get(index));
						index++;
						this.fill(k, col2, this.encodedData.get(index));
						index++;
						k++;
					}
					break;
				}
				else
				{
					this.fill(i, col1, this.encodedData.get(index));
					index++;
					this.fill(i, col2, this.encodedData.get(index));
					index++;
				}
			}
		}
	}

	private void determineVersion(int polynomialInputLength)
	{
		int len = polynomialInputLength;
		System.err.println("len: " + len);
		if( len <= 40 )
		{
			this.matrixSize = VERSION_ONE_SIZE;
		}
		else if( len > 40 && len <= 72 )
		{
			this.matrixSize = VERSION_TWO_SIZE;
		}
		else if( len > 72 && len <= 98 )
		{
			this.matrixSize = VERSION_THREE_SIZE;
		}
		else if( len > 98 && len <= 128 )
		{
			this.matrixSize = VERSION_FOUR_SIZE;
		}
		else if( len > 128 && len <= 200 )
		{
			this.matrixSize = VERSION_FIVE_SIZE;
		}
		else if( len > 200 && len <= 242 )
		{
			this.matrixSize = VERSION_SIX_SIZE;
		}
		else
		{
			throw new IllegalArgumentException("The size of the input should be less than or equal to 252 characters");
		}
		
		this.matrixDataSize = this.matrixSize - 2;
	}
	
	@Override
	public String toString()
	{
		String s = new String();
		
		s += "[\n";
		for( int i = 0; i < this.matrixSize; ++i )
		{
			s += "[";
			for( int j = 0; j < this.matrixSize; ++j )
			{
				s += " " + this.cells[i][j].getIntegerState() + " ";
			}
			s += "]\n";
		}
		s += "]\n";
		
		return s;
	}
	
	private void finderPattern()
	{
		// top
		for( int j = 1; j < this.matrixSize - 1; ++j )
		{
			this.cells[1][j].setCharacterState('1');
		}
		
		// left
		for( int i = 1; i < this.matrixSize - 1; ++i )
		{
			this.cells[i][1].setCharacterState('1');
		}
		
		// bottom
		for( int j = 1; j < this.matrixSize - 1; ++j )
		{
			if( (j + 1) % 2 == 0 )
			{
				this.cells[this.matrixSize - 2][j].setCharacterState('1');
			}
		}

		// right
		for( int i = 1; i < this.matrixSize - 1; ++i )
		{
			if( (i + 1) % 2 == 0 )
			{
				this.cells[i][this.matrixSize - 2].setCharacterState('1');
			}
		}
	}
	
	private void fill(int i, int j, char c)
	{
		this.cells[i][j].setCharacterState(c);
	}
	
	public boolean get(int i, int j)
	{
		return this.cells[i][j].getState();
	}
	
	public void addMask()
	{
		int mask;
		boolean state;
		
		for( int i = 2; i < this.matrixDataSize; ++i )
		{
			for( int j = 2; j < this.matrixDataSize; ++j )
			{
				mask = this.generateMask(i, j);
				// if mask is odd, we change the value in coordinate (i, j)
				if( mask == 2 || mask == 3 || mask == 5 )
				{
					state = !(this.get(i, j));
					this.fill(i, j, state);
				}
			}
		}
	}

	public void removeMask()
	{
		int mask;
		boolean state;
		
		for( int i = 2; i < this.matrixDataSize; ++i )
		{
			for( int j = 2; j < this.matrixDataSize; ++j )
			{
				mask = this.generateMask(i, j);
				// if mask is odd, we change the value in coordinate (i, j)
				if( mask == 2 || mask == 3 || mask == 5 )
				{
					state = !(this.get(i, j));
					this.fill(i, j, state);
				}
			}
		}
	}
	
	private int generateMask(int i, int j)
	{
		double calc = ((float) i * 7.8) + ((float) j * 2.3);
		int res = (int) Math.ceil(calc);
		
		return res % 7;
	}

	private void fill(int i, int j, boolean state)
	{
		this.cells[i][j].setState(state);
	}
	
	public boolean getMasked()
	{
		return this.isMasked;
	}
	
	public void setMasked(boolean mask)
	{
		this.isMasked = mask;
	}
	
	public int getMatrixSize()
	{
		return matrixSize;
	}
}
