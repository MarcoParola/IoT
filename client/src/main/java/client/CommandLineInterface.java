package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.*;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class CommandLineInterface {
	
	public static void main(String args[]) throws IOException{
	
	    InetAddress address=InetAddress.getLocalHost();
	    Socket s1=null;
	    String line=null;
	    BufferedReader br=null;
	    BufferedReader is=null;
	    PrintWriter os=null;
	    ObservingCoapClient obs_res = null; 
	    
	    try {
	        s1=new Socket(address, 4445);
	        br= new BufferedReader(new InputStreamReader(System.in));
	        is=new BufferedReader(new InputStreamReader(s1.getInputStream()));
	        os= new PrintWriter(s1.getOutputStream());
	    }
	    catch (IOException e){
	        e.printStackTrace();
	        System.err.print("IO Exception");
	    }
	
	    System.out.println("Client Address : "+address);
	    show_commands();
	    
	    String response=null;
	    try{
	    	System.out.print(" >> ");
	        line=br.readLine(); 
	        while(line.compareTo("QUIT")!=0){
	        	
	        	if(!check_command(line)) {
	        		System.out.println("Command or parameter NOT VALID");
	        		show_commands();
	                System.out.print(" >> ");
	                line=br.readLine();
	                continue;
	        	}
	        	
        		
                os.println(line);
                os.flush();
                response = is.readLine();
                //System.out.println(ConsoleColors.RED + "Server Response : "+ response + ConsoleColors.RESET);
                
                if(!response.equals("Error")) {
                	ResourceCoapClient new_resource = new ResourceCoapClient(response.split("--")[0], response.split("--")[1]);
                	CoapResponse get_response = null;
                	
                	
                	// READ LIGHT
                	if(line.startsWith("READ")) {
                		get_response = new_resource.get();
                		String ret = get_response.getResponseText().replace("\"", "");
                		ret = ret.replace("{", "").replace("}", "");
                		System.out.print(ConsoleColors.WHITE_BOLD + ret + ConsoleColors.RESET +"\n");
                	}
                	
                	// SET BULB
                	else if(line.startsWith("SET_BULB")) {
                		ArrayList<String> cmd = new ArrayList(Arrays.asList("OFF", "LOW", "MEDIUM", "HIGH"));
                		String val = line.split(" ")[line.split(" ").length-1];
                		if(cmd.contains(val))
                			get_response = new_resource.post("\"mode\"=\"" + val + "\"",MediaTypeRegistry.TEXT_PLAIN);
                		System.out.print(get_response.getResponseText()+"\n");
                		
                	}
                	
                	// SET MUSIC
                	else if(line.startsWith("SET_MUSIC")) {
                		ArrayList<String> cmd = new ArrayList(Arrays.asList("OFF", "ON"));
                		String val = line.split(" ")[line.split(" ").length-1];
                		if(cmd.contains(val))
                			get_response = new_resource.post("\"mode\"=\"" + val + "\"",MediaTypeRegistry.TEXT_PLAIN);
                		System.out.print(get_response.getResponseText()+"\n");
                		
                	}
                	
                	// SET VOLUME
                	else if(line.startsWith("SET_VOLUME")) {
                		int val = Integer.parseInt(line.split(" ")[line.split(" ").length-1]);
                		if(val < 0 || val > 100)
                			System.out.println("Il valore deve essere compreso tra 0 e 100");
                		else {
                			System.out.println("Prima della poooost" + val);
                			get_response = new_resource.post("\"volume\"=" + Integer.toString(val) ,MediaTypeRegistry.TEXT_PLAIN);
                			System.out.print(get_response.getResponseText()+"\n");
                		}
                	}
                	
                	// CAMERA ON
                	else if(line.contains("CAMERA ON")) {
                		if(obs_res == null) {
	                		obs_res = new ObservingCoapClient(response.split("--")[0], response.split("--")[1]);
	                		obs_res.start();
                		}
                		else
                			System.out.println("Already observing\n");
                	}
                
                	// CAMERA OFF
                	else if(line.equals("CAMERA OFF")) {
                		if(obs_res != null) {
                			obs_res.stop();
                			obs_res = null;
                		}
                		else
                			System.out.println("Nothing to observe\n");
                	}
    				
                }
                show_commands();
                System.out.print(" >> ");
                line=br.readLine();
            }	
	    }
	    catch(IOException e){
	        e.printStackTrace();
	        System.out.println("Socket read Error");
	    }
	    finally{
	
	        is.close();os.close();br.close();s1.close();
	                System.out.println("Connection Closed");
	    }
	}
	
	
	public static void show_commands() {
		System.out.println("\n\nType " + ConsoleColors.RED +"'READ_LIGHT'" + ConsoleColors.RESET + " to read light intensity");
		System.out.println("Type " + ConsoleColors.RED +"'READ_BULB'" + ConsoleColors.RESET + " to read light intensity");
		System.out.println("Type " + ConsoleColors.RED +"'SET_BULB OFF|LOW|MEDIUM|HIGH'" + ConsoleColors.RESET + " to read light intensity");
		System.out.println("Type " + ConsoleColors.RED +"'READ_MUSIC'" + ConsoleColors.RESET + " to read light intensity");
		System.out.println("Type " + ConsoleColors.RED +"'SET_MUSIC ON|OFF'" + ConsoleColors.RESET + " to read light intensity");
		System.out.println("Type " + ConsoleColors.RED +"'READ_VOLUME'" + ConsoleColors.RESET + " to read light intensity");
		System.out.println("Type " + ConsoleColors.RED +"'SET_VOLUME value'" + ConsoleColors.RESET + " to read light intensity");
		System.out.println("Type " + ConsoleColors.RED +"'CAMERA ON|OFF'" + ConsoleColors.RESET + " to read light intensity");
		System.out.println("Type " + ConsoleColors.RED +"'QUIT'" + ConsoleColors.RESET + " to read light intensity");
	}
	
	public static boolean check_command(String command) {
		ArrayList<String> cmd_list = new ArrayList(Arrays.asList("READ_LIGHT","READ_BULB","READ_MUSIC","SET_MUSIC ON","SET_MUSIC OFF",
																"SET_BULB LOW","SET_BULB OFF","SET_BULB MEDIUM","SET_BULB HIGH",
																"READ_VOLUME","CAMERA ON","CAMERA OFF","QUIT"));
		
		return ( cmd_list.contains(command) || command.startsWith("SET_VOLUME") );		
		
	}
	
	
	
}


