package gc.story.inits;

import gc.story.entites.kirpitch.KirpithEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<KirpithEntity> KIRPITH_ENTITY_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("story", "kirpith"),
            EntityType.Builder.<KirpithEntity>create(KirpithEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25f, 0.25f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(1)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("story", "kirpith")))
    );

    public static void initialize() {
    }
}