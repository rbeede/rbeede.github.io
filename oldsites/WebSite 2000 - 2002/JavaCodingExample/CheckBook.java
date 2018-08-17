//=========================================================================
//	CheckBook.java
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
//	Description:		This class manages a collection of Check
//				objects.  It adds & voids checks
//				It also allows withdraws and deposits into
//				the checkbook.
//				In addition it also calculates the balance
//				with each new transaction and keeps a
//				register full of the transactions
//				It can also search for specific checks
//
//	Public Methods:		addCheck -- Adds a new check to the book
//				
//				deleteCheck -- Delete a check (not void)
//				
//				voidCheck -- Voids a check (not delete)
//				
//				withdraw -- Withdraws from the account
//				
//				deposit -- Deposits into the account
//				
//				giveCheck -- Gives the specified check
//
//				giveRegister -- Returns a address to a
//				String array that has the register for the
//				checkbook
//
//				giveRegisterSize -- Gives the register size
//
//				giveNumChks -- Give the number of current checks
//
//				giveBalance -- Returns the balance
//
//	Private Methods:	findCheck -- Finds a check by it's number
//				and returns it's index number
//
//				addRegEntry -- Adds an entry to the reg
//
//
//=========================================================================

class CheckBook {
	private Check objChecks[];  //Array of checks in this book
	//For holding record of all transactions
	private String strRegisterEntries[];
	
	public String strBookName;  //Name of the checkbook

	private double dblBalance = 0.0;  //Balance in book
	public int intLastCheckNum;  //Holds the last used checknumber


	/*
	  Method:	CheckBook

	  Purpose:	Class constructor

	  Parameters:	strBookName -- name of the checkbook

	  Returns:	N/A
	*/
	public CheckBook( String strBookName ) {
		this.strBookName = strBookName;
	}

	//Constructor when no name is provided
	public CheckBook( ) {
		this( "My Checkbook" );  //Pass a default name
	}


	/*
	  Method:	addCheck

	  Purpose:	Adds a check to the checkbook and an entry
			into the register

	  Parameters:	intChkNum -- Check number to assign
			strDate -- Date of the check
			strPayTo -- Who check is paid to
			strWritAmount -- Written (word) amount
			fltNumAmount -- Numerical amount
			strMemo -- Check notes

	  Returns:	none
	*/
	public void addCheck( int intChkNum, String strDate,
	   String strPayTo, String strWritAmount, float fltNumAmount,
	   String strMemo ) {
		int intNewSize;  //Holds new size of array
		final char TAB = (char) 9;  //Tab code

		//Figure out if this is the first time to add a check
		if( objChecks == null )
			//It is, set the size correctly
			intNewSize = 1;
		else
			//Adding on to existing
			intNewSize = objChecks.length + 1;

		//Make a temporary Check array for resizing of objChecks
		Check objTempChecks[] = new Check[intNewSize];

		//Check to see if anything is being copied at all
		if( objChecks != null ) {
			//Do have data to copy, so copy it
			//Make space in objChecks[] by copying it into a temporary
			//array, resizing it by pointing it to the temp array
			for( int i = 0; i < objChecks.length; i++ ) {
				//objChecks ==> objTempChecks
				objTempChecks[i] = objChecks[i];
			}
		}

		//Make objChecks piont to objTempChecks which is 1 bigger
		objChecks = objTempChecks;
			
		//Add the new check to the last entry in objChecks
		objChecks[(objChecks.length - 1)] = new Check( intChkNum,
		   strDate, strPayTo, strWritAmount, fltNumAmount,
		   strMemo );

		//Update the balance
		dblBalance = dblBalance -
		   objChecks[(objChecks.length-1)].gimmeNumAmount();

		//Add the check to the register in this format:
		//CHKNUM     DATE     DESCRIPTION     AMOUNT     NEW BAL
		addRegEntry(
		   String.valueOf(objChecks[(objChecks.length-1)].gimmeCheckNum()) + String.valueOf(TAB) + String.valueOf(TAB) +
		   objChecks[(objChecks.length-1)].gimmeDate() + TAB +
		   objChecks[(objChecks.length-1)].gimmeMemo() + TAB + String.valueOf(TAB) +
		   objChecks[(objChecks.length-1)].gimmeNumAmount() + TAB + String.valueOf(TAB) +
		   round( dblBalance, 2 ) );

		//Mark the new check number if the one used is higher then the last
		if( intChkNum > intLastCheckNum )  intLastCheckNum = intChkNum;
	}


	/*
	  Method:	voidCheck

	  Purpose:	Voids a check.  This doesn't delete it.

	  Parameters:	intChkNum -- Number of check to void

	  Returns:	(boolean) -- true if check was voided, false if not
	*/
	public boolean voidCheck( int intChkNum ) {

		//Find the index of the check with intChkNum
		int intChkIndex = findCheck(intChkNum);

		if( intChkIndex == -1 ) {  //Find failed
			return false;  //Send back failed signal
		}

		//Check to see if check is already void
		if( objChecks[intChkIndex].gimmeVoided() )
			//Already void, return failure
			return false;

		//Mark the check as void
		objChecks[intChkIndex].setVoid();

		//Update the balance accordingly
		dblBalance = dblBalance +
		   objChecks[intChkIndex].gimmeNumAmount();

		//Mark in the register that it was voided
		addRegEntry( "Check Number " +
		   objChecks[intChkIndex].gimmeCheckNum() +
		   " has been voided.  New balance is " + dblBalance );

		return true;  //Success
	}


	/*
	  Method:	withdraw

	  Purpose:	Withdraws the specified amount from the account

	  Parameters:	fltAmount -- Amount to withdraw

	  Returns:	None
	*/
        public void withdraw( float fltAmount ) {
		//Remove the specified amount from the balance
		dblBalance = dblBalance - fltAmount;

		//Update the register
		addRegEntry( "Withdraw     Amount: " + fltAmount +
		   "     Balance: " + dblBalance );
	}


	/*
	  Method:	deposit

	  Purpose:	Deposits the specified amount into the account

	  Parameters:	fltAmount -- Amount to deposit

	  Returns:	None
	*/
	public void deposit( float fltAmount ) {
		//Add the specified amount to the balance
		dblBalance = dblBalance + fltAmount;

		//Update the register
		addRegEntry( "Deposit     Amount: " + fltAmount +
		   "     Balance: " + dblBalance );
	}


	/*
	  Method:	giveCheck

	  Purpose:	Finds the check (specified by it's check number)
			and returns a copied Check object back

	  Parameters:	intChkNum -- Check number

	  Returns:	(Check)
	*/
	public Check giveCheck( int intChkNum ) {
		Check objCopyOfCheck;  //Used when return a check
		int intChkIndex;  //Holds position of check in objChecks

		//Find the desired check
		intChkIndex = findCheck( intChkNum );

		//Make a new check that is a copy of the found check in
		//objChecks, that way we can return it and not worry about
		//the user being able to modify objChecks
		objCopyOfCheck = new Check(
		   objChecks[intChkIndex].gimmeCheckNum(),
		   objChecks[intChkIndex].gimmeDate(),
		   objChecks[intChkIndex].gimmePayTo(),
		   objChecks[intChkIndex].gimmeWritAmount(),
		   objChecks[intChkIndex].gimmeNumAmount(),
		   objChecks[intChkIndex].gimmeMemo() );

		//Return objCopyOfCheck's address so it's values can be
		//used.  We do this so the user can't modify the objChecks
		//array
		return objCopyOfCheck;
	}


	/*
	  Method:	giveRegister

	  Purpose:	Returns a new copy of this checkbook's register
			We use a new copy so the user can't modify the
			original

	  Parameters:	intIndex -- Index of register entry to return
			If the specified one is out of bounds "-1" is sent

	  Returns:	(String)
	*/
	public String giveRegister( int intIndex ) {
		//Make sure register isn't empty
		if( strRegisterEntries == null ) {
			//It is, send back -1 flag
			return "-1";
		}
		else if( intIndex >= strRegisterEntries.length ) {
			//Out of bounds, return -1
			return "-1";
		}

		//Create a new copy that we can pass back instead of
		//passing by reference
		String strRegCopy =
		   new String(strRegisterEntries[intIndex]);

		//Send the copy of the register entry back
		return strRegCopy;
	}


	/*
	  Method:	giveRegisterSize

	  Purpose:	Returns the size of the register

	  Parameters:	None

	  Returns:	(int)
	*/
	public int giveRegisterSize( ) {
		//Make sure register isn't empty
		if( strRegisterEntries == null ) {
			//It is, send back -1 flag
			return -1;
		}

		//Send the size back
		return strRegisterEntries.length;
	}


	/*
	  Method:	giveNumChks

	  Purpose:	Returns the number of checks

	  Parameters:	None

	  Returns:	(int)
	*/
	public int giveNumChks( ) {
		//Make sure objChecks isn't empty
		if( objChecks == null ) {
			//It is, send back -1 flag
			return -1;
		}

		//Send the size back
		return objChecks.length;
	}


	/*
	  Method:	giveBalance

	  Purpose:	Shoots back the balance

	  Parameters:	None

	  Returns:	(double)
	*/
	public double giveBalance( ) {
		//Return the balance rounded to 2 decimals
		return round( dblBalance, 2 );
	}


	/*
	  Method:	findCheck

	  Purpose:	Finds a check by it's check number and then returns
			it's index number in objChecks
			Note:  This isn't meant to be used by the user, use
			giveCheck for that

	  Parameters:	intChkNum -- Check number

	  Returns:	(int) The check index in objChecks or
			-1 if the check isn't found
	*/
	private int findCheck( int intChkNum ) {
		int intChkIndex;  //Holds index of the check

		//Assume check wasn't found
		intChkIndex = -1;

		//Loop through until the check is found
		for( int i = 0; i < objChecks.length; i++ ) {
			if( objChecks[i].gimmeCheckNum() == intChkNum ) {
				intChkIndex = i;  //Mark the spot
				break;  //Leave the for loop now
			}
		}

		return intChkIndex;  //Return the result
	}


	/*
	  Method:	addRegEntry

	  Purpose:	Makes room in the strRegisterEntries array to add a
			String entry, then it adds it

	  Parameters:	strNewEntry -- Data to enter into register

	  Returns:	None
	*/
	private void addRegEntry( String strNewEntry ) {
		int intSize;  //Used to hold array size

		if( strRegisterEntries == null ) {
			//Starting a new one, mark correct size
			intSize = 0;
		}
		else  //Already have one, just mark current size
			intSize = strRegisterEntries.length;
			

		//Make a temp String array for resizing of
		//strRegisterEntries
		String strTempRegister[] =
		   new String[(intSize + 1)];

		//Make sure strTempRegister gets at least one value
		strTempRegister[0] = " ";

		//Make sure we have something to copy
		if( strRegisterEntries != null ) {
			//Make space in strRegisterEntries[] by copying it into a
			//temporary array, resizing it by making a new one, and
			//then copying the data back
			for( int i = 0; i < intSize; i++ ) {
				//strRegisterEntries ==> strTempRegister
				strTempRegister[i] = strRegisterEntries[i];
			}
		}

		//Make strRegisterEntries resized 1 bigger
		strRegisterEntries = strTempRegister;
			
		//Add the new entry to the end
		strRegisterEntries[(strRegisterEntries.length - 1)] =
		   strNewEntry;
	}


	/*
	  Method:	round

	  Purpose:	This method rounds a number to the specified number of
			places in the decimal or in the whole number area
			To set the place to round to a user enters what power of ten
			to multiple the number by

			Example:  If dblNumber = 5.53 and someone wanted to round it
			to the 1st decimal number they would pass the value 1 for
			intPlace (10^1 = 10, 10 * 5.53 = 55.3, 55.3 + 0.5 = 55.8, (int) 55.8 / 10^1 = 5.5)
			If dblNumber = 2654 and they wanted to round it to the
			hundreds place the would pass the value -2 for intPlace
			(10^-2 = .01, .01 * 2654 = 26.54, 26.54 + 0.5 = 27.04, (int) 27.04 / 10^-2 = 2700)
			A quick chart for this would be:
			   Any number, the place to round to decides the power of ten
			                          #########.##########
			   Power of ten is --     876543210,12345678910
			Where places on the left side of the decimal are a negative
			value for the power and places on the right are postive values
	  Parameters:	dblNumber -- The decimal number to round

			intPlace -- The place to found to (see above chart)

	  Returns:	(double)
	*/
	private double round( double dblNumber, int intPlace ) {
		int intCutOffNum = 0;  //Used to cutoff extra decimals

		//Align the placement where the rounding should occur
		dblNumber = dblNumber
		   * Math.pow( (double) 10, (double) intPlace );

		//Determine if number is positive or negative
		if( dblNumber >= 0 ) {  //Number is postive
			//Round off decimal
			intCutOffNum = (int) (dblNumber + 0.5);
		}
		else {  //Number is negative
			//Round off decimal
			intCutOffNum = (int) (dblNumber - 0.5);
		}

		//Put decimal back into the proper place for the number
		dblNumber = (double) (intCutOffNum / Math.pow( (double) 10,
		   (double) intPlace ));

		return dblNumber;  //Return the value
	}  //End of method round
}  //End of class CheckBook
