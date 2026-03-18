package gc.story.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ActivateAbilityPayload() implements CustomPayload {

    // Создаем ID для пакета
    public static final CustomPayload.Id<ActivateAbilityPayload> ID =
            new CustomPayload.Id<>(Identifier.of("gcstory", "activate_ability"));

    // Создаем codec - для пустого пакета используем unit()
    public static final PacketCodec<RegistryByteBuf, ActivateAbilityPayload> CODEC =
            PacketCodec.unit(new ActivateAbilityPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}