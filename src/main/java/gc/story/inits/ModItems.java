package gc.story.inits;

import gc.story.Story;
import gc.story.entites.MagicKirpitch;
import gc.story.items.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.equipment.*;
import net.minecraft.registry.*;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    public static final ToolMaterial GUIDITE_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            0,
            5.0F,
            1.5F,
            1,
            null
    );

    public static final ToolMaterial IFIRIUM_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            3031, 10.0F, 6.0F, 20,
            TagKey.of(RegistryKeys.ITEM, Identifier.of(Story.MOD_ID,"crystal_clear"))
    );

    public static final Item SCALPEL = register(
            Scalpel::new,
            new Item.Settings().sword(GUIDITE_TOOL_MATERIAL, 6, 1),
            "scalpel"
    );

    public static final Item IFIRIUM_SWORD = register(
            Item::new,
            new Item.Settings().sword(IFIRIUM_TOOL_MATERIAL, 5, -2.4f),
            "ifirium_sword"
    );
    public static final Item IFIRIUM_AXE = register(
            Item::new,
            new Item.Settings().axe(IFIRIUM_TOOL_MATERIAL, 6, -2.8f),
            "ifirium_axe"
    );
    public static final Item IFIRIUM_PICAXE = register(
            Item::new,
            new Item.Settings().pickaxe(IFIRIUM_TOOL_MATERIAL, 1, -3),
            "ifirium_picaxe"
    );
    public static final Item IFIRIUM_HOE = register(
            Item::new,
            new Item.Settings().hoe(IFIRIUM_TOOL_MATERIAL, 1, -3),
            "ifirium_hoe"
    );
    public static final Item IFIRIUM_SHOVEL = register(
            Item::new,
            new Item.Settings().shovel(IFIRIUM_TOOL_MATERIAL, 1, -3),
            "ifirium_shovel"
    );


    public static final Item KIRPITCH = register(
            MagicKirpitch::new,
            new Item.Settings(),
            "kirpitch"
    );

    public static final Item NIGHTLIGHT = register(
            NightLight::new,
            new Item.Settings().maxCount(1),
            "nightlight"
    );
    public static final Item DEVOURER = register(
            Devourer::new,
            new Item.Settings().maxCount(1),
            "devourer"
    );

    public static final Item NUMBNESS = register(
            Numbness::new,
            new Item.Settings().maxCount(1),
            "numbness"
    );
    public static final Item LIBERT = register(
            Liberation::new,
            new Item.Settings().maxCount(1),
            "liberation"
    );

    public static final Item GULE = register(
            Gule::new,
            new Item.Settings().maxCount(1),
            "gule"
    );
    public static final Item UNDERWATERMASK = register(
            UnderwaterMask::new,
            new Item.Settings().maxCount(1),
            "underwatermask"
    );

    public static final Item DECEIT = register(
            Deceit::new,
            new Item.Settings().maxCount(1),
            "deceit"
    );
    public static final Item FINDED = register(
            Finded::new,
            new Item.Settings().maxCount(1),
            "finded"
    );

    public static final Item CLEANER_CHU = register(
            Item::new,
            new Item.Settings(),
            "cleaner_chu"
    );
    public static final Item CRUSHER_CHU = register(
            Item::new,
            new Item.Settings(),
            "crusher_chu"
    );
    public static final Item PRESS_CHU = register(
            Item::new,
            new Item.Settings(),
            "press_chu"
    );
    public static final Item SPLAV_CHU = register(
            Item::new,
            new Item.Settings(),
            "splav_chu"
    );

    public static final Item SWORD_CHU = register(
            Item::new,
            new Item.Settings(),
            "sword_chu"
    );
    public static final Item AXE_CHU = register(
            Item::new,
            new Item.Settings(),
            "axe_chu"
    );
    public static final Item HOE_CHU = register(
            Item::new,
            new Item.Settings(),
            "hoe_chu"
    );
    public static final Item PICKAXE_CHU = register(
            Item::new,
            new Item.Settings(),
            "pickaxe_chu"
    );
    public static final Item SHOVEL_CHU = register(
            Item::new,
            new Item.Settings(),
            "shovel_chu"
    );
    public static final Item HELMET_CHU = register(
            Item::new,
            new Item.Settings(),
            "helmet_chu"
    );
    public static final Item CHEST_CHU = register(
            Item::new,
            new Item.Settings(),
            "chest_chu"
    );
    public static final Item LEGS_CHU = register(
            Item::new,
            new Item.Settings(),
            "legs_chu"
    );
    public static final Item BOOTS_CHU = register(
            Item::new,
            new Item.Settings(),
            "boots_chu"
    );

    public static final Item WRITH_BOOK = register(
            Item::new,
            new Item.Settings(),
            "writable_book"
    );


    public static final Item CRYSTAL_CLEAR = register(
            Item::new,
            new Item.Settings(),
            "crystal_clear"
    );
    public static final Item DUST = register(
            Item::new,
            new Item.Settings(),
            "dust"
    );
    public static final Item MASS = register(
            Item::new,
            new Item.Settings(),
            "mass"
    );
    public static final Item DUST_CLEAR = register(
            Item::new,
            new Item.Settings(),
            "dust_clear"
    );
    public static final Item CRYSTAL_CORE = register(
            Item::new,
            new Item.Settings(),
            "crystal_core"
    );
    public static final Item CRYSTAL_IGNOT_INF = register(
            Item::new,
            new Item.Settings(),
            "crystal_ignot_inf"
    );
    public static final Item CRYSTAL_IGNOT_CLEAR = register(
            Item::new,
            new Item.Settings(),
            "crystal_ignot_clear"
    );
    public static final Item CRYSTAL_IGNOT_UNSTABLE = register(
            Item::new,
            new Item.Settings(),
            "crystal_ignot_unstable"
    );
    public static final Item CRYSTAL_IGNOT_STABLE = register(
            Item::new,
            new Item.Settings(),
            "crystal_ignot_stable"
    );
    public static final Item POTION_INF = register(
            PotionInf::new,
            new Item.Settings().maxCount(1),
            "potion_inf"
    );
    public static final Item POTION_CLEAR = register(
            PotionClear::new,
            new Item.Settings().maxCount(1),
            "potion_clear"
    );

    public static final Item IFIRIUM = register(
            settings -> new IfiriumItem(settings, 1),
            new Item.Settings(),
            "ifirium"
    );
    public static final Item THICK_LEATHER = register(
            ThickLeatherItem::new,
            new Item.Settings(),
            "thick_leather"
    );
    public static final Item DUB_LEATHER = register(
            DubLeatherItem::new,
            new Item.Settings(),
            "dub_leather"
    );

    public static final Item UMBRELLA_VAMPIRE = register(
            UmbrellaVampire::new,
            new Item.Settings().maxCount(1),
            "umbrella_vampire"
    );
    public static final Item UMBRELLA_SPRAY = register(
            UmbrellaSpray::new,
            new Item.Settings().maxCount(1),
            "umbrella_spray"
    );

    public static final Item UMBRELLA = register(
            Item::new,
            new Item.Settings().maxCount(1),
            "umbrella"
    );
    public static final Item UMBRELLA_GREEN = register(
            Item::new,
            new Item.Settings().maxCount(1),
            "umbrella_green"
    );
    public static final Item UMBRELLA_FLWR = register(
            Item::new,
            new Item.Settings().maxCount(1),
            "umbrella_flwr"
    );
    public static final Item UMBRELLA_HONEY = register(
            Item::new,
            new Item.Settings().maxCount(1),
            "umbrella_honey"
    );
    public static final Item UMBRELLA_PURPLE = register(
            Item::new,
            new Item.Settings().maxCount(1),
            "umbrella_purple"
    );

    public static final Item IFIRIUM_HELMET = new Item(
            new Item.Settings()
                    .armor(ModArmorMaterials.IFIRIUM, EquipmentType.HELMET)
                    .maxDamage(EquipmentType.HELMET.getMaxDamage(50))
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM,
                            Identifier.of(Story.MOD_ID, "ifirium_helmet")))
    );

    public static final Item IFIRIUM_CHESTPLATE = new Item(
            new Item.Settings()
                    .armor(ModArmorMaterials.IFIRIUM, EquipmentType.CHESTPLATE)
                    .maxDamage(EquipmentType.CHESTPLATE.getMaxDamage(50))
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM,
                            Identifier.of(Story.MOD_ID, "ifirium_chestplate")))
    );

    public static final Item IFIRIUM_LEGGINGS = new Item(
            new Item.Settings()
                    .armor(ModArmorMaterials.IFIRIUM, EquipmentType.LEGGINGS)
                    .maxDamage(EquipmentType.LEGGINGS.getMaxDamage(50))
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM,
                            Identifier.of(Story.MOD_ID, "ifirium_leggings")))
    );

    public static final Item IFIRIUM_BOOTS = new Item(
            new Item.Settings()
                    .armor(ModArmorMaterials.IFIRIUM, EquipmentType.BOOTS)
                    .maxDamage(EquipmentType.BOOTS.getMaxDamage(50))
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM,
                            Identifier.of(Story.MOD_ID, "ifirium_boots")))
    );

    public static final Item GUIDITE_HELMET = new Item(
            new Item.Settings()
                    .armor(ModArmorMaterials.INSTANCE, EquipmentType.HELMET)
                    .maxDamage(EquipmentType.HELMET.getMaxDamage(50))
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM,
                            Identifier.of(Story.MOD_ID, "him_helmet")))
    );

    public static final Item GUIDITE_CHESTPLATE = new Item(
            new Item.Settings()
                    .armor(ModArmorMaterials.INSTANCE, EquipmentType.CHESTPLATE)
                    .maxDamage(EquipmentType.CHESTPLATE.getMaxDamage(50))
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM,
                            Identifier.of(Story.MOD_ID, "him_chestplate")))
    );

    public static final Item GUIDITE_LEGGINGS = new Item(
            new Item.Settings()
                    .armor(ModArmorMaterials.INSTANCE, EquipmentType.LEGGINGS)
                    .maxDamage(EquipmentType.LEGGINGS.getMaxDamage(50))
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM,
                            Identifier.of(Story.MOD_ID, "him_leggings")))
    );

    public static final Item GUIDITE_BOOTS = new Item(
            new Item.Settings()
                    .armor(ModArmorMaterials.INSTANCE, EquipmentType.BOOTS)
                    .maxDamage(EquipmentType.BOOTS.getMaxDamage(50))
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM,
                            Identifier.of(Story.MOD_ID, "him_boots")))
    );

    // Метод для регистрации всех предметов
    public static void registerAll() {
        Registry.register(Registries.ITEM, Identifier.of(Story.MOD_ID, "him_helmet"), GUIDITE_HELMET);
        Registry.register(Registries.ITEM, Identifier.of(Story.MOD_ID, "him_chestplate"), GUIDITE_CHESTPLATE);
        Registry.register(Registries.ITEM, Identifier.of(Story.MOD_ID, "him_leggings"), GUIDITE_LEGGINGS);
        Registry.register(Registries.ITEM, Identifier.of(Story.MOD_ID, "him_boots"), GUIDITE_BOOTS);
        Registry.register(Registries.ITEM, Identifier.of(Story.MOD_ID, "ifirium_helmet"), IFIRIUM_HELMET);
        Registry.register(Registries.ITEM, Identifier.of(Story.MOD_ID, "ifirium_chestplate"), IFIRIUM_CHESTPLATE);
        Registry.register(Registries.ITEM, Identifier.of(Story.MOD_ID, "ifirium_leggings"), IFIRIUM_LEGGINGS);
        Registry.register(Registries.ITEM, Identifier.of(Story.MOD_ID, "ifirium_boots"), IFIRIUM_BOOTS);
    }

    public static Item register(Function<Item.Settings, Item> factory, Item.Settings settings, String id) {
        Identifier itemID = Story.id(id);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, itemID);

        return Registry.register(Registries.ITEM, itemID, factory.apply(settings.registryKey(key)));
    }

    public static void initialize() {

        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(IFIRIUM));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(THICK_LEATHER));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(DUB_LEATHER));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(WRITH_BOOK));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(UMBRELLA));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(UMBRELLA_FLWR));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(UMBRELLA_GREEN));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(UMBRELLA_HONEY));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(UMBRELLA_PURPLE));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(GUIDITE_HELMET));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(GUIDITE_CHESTPLATE));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(GUIDITE_LEGGINGS));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(GUIDITE_BOOTS));


        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CRUSHER_CHU));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CLEANER_CHU));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(PRESS_CHU));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(SPLAV_CHU));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CRYSTAL_CLEAR));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CRYSTAL_CORE));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(DUST));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(MASS));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(DUST_CLEAR));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(POTION_INF));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(POTION_CLEAR));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(GULE));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(UNDERWATERMASK));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(NUMBNESS));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(NIGHTLIGHT));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(LIBERT));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(DECEIT));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(FINDED));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(DEVOURER));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(KIRPITCH));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(UMBRELLA_VAMPIRE));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(UMBRELLA_SPRAY));

        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(SCALPEL));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(IFIRIUM_SWORD));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(IFIRIUM_AXE));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(IFIRIUM_HOE));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(IFIRIUM_SHOVEL));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(IFIRIUM_PICAXE));

        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(IFIRIUM_BOOTS));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(IFIRIUM_LEGGINGS));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(IFIRIUM_CHESTPLATE));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(IFIRIUM_HELMET));

        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CRYSTAL_IGNOT_INF));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CRYSTAL_IGNOT_CLEAR));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CRYSTAL_IGNOT_UNSTABLE));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CRYSTAL_IGNOT_STABLE));

        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(AXE_CHU));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(SWORD_CHU));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(PICKAXE_CHU));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(SHOVEL_CHU));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(HOE_CHU));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(HELMET_CHU));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CHEST_CHU));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(LEGS_CHU));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(BOOTS_CHU));

    }


}