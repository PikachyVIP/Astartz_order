package gc.story.events;

import com.mojang.serialization.Codec;
import gc.story.Story;
import gc.story.events.buff.*;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MutationStage3BuffHandler {

    public enum Buff {
        UNDERWATER("Подводник"),
        SUN_BURN("Солнечный ожог"),
        BROKEN_WINGS("Страх темноты"),
        SCAVENGER("Падальщик"),
        CRYSTALLIZATION("Кристаллизация"),
        WEAK_HEART("Слабое сердце"),
        HUNGER_CURSE("Проклятие голода"),
        ANTIGRAVITY("Антигравитация"),
        LOST("Потерянный"),
        FIRE("Огненый");

        private final String displayName;

        Buff(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Аттачмент для хранения текущего баффа игрока
    public static final AttachmentType<String> CURRENT_BUFF = AttachmentRegistry.create(
            Identifier.of(Story.MOD_ID, "current_buff"),
            builder -> builder.persistent(Codec.STRING).copyOnDeath()
    );

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(MutationStage3BuffHandler::onServerTick);
        UnderwaterBuff.register();
        BrokenWingsBuff.register();
        StoneKojaBuff.register();
        HitInfHandler.register();
        SunBuff.register();
        FireBuff.register();
       // ScavengerBuff.register();
        AntigravBuff.register();
        LostBuff.register();
    }

    private static void onServerTick(MinecraftServer server) {
        if (server.getTicks() % 20 != 0) return;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (InfectionHandler.getCurrentStage(player) != 3) {
                if (getCurrentBuff(player) != null) {
                    removeBuff(player);
                }
                continue;
            }

            MutationStage2Handler.Debuff playerDebuff = MutationStage2Handler.getCurrentDebuff(player);
            if (playerDebuff == null) continue;

            Buff correspondingBuff = getBuffFromDebuff(playerDebuff);
            if (correspondingBuff == null) continue;

            if (getCurrentBuff(player) != correspondingBuff) {
                applyBuff(player, correspondingBuff, server.getOverworld());
            }
        }
    }

    private static Buff getBuffFromDebuff(MutationStage2Handler.Debuff debuff) {
        switch (debuff) {
            case UNDERWATER:
                return Buff.UNDERWATER;
            case SUN_BURN:
                return Buff.SUN_BURN;
            case BROKEN_WINGS:
                return Buff.BROKEN_WINGS;
            case SCAVENGER:
                return Buff.SCAVENGER;
            case CRYSTALLIZATION:
                return Buff.CRYSTALLIZATION;
            case WEAK_HEART:
                return Buff.WEAK_HEART;
            case HUNGER_CURSE:
                return Buff.HUNGER_CURSE;
            case ANTIGRAVITY:
                return Buff.ANTIGRAVITY;
            case LOST:
                return Buff.LOST;
            case FIRE:
                return Buff.FIRE;
            default:
                return null;
        }
    }

    public static void applyBuff(ServerPlayerEntity player, Buff buff, World world) {
        player.setAttached(CURRENT_BUFF, buff.name());
        switch (buff) {
            case UNDERWATER:
                UnderwaterBuff.applyBuff(player, world);
                break;
            default:
                break;
        }
    }

    public static void removeBuff(ServerPlayerEntity player) {
        String buffName = player.getAttached(CURRENT_BUFF);
        if (buffName != null) {
            try {
                Buff buff = Buff.valueOf(buffName);
                removeBuffEffects(player, buff);
            } catch (IllegalArgumentException e) {
            }
        }
        player.setAttached(CURRENT_BUFF, null);
    }

    private static void removeBuffEffects(ServerPlayerEntity player, Buff buff) {
        switch (buff) {
            case UNDERWATER:
                UnderwaterBuff.removeBuff(player);
                break;
            default:
                break;
        }
    }

    public static Buff getCurrentBuff(ServerPlayerEntity player) {
        String buffName = player.getAttached(CURRENT_BUFF);
        if (buffName != null) {
            try {
                return Buff.valueOf(buffName);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }



    public static boolean hasBuff(ServerPlayerEntity player, Buff buff) {
        return getCurrentBuff(player) == buff;
    }
}