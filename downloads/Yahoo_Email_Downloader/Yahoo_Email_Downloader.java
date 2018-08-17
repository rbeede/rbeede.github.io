/**
 * Yahoo_Email_Downloader
 *
 * Author:  Rodney Beede
 *
 * @version 1.00 04/09/17
 * @version 1.01 2004/12/18
 * @version 1.02 2005/13/14
 *
 * Downloads Yahoo e-mail and saves it to disk
 *
 * Recommended Logging Options:
 *	 -Dorg.apache.commons.logging.simplelog.defaultlog=debug -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog -Dorg.apache.commons.logging.simplelog.showlogname=true -Dorg.apache.commons.logging.simplelog.showdatetime=true
 *
 * History:  2004/12/18 -- Updated code to new formatting on Yahoo.
 	     2005/03/14 -- Updated code to work on Linux (fixed path sep problem)
	     		   Also added download delay into code to try to prevent tripping Yahoo's anti-bandwidth abuse system (yahoo error 999)
			   Updated code for change with attachments and Yahoo
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.util.Properties;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.protocol.*;
import org.apache.commons.httpclient.Header;

import java.io.*;
import java.util.*;
import java.text.*;

public class Yahoo_Email_Downloader {
	//All messages are logged here
	private static Logger log = Logger.getLogger(Yahoo_Email_Downloader.class.getName());

	private static String baseURL = "";
	
	protected static DecimalFormat MsgNumForm = new DecimalFormat("0000");

	protected static String FILESEP = System.getProperty("file.separator");

	protected static int PAUSE_TIME = 5000;  //5 seconds
	protected static int MSG_THRES = 5;  //Pause every 5 messages


	/* main method
	 *	Params:		Yahoo login name
	 *				Yahoo password
	 *	Returns:	none
	*/
    public static void main(String[] args) throws Exception {
		//Quite up logging for HttpClient, otherwise get lots of Warning Unknown Content-length
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");


		if(args != null && args.length > 0) {
			PropertyConfigurator.configure(args[0]);
		} else {
			Properties defaultLogProp = new Properties();
			defaultLogProp.setProperty("log4j.rootLogger", "DEBUG, A1");
			defaultLogProp.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
			defaultLogProp.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
			defaultLogProp.setProperty("log4j.appender.A1.layout.ConversionPattern", "%-5p %d [%t] %c %x - %m%n");
			PropertyConfigurator.configure(defaultLogProp);
	   	}
	

		//Check to see if user passed all required args
		if(args == null || args.length < 3) {
			log.fatal("Insufficent number of arguments.");
			log.info("Usage:  java Yahoo_Email_Downloader log4j.prop username password");
			System.exit(1);
		}



		String strUsername = args[1];
		String strPassword = args[2];
		
		HttpClient client = null;
		GetMethod getm = null;
		String strResponse;
		String strNextURL;  //For building next url to visit
		int iPos, iPos2;  //Used for searching inside strings

		YahooFolder[] folders = null;

		client = new HttpClient();  //Setup http client

		
		//Try the login page
		getm = new GetMethod("https://login.yahoo.com");
		
		log.info("Trying login page");
		client.executeMethod(getm);
		
		log.info("Logging in as " + strUsername);
		getPage(client,
				"https://login.yahoo.com/config/login?login=" + strUsername + "&passwd=" + strPassword + "&.done=http://mail.yahoo.com",
				"DUMP",
				"01 - redirect_to_mail.yahoo.com.htm");
		log.info("Going to main page");
		strResponse = getPage(client,
						 "http://mail.yahoo.com",
						 "DUMP",
						 "02 - main_page.htm");

		//Last response should have list of folders, parse them
		folders = getFolders(strResponse);
		//Also store base URL for accessing pages
		iPos = strResponse.indexOf("location='http://") + 10;
		iPos2 = strResponse.indexOf("/", iPos+7);
		baseURL = strResponse.substring(iPos, iPos2);

		
		//Writeout debug info
		for(int i=0; i < folders.length; i++) {
			log.debug(folders[i].getName() + " == " + folders[i].getURL());
		}
		
		
		//Go through each folder and grab it's messages
		for(int i=0; i < folders.length; i++) {
			String currName = folders[i].getName();
			String currURL = folders[i].getURL();
			
			String currPage = null;
			String currMsgURLs[] = null;  //Holds URLs to messages

			log.info("Reading " + currName + " folder");
			currPage = getPage(client, currURL, "DUMP" + FILESEP + currName, "_listing.htm");

			log.info("Reading messages in folder");
			currMsgURLs = getURLs(client, currPage, "ShowLetter");
			log.debug("Number of message urls is " + currMsgURLs.length);

			//Now loop through all the URL's and save the messages
			for(int j=0; j < currMsgURLs.length; j++) {
				if(j % MSG_THRES == 0) {
					log.debug("Pausing downloads to avoid Yahoo 999 error (abuse trip)");
					//Pause to try to prevent Yahoo 999 error (abuse trip)
					try {
						Thread.sleep( PAUSE_TIME );
					} catch( InterruptedException e ) {
					}
				}

				String strMsgFilename = "Msg #" + MsgNumForm.format(j) + ".htm";

				log.debug("\tSaving " + strMsgFilename);
				currPage = getPage(client, currMsgURLs[j], "DUMP" + FILESEP + currName, strMsgFilename);
				
				//Extracts Complete Message link, follows it, and saves msg with subject as filename
				saveMessage(client, currPage, "DUMP" + FILESEP + currName, j);
			}
		}

    }


	//This method simply accesses a page, saves it to disk, and returns it as a string
	//It follows cross-site redirection as well.
    public static String getPage(HttpClient httpclient, String sURL, String sDir, String sDumpName) throws java.io.IOException {
		String responseBody;
		GetMethod httpget = null;
		
		//If sURL doesn't start with http it's not valid
		//Just add static variable baseURL to it
		if(sURL.startsWith("http") == false) {
			sURL = baseURL + sURL;
		}

		httpget = new GetMethod(sURL);
		try {  //sometimes connection resets, so we try 2 times
			httpclient.executeMethod(httpget);
		} catch(Throwable e) {
			httpclient.executeMethod(httpget);
		}
	
		Header locationHeader = httpget.getResponseHeader("location");
		if (locationHeader != null) {
            String redirectLocation = locationHeader.getValue();
			log.info("Following redirect to " + redirectLocation);
			responseBody = getPage(httpclient, redirectLocation, sDir, sDumpName);
		} else {
			//Read in the page
			responseBody = new String(httpget.getResponseBody());

			//If sDumpName isn't empty Dump the page to a file
			if( sDumpName.compareTo("") != 0 ) {
				savePage(responseBody, sDir, sDumpName);
			}
		}
		
		return responseBody;
    }  //end getPage

	//This method saves the passed string to disk
    public static void savePage(String sData, String sDir, String sFileName) {
		File myFile = new File(sDir + FILESEP + sFileName);
		FileWriter myWriter;
		
		try {
			//Make sDir incase it doesn't exist
			log.debug("Trying to create directories " + sDir);
			new File(sDir).mkdirs();

			//Make the file
			log.debug("Trying to create file " + myFile.getPath());
			myFile.createNewFile();
			
			//Write the contents to the file
			myWriter = new FileWriter(myFile, false);  //non-append mode
			myWriter.write(sData);
			myWriter.close();  //Saves file to disk and close it
		} catch(Exception e) {
			log.fatal("Unable to write to disk for " + sFileName + " because " + e);
		}
    }  //end savePage


	//This method parses an html response for folder names & URLs
	public static YahooFolder[] getFolders(String strHtml) {
		String strParse;
		int iPos;
		int iPos2;
		ArrayList folders = new ArrayList();
		String strBase;
		
		//Get the URL base for the folder URLs
		iPos = strHtml.indexOf("location='http://") + 10;
		iPos2 = strHtml.indexOf("/", iPos+7);
		strBase = strHtml.substring(iPos, iPos2);

		iPos = strHtml.indexOf("defaultfolders");
		iPos = strHtml.indexOf("<li id=\"", iPos) + 8;
		while(iPos != (-1 + 8) && iPos != -1) {
			YahooFolder newFolder = new YahooFolder();
			iPos2 = strHtml.indexOf("\"", iPos);
			strParse = strHtml.substring(iPos, iPos2);
			newFolder.setName(strParse);
			iPos = strHtml.indexOf("href=\"", iPos2) + 6;
			iPos2 = strHtml.indexOf("\"", iPos);
			strParse = strHtml.substring(iPos, iPos2);
			newFolder.setURL(strBase + strParse);
			
			folders.add(newFolder);
			
			iPos = strHtml.indexOf("<li id=\"", iPos2) + 8;  //Get next folder
			//Do a quick check to make sure we didn't skip to customfolders
			iPos2 = strHtml.indexOf("customfolders");
			if(iPos > iPos2)  iPos = -1;  //We are done with folderlist
		}

		//Now grab our customfolders list
		iPos = strHtml.indexOf("customfolders");
		iPos = strHtml.indexOf("href=\"", iPos) + 6;
		while(iPos != (-1 + 6) && iPos != -1) {
			YahooFolder newFolder = new YahooFolder();
			
			iPos2 = strHtml.indexOf("\"", iPos);
			strParse = strHtml.substring(iPos, iPos2);
			newFolder.setURL(strBase + strParse);

			iPos = strHtml.indexOf(">", iPos2) + 1;
			iPos2 = strHtml.indexOf("</a>", iPos);
			strParse = strHtml.substring(iPos, iPos2);
			newFolder.setName(strParse);
			
			folders.add(newFolder);
			
			iPos = strHtml.indexOf("href=\"", iPos2) + 6;  //Get next folder
			//Do a quick check to make sure we didn't skip to customfolders
			iPos2 = strHtml.indexOf("</ul>", iPos2);
			if(iPos > iPos2)  iPos = -1;  //We are done with folderlist
		}

		//Return an array of YahooFolder
		return (YahooFolder[]) folders.toArray(new YahooFolder[0]);
	}


    //This method parses out a list of URLs.
    // sContent is the html content
	// sGrabKey is the string inbetween the a href= sequence and the closing "
    // This method is recursive in that it searches over multiple pages
    public static String[] getURLs(HttpClient client, String sContent, String sGrabKey) throws java.io.IOException {
		int iPos, iPos2;  //For position searching
		ArrayList urls = new ArrayList();
	
		iPos = sContent.indexOf(sGrabKey);
		while( iPos != -1 ) {
			iPos = sContent.lastIndexOf("a href=\"", iPos) + 8;
			iPos2 = sContent.indexOf("\"", iPos);
			String strCurrURL = sContent.substring(iPos,iPos2);

			//If it's not an #attachment link add it to the list
			//A duplicate can occur when a link has an attachment icon with link
			if(strCurrURL.indexOf("#attachment") == -1) {
				urls.add(strCurrURL);

			}
			iPos = sContent.indexOf(sGrabKey, iPos2);
		}  //end while

//TODO make so works for any language, not just italian
		//Check for succesive pages
		iPos = sContent.indexOf("id=\"messageviewselector");
		iPos = sContent.indexOf("class=\"last", iPos);
		iPos = sContent.indexOf(">Successivo</a>", iPos);
		if(iPos != -1) {  //found next page button
			String sndURLs[];
			String newContent;
	
			//extract the URL and call this method on that URL
			iPos = sContent.lastIndexOf("a href=", iPos) + 8;
			iPos2 = sContent.indexOf("\"", iPos);

			log.debug("\tAccessing next page for msgs");
			log.debug("\tNext page URL is " + sContent.substring(iPos, iPos2));
			newContent = getPage(client, sContent.substring(iPos, iPos2),"." + FILESEP + "DUMP" + FILESEP + "FOLDERS" + FILESEP, "MultiPage.html");
			sndURLs = getURLs(client, newContent, sGrabKey);
			
			//copy these second URLs into urls
			for(int i=0; i < sndURLs.length; i++) {
				urls.add(sndURLs[i]);
			}
		}
		
		return (String[]) urls.toArray(new String[0]);
	}  //end of getURLs method


	//Subroutine saveMessage
	//	Params:  httpclient with current session
	//			 html page with Intestizone Completa url
	//			 folder to save message in
	//			 message number (for making unique filenames)
	public static void saveMessage(HttpClient client, String strMsgPage, String strFolder, int iMsgNum) throws IOException {
		int iPos, iPos2;  //For string searching
		String strMsgURL;  //Link to complete message page
		String strMsgSubject;  //Grabbed from strMsgPage for filename
		String strMsgFilename = MsgNumForm.format(iMsgNum) + " -- ";

		//Find the complete message link (page includes all headers)
		iPos = strMsgPage.indexOf("<!-- START MAIN APPLICATION CONTENT -->");
		iPos = strMsgPage.indexOf("<span class=\"last\">", iPos);
		iPos = strMsgPage.indexOf("a href", iPos)+2;  //+2 so we get next a href
		iPos = strMsgPage.indexOf("a href", iPos) + 8;
		iPos2 = strMsgPage.indexOf("\"", iPos);
		strMsgURL = strMsgPage.substring(iPos, iPos2);
		

		//Grab the subject for use in the filename
//TODO, make it work in all langauges
		iPos = strMsgPage.indexOf("Oggetto:</td>");
		iPos = strMsgPage.indexOf("<td>", iPos) + 4;
		iPos2 = strMsgPage.indexOf("</td>", iPos);
		strMsgSubject = strMsgPage.substring(iPos, iPos2);
		if(strMsgSubject.indexOf("</a>") != -1) {  //Attachments link inside subject
			iPos = strMsgSubject.indexOf("</a>") + 4;
			strMsgSubject = strMsgSubject.substring(iPos);
		}
		if(strMsgSubject.indexOf("<") != -1) {
			//Some img tag or other unwanted html inside subject section
			//Remove the unwanted html
			iPos = strMsgSubject.indexOf("<");
			iPos2 = strMsgSubject.indexOf(">", iPos);
			if(iPos2 > strMsgSubject.length())  iPos2 = strMsgSubject.length() - 1;
			strMsgSubject = strMsgSubject.substring(iPos2+1);
			log.debug("Corrected subject to have no html.  " + strMsgSubject);
		}

		//Construct the filename and remove invalid chars
		strMsgFilename = strMsgFilename + strMsgSubject.trim() + ".htm";
		strMsgFilename = strMsgFilename.replace('*', ' ');
		strMsgFilename = strMsgFilename.replace('?', ' ');
		strMsgFilename = strMsgFilename.replace('|', ' ');
		strMsgFilename = strMsgFilename.replace('\\', ' ');
		strMsgFilename = strMsgFilename.replace('/', ' ');
		strMsgFilename = strMsgFilename.replace(':', ' ');
		strMsgFilename = strMsgFilename.replace('\'', ' ');
		strMsgFilename = strMsgFilename.replace('<', ' ');
		strMsgFilename = strMsgFilename.replace('>', ' ');
		strMsgFilename = strMsgFilename.replace('"', ' ');

		//Now access the msg url and save it
		getPage(client, strMsgURL, strFolder, strMsgFilename);

		//Grab and save any attachments
		saveAttachments(client, strMsgPage, strFolder, iMsgNum);
	}


	//Subroutine saveAttachments
	//	Params:  httpclient with current session
	//			 html page with Intestizone Completa url
	//			 folder to save attachments in
	//			 message number (for making unique filenames)
	public static void saveAttachments(HttpClient client, String strMsgPage, String strFolder, int iMsgNum) throws IOException {
		int iPos, iPos2;  //For string searching
		int jPos, jPos2;
		String strCurrURL;  //Link to attachment
		String strFilename;
		int iAttachNum = 0;

		//Find all the attachment links and follow them.
		iPos = strMsgPage.indexOf("Salva sul computer</a>");

		if(iPos == -1) {
			//No attachment found
			log.debug("\t\tNo attachment found for #" + iMsgNum);
		}

		while(iPos != -1) {
			iPos = strMsgPage.lastIndexOf("a href", iPos) + 8;
			iPos2 = strMsgPage.indexOf("\"", iPos);
			strCurrURL = strMsgPage.substring(iPos, iPos2);
			//We want the Download (or virus scan & download) url, not the save to yahoo storage
			if(strCurrURL.indexOf("Briefcase") != -1) {
				//Get the second a href link
				iPos = strMsgPage.indexOf("a href", iPos) + 8;
				iPos2 = strMsgPage.indexOf("\"", iPos);
				strCurrURL = strMsgPage.substring(iPos, iPos2);
			}

			//If link has VScan=1 in it then file has to be virus scanned before being downloadable.
			//We simply ignore this and download the file without Yahoo scanning
			if(strCurrURL.indexOf("VScan=1") != -1) {
				log.warn("Ignoring attachment virus check");
				jPos = strCurrURL.indexOf("VScan=1");
				jPos2 = jPos + 7;
				strCurrURL = strCurrURL.substring(0, jPos) + strCurrURL.substring(jPos2);
			}

			//Now look backwards for filename
			jPos = strMsgPage.lastIndexOf("alt=", iPos) + 5;
			jPos2 = strMsgPage.indexOf("\"", jPos);
			strFilename = MsgNumForm.format(iMsgNum) + "." + iAttachNum + " -- " + strMsgPage.substring(jPos, jPos2);
			
			//Now save the actual attachment
			log.debug("\t\tSaving attachment " + strFilename);
			saveAttachment(client, strCurrURL, strFolder, strFilename);

			iPos = strMsgPage.indexOf("<!-- END AD CALL AV -->", iPos);
			iAttachNum++;
		}
	}


	//This method simply accesses an attachment and saves it to disk
    public static void saveAttachment(HttpClient httpclient, String sURL, String sDir, String strFileName) throws java.io.IOException {
		byte[] responseBody;
		GetMethod httpget = null;
		
		//If sURL doesn't start with http it's not valid
		//Just add static variable baseURL to it
		if(sURL.startsWith("http") == false) {
			sURL = baseURL + sURL;
		}

		httpget = new GetMethod(sURL);
		try {  //sometimes connection resets, so we try 2 times
			httpclient.executeMethod(httpget);
		} catch(Throwable e) {
			httpclient.executeMethod(httpget);
		}
	
		//Read in the page
		responseBody = httpget.getResponseBody();
	
		//If sDumpName isn't empty Dump the page to a file
		if( strFileName.compareTo("") != 0 ) {
			File myFile = new File(sDir + FILESEP + strFileName);
			FileOutputStream myWriter;
			
			try {
				//Make sDir incase it doesn't exist
				new File(sDir).mkdirs();
				
				//Make the file
				myFile.createNewFile();
				
				//Write the contents to the file
				myWriter = new FileOutputStream(myFile, false);  //non-append mode
				myWriter.write(responseBody);
				myWriter.close();  //Saves file to disk and close it
			} catch(Exception e) {
				log.fatal("Unable to write to disk for " + strFileName);
			}

		}
    }  //end saveAttachment

}
