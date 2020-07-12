package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.api.helper.GliderHelper;
import com.crowsofwar.avatar.common.entity.EntityAscendedFlyingLemur;
import com.crowsofwar.avatar.common.entity.EntityFlyingLemur;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * @author Korog3
 */

public class ModelLemur extends ModelBase {
    public ModelRenderer Body;
    public ModelRenderer BodyLower;
    public ModelRenderer UpperBody;
    public ModelRenderer ArmRight;
    public ModelRenderer ArmLeft;
    public ModelRenderer Neck;
    public ModelRenderer BodyLowerBack;
    public ModelRenderer BodyLowerFront;
    public ModelRenderer BodyLowerSideRight;
    public ModelRenderer BodyLowerSideLeft;
    public ModelRenderer LegRight;
    public ModelRenderer LegLeft;
    public ModelRenderer Tail_start;
    public ModelRenderer BodyLDetail;
    public ModelRenderer BodyLDetail1;
    public ModelRenderer BodyLDetail2;
    public ModelRenderer BodyLDetail4;
    public ModelRenderer LegDetailRight;
    public ModelRenderer LegDetailRight1;
    public ModelRenderer LegRightLower;
    public ModelRenderer LegRightLowerDetail;
    public ModelRenderer LegRightLowerDetail1;
    public ModelRenderer LegRightFoot;
    public ModelRenderer LegRightFootDetail;
    public ModelRenderer LegRightFootDetail1;
    public ModelRenderer LegRightFootDetail2;
    public ModelRenderer LegDetailLeft;
    public ModelRenderer LegDetaiLeft1;
    public ModelRenderer LegLeftLower;
    public ModelRenderer LegLeftLowerDetail;
    public ModelRenderer LegLeftLowerDetail1;
    public ModelRenderer LegLeftFoot;
    public ModelRenderer LegLeftFootDetail;
    public ModelRenderer LegLeftFootDetail1;
    public ModelRenderer LegLeftFootDetail2;
    public ModelRenderer Tail_mid1;
    public ModelRenderer Tail_mid2;
    public ModelRenderer Tail_mid3;
    public ModelRenderer Tail_mid4;
    public ModelRenderer Tail_end;
    public ModelRenderer BodyDetailFront;
    public ModelRenderer BodyDetailBack;
    public ModelRenderer BodyDetailSideRight;
    public ModelRenderer BodyDetailSideLeft;
    public ModelRenderer neckDetail;
    public ModelRenderer neckDetail2;
    public ModelRenderer neckDetail3;
    public ModelRenderer neckDetail4;
    public ModelRenderer ArmRightDetail;
    public ModelRenderer ArmRightDetail1;
    public ModelRenderer ArmRightDetail11;
    public ModelRenderer ArmRightDetail12;
    public ModelRenderer ArmRightLower;
    public ModelRenderer Right_Wing;
    public ModelRenderer ArmRightLowerDetail;
    public ModelRenderer ArmRightLowerDetail1;
    public ModelRenderer ArmRightHand;
    public ModelRenderer ArmRightFingerMid;
    public ModelRenderer ArmRightFingerback;
    public ModelRenderer ArmRightFingerfront;
    public ModelRenderer ArmRightFingersBig;
    public ModelRenderer Right_Wing_Overlay;
    public ModelRenderer ArmLeftDetail;
    public ModelRenderer ArmLeftDetail1;
    public ModelRenderer ArmLeftDetail11;
    public ModelRenderer ArmLeftDetail12;
    public ModelRenderer ArmLeftLower;
    public ModelRenderer Left_Wing;
    public ModelRenderer ArmLeftLowerDetail;
    public ModelRenderer ArmLeftLowerDetail1;
    public ModelRenderer ArmLeftHand;
    public ModelRenderer ArmLeftFingerMid;
    public ModelRenderer ArmLeftFingerback;
    public ModelRenderer ArmLeftFingerfront;
    public ModelRenderer ArmLeftFingersBig;
    public ModelRenderer Left_Wing_Overlay;
    public ModelRenderer theHead;
    public ModelRenderer theHeadDetail;
    public ModelRenderer theHeadDetail1;
    public ModelRenderer theHeadDetail2;
    public ModelRenderer headtop;
    public ModelRenderer headBack;
    public ModelRenderer headBack1;
    public ModelRenderer theHeadDetail7999;
    public ModelRenderer headfrontdetaila1;
    public ModelRenderer headfrontdetaila1_1;
    public ModelRenderer FF1;
    public ModelRenderer EarRight;
    public ModelRenderer EarLeft;
    public ModelRenderer theHeadDetaile;
    public ModelRenderer theHeadDetailh;
    public ModelRenderer theHeadDetail1e;
    public ModelRenderer theHeadDetail1h;
    public ModelRenderer theHeadDetail2e;
    public ModelRenderer theHeadDetail2h;
    public ModelRenderer headBack2;
    public ModelRenderer headBack113;
    public ModelRenderer headBack11;
    public ModelRenderer headBack111;
    public ModelRenderer theHeadDetail2e768;
    public ModelRenderer theHeadDetail2h789;
    public ModelRenderer headfrontdetaila4;
    public ModelRenderer headfrontdetaila5;
    public ModelRenderer headfrontdetaila4_1;
    public ModelRenderer headfrontdetaila5_1;
    public ModelRenderer FF2;
    public ModelRenderer FF3;
    public ModelRenderer chek;
    public ModelRenderer chek1;
    public ModelRenderer FF4;
    public ModelRenderer EarR1;
    public ModelRenderer EarRB1;
    public ModelRenderer EarR2;
    public ModelRenderer EarM2;
    public ModelRenderer EarR3;
    public ModelRenderer EarM5;
    public ModelRenderer EarRB2;
    public ModelRenderer EarM1;
    public ModelRenderer EarRB3;
    public ModelRenderer EarRB2Detail;
    public ModelRenderer EarM3;
    public ModelRenderer EarRB2Detaildetail;
    public ModelRenderer EarL1;
    public ModelRenderer EarLB1;
    public ModelRenderer EarL2;
    public ModelRenderer EarL2_1;
    public ModelRenderer EarL3;
    public ModelRenderer EarL5;
    public ModelRenderer EarLB2;
    public ModelRenderer EarlM1;
    public ModelRenderer EarLB3;
    public ModelRenderer EarLB2Detail;
    public ModelRenderer EarML3;
    public ModelRenderer EarLB2Detaildetail;

    private State state = State.STANDING;
    
    public ModelLemur() {
        this.textureWidth = 160;
        this.textureHeight = 64;
        this.BodyLowerBack = new ModelRenderer(this, 24, 0);
        this.BodyLowerBack.setRotationPoint(0.0F, 0.0F, 2.0F);
        this.BodyLowerBack.addBox(-1.5F, 0.0F, -2.0F, 3, 6, 2, 0.0F);
        this.setRotateAngle(BodyLowerBack, -0.12217304763960307F, 0.0F, 0.0F);
        this.BodyDetailFront = new ModelRenderer(this, 8, 9);
        this.BodyDetailFront.setRotationPoint(0.0F, 0.0F, -2.0F);
        this.BodyDetailFront.addBox(-1.5F, -2.0F, 0.0F, 3, 2, 1, 0.0F);
        this.setRotateAngle(BodyDetailFront, -0.5235987755982988F, 0.0F, 0.0F);
        this.Right_Wing = new ModelRenderer(this, 92, 0);
        this.Right_Wing.setRotationPoint(0.0F, -3.5F, 0.0F);
        this.Right_Wing.addBox(0.0F, 0.0F, 0.0F, 1, 30, 11, 0.0F);
        this.setRotateAngle(Right_Wing, -0.2792526803190927F, 0.0F, 0.02617993877991494F);
        this.BodyLDetail = new ModelRenderer(this, 35, 11);
        this.BodyLDetail.setRotationPoint(-2.5F, 0.0F, -2.0F);
        this.BodyLDetail.addBox(0.0F, 0.0F, 0.0F, 1, 6, 1, 0.0F);
        this.setRotateAngle(BodyLDetail, 0.12217304763960307F, 0.0F, -0.13788101090755206F);
        this.EarRB2Detail = new ModelRenderer(this, 30, 37);
        this.EarRB2Detail.setRotationPoint(-1.0F, 2.0F, 4.0F);
        this.EarRB2Detail.addBox(0.0F, -1.0F, 0.0F, 2, 1, 2, 0.0F);
        this.setRotateAngle(EarRB2Detail, 0.389033890269536F, 0.0F, 0.0F);
        this.Body = new ModelRenderer(this, 0, 0);
        this.Body.setRotationPoint(0.0F, 7.5F, 0.0F);
        this.Body.addBox(-2.5F, -5.0F, -2.0F, 5, 5, 4, 0.0F);
        this.neckDetail = new ModelRenderer(this, 18, 3);
        this.neckDetail.setRotationPoint(-2.0F, -0.0F, -1.5F);
        this.neckDetail.addBox(-0.9F, -2.0F, 0.0F, 2, 2, 1, 0.0F);
        this.setRotateAngle(neckDetail, -0.3839724354387525F, 0.7853981633974483F, 0.0F);
        this.neckDetail3 = new ModelRenderer(this, 18, 6);
        this.neckDetail3.setRotationPoint(2.0F, 0.0F, 1.5F);
        this.neckDetail3.addBox(-0.9F, -2.0F, 0.0F, 2, 2, 1, 0.0F);
        this.setRotateAngle(neckDetail3, -0.3839724354387525F, -2.356194490192345F, 0.0F);
        this.ArmRight = new ModelRenderer(this, 0, 27);
        this.ArmRight.setRotationPoint(2.5F, -5.0F, 0.0F);
        this.ArmRight.addBox(0.0F, 0.0F, -1.01F, 1, 8, 1, 0.0F);
        this.setRotateAngle(ArmRight, 0.0F, -0.17453292519943295F, -0.17453292519943295F);
        this.headtop = new ModelRenderer(this, 66, 0);
        this.headtop.setRotationPoint(0.0F, -6.42F, 0.0F);
        this.headtop.addBox(-1.5F, 0.0F, -1.5F, 3, 1, 3, 0.0F);
        this.EarL2 = new ModelRenderer(this, 118, 10);
        this.EarL2.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.EarL2.addBox(0.0F, -1.0F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(EarL2, 0.5235987755982988F, 0.0F, 0.0F);
        this.ArmLeftFingerback = new ModelRenderer(this, 43, 13);
        this.ArmLeftFingerback.setRotationPoint(0.5F, 0.5F, 0.5F);
        this.ArmLeftFingerback.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(ArmLeftFingerback, 0.17453292519943295F, 0.5235987755982988F, -0.2617993877991494F);
        this.theHeadDetailh = new ModelRenderer(this, 54, 12);
        this.theHeadDetailh.setRotationPoint(3.0F, 0.0F, 0.0F);
        this.theHeadDetailh.addBox(0.0F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(theHeadDetailh, 0.0F, 0.0F, -0.6981317007977318F);
        this.headfrontdetaila4_1 = new ModelRenderer(this, 66, 44);
        this.headfrontdetaila4_1.setRotationPoint(0.0F, 0.0F, 2.6F);
        this.headfrontdetaila4_1.addBox(-1.5F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(headfrontdetaila4_1, 0.7365289443416071F, 0.0F, 0.0F);
        this.ArmRightFingerfront = new ModelRenderer(this, 43, 13);
        this.ArmRightFingerfront.setRotationPoint(0.5F, 0.5F, 0.5F);
        this.ArmRightFingerfront.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(ArmRightFingerfront, 0.17453292519943295F, 0.5235987755982988F, -0.2617993877991494F);
        this.EarLB3 = new ModelRenderer(this, 114, 46);
        this.EarLB3.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.EarLB3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 6, 0.0F);
        this.setRotateAngle(EarLB3, -0.1223475805648025F, 0.3490658503988659F, 0.0F);
        this.EarR3 = new ModelRenderer(this, 6, 36);
        this.EarR3.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.EarR3.addBox(-1.0F, -1.0F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(EarR3, 0.4363323129985824F, 0.0F, 0.0F);
        this.headBack2 = new ModelRenderer(this, 66, 8);
        this.headBack2.setRotationPoint(0.0F, 0.0F, 2.6F);
        this.headBack2.addBox(-1.5F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(headBack2, 0.7312929565856241F, 0.0F, 0.0F);
        this.Neck = new ModelRenderer(this, 8, 12);
        this.Neck.setRotationPoint(0.0F, -5.75F, 0.0F);
        this.Neck.addBox(-1.5F, -2.0F, -1.0F, 3, 1, 2, 0.0F);
        this.theHeadDetail1e = new ModelRenderer(this, 78, 20);
        this.theHeadDetail1e.setRotationPoint(-3.0F, 0.0F, 0.0F);
        this.theHeadDetail1e.addBox(-3.0F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(theHeadDetail1e, 0.0F, 0.0F, 0.9599310885968813F);
        this.LegRightLowerDetail1 = new ModelRenderer(this, 34, 10);
        this.LegRightLowerDetail1.setRotationPoint(0.0F, -0.3F, 0.1F);
        this.LegRightLowerDetail1.addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(LegRightLowerDetail1, -0.2399827721492203F, 0.0F, 0.0F);
        this.EarRB2Detaildetail = new ModelRenderer(this, 30, 45);
        this.EarRB2Detaildetail.setRotationPoint(0.0F, -1.0F, 2.5F);
        this.EarRB2Detaildetail.addBox(0.0F, 0.0F, -2.0F, 2, 1, 2, 0.0F);
        this.setRotateAngle(EarRB2Detaildetail, -0.4557054676957194F, 0.0F, 0.0F);
        this.EarLB2Detaildetail = new ModelRenderer(this, 112, 61);
        this.EarLB2Detaildetail.setRotationPoint(0.0F, -1.0F, 2.5F);
        this.EarLB2Detaildetail.addBox(0.0F, 0.0F, -2.0F, 2, 1, 2, 0.0F);
        this.setRotateAngle(EarLB2Detaildetail, -0.4557054676957194F, 0.0F, 0.0F);
        this.LegLeftLowerDetail = new ModelRenderer(this, 37, 9);
        this.LegLeftLowerDetail.setRotationPoint(0.0F, -0.53F, -0.88F);
        this.LegLeftLowerDetail.addBox(-0.5F, 0.0F, 0.0F, 1, 5, 1, 0.0F);
        this.BodyDetailSideLeft = new ModelRenderer(this, 15, 15);
        this.BodyDetailSideLeft.setRotationPoint(2.5F, 0.0F, 0.0F);
        this.BodyDetailSideLeft.addBox(-1.0F, -2.0F, -1.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(BodyDetailSideLeft, 0.0F, 0.0F, -0.5235987755982988F);
        this.EarM2 = new ModelRenderer(this, 8, 41);
        this.EarM2.setRotationPoint(1.0F, -1.0F, 0.0F);
        this.EarM2.addBox(-1.0F, -3.0F, 0.0F, 1, 3, 4, 0.0F);
        this.EarL1 = new ModelRenderer(this, 116, 5);
        this.EarL1.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.EarL1.addBox(-1.0F, -1.0F, 0.0F, 2, 1, 4, 0.0F);
        this.setRotateAngle(EarL1, 0.6108652381980153F, 0.0F, 0.0F);
        this.headBack1 = new ModelRenderer(this, 54, 16);
        this.headBack1.setRotationPoint(-1.5F, 0.0F, 1.5F);
        this.headBack1.addBox(-1.5F, -1.0F, -0.4F, 3, 1, 3, 0.0F);
        this.setRotateAngle(headBack1, 0.9599310885968813F, -0.7853981633974483F, 0.0F);
        this.Tail_start = new ModelRenderer(this, 60, 59);
        this.Tail_start.setRotationPoint(0.0F, 5.5F, 1.0F);
        this.Tail_start.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(Tail_start, 0.22183134792847928F, 0.0F, 0.0F);
        this.FF3 = new ModelRenderer(this, 71, 60);
        this.FF3.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.FF3.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(FF3, 1.4486232791552935F, 0.0F, 0.0F);
        this.BodyLDetail4 = new ModelRenderer(this, 43, 0);
        this.BodyLDetail4.setRotationPoint(-2.5F, 0.0F, 2.0F);
        this.BodyLDetail4.addBox(0.0F, 0.0F, -1.0F, 1, 6, 1, 0.0F);
        this.setRotateAngle(BodyLDetail4, -0.12217304763960307F, 0.0F, -0.13788101090755206F);
        this.BodyLowerSideRight = new ModelRenderer(this, 37, 0);
        this.BodyLowerSideRight.setRotationPoint(-2.5F, 0.0F, 0.0F);
        this.BodyLowerSideRight.addBox(0.0F, 0.0F, -1.0F, 1, 6, 2, 0.0F);
        this.setRotateAngle(BodyLowerSideRight, 0.0F, 0.0F, -0.13962634015954636F);
        this.headfrontdetaila1_1 = new ModelRenderer(this, 54, 28);
        this.headfrontdetaila1_1.setRotationPoint(1.5F, 0.0F, -1.5F);
        this.headfrontdetaila1_1.addBox(-1.5F, -1.0F, -0.4F, 3, 1, 3, 0.0F);
        this.setRotateAngle(headfrontdetaila1_1, 0.9599310885968813F, 2.356194490192345F, 0.0F);
        this.ArmRightDetail1 = new ModelRenderer(this, 32, 28);
        this.ArmRightDetail1.setRotationPoint(1.75F, 1.0F, 0.0F);
        this.ArmRightDetail1.addBox(-1.0F, 0.0F, -1.0F, 1, 7, 1, 0.0F);
        this.setRotateAngle(ArmRightDetail1, 0.0F, 0.0F, 0.1117010721276371F);
        this.LegLeftLower = new ModelRenderer(this, 52, 62);
        this.LegLeftLower.setRotationPoint(0.5F, 5.2F, 0.0F);
        this.LegLeftLower.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(LegLeftLower, 0.3490658503988659F, 0.0F, 0.0F);
        this.BodyDetailBack = new ModelRenderer(this, 8, 9);
        this.BodyDetailBack.setRotationPoint(0.0F, 0.0F, 2.0F);
        this.BodyDetailBack.addBox(-1.5F, -2.0F, -1.0F, 3, 2, 1, 0.0F);
        this.setRotateAngle(BodyDetailBack, 0.5235987755982988F, 0.0F, 0.0F);
        this.LegRightLower = new ModelRenderer(this, 52, 62);
        this.LegRightLower.setRotationPoint(-0.5F, 5.2F, 0.0F);
        this.LegRightLower.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(LegRightLower, 0.3490658503988659F, 0.0F, 0.0F);
        this.LegRightLowerDetail = new ModelRenderer(this, 34, 9);
        this.LegRightLowerDetail.setRotationPoint(0.0F, -0.53F, -0.88F);
        this.LegRightLowerDetail.addBox(-0.5F, 0.0F, 0.0F, 1, 5, 1, 0.0F);
        this.EarLB1 = new ModelRenderer(this, 116, 34);
        this.EarLB1.setRotationPoint(1.0F, -0.2F, -0.4F);
        this.EarLB1.addBox(-1.0F, 0.0F, 0.0F, 2, 2, 4, 0.0F);
        this.setRotateAngle(EarLB1, 1.2217304763960306F, 0.0F, 0.0F);
        this.EarRB2 = new ModelRenderer(this, 16, 36);
        this.EarRB2.setRotationPoint(0.0F, 0.0F, 3.6F);
        this.EarRB2.addBox(-1.0F, 0.0F, 0.0F, 2, 2, 4, 0.0F);
        this.setRotateAngle(EarRB2, -0.4363323129985824F, 0.3490658503988659F, 0.0F);
        this.EarRight = new ModelRenderer(this, 0, 39);
        this.EarRight.setRotationPoint(-1.0F, -6.0F, 1.0F);
        this.EarRight.addBox(-1.0F, -1.0F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(EarRight, -0.2617993877991494F, -0.6108652381980153F, 0.0F);
        this.theHeadDetail1 = new ModelRenderer(this, 78, 16);
        this.theHeadDetail1.setRotationPoint(-1.5F, 0.0F, -1.5F);
        this.theHeadDetail1.addBox(-3.0F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(theHeadDetail1, 0.0F, 0.0F, 0.7853981633974483F);
        this.headfrontdetaila4 = new ModelRenderer(this, 66, 36);
        this.headfrontdetaila4.setRotationPoint(0.0F, 0.0F, 2.6F);
        this.headfrontdetaila4.addBox(-1.5F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(headfrontdetaila4, 0.7312929565856241F, 0.0F, 0.0F);
        this.ArmLeftFingerMid = new ModelRenderer(this, 43, 13);
        this.ArmLeftFingerMid.setRotationPoint(0.0F, 0.5F, 0.5F);
        this.ArmLeftFingerMid.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(ArmLeftFingerMid, 0.3490658503988659F, 0.0F, 0.0F);
        this.ArmLeftLower = new ModelRenderer(this, 52, 62);
        this.ArmLeftLower.setRotationPoint(-0.5F, 8.0F, -0.5F);
        this.ArmLeftLower.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(ArmLeftLower, -0.5235987755982988F, 0.0F, 0.0F);
        this.theHeadDetail2h789 = new ModelRenderer(this, 54, 0);
        this.theHeadDetail2h789.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.theHeadDetail2h789.addBox(0.0F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(theHeadDetail2h789, 0.6981317007977318F, 0.0F, 0.0F);
        this.EarRB1 = new ModelRenderer(this, 21, 46);
        this.EarRB1.setRotationPoint(-1.0F, -0.2F, -0.4F);
        this.EarRB1.addBox(-1.0F, 0.0F, 0.0F, 2, 2, 4, 0.0F);
        this.setRotateAngle(EarRB1, 1.2217304763960306F, 0.0F, 0.0F);
        this.theHeadDetail2e = new ModelRenderer(this, 78, 32);
        this.theHeadDetail2e.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.theHeadDetail2e.addBox(0.0F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(theHeadDetail2e, 0.9599310885968813F, 0.0F, 0.0F);
        this.EarL2_1 = new ModelRenderer(this, 118, 20);
        this.EarL2_1.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.EarL2_1.addBox(-1.0F, -3.0F, 0.0F, 1, 3, 4, 0.0F);
        this.headBack11 = new ModelRenderer(this, 66, 16);
        this.headBack11.setRotationPoint(0.0F, 0.0F, 2.6F);
        this.headBack11.addBox(-1.5F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(headBack11, 0.7312929565856241F, 0.0F, 0.0F);
        this.theHeadDetaile = new ModelRenderer(this, 78, 8);
        this.theHeadDetaile.setRotationPoint(3.0F, 0.0F, -1.5F);
        this.theHeadDetaile.addBox(0.0F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(theHeadDetaile, 0.0F, 0.0F, -0.9599310885968813F);
        this.EarL5 = new ModelRenderer(this, 116, 27);
        this.EarL5.setRotationPoint(0.0F, -1.8F, 3.0F);
        this.EarL5.addBox(-1.02F, 0.0F, 0.0F, 1, 2, 5, 0.0F);
        this.setRotateAngle(EarL5, 0.49497537586559187F, 0.0F, 0.0F);
        this.ArmRightFingerMid = new ModelRenderer(this, 43, 13);
        this.ArmRightFingerMid.setRotationPoint(0.0F, 0.5F, 0.5F);
        this.ArmRightFingerMid.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(ArmRightFingerMid, 0.3490658503988659F, 0.0F, 0.0F);
        this.Left_Wing_Overlay = new ModelRenderer(this, 136, 0);
        this.Left_Wing_Overlay.mirror = true;
        this.Left_Wing_Overlay.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Left_Wing_Overlay.addBox(0.02F, 0.0F, 0.0F, 1, 30, 11, 0.0F);
        this.headBack111 = new ModelRenderer(this, 79, 43);
        this.headBack111.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.headBack111.addBox(-1.5F, -1.0F, 0.0F, 3, 1, 2, 0.0F);
        this.setRotateAngle(headBack111, 0.5916666164260777F, 0.0F, 0.0F);
        this.LegRightFootDetail = new ModelRenderer(this, 34, 11);
        this.LegRightFootDetail.setRotationPoint(-0.8F, 0.0F, 0.0F);
        this.LegRightFootDetail.addBox(0.5F, 0.0F, -3.2F, 1, 1, 3, 0.0F);
        this.setRotateAngle(LegRightFootDetail, 0.0F, -0.4363323129985824F, 0.17453292519943295F);
        this.BodyLDetail1 = new ModelRenderer(this, 35, 11);
        this.BodyLDetail1.mirror = true;
        this.BodyLDetail1.setRotationPoint(2.5F, 0.0F, -2.0F);
        this.BodyLDetail1.addBox(-1.0F, 0.0F, 0.0F, 1, 6, 1, 0.0F);
        this.setRotateAngle(BodyLDetail1, 0.12217304763960307F, 0.0F, 0.13788101090755206F);
        this.ArmLeftFingersBig = new ModelRenderer(this, 0, 2);
        this.ArmLeftFingersBig.setRotationPoint(-0.5F, 0.5F, 0.0F);
        this.ArmLeftFingersBig.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(ArmLeftFingersBig, -0.4363323129985824F, 0.0F, 0.3839724354387525F);
        this.ArmRightHand = new ModelRenderer(this, 0, 2);
        this.ArmRightHand.mirror = true;
        this.ArmRightHand.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.ArmRightHand.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(ArmRightHand, -0.296705972839036F, 1.5707963267948966F, 0.0F);
        this.theHead = new ModelRenderer(this, 78, 0);
        this.theHead.setRotationPoint(0.0F, -2.0F, -0.15F);
        this.theHead.addBox(-1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F);
        this.LegLeft = new ModelRenderer(this, 22, 27);
        this.LegLeft.setRotationPoint(1.5F, 5.9F, 0.0F);
        this.LegLeft.addBox(0.02F, 2.0F, -1.02F, 1, 3, 2, 0.0F);
        this.setRotateAngle(LegLeft, -0.2617993877991494F, -0.2617993877991494F, -0.08726646259971647F);
        this.LegDetailRight1 = new ModelRenderer(this, 16, 32);
        this.LegDetailRight1.setRotationPoint(-1.0F, 2.0F, 0.0F);
        this.LegDetailRight1.addBox(0.0F, -2.0F, -1.01F, 1, 2, 2, 0.0F);
        this.setRotateAngle(LegDetailRight1, 0.0F, 0.0F, 0.3804817769347638F);
        this.LegDetailLeft = new ModelRenderer(this, 25, 20);
        this.LegDetailLeft.setRotationPoint(0.2F, -0.2F, 0.0F);
        this.LegDetailLeft.addBox(-1.0F, 0.0F, -1.0F, 1, 5, 2, 0.0F);
        this.setRotateAngle(LegDetailLeft, 0.0F, 0.0F, -0.15707963267948966F);
        this.Tail_mid4 = new ModelRenderer(this, 60, 59);
        this.Tail_mid4.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.Tail_mid4.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(Tail_mid4, 0.5248205060746949F, 0.0F, -0.035430183815484885F);
        this.LegLeftLowerDetail1 = new ModelRenderer(this, 25, 11);
        this.LegLeftLowerDetail1.setRotationPoint(0.0F, -0.3F, 0.1F);
        this.LegLeftLowerDetail1.addBox(-0.5F, 0.0F, 0.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(LegLeftLowerDetail1, -0.2399827721492203F, 0.0F, 0.0F);
        this.ArmLeftDetail11 = new ModelRenderer(this, 12, 28);
        this.ArmLeftDetail11.mirror = true;
        this.ArmLeftDetail11.setRotationPoint(-1.75F, 1.0F, 1.0F);
        this.ArmLeftDetail11.addBox(0.01F, 0.0F, -1.0F, 1, 7, 1, 0.0F);
        this.setRotateAngle(ArmLeftDetail11, -0.14486232791552936F, 0.0F, -0.1117010721276371F);
        this.EarR2 = new ModelRenderer(this, 0, 49);
        this.EarR2.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.EarR2.addBox(-1.0F, -1.0F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(EarR2, 0.5235987755982988F, 0.0F, 0.0F);
        this.EarlM1 = new ModelRenderer(this, 102, 58);
        this.EarlM1.setRotationPoint(-1.0F, 1.0F, 0.8F);
        this.EarlM1.addBox(0.0F, -2.0F, 0.0F, 1, 2, 4, 0.0F);
        this.setRotateAngle(EarlM1, -1.1370820076743058F, -0.08970992355250852F, 0.17453292519943295F);
        this.theHeadDetail2 = new ModelRenderer(this, 78, 28);
        this.theHeadDetail2.setRotationPoint(-1.5F, 0.0F, 1.5F);
        this.theHeadDetail2.addBox(0.0F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(theHeadDetail2, 0.7853981633974483F, 0.0F, 0.0F);
        this.neckDetail2 = new ModelRenderer(this, 18, 3);
        this.neckDetail2.setRotationPoint(2.0F, 0.0F, -1.5F);
        this.neckDetail2.addBox(-0.9F, -2.0F, 0.0F, 2, 2, 1, 0.0F);
        this.setRotateAngle(neckDetail2, -0.3839724354387525F, -0.7853981633974483F, 0.0F);
        this.FF1 = new ModelRenderer(this, 83, 59);
        this.FF1.setRotationPoint(0.0F, -3.3F, -4.0F);
        this.FF1.addBox(-0.5F, 0.0F, -1.0F, 1, 3, 2, 0.0F);
        this.setRotateAngle(FF1, 0.3490658503988659F, 0.0F, 0.0F);
        this.BodyLowerSideLeft = new ModelRenderer(this, 37, 0);
        this.BodyLowerSideLeft.mirror = true;
        this.BodyLowerSideLeft.setRotationPoint(2.5F, 0.0F, 0.0F);
        this.BodyLowerSideLeft.addBox(-1.0F, 0.0F, -1.0F, 1, 6, 2, 0.0F);
        this.setRotateAngle(BodyLowerSideLeft, 0.0F, 0.0F, 0.13962634015954636F);
        this.LegLeftFootDetail = new ModelRenderer(this, 34, 9);
        this.LegLeftFootDetail.setRotationPoint(-0.8F, 0.0F, 0.0F);
        this.LegLeftFootDetail.addBox(0.5F, 0.0F, -3.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(LegLeftFootDetail, 0.0F, -0.4363323129985824F, 0.17453292519943295F);
        this.Left_Wing = new ModelRenderer(this, 92, 0);
        this.Left_Wing.mirror = true;
        this.Left_Wing.setRotationPoint(-1.0F, -3.5F, 0.0F);
        this.Left_Wing.addBox(0.0F, 0.0F, 0.0F, 1, 30, 11, 0.0F);
        this.setRotateAngle(Left_Wing, -0.2792526803190927F, 0.0F, -0.02617993877991494F);
        this.EarR1 = new ModelRenderer(this, 0, 44);
        this.EarR1.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.EarR1.addBox(-1.0F, -1.0F, 0.0F, 2, 1, 4, 0.0F);
        this.setRotateAngle(EarR1, 0.6108652381980153F, 0.0F, 0.0F);
        this.ArmRightFingersBig = new ModelRenderer(this, 0, 2);
        this.ArmRightFingersBig.setRotationPoint(0.5F, 0.5F, 0.0F);
        this.ArmRightFingersBig.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(ArmRightFingersBig, -0.4363323129985824F, 0.0F, -0.3839724354387525F);
        this.FF2 = new ModelRenderer(this, 77, 60);
        this.FF2.setRotationPoint(0.0F, 0.5F, -1.0F);
        this.FF2.addBox(-0.5F, -2.0F, 0.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(FF2, -1.48352986419518F, 0.0F, 0.0F);
        this.LegLeftFoot = new ModelRenderer(this, 26, 11);
        this.LegLeftFoot.setRotationPoint(0.0F, 4.5F, 0.0F);
        this.LegLeftFoot.addBox(-0.5F, 0.0F, -3.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(LegLeftFoot, 0.0F, -0.17453292519943295F, 0.08726646259971647F);
        this.theHeadDetail1h = new ModelRenderer(this, 54, 4);
        this.theHeadDetail1h.mirror = true;
        this.theHeadDetail1h.setRotationPoint(-3.0F, 0.0F, 0.0F);
        this.theHeadDetail1h.addBox(-3.0F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(theHeadDetail1h, 0.0F, 0.0F, 0.6981317007977318F);
        this.FF4 = new ModelRenderer(this, 90, 62);
        this.FF4.setRotationPoint(-0.13F, 0.6F, -1.01F);
        this.FF4.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        this.EarL3 = new ModelRenderer(this, 118, 15);
        this.EarL3.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.EarL3.addBox(0.0F, -1.0F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(EarL3, 0.4363323129985824F, 0.0F, 0.0F);
        this.UpperBody = new ModelRenderer(this, 14, 0);
        this.UpperBody.setRotationPoint(0.0F, -5.0F, 0.0F);
        this.UpperBody.addBox(-1.5F, -1.74F, -1.0F, 3, 1, 2, 0.0F);
        this.BodyDetailSideRight = new ModelRenderer(this, 9, 15);
        this.BodyDetailSideRight.setRotationPoint(-2.5F, 0.0F, 0.0F);
        this.BodyDetailSideRight.addBox(0.0F, -2.0F, -1.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(BodyDetailSideRight, 0.0F, 0.0F, 0.5235987755982988F);
        this.headBack113 = new ModelRenderer(this, 79, 40);
        this.headBack113.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.headBack113.addBox(-1.5F, -1.0F, 0.0F, 3, 1, 2, 0.0F);
        this.setRotateAngle(headBack113, 0.5916666164260777F, 0.0F, 0.0F);
        this.headfrontdetaila5_1 = new ModelRenderer(this, 79, 49);
        this.headfrontdetaila5_1.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.headfrontdetaila5_1.addBox(-1.5F, -1.0F, 0.0F, 3, 1, 2, 0.0F);
        this.setRotateAngle(headfrontdetaila5_1, 0.5916666164260777F, 0.0F, 0.0F);
        this.ArmRightLowerDetail1 = new ModelRenderer(this, 40, 29);
        this.ArmRightLowerDetail1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ArmRightLowerDetail1.addBox(-0.51F, 0.0F, -0.23F, 1, 6, 1, 0.0F);
        this.setRotateAngle(ArmRightLowerDetail1, -0.04014257279586957F, 0.0F, 0.0F);
        this.ArmLeft = new ModelRenderer(this, 0, 27);
        this.ArmLeft.mirror = true;
        this.ArmLeft.setRotationPoint(-2.5F, -5.0F, 0.0F);
        this.ArmLeft.addBox(-1.02F, 0.0F, -1.02F, 1, 8, 1, 0.0F);
        this.setRotateAngle(ArmLeft, 0.0F, 0.17453292519943295F, 0.17453292519943295F);
        this.LegDetailRight = new ModelRenderer(this, 19, 20);
        this.LegDetailRight.setRotationPoint(-0.2F, -0.2F, 0.0F);
        this.LegDetailRight.addBox(0.0F, 0.0F, -1.0F, 1, 5, 2, 0.0F);
        this.setRotateAngle(LegDetailRight, 0.0F, 0.0F, 0.15707963267948966F);
        this.ArmLeftLowerDetail1 = new ModelRenderer(this, 40, 29);
        this.ArmLeftLowerDetail1.mirror = true;
        this.ArmLeftLowerDetail1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ArmLeftLowerDetail1.addBox(-0.5F, 0.0F, -0.25F, 1, 6, 1, 0.0F);
        this.setRotateAngle(ArmLeftLowerDetail1, -0.04014257279586957F, 0.0F, 0.0F);
        this.headfrontdetaila5 = new ModelRenderer(this, 79, 46);
        this.headfrontdetaila5.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.headfrontdetaila5.addBox(-1.5F, -1.0F, 0.0F, 3, 1, 2, 0.0F);
        this.setRotateAngle(headfrontdetaila5, 0.5916666164260777F, 0.0F, 0.0F);
        this.EarM1 = new ModelRenderer(this, 10, 50);
        this.EarM1.setRotationPoint(1.0F, 1.0F, 0.8F);
        this.EarM1.addBox(-1.0F, -2.0F, 0.0F, 1, 2, 4, 0.0F);
        this.setRotateAngle(EarM1, -1.1370820076743058F, 0.08970992355250852F, -0.17453292519943295F);
        this.Tail_end = new ModelRenderer(this, 56, 59);
        this.Tail_end.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.Tail_end.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(Tail_end, -0.39025562074593206F, 0.0F, 0.01727875959474386F);
        this.chek = new ModelRenderer(this, 65, 60);
        this.chek.setRotationPoint(0.5F, 0.0F, -1.0F);
        this.chek.addBox(0.0F, 0.01F, 0.0F, 2, 3, 1, 0.0F);
        this.setRotateAngle(chek, 0.0F, -1.186823891356144F, 0.0F);
        this.ArmRightDetail12 = new ModelRenderer(this, 0, 18);
        this.ArmRightDetail12.setRotationPoint(0.0F, 0.9F, 1.0F);
        this.ArmRightDetail12.addBox(0.0F, 0.0F, -1.01F, 1, 7, 1, 0.0F);
        this.setRotateAngle(ArmRightDetail12, -0.14486232791552936F, 0.0F, 0.0F);
        this.LegRightFootDetail1 = new ModelRenderer(this, 34, 11);
        this.LegRightFootDetail1.setRotationPoint(0.8F, 0.0F, 0.0F);
        this.LegRightFootDetail1.addBox(-1.5F, 0.0F, -3.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(LegRightFootDetail1, 0.0F, 0.4363323129985824F, -0.17453292519943295F);
        this.BodyLower = new ModelRenderer(this, 52, 62);
        this.BodyLower.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.BodyLower.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
        this.ArmLeftLowerDetail = new ModelRenderer(this, 35, 21);
        this.ArmLeftLowerDetail.mirror = true;
        this.ArmLeftLowerDetail.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ArmLeftLowerDetail.addBox(-0.52F, 0.0F, -0.75F, 1, 6, 1, 0.0F);
        this.setRotateAngle(ArmLeftLowerDetail, 0.04014257279586957F, 0.0F, 0.0F);
        this.ArmRightDetail11 = new ModelRenderer(this, 12, 28);
        this.ArmRightDetail11.setRotationPoint(1.75F, 1.0F, 1.0F);
        this.ArmRightDetail11.addBox(-0.99F, 0.0F, -0.99F, 1, 7, 1, 0.0F);
        this.setRotateAngle(ArmRightDetail11, -0.14486232791552936F, 0.0F, 0.1117010721276371F);
        this.headBack = new ModelRenderer(this, 54, 24);
        this.headBack.setRotationPoint(1.5F, 0.0F, 1.5F);
        this.headBack.addBox(-1.5F, -1.0F, -0.4F, 3, 1, 3, 0.0F);
        this.setRotateAngle(headBack, 0.9599310885968813F, 0.7853981633974483F, 0.0F);
        this.Tail_mid3 = new ModelRenderer(this, 60, 59);
        this.Tail_mid3.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.Tail_mid3.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(Tail_mid3, 0.942128730226539F, 0.0F, -0.015184364492350668F);
        this.chek1 = new ModelRenderer(this, 65, 56);
        this.chek1.setRotationPoint(-0.5F, 0.0F, -1.0F);
        this.chek1.addBox(-2.0F, 0.01F, 0.0F, 2, 3, 1, 0.0F);
        this.setRotateAngle(chek1, 0.0F, 1.186823891356144F, 0.0F);
        this.ArmLeftDetail12 = new ModelRenderer(this, 0, 18);
        this.ArmLeftDetail12.mirror = true;
        this.ArmLeftDetail12.setRotationPoint(0.0F, 0.9F, 1.0F);
        this.ArmLeftDetail12.addBox(-1.0F, 0.0F, -1.02F, 1, 7, 1, 0.0F);
        this.setRotateAngle(ArmLeftDetail12, -0.14486232791552936F, 0.0F, 0.0F);
        this.EarM3 = new ModelRenderer(this, 36, 36);
        this.EarM3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.EarM3.addBox(0.0F, 0.0F, 0.0F, 1, 2, 5, 0.0F);
        this.EarML3 = new ModelRenderer(this, 116, 53);
        this.EarML3.setRotationPoint(-1.0F, 0.0F, 0.0F);
        this.EarML3.addBox(0.0F, 0.0F, 0.0F, 1, 2, 5, 0.0F);
        this.Tail_mid1 = new ModelRenderer(this, 60, 59);
        this.Tail_mid1.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.Tail_mid1.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(Tail_mid1, 0.8609709200088027F, 0.0F, 0.04834562028024293F);
        this.LegRightFoot = new ModelRenderer(this, 34, 8);
        this.LegRightFoot.setRotationPoint(0.0F, 4.5F, 0.0F);
        this.LegRightFoot.addBox(-0.5F, 0.0F, -3.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(LegRightFoot, -0.0F, 0.17453292519943295F, -0.08726646259971647F);
        this.ArmLeftFingerfront = new ModelRenderer(this, 43, 13);
        this.ArmLeftFingerfront.setRotationPoint(-0.5F, 0.5F, 0.5F);
        this.ArmLeftFingerfront.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(ArmLeftFingerfront, 0.17453292519943295F, -0.5235987755982988F, 0.2617993877991494F);
        this.ArmRightDetail = new ModelRenderer(this, 8, 23);
        this.ArmRightDetail.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ArmRightDetail.addBox(0.0F, 0.0F, -1.02F, 2, 1, 2, 0.0F);
        this.setRotateAngle(ArmRightDetail, 0.0F, 0.0F, 0.5235987755982988F);
        this.headfrontdetaila1 = new ModelRenderer(this, 54, 20);
        this.headfrontdetaila1.setRotationPoint(-1.5F, 0.0F, -1.5F);
        this.headfrontdetaila1.addBox(-1.5F, -1.0F, -0.4F, 3, 1, 3, 0.0F);
        this.setRotateAngle(headfrontdetaila1, 0.9599310885968813F, -2.356194490192345F, 0.0F);
        this.ArmRightFingerback = new ModelRenderer(this, 43, 13);
        this.ArmRightFingerback.setRotationPoint(-0.5F, 0.5F, 0.5F);
        this.ArmRightFingerback.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(ArmRightFingerback, 0.17453292519943295F, -0.5235987755982988F, 0.2617993877991494F);
        this.Tail_mid2 = new ModelRenderer(this, 60, 59);
        this.Tail_mid2.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.Tail_mid2.addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1, 0.0F);
        this.setRotateAngle(Tail_mid2, 0.7670722062515078F, 0.0F, 0.0F);
        this.ArmRightLower = new ModelRenderer(this, 52, 62);
        this.ArmRightLower.setRotationPoint(0.5F, 8.0F, -0.5F);
        this.ArmRightLower.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(ArmRightLower, -0.5235987755982988F, 0.0F, 0.0F);
        this.EarRB3 = new ModelRenderer(this, 22, 36);
        this.EarRB3.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.EarRB3.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 6, 0.0F);
        this.setRotateAngle(EarRB3, -0.1223475805648025F, -0.3490658503988659F, 0.0F);
        this.theHeadDetail7999 = new ModelRenderer(this, 66, 20);
        this.theHeadDetail7999.setRotationPoint(1.5F, 0.0F, -1.5F);
        this.theHeadDetail7999.addBox(0.0F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(theHeadDetail7999, 0.7853981633974483F, 3.141592653589793F, 0.0F);
        this.LegLeftFootDetail2 = new ModelRenderer(this, 37, 11);
        this.LegLeftFootDetail2.setRotationPoint(0.0F, 0.0F, -0.5F);
        this.LegLeftFootDetail2.addBox(-2.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
        this.setRotateAngle(LegLeftFootDetail2, 0.13962634015954636F, -0.40142572795869574F, -0.5462880558742251F);
        this.theHeadDetail2h = new ModelRenderer(this, 54, 8);
        this.theHeadDetail2h.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.theHeadDetail2h.addBox(0.0F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(theHeadDetail2h, 0.6981317007977318F, 0.0F, 0.0F);
        this.EarLB2Detail = new ModelRenderer(this, 120, 61);
        this.EarLB2Detail.setRotationPoint(-1.0F, 2.0F, 4.0F);
        this.EarLB2Detail.addBox(0.0F, -1.0F, 0.0F, 2, 1, 2, 0.0F);
        this.setRotateAngle(EarLB2Detail, 0.389033890269536F, 0.0F, 0.0F);
        this.ArmRightLowerDetail = new ModelRenderer(this, 35, 21);
        this.ArmRightLowerDetail.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ArmRightLowerDetail.addBox(-0.5F, 0.0F, -0.77F, 1, 6, 1, 0.0F);
        this.setRotateAngle(ArmRightLowerDetail, 0.04014257279586957F, 0.0F, 0.0F);
        this.Right_Wing_Overlay = new ModelRenderer(this, 136, 0);
        this.Right_Wing_Overlay.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Right_Wing_Overlay.addBox(-0.02F, 0.0F, 0.0F, 1, 30, 11, 0.0F);
        this.EarM5 = new ModelRenderer(this, 13, 43);
        this.EarM5.setRotationPoint(0.0F, -1.8F, 3.0F);
        this.EarM5.addBox(-0.98F, 0.0F, 0.0F, 1, 2, 5, 0.0F);
        this.setRotateAngle(EarM5, 0.49497537586559187F, 0.0F, 0.0F);
        this.ArmLeftDetail1 = new ModelRenderer(this, 32, 28);
        this.ArmLeftDetail1.mirror = true;
        this.ArmLeftDetail1.setRotationPoint(-1.75F, 1.0F, 0.0F);
        this.ArmLeftDetail1.addBox(0.0F, 0.0F, -0.98F, 1, 7, 1, 0.0F);
        this.setRotateAngle(ArmLeftDetail1, 0.0F, 0.0F, -0.1117010721276371F);
        this.BodyLDetail2 = new ModelRenderer(this, 43, 0);
        this.BodyLDetail2.setRotationPoint(2.5F, 0.0F, 2.0F);
        this.BodyLDetail2.addBox(-1.0F, 0.0F, -1.0F, 1, 6, 1, 0.0F);
        this.setRotateAngle(BodyLDetail2, -0.12217304763960307F, 0.0F, 0.13788101090755206F);
        this.theHeadDetail = new ModelRenderer(this, 78, 4);
        this.theHeadDetail.setRotationPoint(1.5F, 0.0F, 0.0F);
        this.theHeadDetail.addBox(0.0F, -1.0F, -1.5F, 3, 1, 3, 0.0F);
        this.setRotateAngle(theHeadDetail, 0.0F, 0.0F, -0.7853981633974483F);
        this.BodyLowerFront = new ModelRenderer(this, 33, 8);
        this.BodyLowerFront.setRotationPoint(0.0F, 0.0F, -2.0F);
        this.BodyLowerFront.addBox(-1.5F, 0.0F, 0.0F, 3, 6, 2, 0.0F);
        this.setRotateAngle(BodyLowerFront, 0.12217304763960307F, 0.0F, 0.0F);
        this.theHeadDetail2e768 = new ModelRenderer(this, 66, 24);
        this.theHeadDetail2e768.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.theHeadDetail2e768.addBox(0.0F, -1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(theHeadDetail2e768, 0.9599310885968813F, 0.0F, 0.0F);
        this.ArmLeftDetail = new ModelRenderer(this, 8, 23);
        this.ArmLeftDetail.mirror = true;
        this.ArmLeftDetail.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ArmLeftDetail.addBox(-2.0F, 0.0F, -1.03F, 2, 1, 2, 0.0F);
        this.setRotateAngle(ArmLeftDetail, 0.0F, 0.0F, -0.5235987755982988F);
        this.LegDetaiLeft1 = new ModelRenderer(this, 22, 32);
        this.LegDetaiLeft1.setRotationPoint(1.0F, 2.0F, 0.0F);
        this.LegDetaiLeft1.addBox(-1.0F, -2.0F, -1.01F, 1, 2, 2, 0.0F);
        this.setRotateAngle(LegDetaiLeft1, 0.0F, 0.0F, -0.3804817769347638F);
        this.ArmLeftHand = new ModelRenderer(this, 0, 2);
        this.ArmLeftHand.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.ArmLeftHand.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
        this.setRotateAngle(ArmLeftHand, -0.296656372197416F, -1.5707963267948966F, 0.0F);
        this.LegRight = new ModelRenderer(this, 16, 27);
        this.LegRight.setRotationPoint(-1.5F, 5.9F, 0.0F);
        this.LegRight.addBox(-1.02F, 2.0F, -1.02F, 1, 3, 2, 0.0F);
        this.setRotateAngle(LegRight, -0.2617993877991494F, 0.2617993877991494F, 0.08726646259971647F);
        this.EarLB2 = new ModelRenderer(this, 116, 40);
        this.EarLB2.setRotationPoint(0.0F, 0.0F, 3.6F);
        this.EarLB2.addBox(-1.0F, 0.0F, 0.0F, 2, 2, 4, 0.0F);
        this.setRotateAngle(EarLB2, -0.4363323129985824F, -0.3490658503988659F, 0.0F);
        this.neckDetail4 = new ModelRenderer(this, 18, 6);
        this.neckDetail4.setRotationPoint(-2.0F, 0.0F, 1.5F);
        this.neckDetail4.addBox(-0.9F, -2.0F, 0.0F, 2, 2, 1, 0.0F);
        this.setRotateAngle(neckDetail4, -0.3839724354387525F, 2.356194490192345F, 0.0F);
        this.LegRightFootDetail2 = new ModelRenderer(this, 37, 11);
        this.LegRightFootDetail2.setRotationPoint(0.0F, 0.0F, -0.5F);
        this.LegRightFootDetail2.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
        this.setRotateAngle(LegRightFootDetail2, -0.13962634015954636F, 0.40142572795869574F, 0.5462880558742251F);
        this.LegLeftFootDetail1 = new ModelRenderer(this, 34, 10);
        this.LegLeftFootDetail1.setRotationPoint(0.8F, 0.0F, 0.0F);
        this.LegLeftFootDetail1.addBox(-1.5F, 0.0F, -3.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(LegLeftFootDetail1, 0.0F, 0.4363323129985824F, -0.17453292519943295F);
        this.EarLeft = new ModelRenderer(this, 118, 0);
        this.EarLeft.setRotationPoint(1.0F, -6.0F, 1.0F);
        this.EarLeft.addBox(0.0F, -1.0F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(EarLeft, -0.2617993877991494F, 0.4363323129985824F, 0.0F);
        this.BodyLower.addChild(this.BodyLowerBack);
        this.UpperBody.addChild(this.BodyDetailFront);
        this.ArmRight.addChild(this.Right_Wing);
        this.BodyLower.addChild(this.BodyLDetail);
        this.EarRB2.addChild(this.EarRB2Detail);
        this.UpperBody.addChild(this.neckDetail);
        this.UpperBody.addChild(this.neckDetail3);
        this.Body.addChild(this.ArmRight);
        this.theHead.addChild(this.headtop);
        this.EarL1.addChild(this.EarL2);
        this.ArmLeftHand.addChild(this.ArmLeftFingerback);
        this.theHeadDetaile.addChild(this.theHeadDetailh);
        this.headfrontdetaila1_1.addChild(this.headfrontdetaila4_1);
        this.ArmRightHand.addChild(this.ArmRightFingerfront);
        this.EarLB2.addChild(this.EarLB3);
        this.EarR2.addChild(this.EarR3);
        this.headBack.addChild(this.headBack2);
        this.Body.addChild(this.Neck);
        this.theHeadDetail1.addChild(this.theHeadDetail1e);
        this.LegRightLower.addChild(this.LegRightLowerDetail1);
        this.EarRB2Detail.addChild(this.EarRB2Detaildetail);
        this.EarLB2Detail.addChild(this.EarLB2Detaildetail);
        this.LegLeftLower.addChild(this.LegLeftLowerDetail);
        this.UpperBody.addChild(this.BodyDetailSideLeft);
        this.EarR1.addChild(this.EarM2);
        this.EarLeft.addChild(this.EarL1);
        this.theHead.addChild(this.headBack1);
        this.BodyLower.addChild(this.Tail_start);
        this.FF1.addChild(this.FF3);
        this.BodyLower.addChild(this.BodyLDetail4);
        this.BodyLower.addChild(this.BodyLowerSideRight);
        this.theHead.addChild(this.headfrontdetaila1_1);
        this.ArmRight.addChild(this.ArmRightDetail1);
        this.LegLeft.addChild(this.LegLeftLower);
        this.UpperBody.addChild(this.BodyDetailBack);
        this.LegRight.addChild(this.LegRightLower);
        this.LegRightLower.addChild(this.LegRightLowerDetail);
        this.EarLeft.addChild(this.EarLB1);
        this.EarRB1.addChild(this.EarRB2);
        this.theHead.addChild(this.EarRight);
        this.theHead.addChild(this.theHeadDetail1);
        this.headfrontdetaila1.addChild(this.headfrontdetaila4);
        this.ArmLeftHand.addChild(this.ArmLeftFingerMid);
        this.ArmLeft.addChild(this.ArmLeftLower);
        this.theHeadDetail2e768.addChild(this.theHeadDetail2h789);
        this.EarRight.addChild(this.EarRB1);
        this.theHeadDetail2.addChild(this.theHeadDetail2e);
        this.EarL1.addChild(this.EarL2_1);
        this.headBack1.addChild(this.headBack11);
        this.theHeadDetail.addChild(this.theHeadDetaile);
        this.EarL2_1.addChild(this.EarL5);
        this.ArmRightHand.addChild(this.ArmRightFingerMid);
        this.Left_Wing.addChild(this.Left_Wing_Overlay);
        this.headBack11.addChild(this.headBack111);
        this.LegRightFoot.addChild(this.LegRightFootDetail);
        this.BodyLower.addChild(this.BodyLDetail1);
        this.ArmLeftHand.addChild(this.ArmLeftFingersBig);
        this.ArmRightLower.addChild(this.ArmRightHand);
        this.Neck.addChild(this.theHead);
        this.BodyLower.addChild(this.LegLeft);
        this.LegRight.addChild(this.LegDetailRight1);
        this.LegLeft.addChild(this.LegDetailLeft);
        this.Tail_mid3.addChild(this.Tail_mid4);
        this.LegLeftLower.addChild(this.LegLeftLowerDetail1);
        this.ArmLeft.addChild(this.ArmLeftDetail11);
        this.EarR1.addChild(this.EarR2);
        this.EarLB1.addChild(this.EarlM1);
        this.theHead.addChild(this.theHeadDetail2);
        this.UpperBody.addChild(this.neckDetail2);
        this.theHead.addChild(this.FF1);
        this.BodyLower.addChild(this.BodyLowerSideLeft);
        this.LegLeftFoot.addChild(this.LegLeftFootDetail);
        this.ArmLeft.addChild(this.Left_Wing);
        this.EarRight.addChild(this.EarR1);
        this.ArmRightHand.addChild(this.ArmRightFingersBig);
        this.FF1.addChild(this.FF2);
        this.LegLeftLower.addChild(this.LegLeftFoot);
        this.theHeadDetail1e.addChild(this.theHeadDetail1h);
        this.FF1.addChild(this.FF4);
        this.EarL2.addChild(this.EarL3);
        this.Body.addChild(this.UpperBody);
        this.UpperBody.addChild(this.BodyDetailSideRight);
        this.headBack2.addChild(this.headBack113);
        this.headfrontdetaila4_1.addChild(this.headfrontdetaila5_1);
        this.ArmRightLower.addChild(this.ArmRightLowerDetail1);
        this.Body.addChild(this.ArmLeft);
        this.LegRight.addChild(this.LegDetailRight);
        this.ArmLeftLower.addChild(this.ArmLeftLowerDetail1);
        this.headfrontdetaila4.addChild(this.headfrontdetaila5);
        this.EarRB1.addChild(this.EarM1);
        this.Tail_mid4.addChild(this.Tail_end);
        this.FF1.addChild(this.chek);
        this.ArmRight.addChild(this.ArmRightDetail12);
        this.LegRightFoot.addChild(this.LegRightFootDetail1);
        this.Body.addChild(this.BodyLower);
        this.ArmLeftLower.addChild(this.ArmLeftLowerDetail);
        this.ArmRight.addChild(this.ArmRightDetail11);
        this.theHead.addChild(this.headBack);
        this.Tail_mid2.addChild(this.Tail_mid3);
        this.FF1.addChild(this.chek1);
        this.ArmLeft.addChild(this.ArmLeftDetail12);
        this.EarRB3.addChild(this.EarM3);
        this.EarLB3.addChild(this.EarML3);
        this.Tail_start.addChild(this.Tail_mid1);
        this.LegRightLower.addChild(this.LegRightFoot);
        this.ArmLeftHand.addChild(this.ArmLeftFingerfront);
        this.ArmRight.addChild(this.ArmRightDetail);
        this.theHead.addChild(this.headfrontdetaila1);
        this.ArmRightHand.addChild(this.ArmRightFingerback);
        this.Tail_mid1.addChild(this.Tail_mid2);
        this.ArmRight.addChild(this.ArmRightLower);
        this.EarRB2.addChild(this.EarRB3);
        this.theHead.addChild(this.theHeadDetail7999);
        this.LegLeftFoot.addChild(this.LegLeftFootDetail2);
        this.theHeadDetail2e.addChild(this.theHeadDetail2h);
        this.EarLB2.addChild(this.EarLB2Detail);
        this.ArmRightLower.addChild(this.ArmRightLowerDetail);
        this.Right_Wing.addChild(this.Right_Wing_Overlay);
        this.EarM2.addChild(this.EarM5);
        this.ArmLeft.addChild(this.ArmLeftDetail1);
        this.BodyLower.addChild(this.BodyLDetail2);
        this.theHead.addChild(this.theHeadDetail);
        this.BodyLower.addChild(this.BodyLowerFront);
        this.theHeadDetail7999.addChild(this.theHeadDetail2e768);
        this.ArmLeft.addChild(this.ArmLeftDetail);
        this.LegLeft.addChild(this.LegDetaiLeft1);
        this.ArmLeftLower.addChild(this.ArmLeftHand);
        this.BodyLower.addChild(this.LegRight);
        this.EarLB1.addChild(this.EarLB2);
        this.UpperBody.addChild(this.neckDetail4);
        this.LegRightFoot.addChild(this.LegRightFootDetail2);
        this.LegLeftFoot.addChild(this.LegLeftFootDetail1);
        this.theHead.addChild(this.EarLeft);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    	super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

    	if (this.isChild)
        {
    	 	GL11.glPushMatrix();
            GL11.glTranslatef(0F, 1.5F-1.5F*0.35F, 0F);
        	GL11.glScalef(0.35F, 0.35F, 0.35F);
    		this.Body.render(scale);
    		GL11.glPopMatrix();
        }
        else
        { 
           	GL11.glPushMatrix();
            GL11.glTranslatef(0F, 1.5F-1.5F*0.5F, 0F);
        	GL11.glScalef(0.5F, 0.5F, 0.5F);
        	this.Body.render(scale);
    		GL11.glPopMatrix();
        }
    }

    public void setRotateAngle(ModelRenderer ModelRenderer, float x, float y, float z) {
        ModelRenderer.rotateAngleX = x;
        ModelRenderer.rotateAngleY = y;
        ModelRenderer.rotateAngleZ = z;
    }
	
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
    {

        /*
     	this.Left_Wing.setScale(0, 0, 0);
      	this.Right_Wing.setScale(0, 0, 0);
        */
        if (entitylivingbaseIn instanceof EntityFlyingLemur){
        	
        	EntityFlyingLemur lemur = (EntityFlyingLemur)entitylivingbaseIn;

            boolean gliding = GliderHelper.getIsGliderDeployed((EntityPlayer) lemur.getOwner());

            if (lemur.isPartying())
            {
                this.state = State.PARTY;
                //TODO
                this.Body.rotateAngleY = -1.5F;
                this.Body.rotateAngleX = 0.3F;
                this.BodyLower.rotateAngleX = -0.3f;
                this.Neck.rotateAngleX = -0.3F;
                
                this.ArmLeft.rotateAngleZ = -0.3f;
                this.ArmRight.rotateAngleZ = 0.3f;
                
                this.LegRight.rotateAngleZ = -0.1f;
                this.LegLeft.rotateAngleZ = 0.1f;
                this.LegRight.rotateAngleY = -0.2f;
                this.LegLeft.rotateAngleY = 0.2f;
                this.LegRight.rotateAngleX = -0.35f;
                this.LegLeft.rotateAngleX = -0.35f;
                this.LegRightLower.rotateAngleX = 1.25f;
                this.LegLeftLower.rotateAngleX = 1.25f;
                
                this.LegLeftFoot.rotateAngleY = -0.4f;
                this.LegRightFoot.rotateAngleY = 0.4f;
                
                this.LegLeftFoot.rotateAngleZ = -0.1f;
                this.LegRightFoot.rotateAngleZ = 0.1f;
                this.LegLeftFoot.rotateAngleX = -0.8f;
                this.LegRightFoot.rotateAngleX = -0.8f;
                
                
                this.Tail_start.rotateAngleX = 0.2F;
	            this.Tail_mid1.rotateAngleX = 0F;
	            this.Tail_mid2.rotateAngleX =  1.25F;
	            this.Tail_mid3.rotateAngleX = 0F;
	            this.Tail_mid4.rotateAngleX = 0F;
	            this.Tail_end.rotateAngleX = 0F;
	            /*
	            this.Tail_mid3.rotateAngleZ = -1F;
	            this.Tail_mid4.rotateAngleZ = -0.75F;
	            this.Tail_end.rotateAngleZ = -0.5F;
                 */
        		this.ArmLeftHand.rotateAngleY = -3.5F;
        		this.ArmRightHand.rotateAngleY = 3.5F;
	            
            }
            else if (lemur.isSitting() && !lemur.isPartying()) {
        		this.state = State.SITTING;
        		this.Body.offsetY = 0.5F;
        		this.BodyLower.rotateAngleX = 0.35F;
        		
        		this.LegRight.rotateAngleX = -2.6f;
        		this.LegRightLower.rotateAngleX = 2.0f;
        		this.LegRight.rotateAngleZ = -0.5F;
        		this.LegRight.rotateAngleY = 0.5F;
        		this.LegLeft.rotateAngleX = -2.6f;
        		this.LegLeftLower.rotateAngleX = 2.0f;
        		this.LegLeft.rotateAngleZ = 0.5F;
        		this.LegLeft.rotateAngleY = -0.5F;
        		
        		this.ArmRight.rotateAngleY = 0.7F;
        		this.ArmLeft.rotateAngleY = -0.7F;
        		
        		this.ArmLeftLower.rotateAngleX = -0.5F;
        		this.ArmRightLower.rotateAngleX = -0.5F;
        		
        		this.ArmRightLower.rotateAngleZ = 0.35F;
        		this.ArmLeftLower.rotateAngleZ = -0.35F;
        		
         		this.ArmRightLower.rotateAngleY = 0.6F;
        		this.ArmLeftLower.rotateAngleY = -0.6F;
        		
        		this.ArmLeftHand.rotateAngleX = 1.6F;
        		this.ArmRightHand.rotateAngleX = 1.6F;

        		this.ArmLeftHand.rotateAngleY = -1.5F;
        		this.ArmRightHand.rotateAngleY = 1.5F;
        	} 
        	else if (lemur.getLeftShoulder() && !lemur.getOwner().isElytraFlying() && !gliding) {
           		this.state = State.ONLEFTSHOULDER;
        		this.Body.rotateAngleX = 0.35F;
        		this.Body.rotateAngleY = 0.1F;
        		this.Body.rotateAngleZ = 0.15F;
        	
        		this.BodyLower.rotateAngleX = -0.35F;
        		
        		this.LegLeft.rotateAngleX = -2.60F;
        		this.LegRight.rotateAngleX = -2.60F;
        		this.LegLeftLower.rotateAngleX = 2.8f;
        		this.LegRightLower.rotateAngleX = 2.8f;
        		
        		this.LegLeftLower.rotateAngleZ = -0.45f;
        		this.LegRightLower.rotateAngleZ = 0.45f;
        		this.LegLeft.rotateAngleZ = 0.2F;
        		this.LegLeft.rotateAngleY = -0.2F;
        		this.LegRight.rotateAngleZ = -0.2F;
        		this.LegRight.rotateAngleY = 0.2F;
        		
        		
        		this.ArmRight.rotateAngleX = -0.6F;
        		this.ArmLeft.rotateAngleX = -0.6F;
         		this.ArmRight.rotateAngleZ = 0.2F;
        		this.ArmLeft.rotateAngleZ = -0.2F;
        		
        		this.ArmLeftLower.rotateAngleX = -0.35F;
        		this.ArmRightLower.rotateAngleX = -0.35F;
        		
        		this.ArmRightLower.rotateAngleZ = 0.35F;
        		this.ArmLeftLower.rotateAngleZ = -0.35F;
        		
        		this.ArmLeftHand.rotateAngleX = -0.7F;
        		this.ArmRightHand.rotateAngleX = -0.7F;
        		
        		
        		this.ArmLeftHand.rotateAngleZ = 1F;
        		this.ArmRightHand.rotateAngleZ = -1F;
        		
                this.ArmRightHand.rotateAngleY = 3.5F; 
                this.ArmLeftHand.rotateAngleY = -3.5F;  		
        	
        		this.Body.offsetZ =  0.2F;
        		
           		if(lemur.getOwner().isSneaking()) 
        		{
        			this.Body.offsetY = 2.4F;
        		}
        		else 
        		{
        			this.Body.offsetY = 2.0F;
        		}
        
        		if(lemur.isChild()) {
        		this.Body.offsetX =  1.1F;
        		}
        		else {
        		this.Body.offsetX =  0.8F;
        		}
        		
	        }
        	else if (lemur.getRightShoulder() && !lemur.getOwner().isElytraFlying() && !gliding) {
        		this.state = State.ONRIGHTSHOULDER;
        		
        		this.Body.rotateAngleX = 0.35F;
        		this.Body.rotateAngleY = -0.1F;
        		this.Body.rotateAngleZ = -0.15F;
        		
        		this.BodyLower.rotateAngleX = -0.35F;
        		
        		this.LegLeft.rotateAngleX = -2.60F;
        		this.LegRight.rotateAngleX = -2.60F;
        		this.LegLeftLower.rotateAngleX = 2.8f;
        		this.LegRightLower.rotateAngleX = 2.8f;
        		
        		this.LegLeftLower.rotateAngleZ = -0.45f;
        		this.LegRightLower.rotateAngleZ = 0.45f;
        		this.LegLeft.rotateAngleZ = 0.2F;
        		this.LegLeft.rotateAngleY = -0.2F;
        		this.LegRight.rotateAngleZ = -0.2F;
        		this.LegRight.rotateAngleY = 0.2F;
        		
        		
        		this.ArmRight.rotateAngleX = -0.6F;
        		this.ArmLeft.rotateAngleX = -0.6F;
         		this.ArmRight.rotateAngleZ = 0.2F;
        		this.ArmLeft.rotateAngleZ = -0.2F;
        		
        		this.ArmLeftLower.rotateAngleX = -0.35F;
        		this.ArmRightLower.rotateAngleX = -0.35F;
        		
        		this.ArmRightLower.rotateAngleZ = 0.35F;
        		this.ArmLeftLower.rotateAngleZ = -0.35F;
        		
        		this.ArmLeftHand.rotateAngleX = -0.7F;
        		this.ArmRightHand.rotateAngleX = -0.7F;
        		
        		
        		this.ArmLeftHand.rotateAngleZ = 1F;
        		this.ArmRightHand.rotateAngleZ = -1F;
        		
                this.ArmRightHand.rotateAngleY = 3.5F; 
                this.ArmLeftHand.rotateAngleY = -3.5F;  		
        	
        		this.Body.offsetZ =  0.2F;
        		
        		if(lemur.getOwner().isSneaking()) 
        		{
        			this.Body.offsetY = 2.4F;
        		}
        		else 
        		{
        			this.Body.offsetY = 2.0F;
        		}
        		
        		if(lemur.isChild()) {
        		this.Body.offsetX =  -1.1F;
        		}
        		else {
        		this.Body.offsetX =  -0.8F;
        		}
        		
	        } 
        	else if (lemur.getLeftShoulder() && lemur.getOwner().isElytraFlying() || gliding) {
        		this.state = State.ELYTRALEFTSHOULDER;
        		
	            this.Body.rotateAngleX = 1.5F;
	            this.ArmRight.rotateAngleX = -1.5F;
	            this.ArmRight.rotateAngleY = -1.6F;
	            this.ArmRight.rotateAngleZ = -0.05F;
	            this.ArmLeft.rotateAngleX = -1.5F;
	            this.ArmLeft.rotateAngleY = 1.6F;
	            this.ArmLeft.rotateAngleZ = 0.05F;
	        
	            this.Tail_start.rotateAngleX = 0F;
	            this.Tail_mid1.rotateAngleX = 0F;
	            this.Tail_mid2.rotateAngleX = 0F;
	            this.Tail_mid3.rotateAngleX = 0F;
	            this.Tail_mid4.rotateAngleX = 0F;
	            this.Tail_end.rotateAngleX = 0F;
	            //this.Left_Wing.setScale(1, 1, 1);
	            //this.Right_Wing.setScale(1, 1, 1);
        		
	            this.LegRightFoot.rotateAngleX = 1.5F;
	            this.LegLeftFoot.rotateAngleX = 1.5F;
	            
	            
	            this.EarRight.rotateAngleX = -0.45F;
	            this.EarLeft.rotateAngleX = -0.45F;
	            
	            this.Body.offsetX =  1.8F;
	            this.Body.offsetY =  1.5F;
	        } 
        	else if (lemur.getRightShoulder() && lemur.getOwner().isElytraFlying() || gliding) {
        		this.state = State.ELYTRARIGHTSHOULDER;
        		
	            this.Body.rotateAngleX = 1.5F;
	            this.ArmRight.rotateAngleX = -1.5F;
	            this.ArmRight.rotateAngleY = -1.6F;
	            this.ArmRight.rotateAngleZ = -0.05F;
	            this.ArmLeft.rotateAngleX = -1.5F;
	            this.ArmLeft.rotateAngleY = 1.6F;
	            this.ArmLeft.rotateAngleZ = 0.05F;
	        
	            this.Tail_start.rotateAngleX = 0F;
	            this.Tail_mid1.rotateAngleX = 0F;
	            this.Tail_mid2.rotateAngleX = 0F;
	            this.Tail_mid3.rotateAngleX = 0F;
	            this.Tail_mid4.rotateAngleX = 0F;
	            this.Tail_end.rotateAngleX = 0F;
	            //this.Left_Wing.setScale(1, 1, 1);
	            //this.Right_Wing.setScale(1, 1, 1);
        		
	            this.LegRightFoot.rotateAngleX = 1.5F;
	            this.LegLeftFoot.rotateAngleX = 1.5F;
	            
	            this.EarRight.rotateAngleX = -0.45F;
	            this.EarLeft.rotateAngleX = -0.45F;
	            
	            this.Body.offsetX =  -1.8F;
	            this.Body.offsetY =  1.5F;
	        } 
        	else if (lemur.isFlying() && !lemur.isLemurRiding())
        	{
        		this.state = State.FLYING;
        		
	            this.Body.rotateAngleX = 1.5F;
	            this.ArmRight.rotateAngleX = -1.5F;
	            this.ArmRight.rotateAngleY = -1.6F;
	            this.ArmRight.rotateAngleZ = -0.05F;
	            this.ArmLeft.rotateAngleX = -1.5F;
	            this.ArmLeft.rotateAngleY = 1.6F;
	            this.ArmLeft.rotateAngleZ = 0.05F;
	        
	            this.Tail_start.rotateAngleX = 0F;
	            this.Tail_mid1.rotateAngleX = 0F;
	            this.Tail_mid2.rotateAngleX = 0F;
	            this.Tail_mid3.rotateAngleX = 0F;
	            this.Tail_mid4.rotateAngleX = 0F;
	            this.Tail_end.rotateAngleX = 0F;
	            //this.Left_Wing.setScale(1, 1, 1);
	            //this.Right_Wing.setScale(1, 1, 1);
        		
	            this.LegRightFoot.rotateAngleX = 1.5F;
	            this.LegLeftFoot.rotateAngleX = 1.5F;
	            
	            this.EarRight.rotateAngleX = -0.45F;
	            this.EarLeft.rotateAngleX = -0.45F;
	        } 
        	else if (lemur.speed > 0.25f && !lemur.isLemurRiding())
        	{
        		this.state = State.SPRINTING;
        		this.Body.rotateAngleX = 1.5F;
        		this.Body.offsetY = 0.25F;
        		this.BodyLower.rotateAngleX = -0.35F;
        		
                this.ArmRight.rotateAngleX = -1.75F;
                this.ArmLeft.rotateAngleX = -1.75F;
                this.ArmRight.rotateAngleY = 0.1F;
                this.ArmLeft.rotateAngleY = -0.1F;
                this.ArmLeftLower.rotateAngleY = 0.4F; 
                this.ArmRightLower.rotateAngleY = -0.4F;
                this.ArmRightHand.rotateAngleY = 2.6F; 
                this.ArmLeftHand.rotateAngleY = -2.6F;

                this.LegLeft.rotateAngleX = -1.5F;
                this.LegRight.rotateAngleX = -1.5F;
                this.LegLeft.rotateAngleY = -0.5F;
                this.LegRight.rotateAngleY = 0.5F;
     
                
                this.Tail_start.rotateAngleX = 0F;
	            this.Tail_mid1.rotateAngleX = 0F;
	            this.Tail_mid2.rotateAngleX = 0F;
	            this.Tail_mid3.rotateAngleX = 0.1F;
	            this.Tail_mid4.rotateAngleX = 0.1F;
	            this.Tail_end.rotateAngleX = 0.1F;
        
	        }
        	else if (lemur.speed > 0.1F && lemur.speed < 0.25F && !lemur.isLemurRiding())
        	{
        		this.state = State.WALKING;
	        }
            else {
        		this.state = State.STANDING;
        	}
    }
    //TODO
        if (entitylivingbaseIn instanceof EntityAscendedFlyingLemur){

        	EntityAscendedFlyingLemur lemur = (EntityAscendedFlyingLemur)entitylivingbaseIn;

            if (lemur.isPartying())
            {
                this.state = State.PARTY;
                //TODO
                this.Body.rotateAngleY = -1.5F;
                this.Body.rotateAngleX = 0.3F;
                this.BodyLower.rotateAngleX = -0.3f;
                this.Neck.rotateAngleX = -0.3F;
                
                this.ArmLeft.rotateAngleZ = -0.3f;
                this.ArmRight.rotateAngleZ = 0.3f;
                
                this.LegRight.rotateAngleZ = -0.1f;
                this.LegLeft.rotateAngleZ = 0.1f;
                this.LegRight.rotateAngleY = -0.2f;
                this.LegLeft.rotateAngleY = 0.2f;
                this.LegRight.rotateAngleX = -0.35f;
                this.LegLeft.rotateAngleX = -0.35f;
                this.LegRightLower.rotateAngleX = 1.25f;
                this.LegLeftLower.rotateAngleX = 1.25f;
                
                this.LegLeftFoot.rotateAngleY = -0.4f;
                this.LegRightFoot.rotateAngleY = 0.4f;
                
                this.LegLeftFoot.rotateAngleZ = -0.1f;
                this.LegRightFoot.rotateAngleZ = 0.1f;
                this.LegLeftFoot.rotateAngleX = -0.8f;
                this.LegRightFoot.rotateAngleX = -0.8f;
                
                
                this.Tail_start.rotateAngleX = 0.2F;
	            this.Tail_mid1.rotateAngleX = 0F;
	            this.Tail_mid2.rotateAngleX =  1.25F;
	            this.Tail_mid3.rotateAngleX = 0F;
	            this.Tail_mid4.rotateAngleX = 0F;
	            this.Tail_end.rotateAngleX = 0F;
	            /*
	            this.Tail_mid3.rotateAngleZ = -1F;
	            this.Tail_mid4.rotateAngleZ = -0.75F;
	            this.Tail_end.rotateAngleZ = -0.5F;
                 */
        		this.ArmLeftHand.rotateAngleY = -3.5F;
        		this.ArmRightHand.rotateAngleY = 3.5F;
	            
            }
            else if (lemur.isSitting() && !lemur.isPartying()) {
        		this.state = State.SITTING;
        		this.Body.offsetY = 0.5F;
        		this.BodyLower.rotateAngleX = 0.35F;
        		
        		this.LegRight.rotateAngleX = -2.6f;
        		this.LegRightLower.rotateAngleX = 2.0f;
        		this.LegRight.rotateAngleZ = -0.5F;
        		this.LegRight.rotateAngleY = 0.5F;
        		this.LegLeft.rotateAngleX = -2.6f;
        		this.LegLeftLower.rotateAngleX = 2.0f;
        		this.LegLeft.rotateAngleZ = 0.5F;
        		this.LegLeft.rotateAngleY = -0.5F;
        		
        		this.ArmRight.rotateAngleY = 0.7F;
        		this.ArmLeft.rotateAngleY = -0.7F;
        		
        		this.ArmLeftLower.rotateAngleX = -0.5F;
        		this.ArmRightLower.rotateAngleX = -0.5F;
        		
        		this.ArmRightLower.rotateAngleZ = 0.35F;
        		this.ArmLeftLower.rotateAngleZ = -0.35F;
        		
         		this.ArmRightLower.rotateAngleY = 0.6F;
        		this.ArmLeftLower.rotateAngleY = -0.6F;
        		
        		this.ArmLeftHand.rotateAngleX = 1.6F;
        		this.ArmRightHand.rotateAngleX = 1.6F;

        		this.ArmLeftHand.rotateAngleY = -1.5F;
        		this.ArmRightHand.rotateAngleY = 1.5F;
        	} 
        	else if (lemur.getLeftShoulder() && !lemur.getOwner().isElytraFlying()) {
           		this.state = State.ONLEFTSHOULDER;
        		this.Body.rotateAngleX = 0.35F;
        		this.Body.rotateAngleY = 0.1F;
        		this.Body.rotateAngleZ = 0.15F;
        	
        		this.BodyLower.rotateAngleX = -0.35F;
        		
        		this.LegLeft.rotateAngleX = -2.60F;
        		this.LegRight.rotateAngleX = -2.60F;
        		this.LegLeftLower.rotateAngleX = 2.8f;
        		this.LegRightLower.rotateAngleX = 2.8f;
        		
        		this.LegLeftLower.rotateAngleZ = -0.45f;
        		this.LegRightLower.rotateAngleZ = 0.45f;
        		this.LegLeft.rotateAngleZ = 0.2F;
        		this.LegLeft.rotateAngleY = -0.2F;
        		this.LegRight.rotateAngleZ = -0.2F;
        		this.LegRight.rotateAngleY = 0.2F;
        		
        		
        		this.ArmRight.rotateAngleX = -0.6F;
        		this.ArmLeft.rotateAngleX = -0.6F;
         		this.ArmRight.rotateAngleZ = 0.2F;
        		this.ArmLeft.rotateAngleZ = -0.2F;
        		
        		this.ArmLeftLower.rotateAngleX = -0.35F;
        		this.ArmRightLower.rotateAngleX = -0.35F;
        		
        		this.ArmRightLower.rotateAngleZ = 0.35F;
        		this.ArmLeftLower.rotateAngleZ = -0.35F;
        		
        		this.ArmLeftHand.rotateAngleX = -0.7F;
        		this.ArmRightHand.rotateAngleX = -0.7F;
        		
        		
        		this.ArmLeftHand.rotateAngleZ = 1F;
        		this.ArmRightHand.rotateAngleZ = -1F;
        		
                this.ArmRightHand.rotateAngleY = 3.5F; 
                this.ArmLeftHand.rotateAngleY = -3.5F;  		
        	
        		this.Body.offsetZ =  0.2F;
        		
         		if(lemur.getOwner().isSneaking()) 
        		{
        			this.Body.offsetY = 2.4F;
        		}
        		else 
        		{
        			this.Body.offsetY = 2.0F;
        		}
        
        		if(lemur.isChild()) {
        		this.Body.offsetX =  1.1F;
        		}
        		else {
        		this.Body.offsetX =  0.8F;
        		}
        		
	        }
        	else if (lemur.getRightShoulder() && !lemur.getOwner().isElytraFlying()) {
        		this.state = State.ONRIGHTSHOULDER;
        		
        		this.Body.rotateAngleX = 0.35F;
        		this.Body.rotateAngleY = -0.1F;
        		this.Body.rotateAngleZ = -0.15F;
        		
        		this.BodyLower.rotateAngleX = -0.35F;
        		
        		this.LegLeft.rotateAngleX = -2.60F;
        		this.LegRight.rotateAngleX = -2.60F;
        		this.LegLeftLower.rotateAngleX = 2.8f;
        		this.LegRightLower.rotateAngleX = 2.8f;
        		
        		this.LegLeftLower.rotateAngleZ = -0.45f;
        		this.LegRightLower.rotateAngleZ = 0.45f;
        		this.LegLeft.rotateAngleZ = 0.2F;
        		this.LegLeft.rotateAngleY = -0.2F;
        		this.LegRight.rotateAngleZ = -0.2F;
        		this.LegRight.rotateAngleY = 0.2F;
        		
        		
        		this.ArmRight.rotateAngleX = -0.6F;
        		this.ArmLeft.rotateAngleX = -0.6F;
         		this.ArmRight.rotateAngleZ = 0.2F;
        		this.ArmLeft.rotateAngleZ = -0.2F;
        		
        		this.ArmLeftLower.rotateAngleX = -0.35F;
        		this.ArmRightLower.rotateAngleX = -0.35F;
        		
        		this.ArmRightLower.rotateAngleZ = 0.35F;
        		this.ArmLeftLower.rotateAngleZ = -0.35F;
        		
        		this.ArmLeftHand.rotateAngleX = -0.7F;
        		this.ArmRightHand.rotateAngleX = -0.7F;
        		
        		
        		this.ArmLeftHand.rotateAngleZ = 1F;
        		this.ArmRightHand.rotateAngleZ = -1F;
        		
                this.ArmRightHand.rotateAngleY = 3.5F; 
                this.ArmLeftHand.rotateAngleY = -3.5F;  		
        	
        		this.Body.offsetZ =  0.2F;
        		
         		if(lemur.getOwner().isSneaking()) 
        		{
        			this.Body.offsetY = 2.4F;
        		}
        		else 
        		{
        			this.Body.offsetY = 2.0F;
        		}
        		
        		if(lemur.isChild()) {
        		this.Body.offsetX =  -1.1F;
        		}
        		else {
        		this.Body.offsetX =  -0.8F;
        		}
        		
	        } 
        	else if (lemur.getLeftShoulder() && lemur.getOwner().isElytraFlying()) {
        		this.state = State.ELYTRALEFTSHOULDER;
        		
	            this.Body.rotateAngleX = 1.5F;
	            this.ArmRight.rotateAngleX = -1.5F;
	            this.ArmRight.rotateAngleY = -1.6F;
	            this.ArmRight.rotateAngleZ = -0.05F;
	            this.ArmLeft.rotateAngleX = -1.5F;
	            this.ArmLeft.rotateAngleY = 1.6F;
	            this.ArmLeft.rotateAngleZ = 0.05F;
	        
	            this.Tail_start.rotateAngleX = 0F;
	            this.Tail_mid1.rotateAngleX = 0F;
	            this.Tail_mid2.rotateAngleX = 0F;
	            this.Tail_mid3.rotateAngleX = 0F;
	            this.Tail_mid4.rotateAngleX = 0F;
	            this.Tail_end.rotateAngleX = 0F;
	            //this.Left_Wing.setScale(1, 1, 1);
	            //this.Right_Wing.setScale(1, 1, 1);
        		
	            this.LegRightFoot.rotateAngleX = 1.5F;
	            this.LegLeftFoot.rotateAngleX = 1.5F;
	            
	            
	            this.EarRight.rotateAngleX = -0.45F;
	            this.EarLeft.rotateAngleX = -0.45F;
	            
	            this.Body.offsetX =  1.8F;
	            this.Body.offsetY =  1.5F;
	        } 
        	else if (lemur.getRightShoulder() && lemur.getOwner().isElytraFlying()) {
        		this.state = State.ELYTRARIGHTSHOULDER;
        		
	            this.Body.rotateAngleX = 1.5F;
	            this.ArmRight.rotateAngleX = -1.5F;
	            this.ArmRight.rotateAngleY = -1.6F;
	            this.ArmRight.rotateAngleZ = -0.05F;
	            this.ArmLeft.rotateAngleX = -1.5F;
	            this.ArmLeft.rotateAngleY = 1.6F;
	            this.ArmLeft.rotateAngleZ = 0.05F;
	        
	            this.Tail_start.rotateAngleX = 0F;
	            this.Tail_mid1.rotateAngleX = 0F;
	            this.Tail_mid2.rotateAngleX = 0F;
	            this.Tail_mid3.rotateAngleX = 0F;
	            this.Tail_mid4.rotateAngleX = 0F;
	            this.Tail_end.rotateAngleX = 0F;
	            //this.Left_Wing.setScale(1, 1, 1);
	            //this.Right_Wing.setScale(1, 1, 1);
        		
	            this.LegRightFoot.rotateAngleX = 1.5F;
	            this.LegLeftFoot.rotateAngleX = 1.5F;
	            
	            this.EarRight.rotateAngleX = -0.45F;
	            this.EarLeft.rotateAngleX = -0.45F;
	            
	            this.Body.offsetX =  -1.8F;
	            this.Body.offsetY =  1.5F;
	        } 
        	else if (lemur.isFlying() && !lemur.isLemurRiding())
        	{
        		this.state = State.FLYING;
        		
	            this.Body.rotateAngleX = 1.5F;
	            this.ArmRight.rotateAngleX = -1.5F;
	            this.ArmRight.rotateAngleY = -1.6F;
	            this.ArmRight.rotateAngleZ = -0.05F;
	            this.ArmLeft.rotateAngleX = -1.5F;
	            this.ArmLeft.rotateAngleY = 1.6F;
	            this.ArmLeft.rotateAngleZ = 0.05F;
	        
	            this.Tail_start.rotateAngleX = 0F;
	            this.Tail_mid1.rotateAngleX = 0F;
	            this.Tail_mid2.rotateAngleX = 0F;
	            this.Tail_mid3.rotateAngleX = 0F;
	            this.Tail_mid4.rotateAngleX = 0F;
	            this.Tail_end.rotateAngleX = 0F;
	            //this.Left_Wing.setScale(1, 1, 1);
	            //this.Right_Wing.setScale(1, 1, 1);
        		
	            this.LegRightFoot.rotateAngleX = 1.5F;
	            this.LegLeftFoot.rotateAngleX = 1.5F;
	            
	            this.EarRight.rotateAngleX = -0.45F;
	            this.EarLeft.rotateAngleX = -0.45F;
	        } 
        	else if (lemur.speed > 0.25f && !lemur.isLemurRiding())
        	{
        		this.state = State.SPRINTING;
        		this.Body.rotateAngleX = 1.5F;
        		this.Body.offsetY = 0.25F;
        		this.BodyLower.rotateAngleX = -0.35F;
        		
                this.ArmRight.rotateAngleX = -1.75F;
                this.ArmLeft.rotateAngleX = -1.75F;
                this.ArmRight.rotateAngleY = 0.1F;
                this.ArmLeft.rotateAngleY = -0.1F;
                this.ArmLeftLower.rotateAngleY = 0.4F; 
                this.ArmRightLower.rotateAngleY = -0.4F;
                this.ArmRightHand.rotateAngleY = 2.6F; 
                this.ArmLeftHand.rotateAngleY = -2.6F;

                this.LegLeft.rotateAngleX = -1.5F;
                this.LegRight.rotateAngleX = -1.5F;
                this.LegLeft.rotateAngleY = -0.5F;
                this.LegRight.rotateAngleY = 0.5F;
     
                
                this.Tail_start.rotateAngleX = 0F;
	            this.Tail_mid1.rotateAngleX = 0F;
	            this.Tail_mid2.rotateAngleX = 0F;
	            this.Tail_mid3.rotateAngleX = 0.1F;
	            this.Tail_mid4.rotateAngleX = 0.1F;
	            this.Tail_end.rotateAngleX = 0.1F;
        
	        }
        	else if (lemur.speed > 0.1F && lemur.speed < 0.25F && !lemur.isLemurRiding())
        	{
        		this.state = State.WALKING;
	        }
            else {
        		this.state = State.STANDING;
        	}
        }
    }
  
    
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
    	super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        
    }
    private float triangleWave(float p_78172_1_, float p_78172_2_)
    {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5F) - p_78172_2_ * 0.25F) / (p_78172_2_ * 0.25F);
    }
    @SideOnly(Side.CLIENT)
    static enum State
    {
        FLYING,
        SPRINTING,
        SITTING,
        STANDING,
        WALKING,
        ONRIGHTSHOULDER,
        ONLEFTSHOULDER,
        ELYTRARIGHTSHOULDER,
        ELYTRALEFTSHOULDER,
        PARTY;
    }
}
