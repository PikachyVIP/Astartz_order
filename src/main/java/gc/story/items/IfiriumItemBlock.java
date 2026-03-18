package gc.story.items;

import gc.story.events.InfectionHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class IfiriumItemBlock extends BlockItem {
    private int EFFECT_AMPLIFIER = 1;
    private static final int EFFECT_DURATION = 100;

    public IfiriumItemBlock(Block block, Settings settings, int efflevel) {
        super(block, settings);
        this.EFFECT_AMPLIFIER = efflevel;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (!world.isClient() && entity instanceof ServerPlayerEntity player) {
            long time = world.getTime();
            if (time % 20 == 0) {
                if (!InfectionHandler.hasFullCustomArmor(player))applyEffects(player);
            }
        }
    }

    private void applyEffects(PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.NAUSEA,
                EFFECT_DURATION,
                EFFECT_AMPLIFIER,
                false,
                true,
                true
        ));

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.POISON,
                EFFECT_DURATION,
                0,
                false,
                true,
                true
        ));

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.WEAKNESS,
                EFFECT_DURATION,
                EFFECT_AMPLIFIER,
                false,
                true,
                true
        ));
    }
}