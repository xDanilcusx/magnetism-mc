package danilcus.magnetism.entity;

import danilcus.magnetism.Magnetism;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class SawRenderer extends EntityRenderer<SawEntity> {
    public static final Identifier TEXTURE = Magnetism.ID("textures/entity/saw_blade.png");
    private final SawBladeModel model;

    public SawRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new SawBladeModel<>(context.getPart(SawBladeModel.SAWBLADE));
    }

    public void render(SawEntity sawEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        super.render(sawEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public Identifier getTexture(SawEntity entity) {
        return TEXTURE;
    }
}
