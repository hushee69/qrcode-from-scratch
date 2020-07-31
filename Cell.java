/*
 * Authors: CISSE Demba
 * 			JANDU Harry
*/

public class Cell
{
	// cell can be either 0 or 1
	private boolean state;
	
	public Cell()
	{
		this.state = false;
	}
	
	public Cell(boolean s)
	{
		this.state = s;
	}
	
	public boolean getState()
	{
		return this.state;
	}
	
	public int getIntegerState()
	{
		return this.state == true ? 1 : 0;
	}
	
	public void setIntegerState(int s)
	{
		this.state = s != 0 ? true : false;
	}
	
	public void setCharacterState(char c)
	{
		if( c == '0' )
		{
			this.state = false;
		}
		else if( c == '1' )
		{
			this.state = true;
		}
		else
		{
			try
			{
				throw new Exception("Character must be either 0 or 1");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void setState(boolean s)
	{
		this.state = s;
	}
}
