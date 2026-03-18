package gc.story.events.buff;

import gc.story.events.MutationStage3BuffHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;


public class StoneKojaBuff {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(StoneKojaBuff::onServerTick);
    }
    private static final Identifier HEALTH_MODIFIER_ID = Identifier.of("b1c2d3e4-f5a6-7890-1234-567890abcdef");

    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (MutationStage3BuffHandler.hasBuff(player, MutationStage3BuffHandler.Buff.CRYSTALLIZATION)) {
                EntityAttributeInstance maxHealthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);

                if (maxHealthAttribute != null) {
                    maxHealthAttribute.removeModifier(HEALTH_MODIFIER_ID);

                    EntityAttributeModifier healthModifier = new EntityAttributeModifier(
                            HEALTH_MODIFIER_ID,
                            6,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    );

                    maxHealthAttribute.addPersistentModifier(healthModifier);

                    if (player.getHealth() > player.getMaxHealth()) {
                        player.setHealth(player.getMaxHealth());
                    }
                }

            }
        }
    }
}
