package coolosity.tad.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import coolosity.tad.util.TADLogger;

public class SocketListener implements Runnable
{

	private TADServer server;
	private ServerSocket serverSocket;
	
	public SocketListener(TADServer server, int port)
	{
		this.server = server;
		try
		{
			serverSocket = new ServerSocket(port);
			(new Thread(this)).start();
		} catch (IOException e)
		{
			TADLogger.err("An error occured while initializing the server socket on port "+port);
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		TADLogger.log("Server started on port "+serverSocket.getLocalPort());
		while(true)
		{
			try
			{
				Socket socket = serverSocket.accept();
				server.onConnected(socket);
			} catch (IOException e)
			{
				TADLogger.err("An error occured while accepting a new client on port "+serverSocket.getLocalPort());
				e.printStackTrace();
			}
		}
	}
	
}
