package me.superhb.got.entity.character;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityHodor extends EntityCreature {
    public EntityHodor (World world) {
        super(world);
    }

    @Override
    protected void applyEntityAttributes () {
        super.applyEntityAttributes();

        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D); // original 20
        getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(4.5D); // original 4

        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.5D); // original 2
    }

    @Override
    public boolean attackEntityAsMob (Entity target) {
        float f = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        int i = 0;

        if (target instanceof EntityLivingBase) {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase)target).getCreatureAttribute());
            i += EnchantmentHelper.getKnockbackModifier(this);
        }

        boolean flag = target.attackEntityFrom(DamageSource.causeMobDamage(this), f);

        if (flag) {
            if (i > 0 && target instanceof EntityLivingBase) {
                ((EntityLivingBase)target).knockBack(this, (float)i * 0.5F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);

            if (j > 0) target.setFire(j * 4);

            if (target instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer)target;
                ItemStack itemstack = this.getHeldItemMainhand();
                ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.field_190927_a;

                if (!itemstack.func_190926_b() && !itemstack1.func_190926_b() && itemstack.getItem() instanceof ItemAxe && itemstack1.getItem() == Items.SHIELD) {
                    float f1 = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

                    if (this.rand.nextFloat() < f1) {
                        entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
                        this.worldObj.setEntityState(entityplayer, (byte)30);
                    }
                }
            }
            this.applyEnchantments(this, target);
        }
        return flag;
    }
}
