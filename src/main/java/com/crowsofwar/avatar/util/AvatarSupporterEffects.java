package com.crowsofwar.avatar.util;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarSupporterEffects {

    public static List<String> supporters = new ArrayList<>();

    static {
        supporters.add("MirroredPanda");
        supporters.add("VroFredo");
        supporters.add("Josephinitis");
        supporters.add("_Frozti_");
        supporters.add("starwarsfreak219");
        supporters.add("MultiPS3Gamer");
        supporters.add("WaferNafer");
        supporters.add("MadMiningMaster");
        supporters.add("Gyroplex_14");
    }

    @SubscribeEvent
    public static void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() != null) {
            EntityLivingBase entity = event.getEntityLiving();
            BendingData data = BendingData.getFromEntity(entity);

            if (data != null) {
                if (Bender.get(entity) != null) {
                    Bender bender = Bender.get(entity);
                    int totalLevel = 0;

                    if (entity instanceof EntityPlayer) {
                        if (supporters.contains(((EntityPlayer) entity).getDisplayNameString())) {
                            BendingStyle style = data.getActiveBending();
                            if (style != null) {
                                List<Ability> abilities = style.getAllAbilities();
                                abilities = abilities.stream().filter(ability -> Objects.requireNonNull(AbilityData.get(entity, ability.getName())).getLevel() > -1)
                                        .collect(Collectors.toList());
                                for (Ability ability : abilities) {
                                    AbilityData aD = AbilityData.get(entity, ability.getName());
                                    if (aD != null && aD.getLevel() > -1) {
                                        totalLevel += aD.getLevel() + 1;
                                    }
                                }

                                int maxLevel = abilities.size() * 4;
                                int level = Math.min(3, (int) ((float) totalLevel / maxLevel * 4));

                                float mult = 0.5F + (0.5F / 4F * (level + 1F));

                               /* if (entity.world.isRemote) {
                                    ParticleBuilder builder = ParticleBuilder.instance.pos(entity.getPositionVector());

                                    switch (style.getName()) {
                                        default:
                                            break;
                                        case "firebending":
                                            builder = ParticleBuilder.create(ParticleBuilder.Type.FLASH);
                                            builder.element(new Firebending()).spawnEntity(entity).clr(CLIENT_CONFIG.fireRenderSettings.fireR,
                                                    CLIENT_CONFIG.fireRenderSettings.fireG, CLIENT_CONFIG.fireRenderSettings.fireB, 120);
                                            break;
                                        case "airbending":
                                            builder = ParticleBuilder.create(ParticleBuilder.Type.FLASH);
                                            builder.element(new Airbending()).spawnEntity(entity);
                                            break;
                                        case "waterbending":
                                            break;
                                        case "earthbending":
                                            break;
                                        case "icebending":
//                                            builder = ParticleBuilder.create(ParticleBuilder.Type.ICE);
//                                            builder.element(new Icebending()).spawnEntity(entity);
                                            break;
                                        case "sandbending":
                                            builder = ParticleBuilder.create(ParticleBuilder.Type.FLASH);
                                            builder.element(new Sandbending()).spawnEntity(entity);
                                            break;
                                        case "combustionbending":
                                            break;
                                        case "lightningbending":
                                            builder = ParticleBuilder.create(ParticleBuilder.Type.LIGHTNING);
                                            builder.element(new Lightningbending()).spawnEntity(entity);
                                            break;

                                    }
                                    builder.spawn(entity.world);
                                }**/
                            }
                        }
                    }
                }
            }
        }
    }
}
