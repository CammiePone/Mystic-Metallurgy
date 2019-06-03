package com.camellias.mysticalmetallurgy.common.capability.HotItem;

public interface IHotStack
{
    boolean isHot();

    void setHot();
    void setCold();

    class Impl implements IHotStack
    {
        private boolean isHot = false;

        @Override
        public boolean isHot() { return isHot; }

        @Override
        public void setHot() { isHot = true; }

        @Override
        public void setCold() { isHot = false; }
    }
}
