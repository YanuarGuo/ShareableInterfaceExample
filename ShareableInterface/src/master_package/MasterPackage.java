package master_package;

import javacard.framework.*;


public class MasterPackage extends Applet implements MasterInterface{
	
	private byte[] SLAVE_AID = {(byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x06, (byte)0x05};
	private short miles;
	
	public Shareable getShareableInterfaceObject(AID clientAID, byte parameter) {
		if (!clientAID.equals(
	            JCSystem.lookupAID(SLAVE_AID, (short)0, (byte)SLAVE_AID.length))) {
	        return null;
	    }
	    return this;
	}

	
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		new MasterPackage (bArray, bOffset, bLength);
	}

	protected MasterPackage(byte[] bArray, short bOffset, byte bLength) {
		register();
	}
	
	public void process(APDU apdu) {
		if (selectingApplet()) {
			return;
		}
	}
	
	public void shareable(short amount){
		miles = (short)(miles+amount);
	}
}