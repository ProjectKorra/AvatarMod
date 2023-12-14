package com.crowsofwar.avatar.client.render.lightning.main;

import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

public class ModDamageSource extends DamageSource {

	public static DamageSource cheater = (new DamageSource("cheater")).setDamageIsAbsolute().setDamageBypassesArmor().setDamageAllowedInCreativeMode();

	public static DamageSource blast = (new DamageSource("blast")).setExplosion().setDamageBypassesArmor().setDamageIsAbsolute();
	public static DamageSource electricity = (new DamageSource("electricity")).setDamageIsAbsolute().setDamageBypassesArmor();
	public static DamageSource crucible = new DamageSource("crucible").setDamageIsAbsolute().setDamageBypassesArmor();
	
	public ModDamageSource(String p_i1566_1_) {
		super(p_i1566_1_);
	}

//    public static DamageSource causeDischargeDamage(EntityDischarge p_76353_0_, Entity p_76353_1_)
//    {
//        return (new EntityDamageSourceIndirect("electrified", p_76353_0_, p_76353_1_)).setDamageBypassesArmor();
//    }
    
    public static boolean getIsDischarge(DamageSource source) {
    	if(source instanceof EntityDamageSourceIndirect)
    	{
    		return ((EntityDamageSourceIndirect)source).damageType.equals("electrified");
    	}
    	return false;
    }
}
