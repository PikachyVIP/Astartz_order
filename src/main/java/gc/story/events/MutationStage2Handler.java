package gc.story.events;

import com.mojang.serialization.Codec;
import gc.story.Story;
import gc.story.events.debuff.ScavengerDebuff;
import gc.story.items.Devourer;
import gc.story.items.Liberation;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.Random;

public class MutationStage2Handler {

    private static final Random RANDOM = new Random();

    // Перечисление всех возможных дебаффов
    public enum Debuff {
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

        Debuff(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Аттачмент для хранения текущего дебаффа игрока
    public static final AttachmentType<String> CURRENT_DEBUFF = AttachmentRegistry.create(
            Identifier.of(Story.MOD_ID, "current_debuff"),
            builder -> builder.persistent(Codec.STRING).copyOnDeath()
    );

    // UUID для модификаторов (нужны уникальные ID для каждого дебаффа)
    private static final Identifier HEALTH_MODIFIER_ID = Identifier.of("b1c2d3e4-f5a6-7890-1234-567890abcdef");


    public static void register() {
        // Регистрируем обработчик тиков для дебаффов, которые требуют постоянной проверки
        ServerTickEvents.END_SERVER_TICK.register(MutationStage2Handler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        // Проверяем раз в секунду (20 тиков)
        if (server.getTicks() % 20 != 0) return;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (InfectionHandler.getCurrentStage(player) < 2) {
                continue;
            }
            Debuff currentDebuff = getCurrentDebuff(player);
            if (currentDebuff == null) continue;

            // Применяем тиковые эффекты для каждого дебаффа
            switch (currentDebuff) {
                case SUN_BURN:
                    applySunBurnTick(player, server.getOverworld());
                    break;
                case BROKEN_WINGS:
                    break;
                case SCAVENGER:
                    // Проверка еды будет в отдельном обработчике событий
                    break;
                case CRYSTALLIZATION:
                    // Эффект уже постоянный, ничего не делаем
                    break;
                case WEAK_HEART:
                    // Эффект уже применен, ничего не делаем
                    break;
                case HUNGER_CURSE:
                  //  applyHungerCurseTick(player);
                    break;
                case ANTIGRAVITY:
                    applyAntigravityTick(player);
                    break;
                case UNDERWATER:
                    //  applyUnderwaterTick(player);
                    break;
            }
        }
    }

    /**
     * Получает случайный дебафф из списка
     * @return случайный Debuff
     */
    public static Debuff getRandomDebuff() {
        Debuff[] debuffs = Debuff.values();
        return debuffs[RANDOM.nextInt(debuffs.length)];
    }


    public static void applyDebuff(ServerPlayerEntity player, Debuff debuff) {
        // Сохраняем дебафф в аттачмент
        player.setAttached(CURRENT_DEBUFF, debuff.name());

        // Применяем эффекты в зависимости от дебаффа
        switch (debuff) {
            case UNDERWATER:
              //  applyUnderwaterDebuff(player);
                break;
            case SUN_BURN:
                //applySunBurnDebuff(player);
                break;
            case BROKEN_WINGS:
                // applyBrokenWingsDebuff(player);
                break;
            case SCAVENGER:
                applyScavengerDebuff(player);
                break;
            case CRYSTALLIZATION:
                applyCrystallizationDebuff(player);
                break;
            case WEAK_HEART:
                //WeakHeartDebuff.applyWeakHeartDebuff(player);
                break;
            case HUNGER_CURSE:
                //applyHungerCurseDebuff(player);
                break;
            case ANTIGRAVITY:
               // applyAntigravityDebuff(player);
                break;
        }
    }

    public static void removeDebuff(ServerPlayerEntity player) {
        String debuffName = player.getAttached(CURRENT_DEBUFF);
        if (debuffName != null) {
            try {
                Debuff debuff = Debuff.valueOf(debuffName);
                removeDebuffEffects(player, debuff);
            } catch (IllegalArgumentException e) {
            }
        }
        player.setAttached(CURRENT_DEBUFF, null);
    }

    public static Debuff getCurrentDebuff(ServerPlayerEntity player) {
        String debuffName = player.getAttached(CURRENT_DEBUFF);
        if (debuffName != null) {
            try {
                return Debuff.valueOf(debuffName);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }


    private static void applyCrystallizationDebuff(ServerPlayerEntity player) {
        // Постоянная медлительность I
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS,
                StatusEffectInstance.INFINITE,
                1,
                false,
                false,
                true
        ));
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS,
                StatusEffectInstance.INFINITE,
                0,
                false,
                false,
                true
        ));
    }

    private static void removeDebuffEffects(ServerPlayerEntity player, Debuff debuff) {
        switch (debuff) {
            case UNDERWATER:
                break;
            case SUN_BURN:
                break;
            case BROKEN_WINGS:
                break;
            case SCAVENGER:
                break;
            case CRYSTALLIZATION:
                player.removeStatusEffect(StatusEffects.SLOWNESS);
                break;
            case WEAK_HEART:
                EntityAttributeInstance maxHealthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
                if (maxHealthAttribute != null) {
                    maxHealthAttribute.removeModifier(HEALTH_MODIFIER_ID);
                }
                break;
            case HUNGER_CURSE:
                break;
            case ANTIGRAVITY:
                player.removeStatusEffect(StatusEffects.SLOW_FALLING);
                maxHealthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
                if (maxHealthAttribute != null) {
                    maxHealthAttribute.removeModifier(HEALTH_MODIFIER_ID);
                }
                break;
        }
    }

    private static void applySunBurnTick(ServerPlayerEntity player, ServerWorld world) {
        if (world.isSkyVisible(player.getBlockPos()) &&
                world.isDay() &&
                !world.isRaining()) {

            if(!InfectionHandler.hasUmbrella(player)
                    && player.getEntityWorld().getRegistryKey() == World.OVERWORLD) player.setOnFireFor(1);
        }
    }


    private static void applyAntigravityTick(ServerPlayerEntity player) {
        EntityAttributeInstance maxHealthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);

        if (maxHealthAttribute != null) {
            maxHealthAttribute.removeModifier(HEALTH_MODIFIER_ID);

            EntityAttributeModifier healthModifier = new EntityAttributeModifier(
                    HEALTH_MODIFIER_ID,
                    -4,
                    EntityAttributeModifier.Operation.ADD_VALUE
            );

            maxHealthAttribute.addPersistentModifier(healthModifier);

            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
        if (!hasProtectiveItem(player)) {
            if (RANDOM.nextInt(15) == 0) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.LEVITATION,
                        300,
                        0,
                        false,
                        false,
                        true
                ));
            }
        }
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS,
                100,
                0,
                false,
                false,
                true
        ));
    }

    private static boolean hasProtectiveItem(ServerPlayerEntity player) {
        for (ItemStack stack : player.getInventory().getMainStacks()) {
            if (stack.getItem() instanceof Liberation) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasDebuff(ServerPlayerEntity player, Debuff debuff) {
        return getCurrentDebuff(player) == debuff;
    }

    private static void applyScavengerDebuff(ServerPlayerEntity player) {
        ScavengerDebuff.applyDebuff(player);
    }
    private static void removeScavengerDebuff(ServerPlayerEntity player) {
        ScavengerDebuff.removeDebuff(player);
    }
}