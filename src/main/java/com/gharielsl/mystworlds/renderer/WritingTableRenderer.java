package com.gharielsl.mystworlds.renderer;

import com.gharielsl.mystworlds.block.WritingTableBlock;
import com.gharielsl.mystworlds.block.entity.WritingTableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WritingTableRenderer implements BlockEntityRenderer<WritingTableBlockEntity> {
    private final BookModel bookModel;

    public WritingTableRenderer(BlockEntityRendererProvider.Context context) {
        this.bookModel = new BookModel(context.bakeLayer(ModelLayers.BOOK));
    }

    public void render(WritingTableBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockstate = be.getBlockState();
        if (blockstate.getValue(WritingTableBlock.PART) == WritingTableBlock.WritingTablePart.RIGHT && blockstate.getValue(WritingTableBlock.BOOK)) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.88F, 0.5F);
            float f = blockstate.getValue(WritingTableBlock.FACING).getClockWise().getClockWise().toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(-f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90F));
            this.bookModel.setupAnim(0.0F, 0.1F, 0.9F, 1.2F);
            VertexConsumer vertexconsumer = EnchantTableRenderer.BOOK_LOCATION.buffer(bufferSource, RenderType::entitySolid);
            this.bookModel.render(poseStack, vertexconsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
    }
}
