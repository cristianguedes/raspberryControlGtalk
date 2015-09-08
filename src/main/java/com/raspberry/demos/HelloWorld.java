package com.raspberry.demos;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * @author Ashwin Kumar
 * 
 */
public class HelloWorld {
	static JabberSmackAPI api = new JabberSmackAPI();

	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		connectTOGTalk();
	}
*/
	/**
	 * 
	 */
	private static void connectTOGTalk() {
		// Create XMPP connection to gmail.com server
		XMPPConnection connection = new XMPPConnection("gmail.com");

		try {
			// You have to put this code before you login
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			// Connect
			connection.connect();

			// Login with appropriate credentials
			connection.login("emailraspberry@gmail.com", "senha");

			// Get the user's roster
			Roster roster = connection.getRoster();

			// Print the number of contacts
			System.out.println("Number of contacts: " + roster.getEntryCount());
			api.setConnection(connection);
			// Enumerate all contacts in the user's roster
			for (RosterEntry entry : roster.getEntries()) {
				System.out.println("User: " + entry.getUser());
				if (entry.getUser().contains("pandumalladi")) {
					Chat chat = connection.getChatManager().createChat(
							entry.getUser(), api);
					chat.sendMessage("Testing Hi");
					api.fileTransfer(
							"C:\\Test.txt",
							entry.getUser());

				}
			}

			connection.disconnect();
		} catch (XMPPException e) {
			// Do something better than this!
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public static void connectToJabber() {
		
		
		ConnectionConfiguration cc = new ConnectionConfiguration("jabber.org",
				5222, "jabber.org");
		XMPPConnection connection = new XMPPConnection(cc);
		try {
			connection.connect();

			// You have to put this code before you login
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);

			// You have to specify your Jabber ID addres WITHOUT @jabber.org at the end
			connection.login("your.jabber", "password", "resource");

			// See if you are authenticated
			System.out.println(connection.isAuthenticated());

		} catch (XMPPException e1) {
			e1.printStackTrace();
		}
	}
}