package gc.story.entites;

import gc.story.entites.kirpitch.KirpithEntity;
import gc.story.inits.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SnowballItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MagicKirpitch extends SnowballItem {
    private static final float POWER = 1.5F;

    public MagicKirpitch(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                SoundEvents.ENTITY_SNOWBALL_THROW,
                SoundCategory.NEUTRAL,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        if (!world.isClient()) {
            KirpithEntity kirpithEntity = new KirpithEntity(ModEntities.KIRPITH_ENTITY_TYPE, world);
            kirpithEntity.setOwner(user);
            kirpithEntity.setPosition(user.getX(), user.getEyeY() - 0.1, user.getZ());
            kirpithEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, POWER, 1.0F);

            world.spawnEntity(kirpithEntity);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        itemStack.decrementUnlessCreative(1, user);
        return ActionResult.SUCCESS;
    }
}