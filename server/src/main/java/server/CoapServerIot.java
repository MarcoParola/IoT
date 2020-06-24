package server;

import org.eclipse.californium.core.CoapServer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class CoapServerIot extends CoapServer {
	
	static public Map <String, IotResource> resources_dictionary;
	static final int PORT = 1978;
	
	public static void main(String[] args) {
		
		resources_dictionary = new HashMap<String, IotResource>();
		
		// INITIALIZE COAP SERVER
		System.out.print("CoapServer Listening");
		CoapServerIot server = new CoapServerIot();
		server.add(new RegistrationResources("registration", server));
		server.start();
		
		
		// INITIALIZE COAP CLIENT
		Socket s=null;
	    ServerSocket ss2=null;
	    System.out.println("ServerSocket Listening");
	    
	    try{
	        ss2 = new ServerSocket(4445);
	    }
	    catch(IOException e){
		    e.printStackTrace();
		    System.out.println("Server error");
	    }

	    while(true){
	        try{
	            s = ss2.accept();
	            System.out.println("connection Established");
	            ServerThread st=new ServerThread(s);
	            st.start();
	        }
		    catch(Exception e){
		        e.printStackTrace();
		        System.out.println("Connection Error");
		    }
	    }
	}
	
	
	private static class ServerThread extends Thread{  

	    String line=null;
	    BufferedReader  is = null;
	    PrintWriter os=null;
	    Socket s=null;

	    public ServerThread(Socket s){
	        this.s=s;
	    }

	    public void run() {
	    try{
	        is= new BufferedReader(new InputStreamReader(s.getInputStream()));
	        os=new PrintWriter(s.getOutputStream());

	    }catch(IOException e){
	        System.out.println("IO error in server thread");
	    }

	    try {
	        line=is.readLine();
	        while(line.compareTo("QUIT")!=0){

	            // LIGHT
	            if(line.startsWith("READ_LIGHT")) {
	            	int room = Character.getNumericValue(line.charAt(line.length()-1));
	            	System.out.println(room);
	            	String req = resources_dictionary.get("Light sensor").getAddress() + "--" + resources_dictionary.get("Light sensor").getCoapBuilder();
	            	System.out.println("sto per inviare: " + req);
	            	os.println(req);
	            	os.flush();
	            }
	            
	         // READ CAMERA
	            else if(line.startsWith("CAMERA")) {
	            	int room = Character.getNumericValue(line.charAt(line.length()-1));
	            	System.out.println(room);
	            	String req = resources_dictionary.get("Camera sensor").getAddress() + "--" + resources_dictionary.get("Camera sensor").getCoapBuilder();
	            	System.out.println("sto per inviare: " + req);
	            	os.println(req);
	            	os.flush();
	            }
	            
	            // READ MUSIC
	            else if(line.startsWith("READ_MUSIC") || line.startsWith("SET_MUSIC")) {
	            	int room = Character.getNumericValue(line.charAt(line.length()-1));
	            	System.out.println(room);
	            	String req = resources_dictionary.get("Music actuator").getAddress() + "--" + resources_dictionary.get("Music actuator").getCoapBuilder();
	            	System.out.println("sto per inviare: " + req);
	            	os.println(req);
	            	os.flush();
	            }
	            
	            // READ VOLUME
	            else if(line.startsWith("READ_VOLUME") || line.startsWith("SET_VOLUME")) {
	            	int room = Character.getNumericValue(line.charAt(line.length()-1));
	            	System.out.println(room);
	            	String req = resources_dictionary.get("Volume actuator").getAddress() + "--" + resources_dictionary.get("Volume actuator").getCoapBuilder();
	            	System.out.println("sto per inviare: " + req);
	            	os.println(req);
	            	os.flush();
	            }
	            
	            // SET BULB
	            else if(line.startsWith("READ_BULB") || line.startsWith("SET_BULB")) {
	            	int room = Character.getNumericValue(line.charAt(line.length()-1));
	            	System.out.println(room);
	            	String req = resources_dictionary.get("BulbSwitch actuator").getAddress() + "--" + resources_dictionary.get("BulbSwitch actuator").getCoapBuilder();
	            	System.out.println("sto per inviare: " + req);
	            	os.println(req);
	            	os.flush();
	            }
	            
	            line=is.readLine();
	        }   
	    } catch (IOException e) {

	        line=this.getName(); //reused String line for getting thread name
	        System.out.println("IO Error/ Client "+line+" terminated abruptly");
	    }
	    catch(NullPointerException e){
	        line=this.getName(); //reused String line for getting thread name
	        System.out.println("Client "+line+" Closed");
	    }

	    finally{    
		    try{
		        System.out.println("Connection Closing..");
		        if (is!=null){
		            is.close(); 
		            System.out.println(" Socket Input Stream Closed");
		        }
		        if(os!=null){
		            os.close();
		            System.out.println("Socket Out Closed");
		        }
		        if (s!=null){
			        s.close();
			        System.out.println("Socket Closed");
		        }
		    }
		    catch(IOException ie){
		        System.out.println("Socket Close Error");
		    }
	    }
	    }
	}

}



