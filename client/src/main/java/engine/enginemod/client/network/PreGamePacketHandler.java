package engine.enginemod.client.network;

import engine.Platform;
import engine.client.game.GameClientMultiplayer;
import engine.client.input.controller.EntityCameraController;
import engine.enginemod.client.gui.game.GuiServerConnectingStatus;
import engine.entity.CameraEntity;
import engine.event.Listener;
import engine.game.MultiplayerGameData;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.*;
import engine.server.network.packet.PacketDisconnect;
import engine.server.network.packet.PacketGameData;
import engine.server.network.packet.PacketLoginProfile;
import engine.server.network.packet.PacketLoginRequest;

public class PreGamePacketHandler {

    @Listener
    public static void onRequestLogin(PacketReceivedEvent<PacketLoginRequest> event) {
        if (event.getHandler().getStatus() == ConnectionStatus.HANDSHAKE) {
            event.getHandler().setStatus(ConnectionStatus.LOGIN, new ClientLoginNetworkHandlerContext(((HandshakeNetworkHandlerContext) event.getHandler().getContext())));
            event.getHandler().sendPacket(new PacketLoginProfile(Platform.getEngineClient().getPlayerProfile()));
        }
    }

    @Listener
    public static void onDisconnected(PacketReceivedEvent<PacketDisconnect> event) {
        Platform.getLogger().warn("Disconnected from server: {}", event.getPacket().getReason());
        if (Platform.getEngine().getCurrentGame() != null) {
            Platform.getEngine().getCurrentGame().terminate();
        }
        GuiServerConnectingStatus.launchDisconnectedScreen(event.getPacket().getReason());
    }

    @Listener
    public static void onReceivingGameData(PacketReceivedEvent<PacketGameData> event) {
        if (event.getHandler().isChannelOpen() && event.getHandler().getStatus() == ConnectionStatus.LOGIN) {
            Platform.getEngineClient().getGraphicsManager().getGUIManager().close();
            NetworkClient networkClient = ((ClientLoginNetworkHandlerContext) event.getHandler().getContext()).getClient();
            var context = new ClientGameplayNetworkHandlerContext();
            context.setClient(networkClient);
            event.getHandler().setStatus(ConnectionStatus.GAMEPLAY, context);
            var game = new GameClientMultiplayer(Platform.getEngineClient(), networkClient, MultiplayerGameData.fromPacket(event.getPacket()));
            Platform.getEngine().startGame(game);
            //TODO: move client player join to separate position
            game.getWorld("default").map(world -> world.spawnEntity(CameraEntity.class, 0, 6, 0))
                    .map(cameraEntity -> game.joinPlayer(Platform.getEngineClient().getPlayerProfile(), cameraEntity));
            game.getClientPlayer().setEntityController(new EntityCameraController());

        }
    }

}