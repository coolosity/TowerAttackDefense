package coolosity.tad.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import coolosity.tad.packet.Packet;
import coolosity.tad.packet.Packet1Ping;
import coolosity.tad.util.TADLogger;

public class TADServer implements Runnable
{

	private static int nextID = 1;
	
	public static void main(String[] args)
	{
		TADLogger.setPrefix("[SERVER] ");
		Packet.init();
		int port = 7777;
		if(args.length>0)
		{
			try
			{
				port = Integer.parseInt(args[0]);
			}
			catch(Exception e)
			{
				TADLogger.err("Invalid port argument received. Defaulting to port "+port);
			}
		}
		new TADServer(port);
	}
	
	private ArrayList<Client> clients, toadd, toremove;
	private boolean running;
	private int currentTick;
	
	public TADServer(int port)
	{
		clients = new ArrayList<Client>();
		toadd = new ArrayList<Client>();
		toremove = new ArrayList<Client>();
		new SocketListener(this, port);
		running = true;
		(new Thread(this)).start();
		commandLine();
	}
	
	private void commandLine()
	{
		boolean exit = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(!exit)
		{
			try
			{
				String in = reader.readLine();
				String[] line = in.split(" ");
				String command = line[0];
				String[] args = new String[line.length-1];
				for(int i=0;i<line.length-1;i++)
				{
					args[i] = line[i+1];
				}
				try
				{
					parseCommand(command,args);
				}
				catch(Exception e)
				{
					TADLogger.err("An error occured while parsing command "+in);
				}
			}
			catch(IOException e)
			{
				TADLogger.err("An error occured while reading command line");
				e.printStackTrace();
			}
		}
	}
	
	private void parseCommand(String command, String[] args)
	{
		if(command.equalsIgnoreCase("kick"))
		{
			if(args.length>=1)
			{
				int id = Integer.parseInt(args[0]);
				for(int i=clients.size()-1;i>=0;i--)
				{
					if(clients.get(i).getID()==id)
					{
						clients.get(i).closeConnection();
					}
				}
			}
			else
			{
				System.out.println("Usage: /kick [client id]");
			}
		}
		else if(command.equalsIgnoreCase("tick"))
		{
			System.out.println(currentTick);
		}
	}
	
	public void onConnected(Socket socket)
	{
		Client c = new Client(socket,nextID++,this);
		c.setLastPing(currentTick-Settings.PING_TICKS_DELAY+30);
		toadd.add(c);
		TADLogger.log("Client connected: "+c.toString());
	}
	
	public void onDisconnected(Client client)
	{
		toremove.add(client);
		TADLogger.log("Client disconnected: "+client);
	}
	
	public void onPacketReceived(Client client, Packet packet)
	{
		TADLogger.dev("Packet received: "+packet.getClass().getName()+" from client "+client);
		if(packet instanceof Packet1Ping)
		{
			client.setLastPing(currentTick);
		}
	}

	@Override
	public void run()
	{
		while(running)
		{
			currentTick++;
			for(int i=clients.size()-1;i>=0;i--)
			{
				Client c = clients.get(i);
				if(currentTick-c.getLastPing()==Settings.PING_TICKS_DELAY)
				{
					TADLogger.dev("Pinging "+c);
					c.sendPacket(new Packet1Ping());
				}
				if(currentTick-c.getLastPing()>=Settings.DISCONNECT_TICKS)
				{
					c.closeConnection();
				}
			}
			while(toadd.size()>0)
			{
				clients.add(toadd.get(0));
				toadd.remove(0);
			}
			while(toremove.size()>0)
			{
				clients.remove(toremove.get(0));
				toremove.remove(0);
			}
			
			try
			{
				Thread.sleep(1000/Settings.TICKS_PER_SECOND);
			} catch (InterruptedException e)
			{
				TADLogger.err("An error occured while sleeping in the update thread");
				e.printStackTrace();
			}
		}
	}
}
