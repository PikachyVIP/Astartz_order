package gc.story.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record TaskButtonClickPayload(int taskIndex) implements CustomPayload {

    public static final CustomPayload.Id<TaskButtonClickPayload> ID =
            new CustomPayload.Id<>(Identifier.of("gcstory", "task_button_click"));

    public static final PacketCodec<RegistryByteBuf, TaskButtonClickPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER,
                    TaskButtonClickPayload::taskIndex,
                    TaskButtonClickPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}