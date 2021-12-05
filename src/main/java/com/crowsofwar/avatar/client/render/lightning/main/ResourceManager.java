package com.crowsofwar.avatar.client.render.lightning.main;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.client.render.lightning.animloader.AnimatedModel;
import com.crowsofwar.avatar.client.render.lightning.animloader.Animation;
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

    public static final ResourceLocation hat = new ResourceLocation(AvatarInfo.MOD_ID, "textures/armor/hat.png");
    public static final ResourceLocation mod_tesla = new ResourceLocation(AvatarInfo.MOD_ID, "textures/armor/mod_tesla.png");
    //public static final ResourceLocation wings_solstice = new ResourceLocation(AvatarInfo.MOD_ID, "textures/armor/wings_solstice.png");

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

    public static final ResourceLocation fl_cookie = new ResourceLocation(AvatarInfo.MOD_ID, "textures/misc/fl_cookie.png");


    //ANIMATIONS
    public static AnimatedModel lightning_fp;
    public static Animation lightning_fp_anim;

    //SHADERS
    public static Shader lit_particles = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/lit_particles"), shader -> {
        GLCompat.bindAttribLocation(shader, 0, "pos");
        GLCompat.bindAttribLocation(shader, 1, "offsetPos");
        GLCompat.bindAttribLocation(shader, 2, "scale");
        GLCompat.bindAttribLocation(shader, 3, "texData");
        GLCompat.bindAttribLocation(shader, 4, "color");
        GLCompat.bindAttribLocation(shader, 5, "lightmap");
    }).withUniforms(HbmShaderManager2.MODELVIEW_MATRIX, HbmShaderManager2.PROJECTION_MATRIX, HbmShaderManager2.INV_PLAYER_ROT_MATRIX, HbmShaderManager2.LIGHTMAP);

    public static Shader gluon_beam = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/gluon_beam"))
            .withUniforms(shader -> {
                GLCompat.activeTexture(GLCompat.GL_TEXTURE0+3);
                Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.noise_1);
                shader.uniform1i("noise_1", 3);
                GLCompat.activeTexture(GLCompat.GL_TEXTURE0+4);
                Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.noise_2);
                shader.uniform1i("noise_1", 4);
                GLCompat.activeTexture(GLCompat.GL_TEXTURE0);

                float time = (System.currentTimeMillis()%10000000)/1000F;
                shader.uniform1f("time", time);
            });

    public static Shader gluon_spiral = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/gluon_spiral"))
            .withUniforms(shader -> {
                //Well, I accidentally uniformed the same noise sampler twice. That explains why the second noise didn't work.
                GLCompat.activeTexture(GLCompat.GL_TEXTURE0+3);
                Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.noise_1);
                shader.uniform1i("noise_1", 3);
                GLCompat.activeTexture(GLCompat.GL_TEXTURE0+4);
                Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.noise_2);
                shader.uniform1i("noise_1", 4);
                GLCompat.activeTexture(GLCompat.GL_TEXTURE0);

                float time = (System.currentTimeMillis()%10000000)/1000F;
                shader.uniform1f("time", time);
            });

    //Drillgon200: Did I need a shader for this? No, not really, but it's somewhat easier to create a sin wave pattern programmatically than to do it in paint.net.
    public static Shader tau_ray = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/tau_ray"));

    public static Shader book_circle = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/book/circle"));

    public static Shader normal_fadeout = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/normal_fadeout"));

    public static Shader heat_distortion = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/heat_distortion"))
            .withUniforms(shader -> {
                Framebuffer buffer = Minecraft.getMinecraft().getFramebuffer();
                GLCompat.activeTexture(GLCompat.GL_TEXTURE0+3);
                GlStateManager.bindTexture(buffer.framebufferTexture);
                shader.uniform1i("fbo_tex", 3);
                GLCompat.activeTexture(GLCompat.GL_TEXTURE0+4);
                Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.noise_2);
                shader.uniform1i("noise", 4);
                GLCompat.activeTexture(GLCompat.GL_TEXTURE0);

                float time = (System.currentTimeMillis()%10000000)/1000F;
                shader.uniform1f("time", time);
                shader.uniform2f("windowSize", Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            });

    public static Shader desaturate = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/desaturate"));
    public static Shader test_trail = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/trail"), shader ->{
        GLCompat.bindAttribLocation(shader, 0, "pos");
        GLCompat.bindAttribLocation(shader, 1, "tex");
        GLCompat.bindAttribLocation(shader, 2, "color");
    });
    public static Shader blit = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/blit"));
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


    //The actual shaders used in flashlight rendering, not experimental
    public static Shader albedo = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/lighting/albedo"));
    public static Shader flashlight_depth = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/lighting/flashlight_depth"));
    public static Shader flashlight_post = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/lighting/flashlight_post")).withUniforms(shader -> {
        shader.uniform2f("windowSize", Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    });
    public static Shader pointlight_post = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/lighting/pointlight_post")).withUniforms(shader -> {
        shader.uniform2f("windowSize", Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    });
    public static Shader cone_volume = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/lighting/cone_volume")).withUniforms(shader -> {
        shader.uniform2f("windowSize", Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    });
    public static Shader flashlight_blit = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/lighting/blit"));
    public static Shader volume_upscale = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/lighting/volume_upscale")).withUniforms(shader -> {
        shader.uniform2f("windowSize", Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    });

    public static Shader heat_distortion_post = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/heat_distortion_post")).withUniforms(shader ->{
        shader.uniform2f("windowSize", Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        GlStateManager.setActiveTexture(GLCompat.GL_TEXTURE0+4);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.noise_2);
        shader.uniform1i("noise", 4);
        GlStateManager.setActiveTexture(GLCompat.GL_TEXTURE0);
        float time = (System.currentTimeMillis()%10000000)/1000F;
        shader.uniform1f("time", time);
    });

    public static Shader heat_distortion_new = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/heat_distortion_new"));
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
    public static Shader flash_lmap = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/flash_lmap")).withUniforms(HbmShaderManager2.LIGHTMAP);
    public static Shader bimpact = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/bimpact"), shader -> {
        GLCompat.bindAttribLocation(shader, 0, "pos");
        GLCompat.bindAttribLocation(shader, 1, "vColor");
        GLCompat.bindAttribLocation(shader, 3, "tex");
        GLCompat.bindAttribLocation(shader, 4, "lightTex");
        GLCompat.bindAttribLocation(shader, 5, "projTex");
    }).withUniforms(HbmShaderManager2.LIGHTMAP, HbmShaderManager2.WINDOW_SIZE);
    public static Shader blood_dissolve = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/blood/blood")).withUniforms(HbmShaderManager2.LIGHTMAP);
    public static Shader gravitymap_render = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/blood/gravitymap"));
    public static Shader blood_flow_update = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/blood/blood_flow_update"));

//    public static Shader gpu_particle_render = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/gpu_particle_render")).withUniforms(HbmShaderManager2.MODELVIEW_MATRIX, HbmShaderManager2.PROJECTION_MATRIX, HbmShaderManager2.INV_PLAYER_ROT_MATRIX, shader -> {
//        shader.uniform1i("lightmap", 1);
//        shader.uniform1i("particleData0", 2);
//        shader.uniform1i("particleData1", 3);
//        shader.uniform1i("particleData2", 4);
//        shader.uniform4f("particleTypeTexCoords[0]", ModEventHandlerClient.contrail.getMinU(), ModEventHandlerClient.contrail.getMinV(), ModEventHandlerClient.contrail.getMaxU() - ModEventHandlerClient.contrail.getMinU(), ModEventHandlerClient.contrail.getMaxV() - ModEventHandlerClient.contrail.getMinV());
//    });

    public static Shader gpu_particle_udpate = HbmShaderManager2.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/gpu_particle_update")).withUniforms(shader -> {
        shader.uniform1i("particleData0", 2);
        shader.uniform1i("particleData1", 3);
        shader.uniform1i("particleData2", 4);
    });

    public static final Vbo test = Vbo.setupTestVbo();

    public static void init() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(fresnel_ms);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        Minecraft.getMinecraft().getTextureManager().bindTexture(noise_1);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        Minecraft.getMinecraft().getTextureManager().bindTexture(noise_2);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    }

}
