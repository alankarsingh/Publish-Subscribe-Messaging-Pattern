import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

public class NewServerStockTrader {
	static HashMap<String, ArrayList<Integer>> seller = new HashMap<>();
	static HashMap<DataOutputStream, ArrayList<String>> buyer = new HashMap<>();
	static ArrayList<Integer> stocks;
	static ArrayList<String> record;
	static ArrayList<DataOutputStream> outs;

	public static void Buy(String name, DataOutputStream out, DataInputStream in,
			ArrayList<DataOutputStream> listOfClients) throws Exception {

		if (seller.size() == 0) {
			out.writeUTF("Currently no topic has been advertised." + "\n"
					+ "If you would like to sell a stock then press Enter and 2 or " + "\n"
					+ "To procceed further press Enter only");
		} else {

			Set<Entry<String, ArrayList<Integer>>> entryset = seller.entrySet();
			String topics = "Hello, which topic's would you like to buy : " + "\n"
					+ "Kindly Enter the topic name mentioned below :" + "\n";
			int count = 1;
			for (Entry<String, ArrayList<Integer>> entry : entryset) {
				topics = topics + count + ": " + entry.getKey().toUpperCase() + "  |  ";
				count++;
			}
			out.writeUTF(topics);
			String reply = in.readUTF().toLowerCase();
			record = new ArrayList<String>();
			String keys = "";
			for (Integer key : seller.get(reply)) {
				keys = keys + " " + key;
			}
			out.writeUTF("We have " + seller.get(reply).get(1) + " stocks available to sell." + "\n"
					+ "Per unit stock's rate is : " + seller.get(reply).get(0) + "\n"
					+ "How much would you like to buy");
			String num = in.readUTF();
			int buy = Integer.parseInt(num);
			int balance = seller.get(reply).get(1) - buy;
			if (seller.get(reply).get(1) == 0) {
				out.writeUTF("Sorry we don't have any stock available for given topic");
			} else {
				if (balance < 0) {
					out.writeUTF("Sorry We don't have sufficient number of stock available." + "\n"
							+ "Enter again the number of stock you would like to buy.");
					buy = Integer.parseInt(in.readUTF());
					balance = seller.get(reply).get(1) - buy;
					stocks = new ArrayList<Integer>();
					stocks.add(seller.get(reply).get(0));
					stocks.add(balance);
					seller.put(reply, stocks);
				} else {
					if (buyer.containsKey(out)) {
						int add = Integer.parseInt(buyer.get(out).get(1)) + buy;
						String newadd = "add";
						record.add(reply.toLowerCase());
						record.add(newadd);
						buyer.put(out, record);
					} else {
						record.add(reply);
						record.add(num);
						buyer.put(out, record);
					}
					stocks = new ArrayList<Integer>();
					stocks.add(seller.get(reply).get(0));
					stocks.add(balance);
					seller.put(reply, stocks);
				}
			}
			Notify(listOfClients);

		}
	}

	public static void Sell(String name, DataOutputStream out, DataInputStream in,
			ArrayList<DataOutputStream> listOfClients) throws Exception {

		out.writeUTF("Which topic do you want to Sell.");
		String input = in.readUTF().toLowerCase();
		if (buyer.containsKey(out)) {
			String value = buyer.get(out).get(0);
			if (value.equals(input)) {
				int stock = Integer.parseInt(buyer.get(out).get(1));
				out.writeUTF("Enter the per unit price and the total number of stocks you want to sell. " + "\n"
						+ "Enter your value just below to given content and with spaces." + "\n" + "Per unit Price"
						+ "   |   " + "Total number of stocks");
				String[] keywords = in.readUTF().split("\\s+");
				int totalstock = stock - Integer.parseInt(keywords[1]);
				int totalsell = stock + seller.get(input).indexOf(1);
				stocks = new ArrayList<Integer>();
				int temp = seller.get(input).indexOf(0);
				stocks.add(temp);
				stocks.add(totalsell);
				seller.put(input,stocks );
				record = new ArrayList<String>();
				record.add(value);
				record.add("totalstock");
				buyer.put(out, record);
			}
		} else {
			out.writeUTF("Enter the per unit price and the total number of stocks you want to sell. " + "\n"
					+ "Enter your value just below to given content and with spaces." + "\n" + "Per unit Price"
					+ "   |   " + "Total number of stocks");

			String[] keywords = in.readUTF().split("\\s+");
			ArrayList<Integer> keyword = new ArrayList<Integer>();
			for (int i = 0; i < keywords.length; i++) {
				keyword.add(Integer.parseInt(keywords[i]));
			}

			ArrayList<Integer> keys = new ArrayList<Integer>();
			for (int i = 0; i < keyword.size(); i++) {
				keys.add(keyword.get(i));
			}
			seller.put(input, keys);
		}
		Notify(listOfClients);
	}

	public static void Notify(ArrayList<DataOutputStream> listOfClients) throws Exception {
		Set<Entry<String, ArrayList<Integer>>> entryset = seller.entrySet();

		for (DataOutputStream topic : listOfClients) {

			String topics = "___________________________________________________" + "\n" + "The notification : " + "\n"
					+ "Following are the current stocks : " + "\n";
			int count = 1;
			for (Entry<String, ArrayList<Integer>> entry : entryset) {
				topics = topics + count + ": " + entry.getKey().toUpperCase() + "  |  ";
				count++;
			}

			topic.writeUTF(topics + "\n" + "_____________________________________________________________" + "\n"
					+ "Press Enter to continue or wait for the updates from the server ");
		}
	}

	public static void main(String[] args) throws Exception {

		try (ServerSocket ss = new ServerSocket(3000)) {
			System.out.println("Server is conneting to the clients. ");
			System.out.println("_______________________________________________________");
			while (true) {
				Socket sock = ss.accept();
				new Working(sock).start();
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}
}

/*
 * A separate class is desgined to connect clients with server. * Also, it
 * provides options for operation on events to clients.
 */
class NewWorking extends Thread {

	public Socket sock;
	static int client = 0;
	static ArrayList<DataOutputStream> listOfClients = new ArrayList<DataOutputStream>();
	Scanner scan = new Scanner(System.in);
	public String name;
	static ServerStockTrader sst = new ServerStockTrader();

	public NewWorking(Socket sock) {
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
								+ "Enter the number followed by the events to occur : " + "\n" + "1 :  Buy " + "\n"
								+ "2 :  Sell ");

				receive = in.readUTF();
				System.out.println(receive);

				if (receive.equals("1")) {
					sst.Buy(name, out, in, listOfClients);
				} else if (receive.equals("2")) {
					sst.Sell(name, out, in, listOfClients);
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