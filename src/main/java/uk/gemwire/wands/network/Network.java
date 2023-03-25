package uk.gemwire.wands.network;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import uk.gemwire.wands.Capabilities;

import java.util.Objects;
import java.util.function.Supplier;

import static uk.gemwire.wands.Wands.MODID;

public class Network {
    private static final String networkVer = "1";

    private static int PACKETID = 0;

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "types"),
            () -> networkVer,
            networkVer::equals,
            networkVer::equals
    );

    // Register valid packets, called from SocketNukes, the main class
    public static void setup() {
        CHANNEL.messageBuilder(FocusChangedPacket.class, PACKETID++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(FocusChangedPacket::toBytes)
                .decoder(FocusChangedPacket::new)
                .consumerMainThread(FocusChangedPacket::handle)
                .add();
    }

    // Send an arbitrary packet to the given player, from the server.
    public static void sendToClient(Object packet, ServerPlayer player) {
        CHANNEL.sendTo(packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    // Send an arbitrary packet to the server, from the client.
    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }

    public static class FocusChangedPacket {
        private final ResourceLocation config;

        // Deserializer - write this class into a buffer
        public FocusChangedPacket(FriendlyByteBuf buf) {
            config = buf.readResourceLocation();
        }

        public FocusChangedPacket(ResourceLocation config) {
            this.config = config;
        }

        // Serializer - read this class out of a buffer
        public void toBytes(FriendlyByteBuf buf) {
            buf.writeResourceLocation(config);
        }

        // Consumer - actually perform the intended task.
        public boolean handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() ->
                    Objects.requireNonNull(ctx.get().getSender())
                            .getMainHandItem()
                            .getCapability(Capabilities.WAND_FOCUS_CAPABILITY)
                            .ifPresent(cap -> cap.setFocus(config))
            );
            return true;
        }
    }
}