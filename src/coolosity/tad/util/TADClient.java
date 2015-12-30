package coolosity.tad.util;

import coolosity.tad.client.ServerHandler;
import coolosity.tad.packet.Packet;
import coolosity.tad.packet.Packet1Ping;

public class TADClient
{

	public static void main(String[] args)
	{
		TADLogger.setPrefix("[CLIENT] ");
		Packet.init();
		new TADClient();
	}
	
	private ServerHandler server;
	
	public TADClient()
	{
		server = new ServerHandler("localhost",7777,this);
		if(server.connect()==0)
		{
			TADLogger.log("Connected to "+server+" successfully");
		}
		else
		{
			TADLogger.err("Could not connect to "+server);
		}
	}
	
	public void onDisconnected()
	{
		TADLogger.log("Disconnected from server "+server);
	}
	
	public void onPacketReceived(Packet packet)
	{
		TADLogger.dev("Packet received: "+packet.getClass().getName());
		if(packet instanceof Packet1Ping)
		{
			server.sendPacket(new Packet1Ping());
		}
	}
}
