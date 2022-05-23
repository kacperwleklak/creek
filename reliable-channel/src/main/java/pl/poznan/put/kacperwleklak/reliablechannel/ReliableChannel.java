package pl.poznan.put.kacperwleklak.reliablechannel;

public interface ReliableChannel {
    void rbCast(byte[] msg);
    void rbSend(String address, byte[] msg);
    void registerListener(ReliableChannelDeliverListener deliverListener);
}
