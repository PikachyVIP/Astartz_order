package gc.story.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import gc.story.events.InfectionHandler;
import gc.story.events.MutationStage2Handler;
import gc.story.events.MutationStage3BuffHandler;
import gc.story.events.StageHandler;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class StoryAdminCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(CommandManager.literal("story").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("admin")
                        // Подкоманда debug_mod
                        .then(CommandManager.literal("debug_mod")
                                .then(CommandManager.argument("value", BoolArgumentType.bool())
                                        .executes(StoryAdminCommand::executeDebugMod)))
                        // Подкоманда info
                        .then(CommandManager.literal("info")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(StoryAdminCommand::executeInfo)))
                        // Подкоманда set
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .then(CommandManager.literal("inf_state")
                                                .then(CommandManager.literal("-1")
                                                        .executes(context -> executeSetInfected(context, -1)))
                                                .then(CommandManager.literal("0")
                                                        .executes(context -> executeSetInfected(context, 0)))
                                                .then(CommandManager.literal("1")
                                                        .executes(context -> executeSetInfected(context, 1)))
                                                .then(CommandManager.literal("2")
                                                        .executes(context -> executeSetInfected(context, 2)))
                                                .then(CommandManager.literal("3")
                                                        .executes(context -> executeSetInfected(context, 3))))
                                        .then(CommandManager.literal("inf_chance")
                                                .then(CommandManager.argument("value", FloatArgumentType.floatArg(0.0f, 1.0f))
                                                        .executes(StoryAdminCommand::executeSetInfectionChance)))))
                .then(CommandManager.literal("debuff")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.literal("underwater")
                                        .executes(context -> executeSetDebuff(context, MutationStage2Handler.Debuff.UNDERWATER)))
                                .then(CommandManager.literal("sun_burn")
                                        .executes(context -> executeSetDebuff(context, MutationStage2Handler.Debuff.SUN_BURN)))
                                .then(CommandManager.literal("fear_of_the_dark")
                                        .executes(context -> executeSetDebuff(context, MutationStage2Handler.Debuff.BROKEN_WINGS)))
                                .then(CommandManager.literal("scavenger")
                                        .executes(context -> executeSetDebuff(context, MutationStage2Handler.Debuff.SCAVENGER)))
                                .then(CommandManager.literal("crystallization")
                                        .executes(context -> executeSetDebuff(context, MutationStage2Handler.Debuff.CRYSTALLIZATION)))
                                .then(CommandManager.literal("weak_heart")
                                        .executes(context -> executeSetDebuff(context, MutationStage2Handler.Debuff.WEAK_HEART)))
                                .then(CommandManager.literal("hunger_curse")
                                        .executes(context -> executeSetDebuff(context, MutationStage2Handler.Debuff.HUNGER_CURSE)))
                                .then(CommandManager.literal("antigravity")
                                        .executes(context -> executeSetDebuff(context, MutationStage2Handler.Debuff.ANTIGRAVITY)))
                                .then(CommandManager.literal("fire")
                                        .executes(context -> executeSetDebuff(context, MutationStage2Handler.Debuff.FIRE)))
                                .then(CommandManager.literal("lost")
                                        .executes(context -> executeSetDebuff(context, MutationStage2Handler.Debuff.LOST)))
                                .then(CommandManager.literal("random")
                                        .executes(StoryAdminCommand::executeRandomDebuff))
                                .then(CommandManager.literal("remove")
                                        .executes(StoryAdminCommand::executeRemoveDebuff))))));

    }

    private static int executeSetDebuff(CommandContext<ServerCommandSource> context, MutationStage2Handler.Debuff debuff) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");

        // Удаляем текущий дебафф если есть
        MutationStage2Handler.removeDebuff(targetPlayer);

        // Применяем новый дебафф
        MutationStage2Handler.applyDebuff(targetPlayer, debuff);
        InfectionHandler.resethp(targetPlayer);

        source.sendFeedback(() -> Text.literal("§6[Story] §fИгроку §e" + targetPlayer.getName().getString() +
                " §fвыдан дебафф: §a" + debuff.getDisplayName()), true);

        return 1;
    }

    private static int executeRandomDebuff(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");

        MutationStage2Handler.Debuff debuff = MutationStage2Handler.getRandomDebuff();

        // Удаляем текущий дебафф если есть
        MutationStage2Handler.removeDebuff(targetPlayer);

        // Применяем случайный дебафф
        MutationStage2Handler.applyDebuff(targetPlayer, debuff);

        source.sendFeedback(() -> Text.literal("§6[Story] §fИгроку §e" + targetPlayer.getName().getString() +
                " §fвыдан случайный дебафф: §a" + debuff.getDisplayName()), true);

        return 1;
    }

    private static int executeRemoveDebuff(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");

        MutationStage2Handler.Debuff currentDebuff = MutationStage2Handler.getCurrentDebuff(targetPlayer);
        String debuffName = (currentDebuff != null) ? currentDebuff.getDisplayName() : "неизвестный";

        MutationStage2Handler.removeDebuff(targetPlayer);
        MutationStage3BuffHandler.removeBuff(targetPlayer);
        InfectionHandler.resethp(targetPlayer);

        source.sendFeedback(() -> Text.literal("§6[Story] §fДебафф §e" + debuffName + " §fу игрока §e" +
                targetPlayer.getName().getString() + " §fудален"), true);

        return 1;
    }


    private static int executeDebugMod(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        boolean value = BoolArgumentType.getBool(context, "value");

        if (value) {
            player.addCommandTag("story_debug");
            source.sendFeedback(() -> Text.literal("§a[Story] Debug mode enabled for §e" + player.getName().getString()), false);
        } else {
            player.removeCommandTag("story_debug");
            source.sendFeedback(() -> Text.literal("§c[Story] Debug mode disabled for §e" + player.getName().getString()), false);
        }

        return 1;
    }

    private static int executeInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");

        Collection<String> tags = targetPlayer.getCommandTags();

        source.sendFeedback(() -> Text.literal("§6=== Story Mod Info for §e" + targetPlayer.getName().getString() + " §6==="), false);

        // Информация о заражении
        int Infectedstage = InfectionHandler.getCurrentStage(targetPlayer);
        float infectionChance = InfectionHandler.getCurrentChance(targetPlayer);
        String debuff = String.valueOf(MutationStage2Handler.getCurrentDebuff(targetPlayer));
        String buff = String.valueOf(MutationStage3BuffHandler.getCurrentBuff(targetPlayer));
        source.sendFeedback(() -> Text.literal("§bInfection Status:"), false);
        source.sendFeedback(() -> Text.literal("  §f• Infected stage: " + Infectedstage), false);
        source.sendFeedback(() -> Text.literal("  §f• Chance: §e" + String.format("%.2f", infectionChance * 100) + "%"), false);
        source.sendFeedback(() -> Text.literal("  §f• Time until stage "+(Infectedstage+1)+": " +
                StageHandler.getFormattedTimeUntilTransition(targetPlayer)), false);
        source.sendFeedback(() -> Text.literal("  §f• Mutation(debuff): §e" + debuff), false);
        source.sendFeedback(() -> Text.literal("  §f• Mutation(buff): §e" + buff), false);
        source.sendFeedback(() -> Text.literal(""), false);

        if (tags.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§7No story tags found"), false);
        } else {
            source.sendFeedback(() -> Text.literal("§bTags:"), false);

            for (String tag : tags) {
                if (tag.startsWith("story_")) {
                    source.sendFeedback(() -> Text.literal("  §a• " + tag), false);
                }
            }

            source.sendFeedback(() -> Text.literal("§7All tags:"), false);
            for (String tag : tags) {
                source.sendFeedback(() -> Text.literal("  §8• " + tag), false);
            }
        }

        source.sendFeedback(() -> Text.literal("§bStory Debug Mode: §f" +
                (tags.contains("story_debug") ? "§aENABLED" : "§cDISABLED")), false);

        return 1;
    }

    private static int executeSetInfected(CommandContext<ServerCommandSource> context, int value) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
        InfectionHandler.resethp(targetPlayer);
        if(value == -1)StageHandler.scheduleStageTransition(targetPlayer, -1);
        if(value == 0)StageHandler.scheduleStageTransition(targetPlayer, 0);
        if(value == 1)StageHandler.scheduleStageTransition(targetPlayer, 1);
        if(value == 2){
            StageHandler.scheduleStageTransition(targetPlayer, 2);
            if(MutationStage2Handler.getCurrentDebuff(targetPlayer) == null) MutationStage2Handler.applyDebuff(targetPlayer, MutationStage2Handler.getRandomDebuff());
        }
        if(value == 3)StageHandler.scheduleStageTransition(targetPlayer, -1);
        InfectionHandler.setInfected(targetPlayer, value);
        String status;
        if(value >= 0){
            status ="§aзаражён";
        }else {
            status = "§cне заражён";
        }

        source.sendFeedback(() -> Text.literal("§6[Story] §fИгрок §e" + targetPlayer.getName().getString() +
                " §fтеперь " + status), false);

        return 1;
    }

    private static int executeSetInfectionChance(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
        float value = FloatArgumentType.getFloat(context, "value");

        InfectionHandler.setChance(targetPlayer, value);

        source.sendFeedback(() -> Text.literal("§6[Story] §fШанс заражения для игрока §e" + targetPlayer.getName().getString() +
                " §fустановлен на §a" + String.format("%.2f", value * 100) + "%"), false);

        return 1;
    }
}