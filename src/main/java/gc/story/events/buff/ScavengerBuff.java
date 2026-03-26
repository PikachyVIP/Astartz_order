package gc.story.events.buff;

import gc.story.Story;
import gc.story.events.InfectionHandler;
import gc.story.events.MutationStage3BuffHandler;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;
import gc.story.events.MutationStage2Handler;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ScavengerBuff {

    private static final Set<Item> SCAVENGER_FOOD = new HashSet<>();

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



    public static ActionResult onItemUse(PlayerEntity player, net.minecraft.world.World world, net.minecraft.util.Hand hand) {
        if (world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.PASS;
        }
        if (!MutationStage3BuffHandler.hasBuff(serverPlayer, MutationStage3BuffHandler.Buff.SCAVENGER)) {
            return ActionResult.PASS;
        }
        if (InfectionHandler.getCurrentStage(serverPlayer) != 3){
            return ActionResult.PASS;
        }

        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();

        if (itemStack.get(DataComponentTypes.FOOD) != null) {

            if (isScavengerFood(item)) {
                serverPlayer.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.SATURATION, 200, 0, false, false, true
                ));
            }
        }

        return ActionResult.PASS;
    }


    public static boolean isScavengerFood(Item item) {
        return SCAVENGER_FOOD.contains(item);
    }
}