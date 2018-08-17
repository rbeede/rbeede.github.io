/* Transfer
 *
 * @author Rodney Beede
 * @version 1.00  2004/10/15
 * @history 2004/10/15 -- First Version
 *
 * Sends the specified file or directory (recursive) to the specified machine
 *
*/

package SSL_File_Transfer;

import java.net.*;
import javax.net.ssl.*;
import java.io.*;
import java.util.*;
import java.text.NumberFormat;

public class Transfer {
	protected static String strPathSep = System.getProperty("file.separator");

	protected String strRemoteServer = null;
	protected int iPort = -1;
	protected String strFileList[] = null;	
	protected String strKeystoreFile = null;
	protected String strKeystorePassword = null;
	
	protected int iTimeout = 10000;  //10 second socket timeout

	/* Transfer
	 *
	 * Params:  String[4] (server, port, file or directory, keystore file)
	 * Returns:  None
	 * 
	 * Saves the specified options into the object
	 * If third option specified a directory does a recursive search on that
	 * directory to build the filelist.  Otherwise array of length 1 with file
	 * specified is contained in file list.
	*/
	public Transfer(String strServer, int iPort, String strFileOrDir, String strKeystoreFile, String strKeystorePassword) {
		this.strRemoteServer = strServer;
		this.iPort = iPort;
		this.strKeystoreFile = strKeystoreFile;
		this.strKeystorePassword = strKeystorePassword;
		
		System.setProperty("javax.net.ssl.keyStore", strKeystoreFile);
		System.setProperty("javax.net.ssl.keyStorePassword", strKeystorePassword);
		System.setProperty("javax.net.ssl.trustStore", strKeystoreFile);
		System.setProperty("javax.net.ssl.trustStorePassword", strKeystorePassword);

		File oCheckFile = new File(strFileOrDir);
		if(oCheckFile.isDirectory()) {
			System.out.println("Getting file listing for " + strFileOrDir);
			this.strFileList = getFileListing(strFileOrDir);
		} else {
			this.strFileList = new String[1];
			this.strFileList[0] = strFileOrDir;
		}
	}


	/* main
	 *
	 * Params:  String[] (program options)
	 * Returns:  None
	 * 
	 * Transfers the specified file(s) to the specified server via SSL
	*/
	public static void main(String args[]) {
		if(validateArgs(args) == false) {
			System.err.println("Invalid arguments specified.");
			printUsage();
			System.exit(1);
		}

		//args contains:  server, port, file or directory, keystore file
		Transfer oTransfer = new Transfer(args[0], Integer.parseInt(args[1]), args[2], args[3], args[4]);

		System.out.println("Starting send of files at " + (new Date()));
		oTransfer.send();
		System.out.println("Finished send of files at " + (new Date()));
	}

	/* validateArgs
	 *
	 * Params:  String[] (program options)
	 * Returns:  True if required options were passed.  False otherwise
	 * 
	 * Validates the options passed to the program
	*/
	public static boolean validateArgs(String args[]) {
		if(args == null || args.length < 5) {
			return false;
		}
		
		//Check the server name and port number for vadility
		try {
			InetSocketAddress checkOpts = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
		} catch(Exception e) {
			System.err.println("Invalid remote-machine and/or remote-port");
			return false;
		}
		
		//Check the file or directory for existance
		try {
			File checkOpts = new File(args[2]);
			if(checkOpts.exists() == false) {
				System.err.println(args[2] + " does not exist");
				return false;
			}
		} catch(Exception e) {
			return false;
		}
		
		//Check to see if a keystore file was specified and exists
		try {
			File checkOpts = new File(args[3]);
			if(checkOpts.exists() == false) {
				System.err.println(args[3] + " does not exist");
				return false;
			}

		} catch(Exception e) {
			return false;
		}

		if(args[4] == null)  return false;  //No keystore password specified

		//We made it this far, everything looks good
		return true;
	}
	
	/* printUsage
	 *
	 * Params:  None
	 * Returns:  None
	 * 
	 * Prints to stdout the usage
	*/
	public static void printUsage() {
		System.out.println("SSL_File_Transfer");
		System.out.println("");
		System.out.println("Written by Rodney Beede.");
		System.out.println("Usage:");
		System.out.println("\tjava SSL_File_Transfer.Transfer remote-machine remote-port file|dir keystore-file keystore-password");
		System.out.println("");
		System.out.println("Example:");
		System.out.println("\tjava -cp SSL_File_Transfer.jar SSL_File_Transfer.Transfer 10.1.1.1 54321 c:\\myfile.txt c:\\private\\MyKeystore mysecret");
		System.out.println("");
	}
	
	/* getFileListing
	 *
	 * Params:  Directory to recursively search
	 * Returns:  String[] of all files in all subdirectories of specified directory
	 *			      null if no files were found
	 * 
	 * Does a recursive search of all files in the specified directory and returns them
	*/
	protected String[] getFileListing(String strDir) {
		File oDir = new File(strDir);
		ArrayList oFileList = new ArrayList();  //For holding list without just directory entries
		String[] strList;

		strList = oDir.list();
		
		if(strList == null)  return null;
		
		for(int i = 0; i < strList.length; i++) {
			File oCurrItem = new File(strDir + strPathSep + strList[i]);

			if(oCurrItem.exists() == false)  System.err.println("Logical error with getFileListing.");
			if(oCurrItem.isDirectory()) {  //Need to do a search inside this sub-folder
				String[] strSubFolderList = getFileListing(oCurrItem.getPath());
				if(strSubFolderList == null)  continue;  //Sub-folder had no files in it, skip on ahead
				for(int j = 0; j < strSubFolderList.length; j++) {
					oFileList.add(strSubFolderList[j]);
				}
			} else {  //Current list item is a file, add it to "files" list
				oFileList.add(oCurrItem.getPath());
			}
		}

		return ((String[]) oFileList.toArray(new String[0]));
	}
	
	/* send
	 *
	 * Params:  None
	 * Returns:  True if no errors occured
	 *	     False otherwise
	 * 
	 * Establishes an ssl connection to the server, transmits each file, prints to stdout each file transmitted
	 * and closes the connection
	*/
	public boolean send() {
		SSLSocket sckSecure = null;
		BufferedReader buffIn = null;
		OutputStream out = null;

		System.out.println("Connecting to " + this.strRemoteServer + " on port " + this.iPort);
		
		try {
			sckSecure = (SSLSocket) SSLSocketFactory.getDefault().createSocket(this.strRemoteServer, this.iPort);
			sckSecure.setSoTimeout(this.iTimeout);
		} catch(Exception e) {
			System.err.println("Error occured connecting socket");
			System.err.println(e);
			return false;
		}
		

		//Setup a socket reader and writer
		try {
			buffIn = new BufferedReader(new InputStreamReader(sckSecure.getInputStream()));
			out = sckSecure.getOutputStream();
		} catch(IOException e) {
			System.err.println("Error creating input/output streams for connection with " + sckSecure.getRemoteSocketAddress());
			System.err.println(e);
			return false;
		}

			

		//First we must send the keystore password for auth
		try {
			out.write(this.strKeystorePassword.getBytes());
			out.write('\r');
			out.write('\n');
		} catch(IOException e) {
			System.err.println("Error sending keystore password");
			System.err.println(e);
			return false;
		}
		

		//Next we send the "<Session>\r\n" line to indicate start of session
		write(out, "<Session>");
		
		//Now we send each file
		for(int i = 0; i < strFileList.length; i++) {
			File currFile = new File(strFileList[i]);
			String strFilepath = null;

			strFilepath = currFile.getAbsolutePath();
			strFilepath = strFilepath.substring(0, strFilepath.lastIndexOf(currFile.getName()));

			write(out, "\t<File>");
			write(out, "\t\t<Name>" + currFile.getName() + "</Name>");
			write(out, "\t\t<Path>" + strFilepath + "</Path>");
			write(out, "\t\t<Content_Length>" + currFile.length() + "</Content_Length>");
			write(out, "\t\t<Content>");
			writeFile(out, currFile);
			write(out, "\t\t</Content>");
			write(out, "\t</File>");
		}


		//Clean everything up
		try {
			write(out, "</Session>\r\n");
			sckSecure.close();
		} catch(Exception e) {
			System.err.println("Error occured closing socket.");
			System.err.println(e);
			return false;
		}

		//Got this far with no problems, return true
		return true;
	}  //end of send


	//Writes the specified string to the output stream.  If errors occur they are outputted to stderr
	protected void write(OutputStream out, String strData) {
		try {
			out.write(strData.getBytes());
			out.write(new String("\r\n").getBytes());
		} catch(IOException e) {
			System.err.println("Error writing " + strData);
			System.err.println(e);
		}
	}

	//Writes the specified file to the specified stream
	protected void writeFile(OutputStream out, File fileToSend) {
		NumberFormat numForm = NumberFormat.getInstance();
		numForm.setGroupingUsed(true);  //For commas
		long lStartTime;
		long lEndTime;
		double dSendTimeSecs;

		if(fileToSend.length() < 1024) {  //Show in bytes
			System.out.print("Sending file " + fileToSend.getName() + " (" + fileToSend.length() + " Bytes) ... ");
		} else {  //Show in KB
			System.out.print("Sending file " + fileToSend.getName() + " (" + numForm.format(fileToSend.length()/1024) + "KB) ... ");
		}

		try {
			FileInputStream fis = new FileInputStream(fileToSend);
			double dPercentDone = 0;  //Amount of file we've sent
			double dPercentDoneMarked = 0;  //Percent of file we've shown done
			lStartTime = (new Date()).getTime();
			lEndTime = 0;

			//Send the file in 2048 byte blocks
			
			while(fis.available() > 0) {
				byte[] currBlock = new byte[2048];
				fis.read(currBlock);

				out.write(currBlock);

  				//Only print in increments of 25%
				dPercentDone = fileToSend.length() - fis.available();
				dPercentDone = dPercentDone / fileToSend.length();
				dPercentDone = dPercentDone * 100.0;

				if(dPercentDone > dPercentDoneMarked + 25) {
					System.out.print((int)dPercentDone + "%--");
					dPercentDoneMarked = dPercentDone;
				}
			}
			lEndTime = (new Date()).getTime();
		} catch(IOException e) {
			System.err.println("Error sending file " + fileToSend.getName());
			System.err.println(e);
			return;
		}

		dSendTimeSecs = (lEndTime - lStartTime) / 1000.0;
		numForm.setMinimumFractionDigits(3);
		System.out.println("Done in " + numForm.format(dSendTimeSecs) + " seconds");
		System.out.println("\tor " + numForm.format(dSendTimeSecs/60) + " mins)");
		System.out.println("\t" + numForm.format(fileToSend.length() / 1000 / dSendTimeSecs) + " KBytes / sec");
	}
}