package nms.impl.paper;

import nms.impl.ItemAdapter;
import org.bukkit.entity.Item;

public class ItemAdapterImpl implements ItemAdapter {
    @Override
    public int getHealth(Item item) {
        return item.getHealth();
    }
}
