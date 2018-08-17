//=========================================================================
//	CheckBookMain.java
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
//	Description:		This program uses the CheckBookController
//				class to allow the user to manage their
//				checkbooks.  They can record their checks
//				and manage their balances for any of their
//				checkbooks
//
//=========================================================================

class CheckBookMain {
	/*
	  Method:	main

	  Purpose:	Starts the program and starts the
			CheckBookController

	  Parameters:	args[] -- Takes command line arguments

	  Returns:	N/A
	*/
	public static void main( String[] args ) {
		//Create a CheckBookController object
		CheckBookController objChkBookController =
		   new CheckBookController( );

		//Start up the controller
		objChkBookController.start();

		//Program is done, make sure it terminates
		System.exit(0);
	}  //End of main method
}  //End of class CheckBookMain
