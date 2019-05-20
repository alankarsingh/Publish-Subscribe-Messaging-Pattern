/*
 * This project is designed to use pub sub system for publishing and suscribing the events for User's or Clients.
 * A class PubSubAgent is designed to handle clients.
 * 
 * @author Alankar Singh
 * 
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientStockTrader {

	/*
	 * A main method is designed to create multiple threads for multiple users.
	 */
	public static void main(String[] args) throws Exception {

		Scanner scan = new Scanner(System.in);
		
		//local ip address is created.
		String localhost = "localhost";
		
		//Port number is created to connect with client.
		int port = 3000;
		
		// An object of Socket is created.
		Socket sock = null;
		try {
			sock = new Socket(localhost, port);
			/*
			 * Two threads are created one to receive messages from user and
			 * another to send request to server.
			 */
			new rec(sock).start();//Object of Socket is sent to Receive class. 
			new send(sock).start();//Object of Socket is sent to Send class.
		} catch (Exception ioe) {
			System.out.println("Server is disconnected. Kindly try again later.");
		}

	}
}

// A separate class is created to send the message to server 
class send extends Thread {

	private Socket sock;
	private Scanner scan = new Scanner(System.in);
	public OutputStream send;
	public DataOutputStream out;

	public send(Socket sock) {
		this.sock = sock;
	}

	public void run() {

		try {
			while (true) {
				out = new DataOutputStream(sock.getOutputStream());
				String message = scan.nextLine();
				out.writeUTF(message); // Sending messages to server using writeUTF
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Server is disconnected. Kindly try again later.");
		}

	}
}

//A separate class is created to send the message to server
class rec extends Thread {

	private Socket sock;
	public InputStream receive;
	public DataInputStream in;
	
	public rec(Socket sock) {
		this.sock = sock;
	}

	public void run() {

		try {
			in = new DataInputStream(sock.getInputStream());
			String receive;
			while ((receive = in.readUTF()) != null) {
				 // Receiveing and displaying messages from server using readUTF
				System.out.println("Server :" + receive);				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Server is disconnected. Kindly try again later.");
		}
	}
}
