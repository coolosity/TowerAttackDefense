package coolosity.tad.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import coolosity.tad.packet.Packet;
import coolosity.tad.util.TADClient;
import coolosity.tad.util.TADLogger;

public class ServerHandler implements Runnable
{

	private TADClient client;
	private Socket socket;
	private DataOutputStream out;
	private String ip;
	private int port;
	
	public ServerHandler(String ip, int port, TADClient client)
	{
		this.client = client;
		this.ip = ip;
		this.port = port;
		connect();
	}
	
	private void connect()
	{
		int status = -1;
		try
		{
			socket = new Socket(ip,port);
			try
			{
				out = new DataOutputStream(socket.getOutputStream());
				(new Thread(this)).start();
			} catch (IOException e)
			{
				TADLogger.err("An error occured while getting the output stream from server "+this.toString());
				e.printStackTrace();
				closeConnection();
			}
			status = 0;
		} catch (UnknownHostException e)
		{
			TADLogger.err("Error: Unknown Host");
			status = 1;
		} catch (IOException e)
		{
			TADLogger.err("Could not connect to "+ip+":"+port+" - "+e.getMessage());
			status = 2;
		}
		client.onConnected(status);
	}
	
	public void closeConnection()
	{
		try
		{
			socket.close();
		} catch (IOException e)
		{
			TADLogger.err("An error occured while disconnecting from server "+toString());
			e.printStackTrace();
		}
	}
	
	public void sendPacket(Packet packet)
	{
		try
		{
			out.writeUTF(packet.toString());
		} catch (IOException e)
		{
			TADLogger.err("An error occured while sending data to server "+this.toString()+". Data: "+packet.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		DataInputStream in;
		try
		{
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e)
		{
			TADLogger.err("An error occured while getting input stream of server socket");
			e.printStackTrace();
			return;
		}
		while(!socket.isClosed())
		{
			try
			{
				String data = in.readUTF();
				TADLogger.dev("Data received: "+data);
				client.onPacketReceived(Packet.fromString(data));
			} catch (IOException e)
			{
				if(e instanceof EOFException || e.getMessage().equals("Connection reset"))
				{
					closeConnection();
				}
				else if(!e.getMessage().equals("Socket closed"))
				{
					TADLogger.err("An error occured while reading data from input stream");
					e.printStackTrace();
					closeConnection();
				}
			}
		}
		client.onDisconnected();
	}
	
	@Override
	public String toString()
	{
		return ip+":"+port;
	}
}
