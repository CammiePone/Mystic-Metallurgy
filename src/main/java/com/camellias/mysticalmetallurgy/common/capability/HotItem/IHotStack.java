package com.camellias.mysticalmetallurgy.common.capability.HotItem;

public interface IHotStack
{
    boolean isHot();

    void setTemp(int t);

    int getTemp();

    boolean canCool();

    void setCanCool(boolean can);

    class Impl implements IHotStack
    {
        private final int normalTemp = 300;
        private int temp = normalTemp;

        private boolean canCool = true;

        @Override
        public boolean isHot() { return temp > normalTemp; }

        @Override
        public void setTemp(int t) { temp = t; }

        @Override
        public int getTemp() { return temp; }

        @Override
        public boolean canCool() { return canCool; }

        @Override
        public void setCanCool(boolean canCool) { this.canCool = canCool; }
    }
}
