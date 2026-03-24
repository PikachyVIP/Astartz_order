package gc.story.blocks.max;

import gc.story.blocks.ImplementedInventory;
import gc.story.inits.ModBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MaxBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    private String question = "";
    private String answer = "";
    private boolean hasQuestion = false;

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 0;

    public MaxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAX, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MaxBlockEntity.this.progress;
                    case 1 -> MaxBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> MaxBlockEntity.this.progress = value;
                    case 1 -> MaxBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    public boolean hasQuestion() {
        return hasQuestion && !question.isEmpty();
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean createQuestion(String questionText, PlayerEntity player) {
        if (hasQuestion) {
            return false;
        }
        this.question = questionText;
        this.answer = "";
        this.hasQuestion = true;
        markDirty();
        sync();
        return true;
    }

    public boolean setAnswer(String answerText, PlayerEntity player) {
        if (!hasQuestion) {
            return false;
        }
        this.answer = answerText;
        markDirty();
        sync();
        return true;
    }

    public boolean deleteQuestion() {
        if (!hasQuestion) {
            return false;
        }
        this.question = "";
        this.answer = "";
        this.hasQuestion = false;
        markDirty();
        sync();
        return true;
    }

    private void sync() {
        if (world != null && !world.isClient()) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MaxScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData(view, inventory);
        view.putInt("max.progress", progress);
        view.putInt("max.max_progress", maxProgress);
        view.putBoolean("max.has_question", hasQuestion);
        view.putString("max.question", question);
        view.putString("max.answer", answer);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        Inventories.readData(view, inventory);
        progress = view.getInt("max.progress", 0);
        maxProgress = view.getInt("max.max_progress", 0);
        hasQuestion = view.getBoolean("max.has_question", false);
        question = view.getString("max.question", "");
        answer = view.getString("max.answer", "");
    }

    public void tick(World world, BlockPos pos, BlockState state) {
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }
}