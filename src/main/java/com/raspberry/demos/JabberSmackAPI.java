/**
 * 
 */
package com.raspberry.demos;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.proxy.ProxyInfo.ProxyType;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

/**
 * API With some Utitlity Methods
 * @author Ashwin Kumar
 *
 */
public class JabberSmackAPI implements MessageListener {

	XMPPConnection connection;
	private FileTransferManager manager;

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
		manager = new FileTransferManager(connection);
	}

	public JabberSmackAPI() {
	}

	public void login(String userName, String password) throws XMPPException {
		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
		
		
		ProxyInfo proxy = new ProxyInfo(ProxyType.HTTP,"proxy_internet",8080, "usuario","senha") ;
		
		
		ConnectionConfiguration config = new ConnectionConfiguration(
				"talk.google.com", 5222, "gmail.com",proxy);
		connection = new XMPPConnection(config);
		
		connection.connect();
		
		connection.login(userName, password);
	}

	public void fileTransfer(String fileName, String destination)
			throws XMPPException {

		// Create the file transfer manager
		// FileTransferManager manager = new FileTransferManager(connection);
		FileTransferNegotiator.setServiceEnabled(connection, true);
		// Create the outgoing file transfer
		OutgoingFileTransfer transfer = manager
				.createOutgoingFileTransfer(destination);

		// Send the file
		transfer.sendFile(new File(fileName), "You won't believe this!");
		try {
			Thread.sleep(10000);
		} catch (Exception e) {
		}
		System.out.println("Status :: " + transfer.getStatus() + " Error :: "
				+ transfer.getError() + " Exception :: "
				+ transfer.getException());
		System.out.println("Is it done? " + transfer.isDone());
	}

	public void sendMessage(String message, String to) throws XMPPException {
		Chat chat = connection.getChatManager().createChat(to, this);
		chat.sendMessage(message);
	}

	public void displayBuddyList() {
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();

		System.out.println("\n\n" + entries.size() + " buddy(ies):");
		for (RosterEntry r : entries) {
			System.out.println(r.getName());
		}
	}

	public void disconnect() {
		connection.disconnect();
	}

	public void processMessage(Chat chat, Message message) {
		if (message.getType() == Message.Type.chat)
			System.out.println(chat.getParticipant() + " says: "
					+ message.getBody());
	}

	/*public static void main(String args[]) throws XMPPException, IOException {
		// declare variables
		JabberSmackAPI c = new JabberSmackAPI();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String msg;

		// turn on the enhanced debugger
		XMPPConnection.DEBUG_ENABLED = true;

		// Enter your login information here
		c.login("emailraspberry@gmail.com", "senha");

		c.displayBuddyList();

		System.out.println("-----");

		System.out
				.println("Who do you want to talk to? - Type contacts full email address:");
		String talkTo = br.readLine();

		System.out.println("-----");
		System.out.println("All messages will be sent to " + talkTo);
		System.out.println("Enter your message in the console:");
		System.out.println("-----\n");

		while (!(msg = br.readLine()).equals("bye")) {
			c.sendMessage(msg, talkTo);
		}

		c.disconnect();
		System.exit(0);
	}*/

}