package com.crowsofwar.avatar.glider.client.gui;

import com.crowsofwar.avatar.glider.common.config.ConfigHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

import static com.crowsofwar.avatar.AvatarInfo.MOD_ID;
import static com.crowsofwar.avatar.AvatarInfo.MOD_NAME;

public class ConfigGuiOpenGlider extends GuiConfig {

    public ConfigGuiOpenGlider(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(parentScreen), MOD_ID, false, false, MOD_NAME);
    }

    private static List<IConfigElement> getConfigElements(GuiScreen parentScreen) {
        List<IConfigElement> list = new ArrayList<>();

        for (String category : ConfigHandler.categories)
            list.add(new ConfigElement(ConfigHandler.config.getCategory(category)));

        return list;
    }
}
