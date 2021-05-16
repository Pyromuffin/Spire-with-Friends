package chronoMods.network;

import chronoMods.network.steam.*;
import java.util.*;
import java.lang.*;
import java.nio.*;

// Wrapper class for a data packet
public class Packet {
    private ByteBuffer data;
    private RemotePlayer player;
  
    public Packet(RemotePlayer player, ByteBuffer data) {
        this.data = data;
        this.player = player;
    }
  
  	public boolean hasPacket() 
  	{ 
  		if (data.capacity() == 0)
  			return false;
  		return true;
  	}

  	public RemotePlayer player() { return player; }
  	public ByteBuffer data() { return data; }
}