package master_package;

import javacard.framework.Shareable;

public interface MasterInterface extends Shareable{
	public short shareable(byte[] buffer, byte method, short amount);
}
