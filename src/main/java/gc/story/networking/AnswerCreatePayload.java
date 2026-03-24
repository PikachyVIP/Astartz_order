// AnswerCreatePayload.java
package gc.story.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import gc.story.Story;

public record AnswerCreatePayload(String answer) implements CustomPayload {
    public static final CustomPayload.Id<AnswerCreatePayload> ID =
            new CustomPayload.Id<>(Identifier.of(Story.MOD_ID, "answer_create"));

    public static final PacketCodec<PacketByteBuf, AnswerCreatePayload> CODEC =
            PacketCodec.of(AnswerCreatePayload::write, AnswerCreatePayload::new);

    private AnswerCreatePayload(PacketByteBuf buf) {
        this(buf.readString());
    }

    private void write(PacketByteBuf buf) {
        buf.writeString(answer);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}