package nms.impl.v1_16_R3;

import net.minecraft.server.v1_16_R3.EntityLiving;
import nms.impl.ILivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

public class LivingEntityImpl implements ILivingEntity {
    @Override
    public int getExpToDrop(LivingEntity entity) {
        CraftLivingEntity craft = (CraftLivingEntity) entity;
        EntityLiving nms = craft.getHandle();
        return nms.expToDrop;
    }
}
