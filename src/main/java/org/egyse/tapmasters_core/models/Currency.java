package org.egyse.tapmasters_core.models;

public enum Currency {
    CLICK{
        @Override
        public String toString() { return "&f&lClick"; }
    },
    MONEY{
        @Override
        public String toString() { return "&2&lMoney"; }
    },
    GEM{
        @Override
        public String toString() { return "&b&lGem"; }
    },
    TOKEN{
        @Override
        public String toString() { return "&e&lToken"; }
    },
    PRESTIGE{
        @Override
        public String toString() { return "&6&lPrestige"; }
    },
    PRESTIGE_POINT{
        @Override
        public String toString() { return "&1&lPrestige Point"; }
    }
}
