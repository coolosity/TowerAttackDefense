package coolosity.tad.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import coolosity.tad.packet.Packet;
import coolosity.tad.util.TADLogger;

public class Client implements Runnable
{

	private TADServer server;
	private Socket socket;
	private DataOutputStream out;
	private int id;
	private int lastPing;
	
	public Client(Socket socket, int id, TADServer server)
	{
		this.server = server;
		this.socket = socket;
		this.id = id;
		try
		{
			out = new DataOutputStream(socket.getOutputStream());
			(new Thread(this)).start();
		} catch (IOException e)
		{
			TADLogger.err("An error occured while getting the output stream from client "+this.toString());
			e.printStackTrace();
			closeConnection();
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
			TADLogger.err("An error occured while getting the input stream of client "+this.toString());
			e.printStackTrace();
			return;
		}
		while(!socket.isClosed())
		{
			try
			{
				String data = in.readUTF();
				TADLogger.dev("Received \""+data+"\" from client "+this.toString());
				server.onPacketReceived(this, Packet.fromString(data));
			} catch (IOException e)
			{
				if(e instanceof EOFException || e.getMessage().equals("Connection reset"))
				{
					closeConnection();
				}
				else if(!e.getMessage().equals("Socket closed"))
				{
					TADLogger.err("An error occured while reading data from client "+this.toString());
					e.printStackTrace();
				}
			}
		}
		server.onDisconnected(this);
	}
	
	public void sendPacket(Packet packet)
	{
		try
		{
			out.writeUTF(packet.toString());
		} catch (IOException e)
		{
			TADLogger.err("An error occured while sending data to client "+this.toString()+". Data: "+packet.toString());
			e.printStackTrace();
		}
	}
	
	public void closeConnection()
	{
		try
		{
			socket.close();
		} catch (IOException e)
		{
			TADLogger.err("An error occured while closing connection with client "+this.toString());
			e.printStackTrace();
		}
	}
	
	public int getID()
	{
		return id;
	}
	
	public int getLastPing()
	{
		return lastPing;
	}
	
	public void setLastPing(int lastPing)
	{
		this.lastPing = lastPing;
	}
	
	@Override
	public String toString()
	{
		return "("+id+") "+socket.getInetAddress().getHostAddress();
	}
}
