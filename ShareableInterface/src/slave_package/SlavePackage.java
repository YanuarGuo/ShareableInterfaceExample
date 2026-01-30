package slave_package;

import javacard.framework.*;
import master_package.MasterInterface;

public class SlavePackage extends Applet {

	final static byte DEBIT = (byte) 0x21;
	final static byte CREDIT = (byte) 0x20;
	final static byte[] MASTER_AID = {(byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06};
	
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
			case CREDIT: credit(apdu);
			return;
			case DEBIT: debit(apdu);
			return;
			
			default: ISOException.throwIt (ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	
	private void credit(APDU apdu){
		byte[] buffer = apdu.getBuffer();
		short amount = 1;
		short outLen = 2;
		
		requestMiles(buffer, CREDIT, amount);
		
		apdu.setOutgoingAndSend((short)0, outLen);
	}
	
	private void debit(APDU apdu){
		byte[] buffer = apdu.getBuffer();
		short amount = 1;
		short outLen = 2;
		
		requestMiles(buffer, DEBIT, amount);
		
		apdu.setOutgoingAndSend((short)0, outLen);
	}
	
	private void requestMiles(byte[] buffer, byte method, short amount){
		AID aid = JCSystem.lookupAID(MASTER_AID, (short)0, (byte)MASTER_AID.length);
		
		if (aid == null){
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}
		
		MasterInterface sio = (MasterInterface)(JCSystem.getAppletShareableInterfaceObject(aid, (byte)00));
		
		if(sio == null){
			ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
		}
		
		 short currentBalance = sio.shareable(buffer, method, amount);
		 Util.setShort(buffer, (short)0, currentBalance);
	}
}
