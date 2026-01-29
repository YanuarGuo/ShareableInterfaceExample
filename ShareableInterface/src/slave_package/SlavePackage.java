package slave_package;

import javacard.framework.*;
import master_package.MasterInterface;

public class SlavePackage extends Applet {

	final static byte DEBIT = (byte) 0x20;
	
	private short BALANCE = 100;
	private byte[] MASTER_AID = {(byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06};
	
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		new SlavePackage(bArray, bOffset, bLength);
	}
	
	protected SlavePackage(byte[] bArray, short bOffset, byte bLength){
		register();
	}

	public void process(APDU apdu) {
		byte[] buffer = apdu.getBuffer();
		
		if (selectingApplet()) {
			return;
		}	
		
		switch (buffer[ISO7816.OFFSET_INS] )
		{
			case DEBIT: debit(apdu);
			return;
			
			default: ISOException.throwIt (ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	
	private void debit(APDU apdu){
		
		short amount = 1;
		
		if(BALANCE < amount){
			ISOException.throwIt(ISO7816.SW_WRONG_DATA);
		}
		
		BALANCE = (short)(BALANCE - amount);
		
		requestMiles(amount);
	}
	
	
	private void requestMiles(short amount){
		AID aid = JCSystem.lookupAID(MASTER_AID, (short)0, (byte)MASTER_AID.length);
		
		if (aid == null){
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}
		
		MasterInterface sio = (MasterInterface)(JCSystem.getAppletShareableInterfaceObject(aid, (byte)00));
		
		if(sio == null){
			ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
		}
		
		sio.shareable(amount);
	}
}
