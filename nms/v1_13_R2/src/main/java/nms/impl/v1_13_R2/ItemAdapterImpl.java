package nms.impl.v1_13_R2;

import net.minecraft.server.v1_13_R2.EntityItem;
import nms.impl.ItemAdapter;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftItem;
import org.bukkit.entity.Item;

import java.lang.reflect.Field;

import static nms.impl.Versions.getInt;

public class ItemAdapterImpl implements ItemAdapter {
    private final Field itemHealth;
    public ItemAdapterImpl() throws ReflectiveOperationException {
        itemHealth = EntityItem.class.getDeclaredField("e");
        itemHealth.setAccessible(true);
    }
    @Override
    public int getHealth(Item item) {
        CraftItem craft = (CraftItem) item;
        EntityItem nms = (EntityItem) craft.getHandle();
        return getInt(itemHealth, nms);
    }
}
