package gc.story.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import gc.story.Story;

public record QuestionCreatePayload(String question) implements CustomPayload {
    public static final CustomPayload.Id<QuestionCreatePayload> ID =
            new CustomPayload.Id<>(Identifier.of(Story.MOD_ID, "question_create"));

    public static final PacketCodec<PacketByteBuf, QuestionCreatePayload> CODEC =
            PacketCodec.of(QuestionCreatePayload::write, QuestionCreatePayload::new);

    private QuestionCreatePayload(PacketByteBuf buf) {
        this(buf.readString());
    }

    private void write(PacketByteBuf buf) {
        buf.writeString(question);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}