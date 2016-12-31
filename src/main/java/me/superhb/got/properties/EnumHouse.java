package me.superhb.got.properties;

import net.minecraft.util.IStringSerializable;

public enum EnumHouse implements IStringSerializable {
    ARRYN(0, "arryn", "House Arryn", "As High As Honor"),
    BARATHEON(1, "baratheon", "House Baratheon", "Ours Is The Fury"),
    BOLTON(2, "bolton", "House Bolton", "Our Blades Are Sharp"),
    BROTHERHOOD(3, "brotherhood", "Brotherhood Without Banners", ""),
    FAITH(4, "faith", "Faith Militant", ""),
    FREE(5, "free", "Free Folk", ""),
    FREY(6, "frey", "House Frey", ""),
    GREYJOY(7, "greyjoy", "House Greyjoy", "We Do Not Sow"),
    KARSTARK(8, "karstark", "House Karstark", "The Sun of Winter"),
    LANNISTER(9, "lannister", "House Lannister", "Hear Me Roar"),
    MARTELL(10, "martell", "House Martell", "Unbowed, Unbent, Unbroken"),
    MORMONT(11, "mormont", "House Mormont", "Here We Stand"),
    NIGHT(12, "night", "Night's Watch", ""),
    STARK(13, "stark", "House Stark", "Winter Is Coming"),
    TARGARYEN(14, "targaryen", "House Targaryen", "Fire And Blood"),
    TULLY(15, "tully", "House Tully", "Family, Duty, Honor"),
    TYRELL(16, "tyrell", "House Tyrell", "Growing Strong"),
    FLORENT(17, "florent", "House Florent", ""),
    REDWYNE(18, "redwyne", "House Redwyne", ""),
    UMBER(19, "umber", "House Umber", "");

    private int id;
    private String nid;
    private String name;
    private String moto;

    EnumHouse (int id, String nid, String name, String moto) {
        this.id = id;
        this.nid = nid;
        this.name = name;
        this.moto = moto;
    }

    @Override
    public String getName () {
        return nid;
    }

    public static EnumHouse getValue (int id) {
        return values()[id];
    }

    public static String getNid (int id) {
        return values()[id].nid;
    }

    public static String getName (int id) {
        return values()[id].name;
    }
}
