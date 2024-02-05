package danilcus.magnetism.entity;

import danilcus.magnetism.Magnetism;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class SawBladeModel<T extends SawEntity> extends SinglePartEntityModel<T> {
	private final ModelPart Saw;
	public SawBladeModel(ModelPart root) {
		this.Saw = root.getChild("Saw");
	}
	public static final EntityModelLayer SAWBLADE = new EntityModelLayer(Magnetism.ID("sawblade"), "main");
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("Saw", ModelPartBuilder.create().uv(-16, 0).cuboid(-8.0F, 0.8F, -8.0F, 16.0F, 0.1F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 16);
	}
	@Override
	public void setAngles(SawEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		Saw.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getPart() {
		return Saw;
	}
}