package nms.impl.v1_18_R2;

import net.minecraft.world.entity.EntityLiving;
import nms.impl.ILivingEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

public class LivingEntityImpl implements ILivingEntity {
    @Override
    public int getExpToDrop(LivingEntity entity) {
        CraftLivingEntity craft = (CraftLivingEntity) entity;
        EntityLiving nms = craft.getHandle();
        return nms.expToDrop;
    }
}
