package gc.story.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import gc.story.blocks.pc.CompBlockEntity;
import gc.story.blocks.pc.Task;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CompAdminCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(literal("story")
                .then(literal("admin")
                        .then(literal("comp")
                                .then(literal("pos")
                                        .then(argument("pos", BlockPosArgumentType.blockPos())
                                                .then(literal("add")
                                                        .then(argument("type", StringArgumentType.word())
                                                                .suggests((context, builder) -> {
                                                                    builder.suggest("infinite");
                                                                    builder.suggest("once");
                                                                    return builder.buildFuture();
                                                                })
                                                                .then(argument("requiredItem", ItemStackArgumentType.itemStack(registryAccess))
                                                                        .then(argument("requiredCount", IntegerArgumentType.integer(1, 64))
                                                                                .then(argument("rewardItem", ItemStackArgumentType.itemStack(registryAccess))
                                                                                        .then(argument("rewardCount", IntegerArgumentType.integer(1, 64))
                                                                                                .then(argument("text", StringArgumentType.string())
                                                                                                        .then(argument("id", StringArgumentType.word())
                                                                                                                .executes(context -> executeAddTaskToPos(
                                                                                                                        context,
                                                                                                                        BlockPosArgumentType.getBlockPos(context, "pos"),
                                                                                                                        StringArgumentType.getString(context, "type"),
                                                                                                                        ItemStackArgumentType.getItemStackArgument(context, "requiredItem"),
                                                                                                                        IntegerArgumentType.getInteger(context, "requiredCount"),
                                                                                                                        ItemStackArgumentType.getItemStackArgument(context, "rewardItem"),
                                                                                                                        IntegerArgumentType.getInteger(context, "rewardCount"),
                                                                                                                        StringArgumentType.getString(context, "text"),
                                                                                                                        StringArgumentType.getString(context, "id")
                                                                                                                ))
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                                .then(literal("rem")
                                                        .then(argument("id", StringArgumentType.word())
                                                                .executes(context -> executeRemoveTaskFromPos(
                                                                        context,
                                                                        BlockPosArgumentType.getBlockPos(context, "pos"),
                                                                        StringArgumentType.getString(context, "id")
                                                                ))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }


    private static int executeAddTaskToPos(CommandContext<ServerCommandSource> context,
                                           BlockPos pos,
                                           String type,
                                           ItemStackArgument requiredItemArg,
                                           int requiredCount,
                                           ItemStackArgument rewardItemArg,
                                           int rewardCount,
                                           String text,
                                           String id) throws CommandSyntaxException {

        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();

        if (world.getBlockEntity(pos) instanceof CompBlockEntity computer) {
            ItemStack requiredItem = requiredItemArg.createStack(requiredCount, false);
            ItemStack rewardItem = rewardItemArg.createStack(rewardCount, false);

            Task.TaskType taskType = type.equals("infinite") ? Task.TaskType.INFINITE : Task.TaskType.ONCE;

            Task task = new Task(id, text, requiredItem, requiredCount, rewardItem, rewardCount, taskType);

            computer.addTask(task);

            source.sendMessage(Text.literal("§aЗадание добавлено к компьютеру на координатах " +
                    pos.getX() + " " + pos.getY() + " " + pos.getZ()));
            source.sendMessage(Text.literal("§7ID: " + id + " | Тип: " + type));
            source.sendMessage(Text.literal("§7Требуется: " + requiredCount + "x " + requiredItem.getName().getString()));
            source.sendMessage(Text.literal("§7Награда: " + rewardCount + "x " + rewardItem.getName().getString()));

            return 1;
        } else {
            source.sendMessage(Text.literal("§cНа указанных координатах нет компьютера!"));
            return 0;
        }
    }

    private static int executeRemoveTaskFromPos(CommandContext<ServerCommandSource> context,
                                                BlockPos pos,
                                                String id) throws CommandSyntaxException {

        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();

        if (world.getBlockEntity(pos) instanceof CompBlockEntity computer) {
            if (computer.removeTask(id)) {
                source.sendMessage(Text.literal("§aЗадание с ID '" + id + "' удалено с компьютера на координатах " +
                        pos.getX() + " " + pos.getY() + " " + pos.getZ()));
                return 1;
            } else {
                source.sendMessage(Text.literal("§cЗадание с ID '" + id + "' не найдено на этом компьютере!"));
                return 0;
            }
        } else {
            source.sendMessage(Text.literal("§cНа указанных координатах нет компьютера!"));
            return 0;
        }
    }
}