package gc.story.items;

import gc.story.Story;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
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
}