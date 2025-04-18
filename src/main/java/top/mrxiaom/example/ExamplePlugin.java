package top.mrxiaom.example;

import nms.impl.ILivingEntity;
import nms.impl.Versions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        // 初始化 NMS，失败时卸载插件
        if (!Versions.init(getLogger())) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity raw = e.getEntity();
        if (raw instanceof LivingEntity) {
            ILivingEntity nms = Versions.getLivingEntity();
            LivingEntity entity = (LivingEntity) raw;
            int expToDrop = nms.getExpToDrop(entity);
            e.getDamager().sendMessage("经验 " + expToDrop);
        }
    }
}
