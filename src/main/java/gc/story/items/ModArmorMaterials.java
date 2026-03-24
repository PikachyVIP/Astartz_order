package gc.story.items;

import com.google.common.collect.Maps;
import gc.story.Story;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ModArmorMaterials {


    public static final TagKey<Item> REPAIRS_GUIDITE_ARMOR =
            TagKey.of(Registries.ITEM.getKey(), Identifier.of(Story.MOD_ID, "repairs_story_armor"));

    public static final RegistryKey<EquipmentAsset> GUIDITE_ARMOR_MATERIAL_KEY =
            RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(Story.MOD_ID, "dub_leather"));

    public static final ArmorMaterial INSTANCE = new ArmorMaterial(
            50,
            Map.of(
                    EquipmentType.HELMET, 1,
                    EquipmentType.CHESTPLATE, 1,
                    EquipmentType.LEGGINGS, 1,
                    EquipmentType.BOOTS, 1
            ),
            5,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON,
            0.0F,
            0.0F,
            REPAIRS_GUIDITE_ARMOR,
            GUIDITE_ARMOR_MATERIAL_KEY
    );

    public static final TagKey<Item> REPAIRS_IFIRIUM_ARMOR =
            TagKey.of(Registries.ITEM.getKey(), Identifier.of(Story.MOD_ID, "crystal_clear"));

    public static final RegistryKey<EquipmentAsset> IFIRIUM_ARMOR_MATERIAL_KEY =
            RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(Story.MOD_ID, "crystal_clear"));


    public static final ArmorMaterial IFIRIUM = new ArmorMaterial(
            37,
            createDefenseMap(5, 8, 12, 5, 20), 15, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            3.0F,
            0.1F,
            REPAIRS_IFIRIUM_ARMOR,
            IFIRIUM_ARMOR_MATERIAL_KEY
    );

    private static Map<EquipmentType, Integer> createDefenseMap(int bootsDefense, int leggingsDefense, int chestplateDefense, int helmetDefense, int bodyDefense) {
        return Maps.newEnumMap(
                Map.of(
                        EquipmentType.BOOTS,
                        bootsDefense,
                        EquipmentType.LEGGINGS,
                        leggingsDefense,
                        EquipmentType.CHESTPLATE,
                        chestplateDefense,
                        EquipmentType.HELMET,
                        helmetDefense,
                        EquipmentType.BODY,
                        bodyDefense
                )
        );
    }
}