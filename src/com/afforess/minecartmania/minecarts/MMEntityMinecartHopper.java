package com.afforess.minecartmania.minecarts;

//CraftBukkit start
import java.util.List;

import net.minecraft.server.v1_7_R3.Block;
import net.minecraft.server.v1_7_R3.BlockMinecartTrack;
import net.minecraft.server.v1_7_R3.BlockMinecartTrackAbstract;
import net.minecraft.server.v1_7_R3.Blocks;
import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.EntityLiving;
import net.minecraft.server.v1_7_R3.EntityMinecartAbstract;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.World;
import net.minecraft.server.v1_7_R3.WorldServer;

import org.bukkit.Location;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.debug.Logger;
//CraftBukkit end
import com.afforess.minecartmania.events.MinecartClickedEvent;

public class MMEntityMinecartHopper extends net.minecraft.server.v1_7_R3.EntityMinecartHopper implements IMMEntity{

	//NMS
	private boolean a;
	private String b;
	private static final int[][][] matrix = new int[][][] { { { 0, 0, -1}, { 0, 0, 1}}, { { -1, 0, 0}, { 1, 0, 0}}, { { -1, -1, 0}, { 1, 0, 0}}, { { -1, 0, 0}, { 1, -1, 0}}, { { 0, 0, -1}, { 0, -1, 1}}, { { 0, -1, -1}, { 0, 0, 1}}, { { 0, 0, 1}, { 1, 0, 0}}, { { 0, 0, 1}, { -1, 0, 0}}, { { 0, 0, -1}, { -1, 0, 0}}, { { 0, 0, -1}, { 1, 0, 0}}};
	private int d;
	private double e;
	private double f;
	private double g;
	private double h;
	private double i;


	//Mine
	private final double defaultpassengerFriction =  0.996999979019165D;
	private final double defaultemptyFriction  = 0.9599999785423279D;
	private final double defaultgravity = 0.03999999910593033D;
	private final double DefaultslopeSpeed = 0.0078125D;
	private final double defaultDerailedFriction  = 0.5;

	public double derailedFrictioPercent = 100;
	public double passengerFrictionPercent = 100;
	public double emptyFrictionPercent = 100;
	public double slopeSpeedPercent = 100;
	public double MaxPushSpeedPercent = 100;
	public double GravityPercent = 100;

	public boolean onPoweredPoweredRail;
	public boolean onUnpoweredPoweredRail;

	private Block blockbeneath;
	private int blockBeneathData;

	public boolean frozen;

	public boolean onRails;

	public boolean moving;
	public boolean onSlope;
	public boolean uphill;
	public boolean downhill;

	public boolean magnetic = false;
	public boolean collisions = false;


	public boolean onNormalRail(){
		return onRails && !onPoweredPoweredRail && !onUnpoweredPoweredRail;
	}


	private boolean isNew = true;

	public MMEntityMinecartHopper(World world) {
		super(world);
		this.a = false;
		this.k = true;
		this.a(0.98F, 0.7F);
		this.height = this.length / 2.0F;

	}


	private int findRailsYOffset(){

		int xBlock = MathHelper.floor(this.locX);
		int yBlock = MathHelper.floor(this.locY);
		int zBlock = MathHelper.floor(this.locZ);

		for(int i = -1; i <=2 ;i++){
			if (BlockMinecartTrack.a(this.world.getType(xBlock, yBlock + i, zBlock))) {
				return i ;
			}
		}

		return 0;

	}

	private int slopedata = 0;

	private void constrainSpeed(){
		double ts = Math.sqrt(motX*motX + motZ*motZ);

		if (ts > maxSpeed){
			motX = motX / ts * maxSpeed;
			motZ =  motZ / ts * maxSpeed;
		}

	}

	private void setPArams(){
		int xBlock = MathHelper.floor(this.locX);
		int yBlock = MathHelper.floor(this.locY);
		int zBlock = MathHelper.floor(this.locZ);
		blockbeneath = this.world.getType(xBlock, yBlock, zBlock);
		blockBeneathData = this.world.getData(xBlock, yBlock, zBlock);
		
		onRails = 	BlockMinecartTrackAbstract.a(blockbeneath) && this.motY <=0;
		onPoweredPoweredRail = false;
		onUnpoweredPoweredRail = false;	

		if (onRails && (blockbeneath == Blocks.GOLDEN_RAIL)) {
			onPoweredPoweredRail = (blockBeneathData & 8) != 0;
			onUnpoweredPoweredRail = !onPoweredPoweredRail;
		}

		downhill = false;
		uphill = false;
		onSlope = false;
		if(onRails){

			slopedata = blockBeneathData;
			if (((BlockMinecartTrackAbstract) blockbeneath).e()) {
				slopedata &= 7;
			}

			if (slopedata == 2) {
				onSlope = true;
				downhill = motX <0;
				uphill = !downhill;
			}
			if (slopedata == 3) {
				onSlope = true;
				downhill = motX >0;
				uphill = !downhill;
			}

			if (slopedata == 4) {
				onSlope = true;
				downhill = motZ > 0;
				uphill = !downhill;
			}

			if (slopedata == 5) {
				onSlope = true;
				downhill = motZ <0;
				uphill = !downhill;
			}	
		}


	}

	@Override
	public void h() {

		move();		

	}

	public void move() {

		//		com.afforess.minecartmaniacore.debug.MinecartManiaLogger.info(" j start " + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);

		// CraftBukkit start
		double prevX = this.locX;
		double prevY = this.locY;
		double prevZ = this.locZ;
		float prevYaw = this.yaw;
		float prevPitch = this.pitch;
		// CraftBukkit end


		if (this.getType() > 0) {
			this.c(this.getType() - 1);
		}

		if (this.getDamage() > 0) {
			this.setDamage(this.getDamage() - 1);
		}

		if (this.locY < -64.0D) {
			this.F();
		}

		//		if (this.h() && this.random.nextInt(4) == 0) {
		//			this.world.addParticle("largesmoke", this.locX, this.locY + 0.8D, this.locZ, 0.0D, 0.0D, 0.0D);
		//		}

		int i;

		if (!this.world.isStatic && this.world instanceof WorldServer) {
			this.world.methodProfiler.a("portal");
			//	MinecraftServer minecraftserver = ((WorldServer) this.world).getMinecraftServer();

			i = this.C();
			if (this.an) {
				if (true ){// ||minecraftserver.getAllowNether()) { // CraftBukkit - multi-world should still allow teleport even if default vanilla nether disabled
					if (this.vehicle == null && this.ao++ >= i) {
						this.ao = i;
						this.portalCooldown = this.ah();
						byte b0;

						if (this.world.worldProvider.dimension == -1) {
							b0 = 0;
						} else {
							b0 = -1;
						}

						this.b(b0);
					}

					this.an = false;
				}
			} else {
				if (this.ao > 0) {
					this.ao -= 4;
				}

				if (this.ao < 0) {
					this.ao = 0;
				}
			}

			if (this.portalCooldown > 0) {
				--this.portalCooldown;
			}

			this.world.methodProfiler.b();
		}

		if (frozen) {
			if (this.passenger != null && this.passenger instanceof EntityLiving) {
				// there is a passenger	
				double	passengerSpeed = ((EntityLiving)this.passenger).be;

				if (passengerSpeed > 0 ) {
					MinecartClickedEvent mce = new MinecartClickedEvent(com.afforess.minecartmania.entity.MinecartManiaWorld.getMMMinecart((Minecart) this.getBukkitEntity()));
					MinecartMania.callEvent(mce);
				}
			}
			return;
		}

		if (this.world.isStatic) {
			//	com.afforess.minecartmaniacore.debug.MinecartManiaLogger.info(" j static " + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);
			if (this.d > 0) {
				double d0 = this.locX + (this.e - this.locX) / (double) this.d;
				double d1 = this.locY + (this.f - this.locY) / (double) this.d;
				double d2 = this.locZ + (this.g - this.locZ) / (double) this.d;
				double d3 = MathHelper.g(this.h - (double) this.yaw);

				this.yaw = (float) ((double) this.yaw + d3 / (double) this.d);
				this.pitch = (float) ((double) this.pitch + (this.i - (double) this.pitch) / (double) this.d);
				--this.d;
				this.setPosition(d0, d1, d2);
				this.b(this.yaw, this.pitch);
			} else {
				this.setPosition(this.locX, this.locY, this.locZ);
				this.b(this.yaw, this.pitch);
			}
		}

		else {

			//TODO: Clean this unholy mess up.

			//TODO: figure our what happens when the speed, location, or frozen state is changed while iterating this.

			//constrain.

			Logger.motion(" incomming speed x:" + motX + " z:" + motZ);

			constrainSpeed();

			// move in  increments.
			double spd= Math.sqrt(motX*motX + motZ*motZ);



			double speeddelta = 0;
			double incspd= Math.sqrt(motX*motX + motZ*motZ);

			//move in  increments.
			double ii=Math.floor(incspd/.4) ;
			if(ii == 0) ii = 1;
			double itspd =  incspd/ii;

			while (itspd >= .4){
				++ii;
				itspd = Math.abs(incspd)/ii;
			} 

			double itx=0;
			double itz=0;

			for(int derp = 0; derp < ii;derp++) {		

				//setup the iteration speed.
				if (Math.abs(motX) > Math.abs(motZ)){
					itx = itspd * ((motX < 0) ? -1 :1);
					itz = 0;
				}
				else if (Math.abs(motZ) > Math.abs(motX)){
					itz = itspd * ((motZ < 0) ? -1 :1);		
					itx = 0;
				}
				else {
					itz = itspd/Math.sqrt(2) * (motZ < 0 ? -1 :1);	
					itx =itspd/Math.sqrt(2) * (motX < 0 ? -1 :1);
				}	


				Vector res = changePosition(itx, itz);
				//changeposition may move the speed from x to z, but *shouldnt* ever change total speed.

				double ts = Math.sqrt(motX*motX + motZ*motZ);

				//handle moving total speed between X and Z
				if (Math.abs(res.getX()) > Math.abs(res.getZ())){
					motX = ts * (res.getX() < 0 ? -1 :1);	
					motZ = 0;
				}
				else if (Math.abs(res.getZ()) > Math.abs(res.getX())){
					motZ = ts *  (res.getZ() < 0 ? -1 :1);
					motX = 0;
				}
				else{		
					motZ = ts/Math.sqrt(2) *  (res.getZ() < 0 ? -1 :1);	
					motX =ts/Math.sqrt(2) * (res.getX() < 0 ? -1 :1);		
				}


				//Fire the block event(s). The speed may be modified.
				this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleUpdateEvent((Vehicle) this.getBukkitEntity()));

				// process what might have changed.
				constrainSpeed();

				ts = Math.sqrt(motX*motX + motZ*motZ);

				speeddelta += (spd - ts); 

				if(speeddelta >= spd || frozen) break; //prematurely lost all speed.

			} 

			spd = Math.sqrt(motX*motX + motZ*motZ);

			Logger.motion(" outgoing speed x:" + motX + " z:" + motZ + "spd: " + spd + " delta: " + speeddelta);

			//

		}

		//modify these speeds only once per tick, cause physics.

	

		this.motY -= defaultgravity * GravityPercent / 100;

		//slopes
		if (this.onRails){

			if (slopedata == 2) {
				this.motX -= DefaultslopeSpeed * slopeSpeedPercent/100;
			}

			if (slopedata == 3) {
				this.motX += DefaultslopeSpeed * slopeSpeedPercent/100;
			}

			if (slopedata == 4) {
				this.motZ += DefaultslopeSpeed * slopeSpeedPercent/100;
			}

			if (slopedata == 5) {
				this.motZ -=DefaultslopeSpeed * slopeSpeedPercent/100;
			}

			//frictions
			if(slowWhenEmpty && this.passenger == null){
				this.motX *= ((1-this.defaultemptyFriction) * (100-this.emptyFrictionPercent) / 100)  + this.defaultemptyFriction;
				this.motZ *= ((1-this.defaultemptyFriction) * (100-this.emptyFrictionPercent) / 100)  + this.defaultemptyFriction;
			}
			else{
				this.motX *= ((1-this.defaultpassengerFriction) * (100-this.passengerFrictionPercent) / 100)  + this.defaultpassengerFriction;
				this.motZ *= ((1-this.defaultpassengerFriction) * (100-this.passengerFrictionPercent) / 100)  + this.defaultpassengerFriction;
			}
		}
		else {
			//Don't apply friction if is in the block above a rail, cause i think onground retruns true.
			if (this.onGround) {
				this.motX *=  ((1-this.defaultDerailedFriction) * (100-this.derailedFrictioPercent) / 100)  + this.defaultDerailedFriction;
				this.motZ *=   ((1-this.defaultDerailedFriction) * (100-this.derailedFrictioPercent) / 100)  + this.defaultDerailedFriction;
			}
		}


		//		//powered cart	
		//		if (this.type == 2) {
		//			double d18 = this.b * this.b + this.c * this.c;
		//			if (d18 > 1.0E-4D) {
		//				d18 = (double) MathHelper.sqrt(d18);
		//				this.b /= d18;
		//				this.c /= d18;
		//				double d19 = 0.04D;
		//
		//				this.motX *= 0.800000011920929D;
		//				//	this.motY *= 0.0D;
		//				this.motZ *= 0.800000011920929D;
		//				this.motX += this.b * d19;
		//				this.motZ += this.c * d19;
		//			} else {
		//				//powered minecart friction with no fuel?		
		//				this.motX *= 0.8999999761581421D;
		//				//	this.motY *= 0.0D;
		//				this.motZ *= 0.8999999761581421D;
		//			}
		//		}


		//stop motion if very slow.
		double d12 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ + this.motY * this.motY);
		if (d12 < 0.0001D) {
			this.motX *= 0.0D;
			//	this.motY *= 0.0D;
			this.motZ *= 0.0D;
		} 


		Logger.motion(" Final speed x:" + motX + " z:" + motZ + "onground: " + this.onGround + " onrails:" + this.onRails);


		this.H();
		this.pitch = 0.0F; //I think minecart tilting  is handled on the client only.


		//turn
		double d23 = this.lastX - this.locX;
		double d24 = this.lastZ - this.locZ;
		if (d23 * d23 + d24 * d24 > 0.001D) {
			this.yaw = (float) (Math.atan2(d24, d23) * 180.0D / 3.141592653589793D);
			if (this.a) {
				this.yaw += 180.0F;
			}
		}

		double d25 = (double) MathHelper.g(this.yaw - this.lastYaw);

		if (d25 < -170.0D || d25 >= 170.0D) {
			this.yaw += 180.0F;
			this.a = !this.a;
		}

		this.b(this.yaw, this.pitch);

		// CraftBukkit start
		org.bukkit.World bworld = this.world.getWorld();
		Location from = new Location(bworld, prevX, prevY, prevZ, prevYaw, prevPitch);
		Location to = new Location(bworld, this.locX, this.locY, this.locZ, this.yaw, this.pitch);
		Vehicle vehicle = (Vehicle) this.getBukkitEntity();

		if (!isNew) {
			if (!from.equals(to)) {
				this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleMoveEvent(vehicle, from, to));
			}	
		}
		else isNew = false;

		constrainSpeed();

		// CraftBukkit end

		@SuppressWarnings("rawtypes")
		List list = this.world.getEntities(this, this.boundingBox.grow(0.20000000298023224D, 0.0D, 0.20000000298023224D));

		if (list != null && !list.isEmpty()) {
			for (int l1 = 0; l1 < list.size(); ++l1) {
				Entity entity = (Entity) list.get(l1);
				if (entity != this.passenger && entity.R() && entity instanceof EntityMinecartAbstract) {
					//bump the other cart.
					if (!(entity instanceof IMMEntity) ||  !((IMMEntity)entity).getFrozen()){
						if(this.collisions)	entity.collide(this);
					}
				}
			}
		}


		if (this.passenger != null && this.passenger.dead) {
			if (this.passenger.vehicle == this) {
				this.passenger.vehicle = null;
			}

			this.passenger = null;
		}

	}


	private Vector changePosition( double itx, double itz){

		//establish location

		this.lastX = this.locX;
		this.lastY = this.locY;
		this.lastZ = this.locZ;


		int offset = findRailsYOffset();

		if((motY <=0) || magnetic){
			this.setPosition(this.locX, this.locY + offset , this.locZ);	
		}

		int xBlock = MathHelper.floor(this.locX);
		int yBlock = MathHelper.floor(this.locY);
		int zBlock = MathHelper.floor(this.locZ);


		setPArams(); //populate the fields, cause im lazy.


		if ((onRails && offset >=0) || (onSlope && offset ==-1)) { //only count as on rails when above when its a slope.
			//on rails


			this.fallDistance = 0.0F;
			//	Vec3D vec3d = this.a(this.locX, this.locY, this.locZ);



			//Curves
			int[][] aint = matrix[slopedata];
			double dX = (double) (aint[1][0] - aint[0][0]);
			double dZ = (double) (aint[1][2] - aint[0][2]);

			double d8 = Math.sqrt(dX * dX + dZ * dZ);
			double d9 = itx * dX + itz * dZ;

			if (d9 < 0.0D) {
				dX = -dX;
				dZ = -dZ;
			}

			double totalSpeed = Math.sqrt(itx * itx + itz * itz);

			itx = totalSpeed * dX / d8;
			itz = totalSpeed * dZ / d8;


			double fractionalLocation = 0.0D;
			double	d11 = (double) xBlock + 0.5D + (double) aint[0][0] * 0.5D;
			double d13 = (double) zBlock + 0.5D + (double) aint[0][2] * 0.5D;
			double d14 = (double) xBlock + 0.5D + (double) aint[1][0] * 0.5D;
			double d15 = (double) zBlock + 0.5D + (double) aint[1][2] * 0.5D;

			dX = d14 - d11;
			dZ = d15 - d13;

			if (dX == 0.0D) {
				this.locX = (double) xBlock + 0.5D;
				fractionalLocation = this.locZ - (double) zBlock;
			} else if (dZ == 0.0D) {
				this.locZ = (double) zBlock + 0.5D;
				fractionalLocation = this.locX - (double) xBlock;
			} else {
				double d16 = this.locX - d11;
				double d17 = this.locZ - d13;
				fractionalLocation = (d16 * dX + d17 * dZ) * 2.0D;
			}

			this.locX = d11 + dX * fractionalLocation;
			this.locZ = d13 + dZ * fractionalLocation;
			this.locY = yBlock + .5;

			//I guess this positions the cart properly on the center of the track before the move.
			this.setPosition(this.locX, this.locY , this.locZ);


			//constrain.
			constrainSpeed();


			if (this.uphill) {
				this.setPosition(this.locX, yBlock+1.5, this.locZ); //avoid collision
			}
			else if((motY <=0 && offset >=0) || this.magnetic) {
				this.setPosition(this.locX, yBlock+.5, this.locZ); //avoid collision
			}

			if (motY < 0 || magnetic) motY = 0;

			//move the cart. This is where collisions happen, sadly.
			if(collisions)	this.move(itx, motY, itz);
			else {
				locX += itx;
				locY += motY;
				locZ += itz;
				this.setPosition(locX, locY, locZ);
			}


			//				//oh god what is this.
			//				if (aint[0][1] != 0 && MathHelper.floor(this.locX) - xBlock == aint[0][0] && MathHelper.floor(this.locZ) - zBlock == aint[0][2]) {
			//					this.setPosition(this.locX, this.locY + (double) aint[0][1], this.locZ);
			//				} else if (aint[1][1] != 0 && MathHelper.floor(this.locX) - xBlock == aint[1][0] && MathHelper.floor(this.locZ) - zBlock == aint[1][2]) {
			//					this.setPosition(this.locX, this.locY + (double) aint[1][1], this.locZ);
			//				}


			//		Vec3D vec3d1 = this.a(this.locX, this.locY, this.locZ);

			//wat...
			//				if (vec3d1 != null && vec3d != null) {
			//					
			//					double d20 = (vec3d.d - vec3d1.d) * 0.05D; //5% of the change in Y value
			//			why do this when you already have the slope modifier ?
			//					totalSpeed = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
			//					if (totalSpeed > 0.0D) {
			//						this.motX = this.motX / totalSpeed * (totalSpeed + d20);
			//						this.motZ = this.motZ / totalSpeed * (totalSpeed + d20);
			//					}
			///					//	this.setPosition(this.locX, vec3d1.d, this.locZ);
			//				}


			//establish new position		

			boolean wasUphill = uphill;

			int newoffset = findRailsYOffset();
			this.setPosition(this.locX, this.locY + newoffset , this.locZ);

			int newXBlock = MathHelper.floor(this.locX);
			int newZBlock = MathHelper.floor(this.locZ);
			int newYBlock = MathHelper.floor(this.locY);

			setPArams(); //populate the fields, cause im lazy.		


			if (newXBlock != xBlock || newZBlock != zBlock) {

				//now in a new block, move speed from x to z if needed, I think.	
				totalSpeed = Math.sqrt(itx * itx + itz * itz);
				itx = totalSpeed * (double) (newXBlock - xBlock);
				itz = totalSpeed * (double) (newZBlock - zBlock);

				if(!magnetic && !onSlope && wasUphill && !uphill){
					//Ramp
					motY = Math.sqrt(totalSpeed * totalSpeed + totalSpeed*totalSpeed);
				}
			}

			if(motY <=0 && onRails){
				//correct y position on slopes
				double xfrac = this.locX - newXBlock;
				double zfrac = this.locZ - newZBlock;

				this.locY = newYBlock+.5;

				if (slopedata == 2) {
					//down west
					this.locY = xfrac + newYBlock +.5;
				}

				if (slopedata == 3 ) {
					// down east
					this.locY = (1-xfrac)  + newYBlock +.5;
				}

				if (slopedata == 4) {
					//down south
					this.locY = (1-zfrac) + newYBlock +.5;
				}

				if (slopedata == 5 ) {
					//doun north
					this.locY = zfrac + newYBlock +.5;
				}

				this.setPosition(this.locX, this.locY, this.locZ);
			}

			Logger.motion(" j corrected " + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);



			//				if (onPoweredRail) {
			//					d21 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
			//					if (d21 > 0.01D) {
			//						double d22 = 0.06D;
			//
			//						this.motX += this.motX / d21 * d22;
			//						this.motZ += this.motZ / d21 * d22;
			//					} else if (railData == 1) {
			//						if (this.world.t(xBlock - 1, i, zBlock)) {
			//							this.motX = 0.02D;
			//						} else if (this.world.t(xBlock + 1, i, zBlock)) {
			//							this.motX = -0.02D;
			//						}
			//					} else if (railData == 0) {
			//						if (this.world.t(xBlock, i, zBlock - 1)) {
			//							this.motZ = 0.02D;
			//						} else if (this.world.t(xBlock, i, zBlock + 1)) {
			//							this.motZ = -0.02D;
			//						}
			//					}
			//				}


		} else {		//not on rails.

			Logger.motion("offrails1" + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);

			constrainSpeed();

			this.setPosition(this.locX, this.locY, this.locZ); //necessary when first created
			double derp = motY;
			this.move(itx, this.motY, itz);		
			motY= derp; //If it hits the ground in move() the motY goes to 0, and in subsequent iterations it will not be detected as onGound.

			setPArams(); //populate the fields	

			Logger.motion("offrails2 " + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);

		}


		return new Vector(itx, 0, itz);

	}

	@Override
	public String getName(){
		return "MMMinecartHopper";	
	}
	
	@Override
	public boolean c(NBTTagCompound nbttagcompound) {
		if (!this.dead) {
			nbttagcompound.setString("id", "MinecartHopper");
			this.e(nbttagcompound);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean d(NBTTagCompound nbttagcompound) {
		return this.c(nbttagcompound);
	}
	
	public  int getType(){
		return 5;
	}
	//interface

	@Override
	public boolean getUphill() {
		return uphill;
	}


	@Override
	public boolean getDownhill() {
		return downhill;
	}


	@Override
	public boolean getOnRails() {
		return onRails;
	}


	@Override
	public EntityMinecartAbstract getEntity() {
		return this;
	}


	@Override
	public void setCollisions(boolean value) {
		this.collisions = value;
	}


	@Override
	public void setDerailedFriction(double value) {
		this.derailedFrictioPercent = value;
	}


	@Override
	public void setEmptyFriction(double value) {
		this.emptyFrictionPercent = value;
	}


	@Override
	public void setMaxPushSpeed(double value) {
		this.MaxPushSpeedPercent = value;
	}


	@Override
	public void setPassengerFriction(double value) {
		this.passengerFrictionPercent = value;
	}


	@Override
	public void setSlopeSpeed(double value) {
		this.slopeSpeedPercent = value;
	}


	@Override
	public void setFrozen(boolean value) {
		this.frozen = value;
	}


	@Override
	public boolean getFrozen() {
		return frozen;
	}


	@Override
	public void setMagnetic(boolean value) {
		this.magnetic = value;
	}
	@Override
	public void setGravity(double value) {
		this.GravityPercent = value;
	}
}