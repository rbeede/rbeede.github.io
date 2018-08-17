/* Receive
 *
 * @author Rodney Beede
 * @version 1.00  2004/10/15
 * @history 2004/10/15 -- First Version
 *
 * Simply listens for connections using the given keystore and accepts files
 *
 * Run main method to do standalone execution.  To start listening instantiate this
 * class as a Receive object (without passing a socket connection) and call the listen method
*/

package SSL_File_Transfer;

import java.net.*;
import javax.net.ssl.*;
import java.io.*;
import java.util.*;
import java.text.NumberFormat;

public class Receive extends Thread {
	protected static String strPathSep = System.getProperty("file.separator");

	protected int iListenPort = -1;
	protected String strKeystoreFile = null;
	protected String strKeystorePassword = null;
	protected String strStoreDirectory = null;  //Where we save files to
	
	protected int iTimeout = 5000;  //5 second timeout period
	
	protected SSLServerSocket sckListener = null;  //Listens for incoming connections
	
	protected SSLSocket sckConnection = null;  //Handles accepted connection from sckListener
	
	protected boolean bKillSignal = false;  //Signal that this thread received shutdown server signal
	

	/* Receive
	 *
	 * Params:  port to listen on, keystore file, keystore password, store directory
	 * Returns:  None
	 * 
	 * Meant to be used to setup a server socket to listen for connections
	 * Listening won't occur until listen method is called
	*/
	public Receive(int iPort, String strKeystoreFile, String strKeystorePassword, String strStoreDirectory) throws Exception {
		this.iListenPort = iPort;
		this.strKeystoreFile = strKeystoreFile;
		this.strKeystorePassword = strKeystorePassword;
		this.strStoreDirectory = strStoreDirectory;

		System.setProperty("javax.net.ssl.keyStore", strKeystoreFile);
		System.setProperty("javax.net.ssl.keyStorePassword", strKeystorePassword);

		this.sckListener = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(iPort);
	}


	/* Receive
	 *
	 * Params:  remote client connection socket, keystore file, keystore password, store directory
	 * Returns:  None
	 * 
	 * Meant to be used for handling an accepted connection
	 * Performs validation of client and awaits files from client.
	 *
	 * Usually called as new thread.  start() method should be called after creating this object
	*/
	public Receive(SSLSocket sckConnection, String strKeystoreFile, String strKeystorePassword, String strStoreDirectory) {
		this.strKeystoreFile = strKeystoreFile;
		this.strKeystorePassword = strKeystorePassword;
		this.strStoreDirectory = strStoreDirectory;
		
		System.setProperty("javax.net.ssl.keyStore", strKeystoreFile);
		System.setProperty("javax.net.ssl.keyStorePassword", strKeystorePassword);

		this.sckConnection = sckConnection;  //Usually from sckListener.accept() call
	}


	/* main
	 *
	 * Params:  String[] (program options)
	 * Returns:  None
	 * 
	 * Starts listening for connections on the given port
	*/
	public static void main(String args[]) throws Exception {
		if(validateArgs(args) == false) {
			System.err.println("Invalid arguments specified.");
			printUsage();
			System.exit(1);
		}

		//args contains:  port, keystore file, keystore password, store directory
		Receive oReceiveListener = new Receive(Integer.parseInt(args[0]), args[1], args[2], args[3]);

		System.out.println("Started listening at " + (new Date()));
		oReceiveListener.listen();
		System.out.println("Finished listening at " + (new Date()));
	}

	/* validateArgs
	 *
	 * Params:  String[] (program options)
	 * Returns:  True if required options were passed.  False otherwise
	 * 
	 * Validates the options passed to the program
	*/
	public static boolean validateArgs(String args[]) {
		if(args == null || args.length < 4) {
			return false;
		}
		
		//Check the port number for correctness
		try {
			Integer.parseInt(args[0]);  //Throws error if not parseable number
		} catch(Exception e) {
			System.err.println("Invalid port number:  " + args[0]);
			System.err.println(e);
			return false;
		}
		
		//Check to see if a keystore file was specified and exists
		try {
			File checkOpts = new File(args[1]);
			if(checkOpts.exists() == false) {
				System.err.println(args[1] + " does not exist");
				return false;
			}

		} catch(Exception e) {
			System.err.println(e);
			return false;
		}

		if(args[2] == null)  return false;  //No keystore password specified

		if(args[3] == null)  return false;  //No store directory specified

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
		System.out.println("\tjava SSL_File_Transfer.Receive listen-port keystore-file keystore-password store-directory");
		System.out.println("");
		System.out.println("Example:");
		System.out.println("\tjava -cp SSL_File_Transfer.jar SSL_File_Transfer.Receive 54321 c:\\private\\MyKeystore mysecret c:\\uploads");
		System.out.println("");
	}
	

	/* listen
	 *
	 * Params:  None
	 * Returns:  None
	 * 
	 * Blocks with sckListener to accept connections.  Each new connection is started in a new thread
	 * of this class so multiple connections are possible.
	 *
	 * This method is to be used to start listening for connections.  It is useful when the class is created
	 * as an object with a defined listening port instead of a connection socket (see constructors)
	*/
	public void listen() throws Exception {
		boolean bKeepRunning = true;

		//Do sanitity check
		if(iListenPort == -1 || sckConnection != null) {
			//User called wrong constructor, this method is for listening to connection via multi-threading
			//not for handling connected sockets
			System.err.println("Logical programming error.  Called listen() without constructing Receive object for listening for connections.");
			throw new Exception();
		}
		
		Vector oConnections = new Vector();  //Holds our connection threads

		while(bKeepRunning) {
			System.out.println("Listening for connection");

			//Blocks here until connection attempt to server is made from client
			SSLSocket newSckConn = (SSLSocket) sckListener.accept();  
			
			System.out.println("Accepted new connection from " + newSckConn.getRemoteSocketAddress());
			
			//Set socket timeout value
			newSckConn.setSoTimeout(this.iTimeout);

			//Create Receive object to handle new connection
			Receive newConnection = new Receive(newSckConn, this.strKeystoreFile, this.strKeystorePassword, this.strStoreDirectory);
			
			//Start the thread so it processes data from the client
			oConnections.add(newConnection);
			newConnection.start();
			System.out.println("Started new thread for connection with " + newSckConn.getRemoteSocketAddress());

			
			//Loop through our current list and remove any threads that are finished
			for(int i=0; i < oConnections.size(); i++) {
				if( ((Receive)oConnections.get(i)).getKillSignal() == true) {  //Shutdown server
					bKeepRunning = false;
				}
				if( ((Receive)oConnections.get(i)).isAlive() == false) {
					oConnections.remove(i);
				}
			}
		}  //end of while bKeepRunning
		
		//Drop any connections
		oConnections = null;
	}  //end of listen method
	
	
	/* run
	 *
	 * Params:  None
	 * Returns:  None
	 * 
	 * This method is called when a Receive object has been created to handle a new incoming connection
	 * This method auth the client (via the keystore password) and receive files from the client
	*/
	public void run() {
		String strLineIn = null;
		BufferedReader buffIn = null;
		BufferedWriter buffOut = null;
		
		String strRemoteName = sckConnection.getRemoteSocketAddress().toString();

		System.out.println("Beginning conversation with " + strRemoteName);
		
		try {
			buffIn = new BufferedReader(new InputStreamReader(sckConnection.getInputStream()));
			buffOut = new BufferedWriter(new OutputStreamWriter(sckConnection.getOutputStream()));
		} catch(IOException e) {
			System.err.println("Error creating input/output stream for connection with " + strRemoteName);
			System.err.println(e);
			return;
		}

		//First thing sent by client must be keystore password
		try {
			strLineIn = buffIn.readLine();  //Blocks until data is read or socket timeout occurs
		} catch(IOException e) {
			System.err.println("Error reading keystore auth from " + strRemoteName);
			System.err.println(e);
			return;
		}

		
		//Verify that keystore password from client matches ours
		if(!strLineIn.equals(this.strKeystorePassword)) {
			System.err.println("Remote client (" + strRemoteName + ") did not send matching keystore password");
			System.err.println("Dropping connection with " + strRemoteName);
			try {
				sckConnection.close();
			} catch(IOException e) {
				;  //Don't really care
			}
			return;
		} else {
			System.out.println("Remote client (" + strRemoteName + ") validated successfully");
		}

		//Next client should start sending file
		//We expect the <startFile> line next
		strLineIn = readLine(buffIn);
		while(strLineIn != null && strLineIn.equals("</Session>") == false && sckConnection.isConnected()) {
			//Process current file being sent
			if(strLineIn.equals("\t<File>")) {  //Start of file in session
				readInFile();
				strLineIn = readLine(buffIn);
			} else if(strLineIn == null && sckConnection.isConnected()) {
				System.out.println("Remote client (" + strRemoteName + ") closed the connection or the connection was dropped");
			} else if(strLineIn.equals("<Shutdown/>")) {
				//Shutdown server request received
				this.bKillSignal = true;  //listen() method should pick this up and stop looping
				System.out.println("Kill signal received from " + strRemoteName + " at " + (new Date()).toString());
				return;
			} else {
				strLineIn = readLine(buffIn);
			}
		}
		
		//Cleanup everything
		try {
			System.out.println("Closing connection with " + strRemoteName);
			sckConnection.close();
		} catch(IOException e) {
			;  //Don't really care
		}
	}  //end of start()


	//This method reads data from the given stream
	protected String readLine(BufferedReader streamIn) {
		String strRemoteName = sckConnection.getRemoteSocketAddress().toString();
		String strLineIn = null;

		try {
			strLineIn = streamIn.readLine();  //Blocks until data is read or socket timeout occurs
		} catch(IOException e) {
			System.err.println("Error reading from stream for " + strRemoteName);
			System.err.println(e);
			return null;
		}
		
		return strLineIn;
	}
	
	
	//This method reads in a file from the remote client and saves it to disk
	protected void readInFile() {
		String strRemoteName = sckConnection.getRemoteSocketAddress().toString();
		String strLineIn = null;
		File newFile = null;
		FileOutputStream outFile = null;
		NumberFormat numForm = NumberFormat.getInstance();
		numForm.setGroupingUsed(true);  //For commas

		System.out.println("Beginning reception of file.");

		InputStream rawIn = null;
		BufferedReader buffIn = null;
		

		try {
			rawIn = sckConnection.getInputStream();
			buffIn = new BufferedReader(new InputStreamReader(rawIn));
		} catch(IOException e) {
			System.err.println("readInFile:  Error creating input/output stream for connection with " + strRemoteName);
			System.err.println(e);
			return;
		}
	

		//Read in file header information
		try {
			String strFilename = readLine(buffIn);  //<Name>
			strFilename = strFilename.substring(strFilename.indexOf("<Name>")+6, strFilename.indexOf("</Name>"));
			System.out.println("\tFilename:\t" + strFilename);
			
			String strFilepath = readLine(buffIn);  //<Path>
			strFilepath = strFilepath.substring(strFilepath.indexOf("<Path>")+6, strFilepath.indexOf("</Path>"));
			strFilepath = this.strStoreDirectory + strPathSep + strFilepath.replace(':', '_');
			System.out.println("\tPath:\t\t" + strFilepath);
			
			strLineIn = readLine(buffIn);  //<Content_Length>
			strLineIn = strLineIn.substring(strLineIn.indexOf("<Content_Length>")+16, strLineIn.indexOf("</Content_Length>"));
			int iContentLength = Integer.parseInt(strLineIn);
			System.out.println("\tSize:\t\t" + numForm.format(iContentLength) + " bytes");
			
			strLineIn = readLine(buffIn);  //<Content>

			//Build new file
			newFile = new File(strFilepath);
			newFile.mkdirs();  //And it's subdirectories
			newFile = new File(strFilepath + strPathSep + strFilename);
			
			outFile = new FileOutputStream(newFile);
			
			//Read from the stream and save to file
			//Send the file in 2048 byte blocks
			int iBytesRead = 0;
			while(iBytesRead < iContentLength) {
				byte[] currBlock = null;
				//Determine how much we need to read in.  By default we read in blocks of 2048
				//unless we know there is less than 2048 bytes of data left to read.
				if(iContentLength - iBytesRead < 2048) {
					currBlock = new byte[iContentLength - iBytesRead];
				} else {
					currBlock = new byte[2048];
				}

				int iLenRead = rawIn.read(currBlock);
				outFile.write(currBlock);

				iBytesRead += iLenRead;

				System.out.print((char) 167);  //the · char
			}
			outFile.close();
			System.out.println("DONE");

			strLineIn = readLine(buffIn);  //</Content>
			strLineIn = readLine(buffIn);  //</File>
		} catch(IOException e) {
			System.err.println("Error reading file transmission from client.");
			System.err.println(e);
			return;
		}

	}  //end of readInFile method
	
	
	//This method returns whether or not thread received a kill signal
	public boolean getKillSignal() {
		return this.bKillSignal;
	}
}