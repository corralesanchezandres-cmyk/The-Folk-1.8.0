package com.thefolk.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thefolk.TheFolkMod;
import com.thefolk.entity.FolkMonster;
import com.thefolk.entity.UknowEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Bestia cuadrúpeda con dos cuernos curvados, cresta en el lomo y cola. */
public class UknowModel extends EntityModel<UknowEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(TheFolkMod.MOD_ID, "uknow"), "main");

    private static final float RAD = (float) Math.PI / 180.0F;

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart backRightLeg;
    private final ModelPart backLeftLeg;

    public UknowModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.tail = this.body.getChild("tail");
        this.frontRightLeg = root.getChild("front_right_leg");
        this.frontLeftLeg = root.getChild("front_left_leg");
        this.backRightLeg = root.getChild("back_right_leg");
        this.backLeftLeg = root.getChild("back_left_leg");
    }

    private static CubeListBuilder leg() {
        return CubeListBuilder.create().texOffs(68, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition r = mesh.getRoot();

        // Cuerpo largo con cresta de tres púas en el lomo (pivote en el centro del cuerpo)
        PartDefinition body = r.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-6.0F, -5.0F, -11.0F, 12.0F, 10.0F, 22.0F)
                        .texOffs(58, 32)
                        .addBox(-1.0F, -8.0F, -6.0F, 2.0F, 3.0F, 3.0F)
                        .addBox(-1.0F, -8.0F, -1.5F, 2.0F, 3.0F, 3.0F)
                        .addBox(-1.0F, -8.0F, 3.0F, 2.0F, 3.0F, 3.0F),
                PartPose.offset(0.0F, 7.0F, 0.0F));

        // Cabeza baja y cuadrada, con dos cuernos que se curvan hacia delante
        PartDefinition head = body.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, -4.0F, -9.0F, 8.0F, 8.0F, 9.0F),
                PartPose.offset(0.0F, -2.0F, -11.0F));

        PartDefinition rightHorn = head.addOrReplaceChild("right_horn",
                CubeListBuilder.create().texOffs(84, 0).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offsetAndRotation(-3.5F, -4.0F, -6.0F, 0.0F, 0.0F, -0.6F));
        rightHorn.addOrReplaceChild("right_horn_tip",
                CubeListBuilder.create().texOffs(92, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, 0.9F, 0.0F, 0.0F));

        PartDefinition leftHorn = head.addOrReplaceChild("left_horn",
                CubeListBuilder.create().texOffs(84, 0).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offsetAndRotation(3.5F, -4.0F, -6.0F, 0.0F, 0.0F, 0.6F));
        leftHorn.addOrReplaceChild("left_horn_tip",
                CubeListBuilder.create().texOffs(92, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, 0.9F, 0.0F, 0.0F));

        // Cola
        body.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(34, 32).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, -3.0F, 11.0F, 0.4F, 0.0F, 0.0F));

        // Patas
        r.addOrReplaceChild("front_right_leg", leg(), PartPose.offset(-4.0F, 12.0F, -8.0F));
        r.addOrReplaceChild("front_left_leg", leg(), PartPose.offset(4.0F, 12.0F, -8.0F));
        r.addOrReplaceChild("back_right_leg", leg(), PartPose.offset(-4.0F, 12.0F, 8.0F));
        r.addOrReplaceChild("back_left_leg", leg(), PartPose.offset(4.0F, 12.0F, 8.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(UknowEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        int state = entity.getUknowState();
        int status = entity.getStatus();
        float amount = limbSwingAmount;
        float cycle = limbSwing * 0.6662F;
        float breathe = Mth.sin(ageInTicks * 0.1F);

        // Trote: las patas opuestas van juntas
        float gait = Mth.cos(cycle) * 1.4F * amount;
        this.frontRightLeg.xRot = gait;
        this.backLeftLeg.xRot = gait;
        this.frontLeftLeg.xRot = -gait;
        this.backRightLeg.xRot = -gait;

        // Cuerpo que respira y cabeza baja, olfateando
        this.body.xRot = Mth.sin(cycle * 2.0F) * 0.03F * amount + breathe * 0.01F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;
        this.head.xRot = headPitch * RAD + 0.3F + breathe * 0.03F;
        this.head.yRot = netHeadYaw * RAD;
        this.head.zRot = 0.0F;
        this.tail.xRot = 0.4F;
        this.tail.yRot = Mth.sin(ageInTicks * 0.2F) * 0.3F;

        if (state == UknowEntity.STATE_POUNCE) {
            // Salto: patas delanteras estiradas hacia delante y traseras empujando hacia atrás
            this.frontRightLeg.xRot = -1.2F;
            this.frontLeftLeg.xRot = -1.2F;
            this.backRightLeg.xRot = 1.0F;
            this.backLeftLeg.xRot = 1.0F;
            this.body.xRot = -0.15F;
            this.head.xRot = headPitch * RAD + 0.5F;
            this.tail.xRot = 0.1F;
        } else if (state == UknowEntity.STATE_HOLD) {
            // Sujetando a su presa: agachado y sacudiendo la cabeza
            this.body.xRot = 0.1F;
            this.head.xRot = 0.6F + Mth.cos(ageInTicks * 1.8F) * 0.3F;
            this.head.yRot = Mth.sin(ageInTicks * 1.5F) * 0.6F;
            this.tail.yRot = Mth.sin(ageInTicks * 0.8F) * 0.6F;
        }

        // Efectos de las armas
        if (status == FolkMonster.STATUS_PARALYZED) {
            this.body.zRot = Mth.sin(ageInTicks * 2.5F) * 0.05F;
            this.head.zRot = Mth.sin(ageInTicks * 3.1F) * 0.15F;
        } else if (status == FolkMonster.STATUS_CONFUSED) {
            this.head.xRot += Mth.sin(ageInTicks * 0.35F) * 0.4F;
            this.head.yRot += Mth.cos(ageInTicks * 0.3F) * 0.5F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
