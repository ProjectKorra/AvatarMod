/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.client.gui;

import com.crowsofwar.avatar.client.AvatarShaderUtils;
import com.crowsofwar.avatar.client.gui.skills.SkillsGui;
import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.util.data.*;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityAirBubble;
import com.crowsofwar.avatar.entity.EntityIcePrison;
import com.crowsofwar.avatar.entity.EntityIceShield;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.client.gui.AvatarUiTextures.BLOCK_BREAK;
import static com.crowsofwar.avatar.client.uitools.ScreenInfo.*;
import static com.crowsofwar.avatar.config.ConfigClient.CLIENT_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.BUBBLE_CONTRACT;
import static com.crowsofwar.avatar.util.data.StatusControlController.SHIELD_SHATTER;
import static com.crowsofwar.avatar.util.data.TickHandlerController.RENDER_ELEMENT_HANDLER;
import static net.minecraft.client.renderer.GlStateManager.*;

/**
 * @author CrowsOfWar
 */
@SideOnly(Side.CLIENT)
public class AvatarUiRenderer extends Gui {

    public static AvatarUiRenderer instance;
    private final Minecraft mc;
    private RadialMenu currentBendingMenu;
    private RadialSegment fadingSegment;
    private long timeFadeStart;
    private long errorMsgFade;
    private String errorMsg;

    public AvatarUiRenderer() {
        mc = Minecraft.getMinecraft();
        instance = this;
        errorMsgFade = -1;
        errorMsg = "";
    }

    public static void openBendingGui(UUID bending) {

        BendingStyle controller = BendingStyles.get(bending);
        assert controller != null;
        BendingMenuInfo menu = controller.getRadialMenu();

        instance.currentBendingMenu = new RadialMenu(controller, menu.getTheme(), menu.getButtons());
        instance.mc.setIngameNotInFocus();

    }

    public static boolean hasBendingGui() {
        return instance.currentBendingMenu != null;
    }

    public static void fade(RadialSegment segment) {
        instance.fadingSegment = segment;
        instance.timeFadeStart = System.currentTimeMillis();
    }

    public static void displayErrorMessage(String message) {
        instance.errorMsgFade = System.currentTimeMillis();
        instance.errorMsg = message;
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onGuiRender(RenderGameOverlayEvent.Post e) {

        ScaledResolution resolution = e.getResolution();
        if (e.isCancelable())
            e.setCanceled(false);
        if (e.getType() == ElementType.EXPERIENCE) {
            //HUD bending stuff
            renderBattleStatus(resolution);
            renderRadialMenu(resolution);
            renderChiBar(resolution);
            renderChiMsg(resolution);
            renderActiveBending(resolution);
            //Shield health
            renderAirBubbleHealth(resolution);
            renderIceShieldHealth(resolution);
            //Status Controls
            renderStatusControls(resolution);
            //Cooldowns
           // renderCooldown(resolution);
            //Misc.
            applyVisionShader();
            renderPrisonCracks(resolution);
        }

    }

    private void renderCooldown(ScaledResolution res) {
        refreshDimensions();
        BendingData data = BendingData.getFromEntity(mc.player);
        if (data != null) {
            if (data.getActiveBending() != null) {
                List<Ability> abilities = Abilities.all().stream().filter(ability ->
                        ability.getElement().equals(data.getActiveBending())).collect(Collectors.toList());

                for (Ability ability : abilities) {
                    mc.getTextureManager().bindTexture(AvatarUiTextures.STATUS_CONTROL_ICONS);
                    int centerX = res.getScaledWidth() / 2;
                    int centerY = res.getScaledHeight() / 2;

                    double scale = .5;

                    GlStateManager.color(1, 1, 1);

                    GlStateManager.disableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(scale, scale, scale);
                  //  drawTexturedModalRect((int) ((centerX) / scale), (int) ((centerY) / scale),
                   //         statusControl.getTextureU(), statusControl.getTextureV(), 16, 16);
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    private void renderRadialMenu(ScaledResolution resolution) {
        int mouseX = Mouse.getX() * resolution.getScaledWidth() / mc.displayWidth;
        int mouseY = resolution.getScaledHeight()
                - (Mouse.getY() * resolution.getScaledHeight() / mc.displayHeight);

        // For some reason, not including this will cause weirdness in 3rd
        // person
        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        if (currentBendingMenu != null) {
            if (currentBendingMenu.updateScreen(mouseX, mouseY, resolution)) {
                currentBendingMenu = null;

                // If skills menu was opened via radial menu, don't call setIngameFocus because that
                // would close the skills menu
                if (!(mc.currentScreen instanceof SkillsGui)) {
                    mc.setIngameFocus();
                }

            } else {
                currentBendingMenu.drawScreen(mouseX, mouseY, resolution);
                mc.setIngameNotInFocus();
            }
        }
        if (fadingSegment != null) {
            float timeToFade = 500;
            long timeSinceStart = System.currentTimeMillis() - timeFadeStart;
            if (timeSinceStart > timeToFade) {
                fadingSegment = null;
            } else {
                float scale = (float) (1 + Math.sqrt(timeSinceStart / 10000f));
                GlStateManager.color(1, 1, 1, (1 - timeSinceStart / timeToFade) * CLIENT_CONFIG.radialMenuAlpha);
                fadingSegment.draw(true, resolution,
                        (1 - timeSinceStart / timeToFade) * CLIENT_CONFIG.radialMenuAlpha, scale);
            }
        }
        GlStateManager.popMatrix();
    }

    private void renderStatusControls(ScaledResolution resolution) {
        refreshDimensions();
        BendingData data = BendingData.getFromEntity(mc.player);
        if (data != null) {
            List<StatusControl> statusControls = data.getAllStatusControls();
            for (StatusControl statusControl : statusControls) {
                mc.getTextureManager().bindTexture(AvatarUiTextures.STATUS_CONTROL_ICONS);
                int centerX = resolution.getScaledWidth() / 2;
                int centerY = resolution.getScaledHeight() / 2;
                int xOffset = statusControl.getPosition().xOffset();
                int yOffset = statusControl.getPosition().yOffset();

                double scale = .5;

                GlStateManager.color(1, 1, 1);

                GlStateManager.disableLighting();
                GlStateManager.disableBlend();
                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, scale);
                drawTexturedModalRect((int) ((centerX - xOffset) / scale), (int) ((centerY - yOffset) / scale),
                        statusControl.getTextureU(), statusControl.getTextureV(), 16, 16);
                GlStateManager.popMatrix();
            }
        }
    }

    private void renderChiBar(ScaledResolution resolution) {
        refreshDimensions();
        if (CLIENT_CONFIG.chiBarSettings.shouldChibarRender) {

            BendingData data = BendingData.getFromEntity(mc.player);
            if (data != null) {
                boolean shouldRender = !CLIENT_CONFIG.chiBarSettings.shouldChiMenuDisappear || data.hasTickHandler(RENDER_ELEMENT_HANDLER);

                if (shouldRender) {
                    float alpha = data.hasTickHandler(RENDER_ELEMENT_HANDLER) ?
                            //Ensures that at 0 duration the opacity is the same as the opacity when not fading.
                            ((float) CLIENT_CONFIG.chiBarSettings.chibarDuration - data.getTickHandlerDuration(RENDER_ELEMENT_HANDLER))
                                    / CLIENT_CONFIG.chiBarSettings.chibarDuration * CLIENT_CONFIG.chiBarAlpha : CLIENT_CONFIG.chiBarAlpha;

                    if (data.getAllBending().isEmpty()) return;

                    Chi chi = data.chi();
                    float total = chi.getTotalChi();
                    float max = chi.getMaxChi();
                    float available = chi.getAvailableChi();
                    float unavailable = total - available;

                    // Dimensions of end result in pixels
                    float scale = 1.1f;
                    float width = 100 * scale;
                    float height = 9F * (float) CLIENT_CONFIG.chiBarSettings.heightScale;


                    mc.getTextureManager().bindTexture(AvatarUiTextures.getChiTexture(data.getActiveBendingId()));

                    pushMatrix();
                    enableBlend();

                    translate(resolution.getScaledWidth() - CLIENT_CONFIG.chiBarSettings.xPos,
                            resolution.getScaledHeight() - height - CLIENT_CONFIG.chiBarSettings.yPos, 0);
                    scale(CLIENT_CONFIG.chiBarSettings.widthScale, CLIENT_CONFIG.chiBarSettings.heightScale, 1);

                    color(1, 1, 1, alpha);

                    // Background of chi bar
                    drawTexturedModalRect(0, 0, 0, 36, 100, 9);

                    // Available chi

                    color(1, 1, 1, alpha * 1.5F);
                    drawTexturedModalRect(0, 0, 0, 27, (int) (99 * (available + unavailable) / max), 9);

                    // Unavailable chi
                    drawTexturedModalRect(0, 0, 0, 45, (int) (98 * unavailable / max), 9);

                    color(1, 1, 1, alpha);
                    if (CLIENT_CONFIG.chiBarSettings.shouldChiNumbersRender) {
                        drawString(mc.fontRenderer, ((int) total) + "/" + ((int) max) + ", " + ((int) available), 25, -10,
                                data.getActiveBending().getTextColour() | ((int) (alpha * 255) << 24));
                    }
                    popMatrix();

                }
            }
        }
    }

    private void renderChiMsg(ScaledResolution res) {
        refreshDimensions();
        GlStateManager.pushMatrix();
        if (errorMsgFade != -1) {

            float seconds = (System.currentTimeMillis() - errorMsgFade) / 1000f;
            float alpha = seconds < 1 ? 1 : 1 - (seconds - 1);
            int alphaI = (int) (alpha * 255);
            // For some reason, any alpha below 4 is displayed at alpha 255
            if (alphaI < 4) alphaI = 4;

            String text = TextFormatting.BOLD + I18n.format(errorMsg);

            //@formatter:off
            drawString(mc.fontRenderer, text,
                    (res.getScaledWidth() - mc.fontRenderer.getStringWidth(text)) / 2,
                    res.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT - 40,
                    0xffffff | (alphaI << 24));
            //@formatter:on

            if (seconds >= 2) errorMsgFade = -1;

        }
        GlStateManager.popMatrix();
    }

    private void renderActiveBending(ScaledResolution res) {
        refreshDimensions();
        if (CLIENT_CONFIG.activeBendingSettings.shouldBendingMenuRender) {
            BendingData data = BendingData.getFromEntity(mc.player);

            if (data != null) {
                boolean shouldRender = !CLIENT_CONFIG.activeBendingSettings.shouldBendingMenuDisappear || data.hasTickHandler(RENDER_ELEMENT_HANDLER);
                if (shouldRender) {
                    if (data.getActiveBending() != null) {
                        GlStateManager.pushMatrix();
                        float alpha = data.hasTickHandler(RENDER_ELEMENT_HANDLER) ?
                                ((float) CLIENT_CONFIG.activeBendingSettings.bendingMenuDuration - data.getTickHandlerDuration(RENDER_ELEMENT_HANDLER))
                                        / CLIENT_CONFIG.activeBendingSettings.bendingMenuDuration * CLIENT_CONFIG.bendingCycleAlpha : CLIENT_CONFIG.bendingCycleAlpha;
                        GlStateManager.color(1, 1, 1, alpha);
                        drawBendingIcon(CLIENT_CONFIG.activeBendingSettings.middleXPosition,
                                CLIENT_CONFIG.activeBendingSettings.middleYPosition, data.getActiveBending(),
                                CLIENT_CONFIG.activeBendingSettings.middleBendingWidth, CLIENT_CONFIG.activeBendingSettings.middleBendingHeight);
                        GlStateManager.popMatrix();
                        List<BendingStyle> allBending = data.getAllBending();
                        allBending.sort(Comparator.comparing(BendingStyle::getName));

                        // Draw next
                        int indexNext = allBending.indexOf(data.getActiveBending()) + 1;
                        if (indexNext == allBending.size()) indexNext = 0;

                        if (allBending.size() > 1) {
                            GlStateManager.pushMatrix();
                            GlStateManager.translate(0, 0, -1);
                            drawBendingIcon(CLIENT_CONFIG.activeBendingSettings.rightXPosition, CLIENT_CONFIG.activeBendingSettings.rightYPosition,
                                    allBending.get(indexNext), CLIENT_CONFIG.activeBendingSettings.rightBendingWidth,
                                    CLIENT_CONFIG.activeBendingSettings.rightBendingHeight);
                            GlStateManager.color(1, 1, 1, alpha * 0.5f);

                            GlStateManager.popMatrix();
                        }

                        // Draw previous
                        int indexPrevious = allBending.indexOf(data.getActiveBending()) - 1;
                        if (indexPrevious <= -1) indexPrevious = allBending.size() - 1;

                        if (allBending.size() > 2) {
                            GlStateManager.pushMatrix();
                            GlStateManager.enableDepth();
                            GlStateManager.translate(0, 0, -1);
                            drawBendingIcon(CLIENT_CONFIG.activeBendingSettings.leftXPosition, CLIENT_CONFIG.activeBendingSettings.leftYPosition,
                                    allBending.get(indexPrevious), CLIENT_CONFIG.activeBendingSettings.leftBendingWidth, CLIENT_CONFIG.activeBendingSettings.leftBendingHeight);
                            GlStateManager.color(1, 1, 1, alpha * 0.5f);

                            GlStateManager.popMatrix();
                        }

                    }

                }
            }
        }
    }


    private void drawBendingIcon(int xOff, int yOff, BendingStyle controller, double width, double height) {
        refreshDimensions();
        int x = screenWidth() / scaleFactor() - 85 + xOff;
        int y = screenHeight() / scaleFactor() - 60 + yOff;
        int level = 0;
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (BendingData.getFromEntity(player) != null) {
            List<Ability> abilities = controller.getAllAbilities();
            abilities = abilities.stream().filter(ability -> AbilityData.get(player, ability.getName()).getLevel() > -1).collect(Collectors.toList());
            for (Ability ability : abilities) {
                AbilityData aD = AbilityData.get(player, ability.getName());
                if (aD.getLevel() > -1) {
                    level += aD.getLevel() + 1;
                }
            }
        }
        mc.renderEngine.bindTexture(AvatarUiTextures.getBendingIconTexture(controller.getId(), level));
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(width / 256F, height / 256F, 1);
        drawTexturedModalRect(0, 0, 0, 0, 256, 256);
        GlStateManager.popMatrix();
    }

    private void renderAirBubbleHealth(ScaledResolution res) {
        refreshDimensions();
        World world = mc.world;
        EntityPlayer player = mc.player;
        BendingData data = BendingData.getFromEntity(player);

        if (data != null) {
            if (data.hasStatusControl(BUBBLE_CONTRACT)) {
                EntityAirBubble bubble = AvatarEntity.lookupControlledEntity(world, EntityAirBubble.class,
                        player);
                if (bubble != null && bubble.getOwner() == player) {
                    renderShieldHealth(res, bubble.getHealth(), bubble.getMaxHealth(), 0);
                }
            }
        }
    }

    private void renderIceShieldHealth(ScaledResolution res) {
        refreshDimensions();
        World world = mc.world;
        EntityPlayer player = mc.player;
        BendingData data = BendingData.getFromEntity(player);

        if (data != null) {
            if (data.hasStatusControl(SHIELD_SHATTER)) {
                EntityIceShield shield = AvatarEntity.lookupControlledEntity(world, EntityIceShield
                        .class, player);
                if (shield != null && shield.getOwner() == player) {
                    renderShieldHealth(res, shield.getHealth(), shield.getMaxHealth(), 9);
                }
            }
        }
    }

    private void renderShieldHealth(ScaledResolution res, float health, float maxHealth, int
            textureV) {
        refreshDimensions();
        GlStateManager.pushMatrix();
        mc.renderEngine.bindTexture(AvatarUiTextures.shieldHealth);
        GlStateManager.color(1, 1, 1, 1);

        int x = res.getScaledWidth() / 2 - 91;
        int y = res.getScaledHeight() - GuiIngameForge.left_height;
        if (mc.player.isCreative())
            y -= 1;
        else if (mc.player.getTotalArmorValue() == 0) {
            y += 10;
        }

        int hearts = (int) (maxHealth / 2);
        for (int i = 0; i < hearts; i++) {

            // Draw background
            drawTexturedModalRect(x + i * 9, y, 0, textureV, 9, 9);

            // Draw hearts or half hearts
            int diff = (int) (health - i * 2);
            if (diff >= 2) {
                drawTexturedModalRect(x + i * 9, y, 18, textureV, 9, 9);
            } else if (diff == 1) {
                drawTexturedModalRect(x + i * 9, y, 27, textureV, 9, 9);
            }

        }
        GlStateManager.popMatrix();

    }

    private void renderPrisonCracks(ScaledResolution res) {
        refreshDimensions();
        EntityPlayer player = mc.player;
        EntityIcePrison prison = EntityIcePrison.getPrison(player);
        if (prison != null) {

            GlStateManager.pushMatrix();

            float scaledWidth = res.getScaledWidth();
            float scaledHeight = res.getScaledHeight();
            float scaleX = scaledWidth / 256;
            float scaleY = scaledHeight / 256;
            float scale = Math.max(scaleX, scaleY);

            // Width of screen: scaledWidth
            // Width of ice: scale * 256

            GlStateManager.translate((scaledWidth - scale * 256) / 2, (scaledHeight - scale * 256) / 2, 0);
            GlStateManager.scale(scale, scale, 1);

            mc.renderEngine.bindTexture(AvatarUiTextures.ICE);
            drawTexturedModalRect(0, 0, 0, 0, 256, 256);

            color(1, 1, 1, 0.5f);
            float percent = 1 - (float) prison.getImprisonedTime() / prison.getMaxImprisonedTime();
            int crackIndex = (int) (percent * percent * percent * (BLOCK_BREAK.length + 1)) - 1;
            if (crackIndex > -1) {
                mc.renderEngine.bindTexture(BLOCK_BREAK[crackIndex]);
                drawTexturedModalRect(0, 0, 0, 0, 256, 256);
            }

            GlStateManager.popMatrix();

        }

    }

    /**
     * Applies shaders to modify vision to match the current Vision of the player.
     *
     * @see BendingData#getVision()
     * @see Vision
     */
    private void applyVisionShader() {
        BendingData data = BendingData.getFromEntity(mc.player);
        if (data != null) {
            Vision vision = data.getVision();
            if (vision != null) {
                AvatarShaderUtils.useShader(vision.getShaderLocation());
            } else {
                AvatarShaderUtils.stopUsingShader();
            }
        }
    }

    /**
     * Displays current Battle Performance and Power Rating.
     */
    private void renderBattleStatus(ScaledResolution res) {
        refreshDimensions();
        BendingData data = BendingData.getFromEntity(mc.player);
        if (data != null) {
            if (data.getAllBending().isEmpty()) {
                return;
            }

            {
                String text = "Performance: " + ((int) data.getPerformance().getScore());
                FontRenderer fr = mc.fontRenderer;

                drawString(fr, text, res.getScaledWidth() - fr.getStringWidth(text) - 10, 10, 0xffffff);
            }
            {
                String text = "PowerRating: " + ((int) data.getPowerRatingManager(data.getActiveBendingId()).getRating(null));
                FontRenderer fr = mc.fontRenderer;

                drawString(fr, text, res.getScaledWidth() - fr.getStringWidth(text) - 10, 20, 0xffffff);
            }
        }
    }

}
