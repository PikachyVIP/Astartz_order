package gc.story.inits;

import gc.story.Story;
import gc.story.items.DubLeatherItem;
import gc.story.items.IfiriumItem;
import gc.story.items.ModArmorMaterials;
import gc.story.items.ThickLeatherItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {

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

    public static final Item UMBRELLA = register(
            Item::new,
            new Item.Settings().maxCount(1),
            "umbrella"
    );
    public static final Item UMBRELLA_BLUE = register(
            Item::new,
            new Item.Settings().maxCount(1),
            "umbrella_blue"
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
    public static final Item UMBRELLA_MSHRM = register(
            Item::new,
            new Item.Settings().maxCount(1),
            "umbrella_mshrm"
    );
    public static final Item UMBRELLA_PURPLE = register(
            Item::new,
            new Item.Settings().maxCount(1),
            "umbrella_purple"
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
                group.add(UMBRELLA));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(UMBRELLA_BLUE));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(UMBRELLA_FLWR));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(UMBRELLA_GREEN));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(UMBRELLA_HONEY));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(UMBRELLA_MSHRM));
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
    }
}