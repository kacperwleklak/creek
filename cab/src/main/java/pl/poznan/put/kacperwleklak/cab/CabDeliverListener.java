package pl.poznan.put.kacperwleklak.cab;

import pl.poznan.put.kacperwleklak.cab.protocol.CabMessageID;

public interface CabDeliverListener {

    void cabDelver(CabMessageID messageID);
}
