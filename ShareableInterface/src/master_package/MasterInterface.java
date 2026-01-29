package master_package;

import javacard.framework.Shareable;

public interface MasterInterface extends Shareable{
	public void shareable(short amount);
}
