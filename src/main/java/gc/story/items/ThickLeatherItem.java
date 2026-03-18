package gc.story.items;

import net.minecraft.item.Item;

public class ThickLeatherItem extends Item {

    private static final int LEATHER_COLOR = 0x5D3A1A; // Темно-коричневый

    public ThickLeatherItem(Settings settings) {
        super(settings);
    }

    public static int getColor() {
        return LEATHER_COLOR;
    }
}