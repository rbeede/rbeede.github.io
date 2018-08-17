//=========================================================================
//	CheckBookController.java
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
//	Description:		This object uses the CheckBook class to
//				manage multiple checkbooks.
//				It handles all the user GUI and gets
//				the input from the user to pass along
//				to CheckBook
//
//	Public Methods:		start -- Kicks off the controller
//
//	Private Methods:	mainMenu -- Asks the user to create,
//				or manage a checkbook
//				It also allows the user to quit
//
//				addCheckBook -- Adds a checkbook
//				
//				manCheckBook -- Manages a checkbook
//
//				pickCheckBook -- Asks the user to pick a
//				checkbook from objCheckBooks
//				
//				writeCheck -- Asks the user to fill in data
//				for a check, then it adds that check data
//
//				voidCheck -- Asks the user to pick a check
//				to void
//
//				withdraw -- Asks the user for the amount
//				to withdraw
//
//				deposit -- Asks the user for the amount to deposit
//
//				saveIt -- Dumps all checkbook regisiters to files
//
//=========================================================================

import javabook.*;  //Load the javabook package

class CheckBookController {
	//Declare a MainWindow object for use as a main frame
	MainWindow objMainWin;
	OutputBox objRegister;  //The checkbook register

	//Declare a CheckBook object array
	CheckBook objCheckBooks[];

	private final char TAB = (char) 9;  //Tab code

	/*
	  Method:	CheckBookController

	  Purpose:	Class constructor

	  Parameters:	None

	  Returns:	None
	*/
	public CheckBookController( ) {
		//Create a MainWindow object and give it a title
		 objMainWin = new MainWindow( "CheckBook Manager" );
	}

	
	/*
	  Method:	start

	  Purpose:	Starts the program on getting inputs from the user
			and keeps the program looping until it should quit

	  Parameters:	None

	  Returns:	None
	*/
	public void start( ) {
		//Constants that define values for selected actions from
		//the mainMenu
		final byte ADDBOOK = 0;
		final byte MANBOOK = 1;
		final byte QUIT = 2;

		byte bytAction;  //Used to hold the user's choice of action

		objMainWin.show();  //Show the parent window

		//Loop until user desires to quit
		do {
			//Bring up the main menu (add, manage books) and
			//record the user selection
			bytAction = mainMenu( );

			//Figure out what we are doing
			switch( bytAction ) {
				case ADDBOOK:		//Adding a book
					//Call method to add a new book
					addCheckBook( );
					break;
				case MANBOOK:		//Manage a book
					manCheckBook( );
					break;
				case QUIT:		//User is quitting
					//Save all the stuff to disk
					saveIt();

					//Loop will terminate after this
					break;
				default:		//Bad Selection
					;  //Do nothing
			}
		} while( bytAction != QUIT );
	}


	/*
	  Method:	mainMenu

	  Purpose:	Asks the user if they want to Add or Manage a
			checkbook.  It also asks if they wish
			to quit.  It returns the result

	  Parameters:	None

	  Returns:	(byte) -- Matches the selected index and the
			selection constants ADDBOOK, MANBOOK, ...
	*/
	private byte mainMenu( ) {
		//Create a ListBox object for getting input, make it modal
		ListBox objList = new ListBox( objMainWin,
		   "Choose a command", true );

		//Add the commands to the list
		objList.addItem( "Create a new checkbook" );
		objList.addItem( "Manage a checkbook" );
		objList.addItem( "Quit" );

		//Get the user's selection and return it
		return (byte) objList.getSelectedIndex( );
	}


	/*
	  Method:	addCheckBook

	  Purpose:	Adds a checkbook the the objCheckBooks array.
			It will also ask for a name for that book

	  Parameters:	None

	  Returns:	None
	*/
	private void addCheckBook() {
		//Make a InputBox object to get a name for the checkbook
		InputBox objInput = new InputBox( objMainWin,
		   "Enter in the name of the checkbook" );

		//Make a temporary CheckBook array for holding data while
		//we are resizing
		CheckBook objTempBooks[];

		int intNewArraySize;  //Holds size of array to make
		String strTitle;  //Holds the name

		//Ask the user to provide a title
		strTitle = objInput.getString( "Name of checkbook" );

		//Create the temporary array, and check to see if this is
		//the first book to be made
		if( objCheckBooks == null ) {
			//First time to make a book
			intNewArraySize = 1;
			//Do this so we don't have null references
			objCheckBooks = new CheckBook[intNewArraySize];
		}
		else {
			intNewArraySize = objCheckBooks.length + 1;
		}

		objTempBooks = new CheckBook[intNewArraySize];

		//We need to make room for the new CheckBook
		//To resize objCheckBooks we copy it's data into a temp
		//array, objTempBooks, and then simply make it use that
		//new array's memory value

		//Copy into temp array
		for( int i = 0; i < objCheckBooks.length; i++ )
			objTempBooks[i] = objCheckBooks[i];

		//Resize objCheckBooks by pointing it to the temp array
		objCheckBooks = objTempBooks;

		//Add the new book
		objCheckBooks[(objCheckBooks.length-1)] =
		   new CheckBook( strTitle );
	}


	/*
	  Method:	manCheckBook

	  Purpose:	Allows the user to manage a checkbook
			Basically the user can make checks with it

	  Parameters:	None

	  Returns:	None
	*/
	private void manCheckBook() {
		ListBox objList;  //Used for listing options
        	int intChkBkIndex;  //Holds what checkbook to work with
		byte bytChoice;  //Holds the user's selections
		final byte WRITECHK = 0;
		final byte VOIDCHK = 1;
		final byte WITHDRAW = 2;
		final byte DEPOSIT = 3;

		//Get what checkbook to work with
		intChkBkIndex = pickCheckBook();

		//Check to see if user cancelled
		if( intChkBkIndex == ListBox.NO_SELECTION ||
		   intChkBkIndex == ListBox.CANCEL )
			return;  //Nothing to do, leave

		//Display the current register
		displayReg( intChkBkIndex );

		//Give the user the option to write a check, void a check,
		//make a withdraw, or make a deposit
		objList = new ListBox( objMainWin, "Choose a command" );
		objList.addItem( "Write a check" );
		objList.addItem( "Void a check" );
		objList.addItem( "Make a withdraw" );
		objList.addItem( "Make a deposit" );
	
		//Loop in this until use is done
		do {
			//Get the user's choice
			bytChoice = (byte) objList.getSelectedIndex();

			//Figure out what the user selected
			switch( bytChoice ) {
				case WRITECHK:		//Write a check
					//Call method to write a check
					writeCheck( intChkBkIndex);

					//Redisplay the register
					displayReg( intChkBkIndex);

					break;
				case VOIDCHK:		//Void a check
					//Use method to do the work
					voidCheck( intChkBkIndex );

					//Update the register
					displayReg( intChkBkIndex);

					break;
				case WITHDRAW:		//Make a withdraw
					//Use method to do the work
					withdraw( intChkBkIndex );					

					//Update the register
					displayReg( intChkBkIndex);

					break;
				case DEPOSIT:		//Make a deposit
					//Use method to do the work
					deposit( intChkBkIndex );

					//Update the register
					displayReg( intChkBkIndex);

					break;
				case objList.NO_SELECTION:  //Do nothing
					break;
				case objList.CANCEL:	//Do nothing
					//Loop will terminate after this
					break;
				default:		//Error
					System.out.println(
					   "Error in check command " +
					   "selection." );
			}
		} while( bytChoice != objList.CANCEL );

		//Get rid of the displayed register
		objRegister.hide();
		objRegister = null;
	}


	/*
	  Method:	displayReg

	  Purpose:	Shows the register for a checkbook

	  Parameters:	intChkBkInd -- Index of checkbook

	  Returns:	None
	*/
	private void displayReg( int intChkBkIndex ) {
		//Destroy the old register
		//This is a very bad design idea, the whole register should
		//be redone since it works so badly.  Lots of nullpointer and
		//segmentation faults cause the whole machine to be unstable
		if( objRegister != null)  objRegister.hide();
		objRegister = null;

		//Setup the checkbook register display
		objRegister = new OutputBox( objMainWin,
		   "Checkbook Register" );
		objRegister.show();  //Show the empty register

		//Put in a header line describing the fields
		objRegister.printLine( "Check Num" + TAB + "Date" + TAB + "Description" + TAB + "Debit Amount" + TAB + "Balance" ); 	

		//Fill in the OutputBox register with any data it
		//should have
		for( int i = 0; (i <
		   objCheckBooks[intChkBkIndex].giveRegisterSize()); i++ ) {
			//Make sure data line isn't empty
			if( objCheckBooks != null )
				objRegister.printLine(
				   objCheckBooks[intChkBkIndex].giveRegister( i ) );
		}
	}


	/*
	  Method:	writeCheck

	  Purpose:	Gets input from the user and adds a check

	  Parameters:	intChkBkInd -- Index of current checkbook

	  Returns:	None
	*/
	private void writeCheck( int intChkBkInd ) {
		//Setup the labels for the input controll
		String strLabels[] = { "Check Num:", "Date:", "Pay To:",
		   "Written Amount:", "Numerical Amount:", "Memo:" };

		String strInputs[];  //Input given from user

		//Use a MultiInputBox object to gather data
		MultiInputBox objMultiIn = new MultiInputBox( objMainWin,
		   strLabels );

                //Increment the last check number by one for a default
		objMultiIn.setValue ( 0, String.valueOf((objCheckBooks[intChkBkInd].intLastCheckNum + 1)) );

		//Get the user's inputs
		strInputs = objMultiIn.getInputs();

		//If user cancelled, escape out
		if( objMultiIn.isCanceled() )  return;

		//Create the new check
		objCheckBooks[intChkBkInd].addCheck( Integer.parseInt(strInputs[0]), strInputs[1],
		   strInputs[2], strInputs[3], new Float(strInputs[4]).floatValue(), strInputs[5] );
	}


	/*
	  Method:	pickCheckBook

	  Purpose:	Allows the user to pick a checkbook
			The index of the chosen one is then returned

	  Parameters:	None

	  Returns:	(int) -- Index in objCheckBooks
	*/
	private int pickCheckBook() {
		//Make a ListBox object to list all the books
		ListBox objList = new ListBox( objMainWin, "Checkbooks" );

		//Check to see if there are any checkbooks
		if( objCheckBooks == null) {
			//Tell user no books
			MessageBox objMsgBox = new MessageBox(
			   objMainWin );
			objMsgBox.show( "No checkbooks" );

			return objList.CANCEL;
		}

		//Add the checkbooks to the list
		for( int i = 0; i < objCheckBooks.length; i++ )
			objList.addItem( objCheckBooks[i].strBookName );

		//Return the user's choice
		return objList.getSelectedIndex();
	}


	/*
	  Method:	voidCheck

	  Purpose:	Asks the user to pick a check to void

	  Parameters:	intChkBkInd -- Index of current checkbook

	  Returns:	None
	*/
	private void voidCheck( int intChkBkInd ) {
		//Used to get the check number
		InputBox objInput = new InputBox( objMainWin, "Input Required" );

		int intChkNum;  //Holds the check number of the check to void
		boolean bolWorked;  //Flag to see if check was voided

		//Ask the user for the check number
		intChkNum = objInput.getInteger( "Type in the check number of the check to void" );

		//Call the voidCheck method in CheckBook
		bolWorked = objCheckBooks[intChkBkInd].voidCheck( intChkNum );

		//See if it worked
		if( bolWorked != true ) {  //Failed
			//Make a message box and tell user void failed
			MessageBox objMsg = new MessageBox( objMainWin, true );
			objMsg.show( "Failed to void the check with check number: " + intChkNum );
		}

		//Register has note that check was voided, it will be displayed after this method
	}


	/*
	  Method:	withdraw

	  Purpose:	Asks the user how much they want to withdraw

	  Parameters:	intChkBkInd -- Index of current checkbook

	  Returns:	None
	*/
	private void withdraw( int intChkBkInd ) {
		//Used to get the check number
		InputBox objInput = new InputBox( objMainWin, "Input Required" );

		float fltWithdraw;  //Holds the amount to withdraw

		//Ask the user for the amount
		fltWithdraw = new Float(objInput.getString( "How much do you want to withdraw" )).floatValue();

		//Call the withdraw method in CheckBook
		objCheckBooks[intChkBkInd].withdraw( fltWithdraw );

		//Register has been updated, it will be displayed after this method
	}


	/*
	  Method:	deposit

	  Purpose:	Asks the user enter an amount to deposit

	  Parameters:	intChkBkInd -- Index of current checkbook

	  Returns:	None
	*/
	private void deposit( int intChkBkInd ) {
		//Used to get the amount
		InputBox objInput = new InputBox( objMainWin, "Input Required" );

		float fltAmount;  //Holds the amount to deposit

		//Ask the user for the amount
		fltAmount = new Float(objInput.getInteger( "Type in the amount to deposit" )).floatValue();

		//Call the deposit method in CheckBook
		objCheckBooks[intChkBkInd].deposit( fltAmount );


		//Register has been updated, it will be displayed after this method
	}


	/*
	  Method:	saveIt

	  Purpose:	Dumps all checkbook registers into OutputBox objects, then those objects are
			saved to disk

	  Parameters:	None

	  Returns:	None
	*/
	private void saveIt( ) {
		OutputBox objSaveRegBox[];  //For saving registers

		//See if there are any books to save, if not leave
		if( objCheckBooks == null )  return;

		objSaveRegBox = new OutputBox[objCheckBooks.length];  //Size the array of objects

		for( int i = 0; i < objCheckBooks.length; i++ ) {
			//Make a bunch of OutputBoxes to use for saving
			objSaveRegBox[i] = new OutputBox( objMainWin, "Register" );

			//Put in the name of the checkbook
			objSaveRegBox[i].printLine( "Name of checkbook:  " + objCheckBooks[i].strBookName );

			//Put in a header line describing the fields
			objSaveRegBox[i].printLine( "Check Num" + TAB + "Date" + TAB + "Description" + TAB + "Debit Amount" + TAB + "Balance" ); 	

			//Fill in the OutputBox register with any data it
			//should have
			for( int j = 0; (j <
			   objCheckBooks[i].giveRegisterSize()); j++ ) {
				objSaveRegBox[i].printLine( objCheckBooks[i].giveRegister( j ) );
			}
		}	
		
		//Now save all of those objects to unique files (based on book names)
		for( int i = 0; i < objSaveRegBox.length; i++ ) {
			objSaveRegBox[i].saveToFile( objCheckBooks[i].strBookName + "_Checkbook.data" );
		}

	}
}  //End of class CheckBookController


