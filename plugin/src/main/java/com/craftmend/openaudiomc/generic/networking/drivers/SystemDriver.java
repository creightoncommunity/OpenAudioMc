package com.craftmend.openaudiomc.generic.networking.drivers;

import com.craftmend.openaudiomc.OpenAudioMc;
import com.craftmend.openaudiomc.generic.storage.enums.StorageKey;
import com.craftmend.openaudiomc.generic.networking.client.objects.player.ClientConnection;
import com.craftmend.openaudiomc.generic.networking.interfaces.SocketDriver;
import com.craftmend.openaudiomc.generic.networking.io.SocketIoConnector;
import com.craftmend.openaudiomc.generic.platform.Platform;
import com.craftmend.openaudiomc.generic.state.states.ConnectedState;
import com.craftmend.openaudiomc.generic.state.states.IdleState;
import io.socket.client.Socket;

public class SystemDriver implements SocketDriver {
    @Override
    public void boot(Socket socket, SocketIoConnector connector) {
        socket.on(Socket.EVENT_CONNECT, args -> {
            // connected with success
            OpenAudioMc.getInstance().getStateService().setState(new ConnectedState(connector.getLastUsedRelay()));
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> {
            // disconnected, probably with a reason or something
            OpenAudioMc.getInstance().getStateService().setState(new IdleState("Disconnected from the socket"));

            String message = Platform.translateColors(OpenAudioMc.getInstance().getConfiguration().getString(StorageKey.MESSAGE_LINK_EXPIRED));
            for (ClientConnection client : OpenAudioMc.getInstance().getNetworkingService().getClients()) {
                if (client.isWaitingToken()) {
                    client.getPlayer().sendMessage(message);
                    client.setWaitingToken(false);
                }
                if (client.isConnected()) {
                    client.onDisconnect();
                }
            }
        });

        socket.on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
            // failed to connect
            OpenAudioMc.getInstance().getStateService().setState(new IdleState("Connecting timed out, something wrong with the api, network or token?"));
        });

        socket.on("time-update", args -> {
            String[] data = ((String) args[args.length - 1]).split(":");
            long timeStamp = Long.parseLong(data[0]);
            long offset = Long.parseLong(data[1]);
            OpenAudioMc.getInstance().getTimeService().pushServerUpdate(timeStamp, offset);
        });
    }
}
