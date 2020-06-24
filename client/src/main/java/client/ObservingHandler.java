package client;

import java.sql.Timestamp;

import javax.swing.JTextField;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class ObservingHandler implements CoapHandler {
	
	JTextField text;
	
	public ObservingHandler(JTextField t){
		super();
		text = t;
	}

	public void onLoad(CoapResponse response) {
		try {
			String code = response.getCode().toString();
			if(code.startsWith("2"))
				getInformation(response.getResponseText());
			else
				System.out.println("Error: " + code);
		}
		catch(ParseException ex) {
			ex.printStackTrace();
		}

	}

	public void onError() {
		System.out.println("Error during observing");
	}
	
	private void getInformation(String resp) throws ParseException {
		
		String val = null;
		Timestamp time = null;
		JSONObject obj = (JSONObject) JSONValue.parseWithException(resp);
		
		if(obj.containsKey("timestamp"))
			time = new Timestamp(((Long)obj.get("timestamp"))*1000);
		else
			System.out.println("Timestamp NOT FOUND\n");
		
		if(obj.containsKey("camera"))
			val = obj.get("camera").toString();
		else
			System.out.println("Value of camera NOT FOUND\n");
		
		text.setText("  " + val + " " + time.toString());
	}

}
