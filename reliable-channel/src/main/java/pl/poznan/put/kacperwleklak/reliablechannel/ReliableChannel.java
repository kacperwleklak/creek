package pl.poznan.put.kacperwleklak.reliablechannel;

public interface ReliableChannel {
    void rCast(byte[] msg);
    void rSend(String address, byte[] msg);
    void registerListener(ReliableChannelDeliverListener deliverListener);
}
