package gc.story.entites.kirpitch;

import gc.story.inits.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class KirpithEntity extends SnowballEntity {
    public KirpithEntity(EntityType<? extends SnowballEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.KIRPITCH;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        Entity entity = entityHitResult.getEntity();

        if (entity instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.disconnect(
                    Text.literal("Вы не убежали от магического кирпича XD")
            );
        }
    }
}
