/*
 * This project is designed to use pub sub system for publishing and suscribing the events for User's or Clients.
 * A class EventManager is designed to handle Server and all the requests from the client.
 * 
 * @author Alankar Singh
 * 
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class EventManager extends Thread {

	static HashMap<String, ArrayList<String>> tableadvert = new HashMap<>();
	static HashMap<String, ArrayList<String>> tablepublish = new HashMap<>();
	static HashMap<String, ArrayList<DataOutputStream>> tablesubscribe = new HashMap<>();
	
	static ArrayList<String> articles = new ArrayList<String>();
	static ArrayList<DataOutputStream> outs = new ArrayList<DataOutputStream>();

	/*
	 * A method Advertise is designed to ask user or client to advertise the topics.
	 * 
	 * @param: DataOutputStream out, DataInputStream in, ArrayList<DataOutputStream
	 * 
	 * @return: None 
	 */
	public static void Advertise(DataOutputStream out, DataInputStream in, ArrayList<DataOutputStream> listOfClients)
			throws Exception {

		out.writeUTF("Which topic do you want to Advertise.");
		String input = in.readUTF();
		out.writeUTF("Enter the list of keywords with spaces to publish");

		String[] keywords = in.readUTF().split("\\s+");

		ArrayList<String> keys = new ArrayList<String>();
		for (int i = 0; i < keywords.length; i++) {
			keys.add(keywords[i].toLowerCase());
		}
		tableadvert.put(input, keys);
		Notify(listOfClients);

	}
	
	/*
	 * A method Notify is designed to notify all the clients after getting a new topic.
	 * 
	 * @param: ArrayList<DataOutputStream> listOfClients
	 * 
	 * @return: None 
	 */
	public static void Notify(ArrayList<DataOutputStream> listOfClients) throws Exception {

		Set<Entry<String, ArrayList<String>>> entryset = tableadvert.entrySet();

		for (DataOutputStream topic : listOfClients) {

			String topics = "___________________________________________________" + "\n"
					+ "The notificatin is that one new topic has been published: " + "\n"
					+ "Following are the current topics : " + "\n";
			int count = 1;
			for (Entry<String, ArrayList<String>> entry : entryset) {
				topics = topics + count + ": " + entry.getKey().toUpperCase() + "  |  ";
				count++;
			}
			topic.writeUTF(topics + "\n" + "_____________________________________________________________" + "\n"
					+ "Press Enter to continue or wait for the updates from the server ");
		}
	}

	/*
	 * A method Unsubscribe is designed to remove user from Subscribed list.
	 * 
	 * @param: DataOutputStream out, DataInputStream in
	 * 
	 * @return: None 
	 */
	public static void Unsubscribe(DataOutputStream out, DataInputStream in) throws Exception {

		if (tableadvert.size() == 0) {
			out.writeUTF("You have not subscribed for any events." + "\n"
					+ "If you would like to subscribe a topic then press Enter and 1 or " + "\n"
					+ "To procceed further press Enter only");
		} else {
			Set<Entry<String, ArrayList<String>>> entryset = tableadvert.entrySet();
			String topics = "Hello, from which following topics would you like to unscribe : " + "\n"
					+ "Kindly Enter the topic name mentioned below : ";
			int count = 1;
			for (Entry<String, ArrayList<String>> entry : entryset) {
				topics = topics + count + ": " + entry.getKey().toUpperCase() + "  |  ";
				count++;
			}
			out.writeUTF(topics + "\n" + "_____________________________________________________________" + "\n"
					+ "Press Enter to continue or wait for the updates from the server ");
			String reply = in.readUTF().toLowerCase();
			Set<Entry<String, ArrayList<DataOutputStream>>> entryunsub = tablesubscribe.entrySet();
			ArrayList<DataOutputStream> temp = new ArrayList<DataOutputStream>();
			temp = tablesubscribe.get(reply);
			temp.remove(out);
			tablesubscribe.put(reply, temp);
		}
	}

	/*
	 * A method Subscribed is designed to ask clients to subscribe for their favourite topics.
	 * 
	 * @param: DataOutputStream out, DataInputStream in
	 * 
	 * @return: None 
	 */
	public static void Subscribe(DataOutputStream out, DataInputStream in) throws Exception {

		if (tableadvert.size() == 0) {
			out.writeUTF("Currently no topic has been advertised." + "\n"
					+ "If you would like to advertise a topic then press Enter and 4 or " + "\n"
					+ "To procceed further press Enter only");
		} else {

			Set<Entry<String, ArrayList<String>>> entryset = tableadvert.entrySet();
			String topics = "Hello, under which topic would you like to subscribe : " + "\n"
					+ "Kindly Enter the topic name mentioned below :";
			int count = 1;
			for (Entry<String, ArrayList<String>> entry : entryset) {
				topics = topics + count + ": " + entry.getKey().toUpperCase() + "  |  ";
				count++;
			}
			out.writeUTF(topics);
			String reply = in.readUTF().toLowerCase();
			if (tablesubscribe.containsKey(reply)) {
				tablesubscribe.get(reply).add(out);

			} else {
				outs = new ArrayList<DataOutputStream>();
				tablesubscribe.put(reply, outs);
				tablesubscribe.get(reply).add(out);
			}
			out.writeUTF("_____________________________________________________________" + "\n"
					+ "Press Enter to continue or wait for the updates from the server ");
		}
	}

	/*
	 * A method publish is designed to ask client to publish the events in respective topics.
	 * 
	 * @param: DataOutputStream out, DataInputStream in, ArrayList<DataOutputStream
	 * 
	 * @return: None 
	 */
	public static void Publish(DataOutputStream out, DataInputStream in) throws Exception {

		if (tableadvert.size() == 0) {
			out.writeUTF("Currently no topic has been advertised." + "\n"
					+ "If you would like to advertise a topic then press Enter and 4 or " + "\n"
					+ "To procceed further press Enter only");
		} else {
			Set<Entry<String, ArrayList<String>>> entryset = tableadvert.entrySet();
			String topics = "\n"+"Hello, under which topic would you like to publish your article : " + "\n"
					+ "Kindly Enter the topic name mentioned below you want to subscribe under :"+"\n";
			int count = 1;
			for (Entry<String, ArrayList<String>> entry : entryset) {
				topics = topics + count + ": " + entry.getKey().toUpperCase() + "  |  ";
				count++;
			}
			out.writeUTF(topics);
			String reply = in.readUTF().toLowerCase();
			String keys ="";
			for(String key:tableadvert.get(reply)){
				keys = keys+" "+key;
			}
			out.writeUTF("Under which keyword would you like to publish"+"\n"+keys);
			String waste = in.readUTF();
			out.writeUTF("Write an article you want to publish");
			String article = in.readUTF();

			if (!tablepublish.containsKey(reply)) {
				articles = new ArrayList<String>();
				articles.add(article);
				tablepublish.put(reply, articles);
			} else {
				articles = tablepublish.get(reply);
				articles.add(article);
				tablepublish.put(reply, articles);
			}

			Set<Entry<String, ArrayList<DataOutputStream>>> entrysetsub = tablesubscribe.entrySet();
			ArrayList<DataOutputStream> subscribing = new ArrayList<DataOutputStream>();
			subscribing = tablesubscribe.get(reply);
			for (DataOutputStream entry : subscribing) {
				ArrayList<String> temp = new ArrayList<String>();
				temp = tablepublish.get(reply);
				String message = "The new published article is: " + "\n" + temp.get(temp.size() - 1);
				entry.writeUTF(message);
			}
			out.writeUTF("_____________________________________________________________" + "\n"
					+ "Press Enter to continue or wait for the updates from the server ");
		}
	}

	/*
	 * A main method is designed to invoke Server thread. 
	 * 
	 * @param: String[] args
	 * 
	 * @return: None 
	 */
	public static void main(String[] args) throws Exception {

		try (ServerSocket ss = new ServerSocket(3000)) {
			System.out.println("Server is conneting to the clients. ");
			System.out.println("_______________________________________________________");
			while (true) {
				Socket sock = ss.accept();
				new WorkingEventManager(sock).start();
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}
}

/*
 * A separate class is desgined to connect clients with server.
 * * Also, it provides options for operation on events to clients.
 */
class WorkingEventManager extends Thread {

	public Socket sock;
	static int client = 0;
	static ArrayList<DataOutputStream> listOfClients = new ArrayList<DataOutputStream>();
	Scanner scan = new Scanner(System.in);
	public String name;
	static EventManager em = new EventManager();

	public WorkingEventManager(Socket sock) {
		this.sock = sock;
		client++;
		System.out.println("Client " + client + " is connected.");
		System.out.println("-----------------------------------");
	}

	public void run() {
		try {

			OutputStream client = sock.getOutputStream();
			DataOutputStream out = new DataOutputStream(client);

			InputStream server = sock.getInputStream();
			DataInputStream in = new DataInputStream(server);
			out.writeUTF("Kindly write your name to connect with server.");

			listOfClients.add(out);
			name = in.readUTF();
			System.out.println();
			System.out.println("Client's name is " + name);

			String receive, send;

			while (true) {

				out.writeUTF(
						"Hello, " + name + " : " + "\n" + "___________________________________________________________"
								+ "\n" + "Now What would you like to do from following options:" + "\n"
								+ "Enter the number followed by the events to occur : " + "\n" + "1 :  Subscribe "
								+ "\n" + "2 :  Unsubscribe " + "\n" + "3 :  Publish " + "\n" + "4 :  Advertise " + "\n"
								+ "5 : Nothing");

				receive = in.readUTF();
				System.out.println(receive);

				if (receive.equals("1")) {
					em.Subscribe(out, in);
				} else if (receive.equals("2")) {
					em.Unsubscribe(out, in);
				} else if (receive.equals("3")) {
					em.Publish(out, in);
				} else if (receive.equals("4")) {
					em.Advertise(out, in, listOfClients);
				} else {
					out.writeUTF(
							"Press Enter to continue with other works or get notified from server on recent activities.");
				}
				receive = in.readUTF();

			}
		} catch (Exception ioe) {
			System.out.println("Clients are disconnected.");
		} finally {
			try {
				sock.close();
			} catch (Exception ioe) {
				System.out.println("Clients are disconnected.");
			}
		}
	}
}