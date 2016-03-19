package cn.daemon.arp;

import net.sourceforge.jpcap.capture.PacketCapture;
import net.sourceforge.jpcap.capture.PacketListener;
import net.sourceforge.jpcap.net.ARPPacket;
import net.sourceforge.jpcap.net.Packet;

public class ArpTest {

	private static final int INFINITE = -1;
	private static final int PACKET_COUNT = INFINITE;

	// BPF filter for capturing any packet
	private static final String FILTER = "arp";

	private PacketCapture capture;
	private String[] devices;

	public ArpTest() throws Exception {

		// Step 1: Instantiate Capturing Engine
		capture = new PacketCapture();

		// Step 2: Look up devices
		String[] devices = capture.lookupDevices();

		// Step 3: Open Device for Capturing (requires root)
		capture.open(devices[2].split("\\\n")[0], true);

		// Step 4: Add a BPF Filter (see tcpdump documentation)
		capture.setFilter(FILTER, true);

		// Step 5: Register a Listener for Raw Packets
		capture.addPacketListener(new PacketHandler());

		// Step 6: Capture Data (max. PACKET_COUNT packets)
		capture.capture(PACKET_COUNT);

	}

	public static void main(String[] args) {
		try {
			System.err.println("waiting for an arp packet.. ");
			ArpTest arpTest = new ArpTest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

class PacketHandler implements PacketListener {
	public void packetArrived(Packet packet) {
		if (!(packet instanceof ARPPacket)) {
			return;
		}
		ARPPacket arpPacket = (ARPPacket) packet;
		System.out.println(arpPacket);
		System.out.println("arpHeader £º"
				+ byte2HexString(arpPacket.getARPHeader()));
		System.out.println("arpData : "
				+ byte2HexString(arpPacket.getARPData()));
		// System.out.println(byte2HexString(arpPacket.getData()));
		// System.out.println(arpPacket.getHeader());
		System.out.println("destination hardware address : "
				+ arpPacket.getDestinationHwAddress());
		System.out.println("proto sender address : "
				+ arpPacket.getDestinationProtoAddress());
		System.out.println("source hardware address : "
				+ arpPacket.getSourceHwAddress());
		System.out.println("proto receiver address : "
				+ arpPacket.getSourceProtoAddress());
		System.out.println("operation : " + arpPacket.getOperation());

	}

	public String byte2HexString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (byte c : b) {
			sb.append(Integer.toHexString(c & 0xFF));
		}
		return sb.toString();
	}
}
