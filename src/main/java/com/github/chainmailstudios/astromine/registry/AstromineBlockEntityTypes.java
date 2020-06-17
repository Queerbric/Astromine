package com.github.chainmailstudios.astromine.registry;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.common.block.EnergyWireConnectorBlock;
import com.github.chainmailstudios.astromine.common.block.entity.EnergyWireConnectorBlockEntity;
import com.github.chainmailstudios.astromine.common.block.entity.OxygenVentBlockEntity;
import com.github.chainmailstudios.astromine.common.block.entity.HolographicBridgeProjectorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class AstromineBlockEntityTypes {
	public static BlockEntityType<HolographicBridgeProjectorBlockEntity> HOLOGRAPHIC_BRIDGE = register("holographic_bridge", HolographicBridgeProjectorBlockEntity::new, AstromineBlocks.HOLOGRAPHIC_BRIDGE_PROJECTOR);

	public static BlockEntityType<OxygenVentBlockEntity> OXYGEN_VENT = register("oxygen_vent", OxygenVentBlockEntity::new, AstromineBlocks.OXYGEN_VENT);

	public static BlockEntityType<EnergyWireConnectorBlockEntity> ENERGY_WIRE_CONNECTOR = register("energy_wire_connector", EnergyWireConnectorBlockEntity::new, AstromineBlocks.ENERGY_WIRE_CONNECTOR);

	public static void initialize() {
		// Unused.
	}

	/**
	 * @param name            Name of BlockEntityType instance to be registered
	 * @param supplier        Supplier of BlockEntity to use for BlockEntityType
	 * @param supportedBlocks Blocks the BlockEntity can be attached to
	 * @return Registered BlockEntityType
	 */
	public static <B extends BlockEntity> BlockEntityType<B> register(String name, Supplier<B> supplier, Block... supportedBlocks) {
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, AstromineCommon.identifier(name), BlockEntityType.Builder.create(supplier, supportedBlocks).build(null));
	}
}
