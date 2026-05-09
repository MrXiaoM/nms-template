package nms.impl.v26_1;

import net.minecraft.world.entity.item.ItemEntity;
import nms.impl.ItemAdapter;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.entity.Item;

import java.lang.reflect.Field;

import static nms.impl.Versions.getInt;

public class ItemAdapterImpl implements ItemAdapter {
    private final Field itemHealth;
    public ItemAdapterImpl() throws ReflectiveOperationException {
        itemHealth = ItemEntity.class.getDeclaredField("health");
        itemHealth.setAccessible(true);
    }
    @Override
    public int getHealth(Item item) {
        CraftItem craft = (CraftItem) item;
        ItemEntity nms = craft.getHandle();
        return getInt(itemHealth, nms);
    }
}
