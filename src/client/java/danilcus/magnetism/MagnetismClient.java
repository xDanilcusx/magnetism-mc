package danilcus.magnetism;

import danilcus.magnetism.entity.SawBladeModel;
import danilcus.magnetism.entity.SawRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class MagnetismClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRendererRegistry.register(Magnetism.SAW, SawRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(SawBladeModel.SAWBLADE, SawBladeModel::getTexturedModelData);
	}
}