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
package com.crowsofwar.avatar.config;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author CrowsOfWar
 */
public class ConfigClient {

    public static ConfigClient CLIENT_CONFIG = new ConfigClient();
    @Load
    public final float bendingCycleAlpha = 0.75f;
    @Load
    public final boolean displayGetBendingMessage = true;
    @Load
    public final float radialMenuAlpha = 0.75f;
    @Load
    public final float chiBarAlpha = 0.625f;
    //For some reason if it's not final it won't work
    //Controls whether or not to show the get bending message
    //when you press the use bending key
    @Load
    public final boolean useCustomParticles = true;
    @Load
    private final Map<String, Integer> nameKeymappings = new HashMap<>();
    @Load
    private final Map<String, Boolean> nameConflicts = new HashMap<>();
    public Map<Ability, Integer> keymappings = new HashMap<>();
    public Map<Ability, Boolean> conflicts = new HashMap<>();
    @Load
    public ShaderSettings shaderSettings = new ShaderSettings();
    @Load
    public ActiveBendingSettings activeBendingSettings = new ActiveBendingSettings();
    @Load
    public ChiBarSettings chiBarSettings = new ChiBarSettings();
    @Load
    public FireRenderSettings fireRenderSettings = new FireRenderSettings();
    @Load
    public AirRenderSettings airRenderSettings = new AirRenderSettings();
    @Load
    public ParticleRenderSettings particleSettings = new ParticleRenderSettings();

    public static void load() {
        ConfigLoader.load(CLIENT_CONFIG, "avatar/cosmetic.yml");

        //CLIENT_CONFIG.keymappings.clear();
        Set<Map.Entry<String, Integer>> entries = CLIENT_CONFIG.nameKeymappings.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            Ability ability = null;
            for (Ability a : Abilities.all()) {
                if (a.getName().equals(entry.getKey())) {
                    ability = a;
                    break;
                }
            }
            if (ability != null) {
                CLIENT_CONFIG.keymappings.put(ability, entry.getValue());
            }
        }
        //CLIENT_CONFIG.conflicts.clear();
        Set<Map.Entry<String, Boolean>> entries2 = CLIENT_CONFIG.nameConflicts.entrySet();
        for (Map.Entry<String, Boolean> entry : entries2) {
            Ability ability = null;
            for (Ability a : Abilities.all()) {
                if (a.getName().equals(entry.getKey())) {
                    ability = a;
                    break;
                }
            }
            if (ability != null) {
                CLIENT_CONFIG.conflicts.put(ability, entry.getValue());
            }
        }

    }

    public static void save() {

        //CLIENT_CONFIG.nameKeymappings.clear();
        Set<Map.Entry<Ability, Integer>> entries = CLIENT_CONFIG.keymappings.entrySet();
        for (Map.Entry<Ability, Integer> entry : entries) {
            CLIENT_CONFIG.nameKeymappings.put(entry.getKey().getName(), entry.getValue());
        }
        //CLIENT_CONFIG.nameConflicts.clear();
        Set<Map.Entry<Ability, Boolean>> entries2 = CLIENT_CONFIG.conflicts.entrySet();
        for (Map.Entry<Ability, Boolean> entry : entries2) {
            CLIENT_CONFIG.nameConflicts.put(entry.getKey().getName(), entry.getValue());
        }

        ConfigLoader.save(CLIENT_CONFIG, "avatar/cosmetic.yml");
    }

    public static final class ShaderSettings {

        @Load
        public final boolean useSlipstreamShaders = false;

        @Load
        public final boolean useCleanseShaders = false;

        @Load
        public final boolean useRestoreShaders = false;

        @Load
        public final boolean useImmolateShaders = false;

        @Load
        public final boolean bslActive = false;

        @Load
        public final boolean sildursActive = false;
    }

    public static final class ActiveBendingSettings {

        @Load
        public final boolean shouldBendingMenuRender = true;
        //For some reason if it's not final it won't work
        //Determines if element menu should render at all

        @Load
        public final boolean shouldBendingMenuDisappear = false;
        //For some reason if it's not final it won't work
        //Makes the menu disappear after the duration

        @Load
        public final int bendingMenuDuration = 200;
        //If the menu should disappear, how long it should take before disappearing

        @Load
        public final int middleXPosition = 0, rightXPosition = 50, leftXPosition = -35;
        //The x pos of the middle/active bending, the bending icon on the right, and the bending icon on the left

        @Load
        public final int middleYPosition = -30, rightYPosition = 5, leftYPosition = 5;
        //The xp pos of the bending icon on the left

        @Load
        public final float middleBendingWidth = 50, leftBendingWidth = 35, rightBendingWidth = 35;

        @Load
        public final float middleBendingHeight = 50, leftBendingHeight = 35, rightBendingHeight = 35;

    }

    public static final class ChiBarSettings {
        @Load
        public final boolean shouldChibarRender = true;

        @Load
        public final boolean shouldChiNumbersRender = true;

        @Load
        public final boolean shouldChiMenuDisappear = false;

        @Load
        public final int chibarDuration = 200;

        @Load
        public final double widthScale = 1.1F, heightScale = 1.1F;

        @Load
        public final int xPos = 115, yPos = 3;

    }

    public static final class FireRenderSettings {

        @Load
        public final boolean flameStrikeSphere = false;

        @Load
        public final boolean showFlameStrikeOrb = false;

        @Load
        public final boolean solidFlamethrowerParticles = false;

        @Load
        public final boolean solidFlameStrikeParticles = true;

        //Only used for supporters
        @Load
        public final int fireR = 255, fireG = 50, fireB = 10;

        //Only used for supporters
        @Load
        public final int lightningR = 135, lightningG = 255, lightningB = 252;


    }

    public static final class AirRenderSettings {
        @Load
        public final boolean airBurstSphere = true;

    }

    public static final class ParticleRenderSettings {
        @Load
        public final boolean realisticFlashParticles = true;

        @Load
        public final boolean realisticLightningParticles = true;

        @Load
        public final boolean collideWithBlocks = true;

        @Load
        public final boolean collideWithParticles = true;

        @Load
        public final boolean releaseShaderOnFlashParticleRender = true;

    }

}
