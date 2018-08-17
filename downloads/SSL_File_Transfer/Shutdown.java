/* Shutdown
 *
 * @author Rodney Beede
 * @version 1.00  2004/10/25
 * @history 2004/10/25 -- First Version
 *
 * Sends the shutdown signal to the specified server
 *
*/

package SSL_File_Transfer;

import java.net.*;
import javax.net.ssl.*;
import java.io.*;
import java.util.*;

public class Shutdown {
	protected String strRemoteServer = null;
	protected int iPort = -1;
	protected String strKeystoreFile = null;
	protected String strKeystorePassword = null;
	
	protected int iTimeout = 10000;  //10 second socket timeout

	/* Shutdown
	 *
	 * Params:  String[4] (server, port, keystore file, keystore password)
	 * Returns:  None
	 * 
	*/
	public Shutdown(String strServer, int iPort, String strKeystoreFile, String strKeystorePassword) {
		this.strRemoteServer = strServer;
		this.iPort = iPort;
		this.strKeystoreFile = strKeystoreFile;
		this.strKeystorePassword = strKeystorePassword;
		
		System.setProperty("javax.net.ssl.keyStore", strKeystoreFile);
		System.setProperty("javax.net.ssl.keyStorePassword", strKeystorePassword);
		System.setProperty("javax.net.ssl.trustStore", strKeystoreFile);
		System.setProperty("javax.net.ssl.trustStorePassword", strKeystorePassword);
	}


	/* main
	 *
	 * Params:  String[] (program options)
	 * Returns:  None
	 * 
	*/
	public static void main(String args[]) {
		if(validateArgs(args) == false) {
			System.err.println("Invalid arguments specified.");
			printUsage();
			System.exit(1);
		}

		//args contains:  server, port, keystore file, keystore password
		Shutdown oShutdown = new Shutdown(args[0], Integer.parseInt(args[1]), args[2], args[3]);

		System.out.println("Sending kill signal to " + args[0]);
		oShutdown.sendKillSignal();
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			;
		}
		oShutdown.sendKillSignal();  //Since remote may have been busy already
		System.out.println("Finished at " + (new Date()));
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
		
		//Check the server name and port number for vadility
		try {
			InetSocketAddress checkOpts = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
		} catch(Exception e) {
			System.err.println("Invalid remote-machine and/or remote-port");
			return false;
		}
		
		//Check to see if a keystore file was specified and exists
		try {
			File checkOpts = new File(args[2]);
			if(checkOpts.exists() == false) {
				System.err.println(args[2] + " does not exist");
				return false;
			}

		} catch(Exception e) {
			return false;
		}

		if(args[3] == null)  return false;  //No keystore password specified

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
		System.out.println("\tjava SSL_File_Transfer.Shutdown remote-machine remote-port keystore-file keystore-password");
		System.out.println("");
		System.out.println("Example:");
		System.out.println("\tjava -cp SSL_File_Transfer.jar SSL_File_Transfer.Shutdown 10.1.1.1 54321 c:\\private\\MyKeystore mysecret");
		System.out.println("");
	}
	
	
	/* sendKillSignal
	 *
	 * Params:  None
	 * Returns:  None
	 * 
	*/
	public void sendKillSignal() {
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
			return;
		}
		

		//Setup a socket reader and writer
		try {
			buffIn = new BufferedReader(new InputStreamReader(sckSecure.getInputStream()));
			out = sckSecure.getOutputStream();
		} catch(IOException e) {
			System.err.println("Error creating input/output streams for connection with " + sckSecure.getRemoteSocketAddress());
			System.err.println(e);
			return;
		}

			

		//First we must send the keystore password for auth
		try {
			out.write(this.strKeystorePassword.getBytes());
			out.write('\r');
			out.write('\n');
		} catch(IOException e) {
			System.err.println("Error sending keystore password");
			System.err.println(e);
			return;
		}
		

		//Now just send the shutdown server command
		write(out, "<Shutdown/>");
		
		//Clean everything up
		try {
			sckSecure.close();
		} catch(Exception e) {
			System.err.println("Error occured closing socket.");
			System.err.println(e);
			return;
		}
		
	}  //end of sendKillSignal


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
}