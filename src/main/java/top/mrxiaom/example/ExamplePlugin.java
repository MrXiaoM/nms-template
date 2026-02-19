package top.mrxiaom.example;

import nms.impl.Versions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin implements Listener {

    public void onLoad() {
        // 初始化 NMS，失败时报错，阻止插件加载
        if (!Versions.init(getLogger())) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.isCancelled()) return;
        Entity entity = e.getEntity();
        if (entity instanceof Item) {
            Item item = (Item) entity;
            int health = Versions.getItemAdapter().getHealth(item);
            double damage = e.getDamage();
            int finalHealth = (int) ((float) health - damage);
            if (finalHealth <= 0) {
                String name = item.getItemStack().getType().name();
                EntityDamageEvent.DamageCause cause = e.getCause();
                getLogger().info("物品 " + name + " 因 " + cause.name() + " 而被销毁");
            }
        }
    }
}
