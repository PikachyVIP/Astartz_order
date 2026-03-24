// QuestionDeletePayload.java
package gc.story.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import gc.story.Story;

public record QuestionDeletePayload() implements CustomPayload {
    public static final CustomPayload.Id<QuestionDeletePayload> ID =
            new CustomPayload.Id<>(Identifier.of(Story.MOD_ID, "question_delete"));

    public static final PacketCodec<PacketByteBuf, QuestionDeletePayload> CODEC =
            PacketCodec.of((value, buf) -> {}, buf -> new QuestionDeletePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}