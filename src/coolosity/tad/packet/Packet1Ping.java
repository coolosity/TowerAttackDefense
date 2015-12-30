package coolosity.tad.packet;

public class Packet1Ping extends Packet
{

	public Packet1Ping()
	{
		super(1);
	}

	@Override
	public Packet createFromString(String string)
	{
		return new Packet1Ping();
	}

}
