package danilcus.magnetism;

import danilcus.magnetism.entity.SawEntity;
import danilcus.magnetism.item.SawBladeItem;
import danilcus.magnetism.item.SawbladeLauncherItem;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Magnetism implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "magnetism";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityType<SawEntity> SAW = Registry.register(
			Registry.ENTITY_TYPE,
			ID("saw_entity"),
			FabricEntityTypeBuilder.<SawEntity>create(SpawnGroup.MISC, SawEntity::new)
					.dimensions(EntityDimensions.fixed(1.0f, 0.2F))
					.build()
	);

	public static final Item SAWBLADE_LAUNCHER = Registry.register(Registry.ITEM, ID("sawblade_launcher"), new SawbladeLauncherItem(new FabricItemSettings().group(ItemGroup.COMBAT).fireproof()));
	public static final Item SAW_BLADE = Registry.register(Registry.ITEM, ID("saw_blade"), new SawBladeItem(new FabricItemSettings().group(ItemGroup.COMBAT)));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
	}

	public static Identifier ID(String path) {
		return new Identifier(MOD_ID, path);
	}
}