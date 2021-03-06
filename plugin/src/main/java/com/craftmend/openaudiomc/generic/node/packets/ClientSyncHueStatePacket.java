package com.craftmend.openaudiomc.generic.node.packets;

import com.craftmend.openaudiomc.OpenAudioMc;
import com.craftmend.openaudiomc.api.velocitypluginmessageframework.PacketWriter;
import com.craftmend.openaudiomc.api.velocitypluginmessageframework.StandardPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ClientSyncHueStatePacket extends StandardPacket {

    public UUID clientUuid;

    public ClientSyncHueStatePacket() {}

    public void handle(DataInputStream dataInputStream) throws IOException {
        this.clientUuid = OpenAudioMc.getGson().fromJson(dataInputStream.readUTF(), UUID.class);
    }

    public PacketWriter write() throws IOException {
        PacketWriter packetWriter = new PacketWriter(this);
        packetWriter.writeUTF(clientUuid.toString());
        return packetWriter;
    }
}
