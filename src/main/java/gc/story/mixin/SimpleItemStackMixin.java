package gc.story.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import gc.story.events.buff.ScavengerBuff;
import gc.story.events.debuff.ScavengerDebuff;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HangingSignItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class SimpleItemStackMixin {

    /**
     * Модифицируем возвращаемое значение метода finishUsing
     * Срабатывает в самом конце метода
     */
    @ModifyReturnValue(method = "finishUsing", at = @At("RETURN"))
    private ItemStack onItemFinished(ItemStack original, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            ItemStack usedStack = (ItemStack) (Object) this;

            Hand hand = Hand.MAIN_HAND;

            ScavengerDebuff.onItemUse(player, world, hand);
            ScavengerBuff.onItemUse(player, world, hand);
        }
        return original;
    }
}