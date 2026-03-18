package gc.story.events.debuff;

import gc.story.Story;
import gc.story.events.InfectionHandler;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;
import gc.story.events.MutationStage2Handler;

import java.util.HashSet;
import java.util.Set;

public class ScavengerDebuff {

    private static final Set<Item> SCAVENGER_FOOD = new HashSet<>();

    public static final AttachmentType<Long> LAST_FOOD_TIME = AttachmentRegistry.create(
            Identifier.of(Story.MOD_ID, "last_food_time"),
            builder -> builder.persistent(Codec.LONG).copyOnDeath()
    );

    public static final AttachmentType<Boolean> POISON_APPLIED = AttachmentRegistry.create(
            Identifier.of(Story.MOD_ID, "poison_applied"),
            builder -> builder.persistent(Codec.BOOL).copyOnDeath()
    );

    private static final int CHECK_INTERVAL = 100;
    private static final long MAX_TIME_WITHOUT_FOOD = 2100;

    static {
        SCAVENGER_FOOD.add(Items.ROTTEN_FLESH);
        SCAVENGER_FOOD.add(Items.SPIDER_EYE);
        SCAVENGER_FOOD.add(Items.POISONOUS_POTATO);
        SCAVENGER_FOOD.add(Items.CHORUS_FRUIT);
        SCAVENGER_FOOD.add(Items.BONE);
        SCAVENGER_FOOD.add(Items.BONE_MEAL);
        SCAVENGER_FOOD.add(Items.FERMENTED_SPIDER_EYE);
        SCAVENGER_FOOD.add(Items.SUSPICIOUS_STEW);
        SCAVENGER_FOOD.add(Items.PUFFERFISH);
    }

    public static void register() {

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            initializePlayer(player);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                tick(player);
            }
        });
    }

    private static void initializePlayer(ServerPlayerEntity player) {
        if (!hasScavengerDebuff(player)) {
            return;
        }

        if (player.getAttached(LAST_FOOD_TIME) == null) {
            player.setAttached(LAST_FOOD_TIME, player.getEntityWorld().getTime());
        }

        checkAndApplyPoison(player);
    }

    private static void tick(ServerPlayerEntity player) {
        if (!hasScavengerDebuff(player)) {
            return;
        }

        if (player.getEntityWorld().getTime() % CHECK_INTERVAL != 0) {
            return;
        }

        checkAndApplyPoison(player);
    }

    private static void checkAndApplyPoison(ServerPlayerEntity player) {
        long currentTime = player.getEntityWorld().getTime();
        Long lastFoodTime = player.getAttached(LAST_FOOD_TIME);

        if (lastFoodTime == null) {
            player.setAttached(LAST_FOOD_TIME, currentTime);
            return;
        }

        long timeWithoutFood = currentTime - lastFoodTime;
        boolean poisonApplied = player.getAttached(POISON_APPLIED) != null && player.getAttached(POISON_APPLIED);

        if (timeWithoutFood >= MAX_TIME_WITHOUT_FOOD && !poisonApplied) {
            applyPersistentPoison(player);
            player.setAttached(POISON_APPLIED, true);
        }
    }

    private static void applyPersistentPoison(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.POISON,
                Integer.MAX_VALUE,
                1,
                false,
                true,
                true
        ));

    }

    public static ActionResult onItemUse(PlayerEntity player, net.minecraft.world.World world, net.minecraft.util.Hand hand) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.PASS;
        }

        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();

        boolean hasScavengerDebuff = MutationStage2Handler.hasDebuff(serverPlayer, MutationStage2Handler.Debuff.SCAVENGER)
                && InfectionHandler.getCurrentStage(serverPlayer) >= 2;

        if (!hasScavengerDebuff) {
            return ActionResult.PASS;
        }

        if (itemStack.get(DataComponentTypes.FOOD) != null) {
            serverPlayer.setAttached(LAST_FOOD_TIME, serverPlayer.getEntityWorld().getTime());

            if (isScavengerFood(item)) {
                removePersistentPoison(serverPlayer);
                serverPlayer.setAttached(POISON_APPLIED, false);

                serverPlayer.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.REGENERATION,
                        100,  // 5 секунд
                        0,
                        false,
                        true,
                        true
                ));
            } else {
                applyTemporaryPoison(serverPlayer);
            }
        }

        return ActionResult.PASS;
    }

    private static void applyTemporaryPoison(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.POISON,
                200,
                1,
                false,
                true,
                true
        ));

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.HUNGER,
                300,
                1,
                false,
                true,
                true
        ));

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.NAUSEA,
                150,
                0,
                false,
                true,
                true
        ));

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.WEAKNESS,
                200,
                0,
                false,
                true,
                true
        ));
    }

    private static void removePersistentPoison(ServerPlayerEntity player) {
        player.removeStatusEffect(StatusEffects.POISON);
        player.removeStatusEffect(StatusEffects.HUNGER);
        player.removeStatusEffect(StatusEffects.NAUSEA);
        player.removeStatusEffect(StatusEffects.WEAKNESS);

        player.setAttached(POISON_APPLIED, false);

    }

    public static boolean isScavengerFood(Item item) {
        return SCAVENGER_FOOD.contains(item);
    }

    public static boolean hasScavengerDebuff(ServerPlayerEntity player) {
        return MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.SCAVENGER)
                && InfectionHandler.getCurrentStage(player) >= 2;
    }

    public static void addScavengerFood(Item item) {
        SCAVENGER_FOOD.add(item);
    }

    public static boolean canEat(ServerPlayerEntity player, ItemStack stack) {
        if (!hasScavengerDebuff(player)) {
            return true;
        }

        return isScavengerFood(stack.getItem());
    }

    public static void applyDebuff(ServerPlayerEntity player) {
        player.setAttached(LAST_FOOD_TIME, player.getEntityWorld().getTime());
        player.setAttached(POISON_APPLIED, false);
    }

    public static void removeDebuff(PlayerEntity player) {
        player.setAttached(LAST_FOOD_TIME, null);
        player.setAttached(POISON_APPLIED, null);
        player.removeStatusEffect(StatusEffects.POISON);
        player.removeStatusEffect(StatusEffects.HUNGER);
        player.removeStatusEffect(StatusEffects.NAUSEA);
        player.removeStatusEffect(StatusEffects.WEAKNESS);
    }
}