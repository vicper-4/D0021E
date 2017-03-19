package Sim;

/**
 * Bind Update message type.
 */
public class BindUpdate extends Message{
	private final NetworkAddr _deprecatedId;

	BindUpdate (NetworkAddr from, NetworkAddr to, int seq, NetworkAddr deprecated)
	{
		super(from,to,seq);
		_deprecatedId = deprecated;
	}
	BindUpdate (NetworkAddr from, NetworkAddr to, int seq, int ttl, NetworkAddr deprecated)
	{
		super(from,to,seq, ttl);
		_deprecatedId = deprecated;
	}

	public NetworkAddr getDeprecated()
	{
		return _deprecatedId;
	}
}
