package com.crowsofwar.avatar.client.render.lightning.main;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.client.render.lightning.animloader.AnimatedModel;
import com.crowsofwar.avatar.client.render.lightning.animloader.Animation;
import com.crowsofwar.avatar.client.render.lightning.animloader.ColladaLoader;
import com.crowsofwar.avatar.client.render.lightning.handler.HbmShaderManager2;
import com.crowsofwar.avatar.client.render.lightning.handler.HbmShaderManager2.Shader;
import com.crowsofwar.avatar.client.render.lightning.render.*;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class ResourceManager {

    //God
    public static final IModelCustom error = AdvancedModelLoader.loadModel(new ResourceLocation(AvatarInfo.MOD_ID, "models/error.obj"));

    //Drillgon200 model loading test
    //Hey it worked! I wonder if I can edit the tessellator to call 1.12.2 builder buffer commands, because that's a lot less laggy.

    ////Obj Items

    //Shimmer Sledge

    ////Texture Items

    //Texture Entities
    public static final ResourceLocation white = new ResourceLocation(AvatarInfo.MOD_ID, "textures/misc/white.png");

    //Blast
    public static final ResourceLocation fireball = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/explosion/fireball.png");
    public static final ResourceLocation balefire = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/explosion/balefire.png");
    public static final ResourceLocation tomblast = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/explosion/tomblast.png");
    public static final ResourceLocation dust = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/explosion/dust.png");

    //Lightning
    public static final ResourceLocation fresnel_ms = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/bfg/fresnel_ms.png");
    public static final ResourceLocation bfg_ring_4 = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/bfg/ring3_lighter.png");
    public static final ResourceLocation bfg_lightning_1 = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/bfg/lightning_isolated.png");
    public static final ResourceLocation bfg_lightning_2 = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/bfg/multi_tester.png");
    public static final ResourceLocation bfg_core_lightning = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/bfg/additivebeam.png");
    public static final ResourceLocation bfg_beam = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/bfg/why.png");
    public static final ResourceLocation bfg_beam1 = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/bfg/why2.png");
    public static final ResourceLocation bfg_beam2 = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/bfg/beam_test0.png");
    public static final ResourceLocation bfg_prefire = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/bfg/perlin_fresnel.png");
    public static final ResourceLocation bfg_particle = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/bfg/particle.png");
    public static final ResourceLocation bfg_smoke = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/bfg/smoke3_bright2.png");

    //Debug
    public static final ResourceLocation uv_debug = new ResourceLocation(AvatarInfo.MOD_ID, "textures/misc/uv_debug.png");

    public static final ResourceLocation noise_1 = new ResourceLocation(AvatarInfo.MOD_ID, "textures/misc/noise_1.png");
    public static final ResourceLocation noise_2 = new ResourceLocation(AvatarInfo.MOD_ID, "textures/misc/noise_2.png");
    public static final ResourceLocation noise_3 = new ResourceLocation(AvatarInfo.MOD_ID, "textures/misc/fract_noise.png");

    public static ResourceLocation skin = new ResourceLocation(AvatarInfo.MOD_ID, "textures/models/ducc_st_engineer.png");

    //ANIMATIONS
    public static AnimatedModel lightning_fp;
    public static Animation lightning_fp_anim;

    public static Shader downsample = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/downsample"));
    public static Shader bloom_h = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/bloom_h"));
    public static Shader bloom_v = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/bloom_v"));
    public static Shader bloom_test = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/bloom_test"));
    public static Shader lightning = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/lightning/lightning"), shader ->{
        GLCompat.bindAttribLocation(shader, 0, "pos");
        GLCompat.bindAttribLocation(shader, 1, "tex");
        GLCompat.bindAttribLocation(shader, 2, "color");
    }).withUniforms(shader -> {
        GLCompat.activeTexture(GLCompat.GL_TEXTURE0+4);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.noise_2);
        shader.uniform1i("noise", 4);
        GLCompat.activeTexture(GLCompat.GL_TEXTURE0);
    });
    public static Shader maxdepth = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/maxdepth"));
    public static Shader lightning_gib = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/lightning/lightning_gib")).withUniforms(HbmShaderManager2.LIGHTMAP, shader -> {
        GLCompat.activeTexture(GLCompat.GL_TEXTURE0+4);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.noise_2);
        shader.uniform1i("noise", 4);
        GLCompat.activeTexture(GLCompat.GL_TEXTURE0);
    });
    public static Shader testlut = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/testlut"));
    public static Shader flashlight_nogeo = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/flashlight_nogeo"));
    public static Shader flashlight_deferred = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/flashlight_deferred")).withUniforms(shader -> {
        shader.uniform2f("windowSize", Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    });

    public static Shader crucible_lightning = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/crucible_lightning"), shader ->{
        GLCompat.bindAttribLocation(shader, 0, "pos");
        GLCompat.bindAttribLocation(shader, 1, "tex");
        GLCompat.bindAttribLocation(shader, 2, "in_color");
    }).withUniforms(shader -> {
        GLCompat.activeTexture(GLCompat.GL_TEXTURE0+4);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.noise_2);
        shader.uniform1i("noise", 4);
        GLCompat.activeTexture(GLCompat.GL_TEXTURE0);
    });

    public static Shader gpu_particle_udpate = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/gpu_particle_update")).withUniforms(shader -> {
        shader.uniform1i("particleData0", 2);
        shader.uniform1i("particleData1", 3);
        shader.uniform1i("particleData2", 4);
    });

    public static final Vbo test = Vbo.setupTestVbo();

    public static void loadAnimatedModels(){

        lightning_fp = ColladaLoader.load(new ResourceLocation(AvatarInfo.MOD_ID, "models/anim/lightning_fp_anim0.dae"));
        lightning_fp_anim = ColladaLoader.loadAnim(4160, new ResourceLocation(AvatarInfo.MOD_ID, "models/anim/lightning_fp_anim0.dae"));

    }

    public static void init() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(fresnel_ms);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        Minecraft.getMinecraft().getTextureManager().bindTexture(noise_1);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        Minecraft.getMinecraft().getTextureManager().bindTexture(noise_2);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    }

}
