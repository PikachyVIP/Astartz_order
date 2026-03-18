package gc.story.events.debuff;

import com.mojang.serialization.Codec;
import gc.story.events.InfectionHandler;
import gc.story.events.MutationStage2Handler;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class HungerCurseDebuff {

    private static final AttachmentType<Long> LAST_CHECK_TIME = AttachmentRegistry.create(
            Identifier.of("gcstory", "last_check_time"),
            builder -> builder.persistent(Codec.LONG).copyOnDeath()
    );

    private static final AttachmentType<Boolean> EFFECTS_ACTIVE = AttachmentRegistry.create(
            Identifier.of("gcstory", "effects_active"),
            builder -> builder.persistent(Codec.BOOL).copyOnDeath()
    );

    private static final AttachmentType<Long> LAST_HUNGER_DECREMENT = AttachmentRegistry.create(
            Identifier.of("gcstory", "last_hunger_decrement"),
            builder -> builder.persistent(Codec.LONG).copyOnDeath()
    );

    private static final int MIN_INTERVAL = 100;
    private static final int EFFECT_DURATION = 400;
    private static final int WEAKNESS_AMPLIFIER = 1;
    private static final int SLOWNESS_AMPLIFIER = 1;
    private static final int CURSED_HUNGER_INTERVAL = 55;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(HungerCurseDebuff::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.HUNGER_CURSE)
                    && InfectionHandler.getCurrentStage(player) >= 2) {
                if (server.getTicks() % 2200 == 0) applyHungerCurse(player);
                if (server.getTicks() % 60 == 0) applyHungerDecay(player);
            } else {
                clearPlayerData(player);
            }
        }
    }

    private static void applyHungerDecay(ServerPlayerEntity player) {
        HungerManager hungerManager = player.getHungerManager();
        int currentFoodLevel = hungerManager.getFoodLevel();

        if (currentFoodLevel > 0) {
            hungerManager.setFoodLevel(currentFoodLevel - 1);
        }
    }


    private static void applyHungerCurse(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.HUNGER,
                EFFECT_DURATION,
                100,
                false,
                true,
                true
        ));


        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS,
                EFFECT_DURATION,
                SLOWNESS_AMPLIFIER,
                false,
                true,
                true
        ));

    }

    private static void clearPlayerData(ServerPlayerEntity player) {
        player.setAttached(LAST_CHECK_TIME, null);
        player.setAttached(EFFECTS_ACTIVE, null);
        player.setAttached(LAST_HUNGER_DECREMENT, null);
    }
}