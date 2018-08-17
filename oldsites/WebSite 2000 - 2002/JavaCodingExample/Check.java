//=========================================================================
//	Check.java
//=========================================================================
//	Programmer:		Rodney Beede
//	ID:			***REMOVED***
//	Course:			CS1323
//	Lab Section:		222
//	Lab TA:			Lalitha
//
//	Project#:		7
//	Due Date:		4-14-2K
//
//	Description:		This class stores information about a check
//				such as:  Check Number, Date of Check,
//				Pay To, Written Amount, Numerical Amount,
//				Check Memo, and if it is voided or not
//
//	Public Methods:		gimmeCheckNum -- returns the check num
//
//				gimmeDate -- returns the check date
//
//				gimmeMemo -- returns the check memo
//
//				gimmeNumAmount -- retuns the numerical
//				amount of the check
//
//				gimmePayTo -- returns the pay to field
//
//				gimmeVoided -- returns if check is void
//
//				gimmeWritAmount -- returns the written
//				amount
//
//				setVoid -- sets a check to void
//
//	Private Methods:	None
//
//=========================================================================

class Check {
	//Private data values for holding the check information
	private int intCheckNum;  //Check number
	private String strDate;  //Date of the check
	private String strPayTo;  //Who the check is for
	private String strWritAmount;  //Written amount of check
	private float fltNumAmount;  //Numerical amount of check
	private String strMemo;  //Check memo
	private boolean bolVoid;  //True if void, false if valid

	/*
	  Method:	Check

	  Purpose:	Class constructor, used to set values for the check

	  Parameters:	intCheckNum -- The check number to use
			strDate -- Date of the check
			strPayTo -- Who the check is written to
			strWritAmount -- The written (word) amount
			fltNumAmount -- The numerical amount of the check
			strMemo -- Check memo (what's for)

	  Returns:	N/A
	*/
	public Check( int intCheckNum, String strDate, String strPayTo,
	   String strWritAmount, float fltNumAmount, String strMemo ) {

		//Set this object's private data values to the passed
		//data types
		this.intCheckNum = intCheckNum;
		this.strDate = strDate;
		this.strPayTo = strPayTo;
		this.strWritAmount = strWritAmount;
		this.fltNumAmount = fltNumAmount;
		this.strMemo = strMemo;

		//Assume this check isn't voided
		bolVoid = false;
	}


	/*
	  Method:	gimmeCheckNum

	  Purpose:	Returns the check number

	  Parameters:	None

	  Returns:	(int)
	*/
	public int gimmeCheckNum( ) {
		return intCheckNum;
	}


	/*
	  Method:	gimmeDate

	  Purpose:	Returns the check date

	  Parameters:	None

	  Returns:	String
	*/
	public String gimmeDate( ) {
		return strDate;
	}


	/*
	  Method:	gimmeMemo

	  Purpose:	Returns the check memo

	  Parameters:	None

	  Returns:	String
	*/
	public String gimmeMemo( ) {
		return strMemo;
	}


	/*
	  Method:	gimmeNumAmount

	  Purpose:	Returns the check's numerical amount

	  Parameters:	None

	  Returns:	(float)
	*/
	public float gimmeNumAmount( ) {
		return fltNumAmount;
	}


	/*
	  Method:	gimmePayTo

	  Purpose:	Returns the pay to field

	  Parameters:	None

	  Returns:	String
	*/
	public String gimmePayTo( ) {
		return strPayTo;
	}


	/*
	  Method:	gimmeVoided

	  Purpose:	Returns if the check is void

	  Parameters:	None

	  Returns:	boolean
	*/
	public boolean gimmeVoided( ) {
		return bolVoid;
	}


	/*
	  Method:	gimmeWritAmount

	  Purpose:	Returns the check's written (word) amount

	  Parameters:	None

	  Returns:	String
	*/
	public String gimmeWritAmount( ) {
		return strWritAmount;
	}


	/*
	  Method:	setVoid

	  Purpose:	Makes the check void

	  Parameters:	None

	  Returns:	None
	*/
	public void setVoid( ) {
		bolVoid = true;
	}
}  //End of class Check