package com.camellias.mysticalmetallurgy.common.capability.HotCarry;

public interface IHotCarry
{
    void carryHot();

    void carryCool();

    int getHotCarryTime();

    boolean isCarryingHot();

    void set(int i);

    class Impl implements IHotCarry
    {
        private int hotCarryTime = 0;

        @Override
        public void carryHot() { hotCarryTime++; }

        public void carryCool()
        {
            if (hotCarryTime - 1 >= 0)
                hotCarryTime--;
        }

        @Override
        public int getHotCarryTime() { return hotCarryTime; }

        @Override
        public boolean isCarryingHot() { return hotCarryTime > 0; }

        @Override
        public void set(int i) { hotCarryTime = i; }
    }
}
