package nms.impl.v1_20_R4;

import net.minecraft.world.entity.item.EntityItem;
import nms.impl.ItemAdapter;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftItem;
import org.bukkit.entity.Item;

import java.lang.reflect.Field;

import static nms.impl.Versions.getInt;

public class ItemAdapterImpl implements ItemAdapter {
    private final Field itemHealth;
    public ItemAdapterImpl() throws ReflectiveOperationException {
        itemHealth = EntityItem.class.getDeclaredField("k");
        itemHealth.setAccessible(true);
    }
    @Override
    public int getHealth(Item item) {
        CraftItem craft = (CraftItem) item;
        EntityItem nms = craft.getHandle();
        return getInt(itemHealth, nms);
    }
}
