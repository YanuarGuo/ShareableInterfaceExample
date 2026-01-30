package master_package;

import javacard.framework.*;


public class MasterPackage extends Applet implements MasterInterface{
	
	private final static byte[] SLAVE_AID = {(byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x06, (byte)0x05};
	private final static byte PARAMETER = 01; 
	private short BALANCE = 1000;
	
	public Shareable getShareableInterfaceObject(AID clientAID, byte parameter){
		if (!clientAID.equals(
	            JCSystem.lookupAID(SLAVE_AID, (short)0, (byte)SLAVE_AID.length))){
			return null;
	    }
		
		if (parameter != PARAMETER) {
			return null;
	    }
		
	    return this;
	}

	public static void install(byte[] bArray, short bOffset, byte bLength){
		new MasterPackage (bArray, bOffset, bLength);
	}

	protected MasterPackage(byte[] bArray, short bOffset, byte bLength){
		register();
	}
	
	public void process(APDU apdu){
		if (selectingApplet()){
			return;
		}
	}
	
	public short shareable(byte[] buffer, byte method, short amount){
		switch ((byte)method){
			case (byte) 0x20: 
				creditProcess(amount);
				return BALANCE;
			case (byte) 0x21:
				debitProcess(amount);
				return BALANCE;
			default: 
				ISOException.throwIt (ISO7816.SW_INS_NOT_SUPPORTED);
		}
		return 0;
	}
	
	public void creditProcess(short amount){
		BALANCE = (short)(BALANCE + amount);
	}
	
	public void debitProcess(short amount){
		BALANCE = (short)(BALANCE - amount);
	}
}