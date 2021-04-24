package com.crowsofwar.avatar.entity.data;

//Used for taking water arc and such out of source entities or filling water pouches
public interface IWaterEntity {

    public int getCharges();

    public int maxCharges();

    public int requiredCharges();

    public boolean isSource();
}
