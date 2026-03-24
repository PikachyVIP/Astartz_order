package gc.story.events;


import gc.story.Story;
import gc.story.inits.ModItems;
import gc.story.items.IfiriumItem;
import gc.story.items.IfiriumItemBlock;
import net.fabricmc.api.EnvironmentInterfaces;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import com.mojang.serialization.Codec;

import java.util.Random;
import java.util.function.Predicate;

// Эта аннотация нужна для использования экспериментального API аттачментов
@EnvironmentInterfaces({})
public class InfectionHandler {
    private static final Random RANDOM = new Random();

    // Регистрация типов аттачментов
    public static final AttachmentType<Integer> INFECTED_ATTACHMENT = AttachmentRegistry.create(
            Identifier.of(Story.MOD_ID, "infected"),
            builder -> builder.persistent(Codec.INT).copyOnDeath() // Копируется при смерти
    );

    public static final AttachmentType<Float> CHANCE_ATTACHMENT = AttachmentRegistry.create(
            Identifier.of(Story.MOD_ID, "chance_infect"),
            builder -> builder.persistent(Codec.FLOAT).copyOnDeath() // Копируется при смерти
    );

    // Счетчик тиков для отслеживания секунд
    private static int tickCounter = 0;
    private static final int TICKS_PER_SECOND = 20;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(InfectionHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer minecraftServer) {
        tickCounter++;

        // Проверяем каждую секунду (каждые 20 тиков)
        if (tickCounter >= TICKS_PER_SECOND) {
            tickCounter = 0;

            // Проходим по всем игрокам на сервере
            for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
                processPlayerInfection(player);
            }
        }
    }

    private static void processPlayerInfection(ServerPlayerEntity player) {
        // Получаем текущий шанс заражения из аттачмента
        Float currentChance = player.getAttached(CHANCE_ATTACHMENT);

        if (currentChance == null) {
            currentChance = 0.0f;
            player.setAttached(CHANCE_ATTACHMENT, currentChance);
        }
        boolean hasIririum = false;
        for (net.minecraft.item.ItemStack stack : player.getInventory()) {
            if (!stack.isEmpty() && (stack.getItem() instanceof IfiriumItem || stack.getItem() instanceof IfiriumItemBlock)) {
                hasIririum = true;
                break;
            }
        }

        if (!hasIririum) {
            for (net.minecraft.item.ItemStack stack : player.getInventory()) {
                if (!stack.isEmpty() && (stack.getItem() instanceof IfiriumItem || stack.getItem() instanceof IfiriumItemBlock)) {
                    hasIririum = true;
                    break;
                }
            }
        }

        if (hasIririum && !hasFullCustomArmor(player)) {
            // Повышаем шанс на 0.2% (0.002 в десятичном виде)
            float newChance = currentChance + 0.0005f;

            // Ограничиваем максимальный шанс 100% (1.0)
            if (newChance > 1.0f) {
                newChance = 1.0f;
            }

            player.setAttached(CHANCE_ATTACHMENT, newChance);

            Integer isInfected = player.getAttached(INFECTED_ATTACHMENT);
            // Если игрок еще не заражен, проверяем шанс
            if ((isInfected == null || isInfected == -1)) {
                // Генерируем случайное число от 0 до 1 и сравниваем с шансом
                if (RANDOM.nextFloat() < newChance) {

                    player.setAttached(INFECTED_ATTACHMENT, 0);
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
                isInfected = player.getAttached(INFECTED_ATTACHMENT);
                String infectedStatus = (isInfected != null && isInfected >= 0) ? "§cЗАРАЖЁН" : "§aНЕ ЗАРАЖЁН";

                player.sendMessage(
                        net.minecraft.text.Text.literal("§7[DEBUG] §fТекущий шанс: §e" +
                                String.format("%.2f", currentChance * 100) + "% §f| Статус: " + infectedStatus),
                        false
                );
            }
        }
    }


    public static int getCurrentStage(ServerPlayerEntity player) {
        Integer stage = player.getAttached(INFECTED_ATTACHMENT);
        return stage != null ? stage : -1;
    }

    // Вспомогательный метод для получения текущего шанса
    public static float getCurrentChance(ServerPlayerEntity player) {
        Float chance = player.getAttached(CHANCE_ATTACHMENT);
        return chance != null ? chance : 0.0f;
    }

    // Метод для ручного заражения (например, через команду)
    public static void setInfected(ServerPlayerEntity player, int infected) {
        player.setAttached(INFECTED_ATTACHMENT, infected);
        InfectionHandler.resethp(player);
        if(infected == -1)StageHandler.scheduleStageTransition(player, -1);
        if(infected == 0)StageHandler.scheduleStageTransition(player, 0);
        if(infected == 1)StageHandler.scheduleStageTransition(player, 1);
        if(infected == 2){
            StageHandler.scheduleStageTransition(player, 2);
            if(MutationStage2Handler.getCurrentDebuff(player) == null) MutationStage2Handler.applyDebuff(player, MutationStage2Handler.getRandomDebuff());
        }
        if(infected == 3)StageHandler.scheduleStageTransition(player, -1);
    }

    // Метод для установки шанса (например, через команду)
    public static void setChance(ServerPlayerEntity player, float chance) {
        player.setAttached(CHANCE_ATTACHMENT, Math.max(0, Math.min(1, chance)));
    }

    public static boolean hasFullCustomArmor(ServerPlayerEntity player) {
        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack leggings = player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);

        boolean hasHelmet = !helmet.isEmpty() && helmet.getItem() == ModItems.GUIDITE_HELMET;
        boolean hasChestplate = !chestplate.isEmpty() && chestplate.getItem() == ModItems.GUIDITE_CHESTPLATE;
        boolean hasLeggings = !leggings.isEmpty() && leggings.getItem() == ModItems.GUIDITE_LEGGINGS;
        boolean hasBoots = !boots.isEmpty() && boots.getItem() == ModItems.GUIDITE_BOOTS;

        boolean hasHelmet2 = !helmet.isEmpty() && helmet.getItem() == ModItems.IFIRIUM_HELMET;
        boolean hasChestplate2 = !chestplate.isEmpty() && chestplate.getItem() == ModItems.IFIRIUM_CHESTPLATE;
        boolean hasLeggings2 = !leggings.isEmpty() && leggings.getItem() == ModItems.IFIRIUM_LEGGINGS;
        boolean hasBoots2 = !boots.isEmpty() && boots.getItem() == ModItems.IFIRIUM_BOOTS;

        return (hasHelmet && hasChestplate && hasLeggings && hasBoots) || (hasHelmet2 && hasChestplate2 && hasLeggings2 && hasBoots2);
    }
    public static boolean hasFullIfiriumArmor(ServerPlayerEntity player) {
        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack leggings = player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);

        boolean hasHelmet = !helmet.isEmpty() && helmet.getItem() == ModItems.IFIRIUM_HELMET;
        boolean hasChestplate = !chestplate.isEmpty() && chestplate.getItem() == ModItems.IFIRIUM_CHESTPLATE;
        boolean hasLeggings = !leggings.isEmpty() && leggings.getItem() == ModItems.IFIRIUM_LEGGINGS;
        boolean hasBoots = !boots.isEmpty() && boots.getItem() == ModItems.IFIRIUM_BOOTS;

        return hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }

    public static boolean hasUmbrella(ServerPlayerEntity player) {
        // Используем getMainHandStack() и getOffHandStack() вместо getActiveHand()
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();

        boolean hasUmbrella =
                isUmbrella(mainHand.getItem()) ||
                        isUmbrella(offHand.getItem());

        return hasUmbrella;
    }

    // Вспомогательный метод для проверки типа зонтика
    private static boolean isUmbrella(Item item) {
        return item == ModItems.UMBRELLA
                || item == ModItems.UMBRELLA_GREEN
                || item == ModItems.UMBRELLA_HONEY
                || item == ModItems.UMBRELLA_PURPLE
                || item == ModItems.UMBRELLA_FLWR
                || item == ModItems.UMBRELLA_SPRAY
                || item == ModItems.UMBRELLA_VAMPIRE;
    }
    private static final Identifier HEALTH_MODIFIER_ID = Identifier.of("b1c2d3e4-f5a6-7890-1234-567890abcdef");
    public static void resethp(ServerPlayerEntity player) {
        EntityAttributeInstance maxHealthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (maxHealthAttribute != null) {
            maxHealthAttribute.removeModifier(HEALTH_MODIFIER_ID);
        }
    }

}