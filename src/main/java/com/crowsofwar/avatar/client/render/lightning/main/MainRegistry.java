package com.crowsofwar.avatar.client.render.lightning.main;

import com.crowsofwar.avatar.AvatarInfo;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

@Mod(modid = AvatarInfo.MOD_ID, version = AvatarInfo.VERSION, name = AvatarInfo.MOD_NAME)
public class MainRegistry {

    @SidedProxy(clientSide = "ClientProxy", serverSide = "ServerProxy")
    public static ServerProxy proxy;

    @Mod.Instance(AvatarInfo.MOD_ID)
    public static MainRegistry instance;

    public static Logger logger;

    public static int generalOverride = 0;
    public static int polaroidID = 1;

    public static final int schrabFromUraniumChance = 100;

    public static int x;
    public static int y;
    public static int z;
    public static long time;

    // Armor Materials
    // Drillgon200: I have no idea what the two strings and the number at the
    // end are.

    Random rand = new Random();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if(logger == null)
            logger = event.getModLog();

        if(generalOverride > 0 && generalOverride < 19) {
            polaroidID = generalOverride;
        } else {
            polaroidID = rand.nextInt(18) + 1;
            while(polaroidID == 4 || polaroidID == 9)
                polaroidID = rand.nextInt(18) + 1;
        }

        if(SharedMonsterAttributes.MAX_HEALTH.clampValue(Integer.MAX_VALUE) <= 2000)
            try{
                @SuppressWarnings("deprecation")
                Field f = ReflectionHelper.findField(RangedAttribute.class, "maximumValue", "field_111118_b");
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                f.set(SharedMonsterAttributes.MAX_HEALTH, Integer.MAX_VALUE);
            } catch(Exception e){}

        proxy.checkGLCaps();
        reloadConfig();

//        MinecraftForge.EVENT_BUS.register(new ModEventHandler());
//        MinecraftForge.TERRAIN_GEN_BUS.register(new ModEventHandler());
//        MinecraftForge.ORE_GEN_BUS.register(new ModEventHandler());
//
//        PacketDispatcher.registerPackets();
//
//        CapabilityManager.INSTANCE.register(HbmLivingCapability.IEntityHbmProps.class, new HbmLivingCapability.EntityHbmPropsStorage(), HbmLivingCapability.EntityHbmProps.FACTORY);
//        CapabilityManager.INSTANCE.register(HbmCapability.IHBMData.class, new HbmCapability.HBMDataStorage(), HbmCapability.HBMData.FACTORY);

        proxy.registerRenderInfo();
        proxy.preInit(event);


        int i = 0;
    }

    public static void reloadConfig() {
        Configuration config = new Configuration(new File(proxy.getDataDir().getPath() + "/config/hbm/hbm.cfg"));
        config.load();
        config.save();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        //Drillgon200: expand the max entity radius for the hunter chopper
        if(World.MAX_ENTITY_RADIUS < 5)
            World.MAX_ENTITY_RADIUS = 5;
        proxy.postInit(event);
    }
}