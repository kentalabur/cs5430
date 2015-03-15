package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.net.ssl.SSLSocket;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.io.File;
import java.security.SecureRandom;

public class ServerConnection implements Runnable {
	static final char REG = 0;
	static final char AUTH = 1;
	
	//response type
	public enum Response {
            SUCCESS,
            FAIL,
            WRONG_PASS,
            WRONG_USR,
            NO_SVC, /* used when the requested service is not found. */
            NAUTH /* used when the user is not logged in, but tries an op other than login */
	}
	
	SSLSocket socket;
	String username; //user associated with this account
	boolean timed_out = false;

         
    public ServerConnection(SSLSocket s) {
    	this.socket = s;
    	
    }
    
    public void run() {
    	try {
    		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream()));
             BufferedReader r = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
             String m = "Welcome to SSL Reverse Echo Server."+
                " Please type in some words.";
             w.write(m,0,m.length());
             w.newLine();
             w.flush();
             while (true){
	             while ((m=r.readLine())!= null) {
	            	//check for authenticated user
	            	if (username != null){
	            		if (m.equals(".")) break;
		                char[] a = m.toCharArray();
		                int n = a.length;
		                for (int i=0; i<n/2; i++) {
		                   char t = a[i];
		                   a[i] = a[n-1-i];
		                   a[n-i-1] = t;
		                }
		                w.write(a,0,n);
		                w.newLine();
		                w.flush();
	                } else { //only allow registration or authentication
	                }
	             }
	             if (timed_out) //TODO this is placeholder, change later for actual timeout check
	            	 break;
             }
             w.close();
             r.close();
             socket.close();
    	} catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    /*
     * Create new account on server
     * Randomly generates a salt and stores a hashed
     * master password.
     * */
	public Response createAccount(String username, String password) throws Exception {
		// Directory already exists
		// Note: Not thread-safe 
		if (new File(username).isDirectory()){
			return Response.FAIL;
		}
		// Create a new directory
		new File(username).mkdirs();
        
		// Generate a salt randomly and append it to master password. 
		// Salt = 32 bytes since we use SHA-256
		byte[] toHash = new byte[32 + password.length()];
		System.arraycopy(password.getBytes(), 0, toHash, 0, password.length());
		byte[] salt = new SecureRandom().generateSeed(32);
		System.arraycopy(salt, 0, toHash, password.length(), 32);
		
		// Hash the master password
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(toHash);
		String hashedpassword = new String(messageDigest.digest());
        
		// Write hashed master password and the salt to a file named "master.txt"
		PrintWriter writer = new PrintWriter(username.concat("/master.txt"), "UTF-8");
		writer.println(hashedpassword);
		writer.println(salt);
		writer.close();
		return Response.SUCCESS;
	}
    
    /*
     * Change password for this user
     * */
    public Response changeAccountPassword(String old_password, String new_password){
    	return Response.FAIL;
    }
    
    /*
     * Delete this account
     * */
    public Response deleteAccount(String password){
    	return Response.FAIL;
    }
	
    /*
     * Authenticate user to system
     * */
    public Response authAccount(String username, String password){
    	return Response.FAIL;
    }
    
    /*
     * Returns a list of services for which credentials stored on server.
     * (Delimited by commas?)
     * */
    public String retrieveCredentials(){
    	return "";
    }
    
    /*
     * Get password for specific service
     * */
    public String getPassword(String service_name){
    	return "";
    }
    
    /*
     * Adds new credentials
     * */
    public Response addCredential(String service_name, String username, String password){
    	return Response.FAIL;
    }
    
    /*
     * Updates credentials with new password
     * */
    public Response updateCredential(String service_name, String password){
    	return Response.FAIL;
    }
    
    /*
     * Deletes specific credential for specified service
     * */
    public Response deleteCredential(String service_name){
    	return Response.FAIL;
    }
    
    /*
     * Checks master password against salted + hashed value stored on server
     * */
    public boolean checkPassword(String password){
    	return false;
    }
    
	   
	public void getAccountCredentialsList(String accountName) {
		   
	}
}
