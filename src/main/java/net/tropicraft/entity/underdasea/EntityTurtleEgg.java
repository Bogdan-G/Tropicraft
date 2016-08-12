package net.tropicraft.entity.underdasea;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityTurtleEgg extends EntityLiving {

    public int hatchingTime;
    public double rotationRand;
    
    public EntityTurtleEgg(World par1World) {
        super(par1World);
        setSize(.1F, .1F);
        hatchingTime = 0;
        rotationRand = 10;
        ignoreFrustumCheck = true;
    }
    
    public void onUpdate() {
        super.onUpdate();        
        rotationYaw = 0;
        
        // Once it has lived 400 ticks (20 seconds (20 sec * 20 ticks = 400))
        // Start the hatch countdown
        if (ticksExisted % 400 == 0) {
            hatchingTime = 360;
        }
        
        // So that we don't try hatching before the countdown has started
        // But if we are starting the process of hatching (not at the end yet), spin and decrement counter
        if (hatchingTime != 0) {
            // Do crazy spinny stuff
            rotationRand += (float)Math.PI/2 * worldObj.rand.nextFloat();
            hatchingTime--;
            
            // Hatch time!
            if (hatchingTime == 1) {
                if (!worldObj.isRemote) {
                    EntitySeaTurtle babyturtle = new EntitySeaTurtle(worldObj, .2F);
                    double d3 = this.posX;
                    double d4 = this.posY;
                    double d5 = this.posZ;
                    babyturtle.setLocationAndAngles(d3, d4, d5, 0.0F, 0.0F);
                    worldObj.spawnEntityInWorld(babyturtle);
                    this.setDead();
                }
                
                for (int i = 0; i < 8; i++) {
                    worldObj.spawnParticle("snowballpoof", posX, posY, posZ,
                            0.0D, 0.0D, 0.0D);
                }
            }
            
            // Stop doing crazy spinny stuff
            if (hatchingTime == 0) {
                rotationRand = 0;
            }
        }
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(2.0D);
    }
}
