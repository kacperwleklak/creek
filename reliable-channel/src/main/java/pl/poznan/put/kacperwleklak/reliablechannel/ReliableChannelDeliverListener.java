package pl.poznan.put.kacperwleklak.reliablechannel;

public interface ReliableChannelDeliverListener {
    void rDeliver(byte msgType, byte[] msg);
}
