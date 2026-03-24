package gc.story.inits;


import gc.story.blocks.cleaner.CleanerBlockEntity;
import gc.story.blocks.crusher.CrusherBlockEntity;
import gc.story.blocks.max.MaxBlockEntity;
import gc.story.blocks.pc.CompBlockEntity;
import gc.story.blocks.press.PressBlockEntity;
import gc.story.blocks.splav.SplavBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<CrusherBlockEntity> CRUSHER = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("story", "crusher"),
            FabricBlockEntityTypeBuilder.create(CrusherBlockEntity::new, ModBlocks.CRUSHER).build()
    );
    public static final BlockEntityType<CleanerBlockEntity> CLEANER = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("story", "cleaner"),
            FabricBlockEntityTypeBuilder.create(CleanerBlockEntity::new, ModBlocks.CLEANER).build()
    );

    public static final BlockEntityType<PressBlockEntity> PRESS = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("story", "press"),
            FabricBlockEntityTypeBuilder.create(PressBlockEntity::new, ModBlocks.PRESS).build()
    );
    public static final BlockEntityType<SplavBlockEntity> SPLAV = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("story", "splav"),
            FabricBlockEntityTypeBuilder.create(SplavBlockEntity::new, ModBlocks.SPLAV).build()
    );

    public static final BlockEntityType<CompBlockEntity> COMP = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("story", "comp"),
            FabricBlockEntityTypeBuilder.create(CompBlockEntity::new, ModBlocks.COMP).build()
    );
    public static final BlockEntityType<MaxBlockEntity> MAX = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("story", "max"),
            FabricBlockEntityTypeBuilder.create(MaxBlockEntity::new, ModBlocks.MAX).build()
    );


    public static void initialize() {}
}