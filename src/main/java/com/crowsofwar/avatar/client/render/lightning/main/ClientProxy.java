package com.crowsofwar.avatar.client.render.lightning.main;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ClientProxy extends ServerProxy {

    public static KeyBinding jetpackActivate;
    public static KeyBinding jetpackHover;
    public static KeyBinding jetpackHud;
    public static KeyBinding fsbFlashlight;

    public static final ModelResourceLocation IRRELEVANT_MRL = new ModelResourceLocation("hbm:placeholdermodel", "inventory");

    //Drillgon200: This is stupid, but I'm lazy
    public static boolean renderingConstant = false;

    public static Field partialTicksPaused;

    public static final FloatBuffer AUX_GL_BUFFER = GLAllocation.createDirectFloatBuffer(16);
    public static final FloatBuffer AUX_GL_BUFFER2 = GLAllocation.createDirectFloatBuffer(16);

    //Drillgon200: Will I ever figure out how to write better code than this?
    public static final List<Runnable> deferredRenderers = new ArrayList<>();

}