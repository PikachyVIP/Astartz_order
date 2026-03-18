package gc.story.items;

import net.minecraft.item.Item;

public class DubLeatherItem extends Item {

    private static final int LEATHER_COLOR = 0x8B5A2B;

    public DubLeatherItem(Settings settings) {
        super(settings);
    }


    public static int getColor() {
        return LEATHER_COLOR;
    }
}