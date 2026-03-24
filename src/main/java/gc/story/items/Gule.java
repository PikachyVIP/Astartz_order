package gc.story.items;

import gc.story.events.MutationStage2Handler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;

public class Gule extends Item {
    private static final String LAST_USE_TIME_KEY = "last_use_time";
    private static final int COOLDOWN_TICKS = 1200;

    public Gule(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (user instanceof ServerPlayerEntity player) {
            if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.SCAVENGER)) return ActionResult.FAIL;
            if (canUse(stack, world)) {
                conjureFangsCross(player);
                setLastUseTime(stack, world.getTime());
                player.getItemCooldownManager().set(this.getDefaultStack(), COOLDOWN_TICKS);
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_EVOKER_CAST_SPELL, SoundCategory.PLAYERS, 1.0f, 1.0f);
                player.sendMessage(Text.literal("§5Челюсти призваны!"), true);
                return ActionResult.SUCCESS;
            } else {
                long remainingTime = getRemainingCooldown(stack, world);
                long remainingSeconds = remainingTime / 20;
                player.sendMessage(Text.literal("§cПредмет на перезарядке! Осталось: " + remainingSeconds + " сек."), true);
                return ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }

    private void conjureFangsCross(ServerPlayerEntity player) {
        Vec3d lookVec = player.getRotationVector();
        Vec3d rightVec = lookVec.rotateY((float)Math.toRadians(-90));
        Vec3d playerPos = player.getEntityPos();
        double y = playerPos.y;
        double maxY = player.getY() - 2.0;
        float yaw = player.getYaw();

        for (int line = 0; line < 4; line++) {
            double offset = (line - 1.5) * 1.5;

            for (int i = 0; i < 5; i++) {
                double distance = 2.0 + i * 1.5;

                double x = playerPos.x + lookVec.x * distance + rightVec.x * offset;
                double z = playerPos.z + lookVec.z * distance + rightVec.z * offset;

                conjureFangs(x, z, maxY, y, yaw, 0, player);
            }
        }
    }

    private void conjureFangs(double x, double z, double maxY, double y, float yaw, int warmup, ServerPlayerEntity player) {
        BlockPos blockPos = BlockPos.ofFloored(x, y, z);
        boolean bl = false;
        double d = 0.0;

        do {
            BlockPos blockPos2 = blockPos.down();
            BlockState blockState = player.getEntityWorld().getBlockState(blockPos2);
            if (blockState.isSideSolidFullSquare(player.getEntityWorld(), blockPos2, Direction.UP)) {
                if (!player.getEntityWorld().isAir(blockPos)) {
                    BlockState blockState2 = player.getEntityWorld().getBlockState(blockPos);
                    VoxelShape voxelShape = blockState2.getCollisionShape(player.getEntityWorld(), blockPos);
                    if (!voxelShape.isEmpty()) {
                        d = voxelShape.getMax(Direction.Axis.Y);
                    }
                }

                bl = true;
                break;
            }

            blockPos = blockPos.down();
        } while (blockPos.getY() >= MathHelper.floor(maxY) - 1);

        if (bl) {
            player.getEntityWorld()
                    .spawnEntity(new EvokerFangsEntity(player.getEntityWorld(), x, blockPos.getY() + d, z, yaw, warmup, player));
            player.getEntityWorld().emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, blockPos.getY() + d, z), GameEvent.Emitter.of(player));
        }
    }

    private boolean canUse(ItemStack stack, World world) {
        Long lastUseTime = getLastUseTime(stack);
        if (lastUseTime == null) return true;
        long currentTime = world.getTime();
        return (currentTime - lastUseTime) >= COOLDOWN_TICKS;
    }

    private long getRemainingCooldown(ItemStack stack, World world) {
        Long lastUseTime = getLastUseTime(stack);
        if (lastUseTime == null) return 0;
        long currentTime = world.getTime();
        long elapsed = currentTime - lastUseTime;
        return Math.max(0, COOLDOWN_TICKS - elapsed);
    }

    private void setLastUseTime(ItemStack stack, long time) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtComponent newComponent = nbtComponent.apply(nbt -> nbt.putLong(LAST_USE_TIME_KEY, time));
        stack.set(DataComponentTypes.CUSTOM_DATA, newComponent);
    }

    private Long getLastUseTime(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent != null) {
            var nbt = nbtComponent.copyNbt();
            if (nbt.contains(LAST_USE_TIME_KEY)) {
                return nbt.getLong(LAST_USE_TIME_KEY).get();
            }
        }
        return null;
    }
}