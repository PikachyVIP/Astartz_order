package gc.story.blocks;

import gc.story.events.InfectionHandler;
import gc.story.events.StageHandler;
import gc.story.items.IfiriumItem;
import gc.story.items.IfiriumItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

import static gc.story.events.InfectionHandler.hasFullCustomArmor;

public class Ifirium extends Block {
    private int RADIUS = 5;
    private static final int EFFECT_DURATION = 100;
    private int EFFECT_AMPLIFIER = 1;
    private static final java.util.Random RANDOM = new java.util.Random();

    public Ifirium(Settings settings, int efflevel, int effradius) {
        super(settings);
        this.EFFECT_AMPLIFIER = efflevel;
        this.RADIUS = effradius;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!world.isClient()) {
            world.scheduleBlockTick(pos, this, 20);
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isClient()) {
            applyDebuffsToNearbyPlayers(world, pos);
            world.scheduleBlockTick(pos, this, 20);
        }
    }

    private void applyDebuffsToNearbyPlayers(World world, BlockPos pos) {
        Box box = new Box(pos).expand(RADIUS);

        List<ServerPlayerEntity> players = world.getEntitiesByClass(
                ServerPlayerEntity.class,
                box,
                player -> player != null && player.isAlive()
        );

        for (ServerPlayerEntity player : players) {
            if (player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= RADIUS * RADIUS) {

                processPlayerInfection(player);
                if (!hasFullCustomArmor(player)) {
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.NAUSEA,
                            EFFECT_DURATION,
                            EFFECT_AMPLIFIER,
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
        }
    }

    private static void processPlayerInfection(ServerPlayerEntity player) {
        Float currentChance = player.getAttached(InfectionHandler.CHANCE_ATTACHMENT);

        if (currentChance == null) {
            currentChance = 0.0f;
            player.setAttached(InfectionHandler.CHANCE_ATTACHMENT, currentChance);
        }


        if (!hasFullCustomArmor(player)) {
            // Повышаем шанс на 0.2% (0.002 в десятичном виде)
            float newChance = currentChance + 0.0005f;

            // Ограничиваем максимальный шанс 100% (1.0)
            if (newChance > 1.0f) {
                newChance = 1.0f;
            }

            player.setAttached(InfectionHandler.CHANCE_ATTACHMENT, newChance);

            Integer isInfected = player.getAttached(InfectionHandler.INFECTED_ATTACHMENT);
            // Если игрок еще не заражен, проверяем шанс
            if ((isInfected == null || isInfected == -1) && !hasFullCustomArmor(player)) {
                // Генерируем случайное число от 0 до 1 и сравниваем с шансом
                if (RANDOM.nextFloat() < newChance) {

                    player.setAttached(InfectionHandler.INFECTED_ATTACHMENT, 0);
                    StageHandler.scheduleStageTransition(player, 1);
                }
            }else {
                if (player.getCommandTags().contains("story_debug")) {
                    player.sendMessage(
                            net.minecraft.text.Text.literal("§7[DEBUG] §fЗащита в химке"),
                            false
                    );
                }
            }
            if (player.getCommandTags().contains("story_debug")) {
                isInfected = player.getAttached(InfectionHandler.INFECTED_ATTACHMENT);
                String infectedStatus = (isInfected != null && isInfected >= 0) ? "§cЗАРАЖЁН" : "§aНЕ ЗАРАЖЁН";

                player.sendMessage(
                        net.minecraft.text.Text.literal("§7[DEBUG] §fТекущий шанс: §e" +
                                String.format("%.2f", currentChance * 100) + "% §f| Статус: " + infectedStatus),
                        false
                );
            }
        }
    }
}