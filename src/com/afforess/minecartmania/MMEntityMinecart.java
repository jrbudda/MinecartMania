package com.afforess.minecartmania;

//CraftBukkit start
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Vehicle;

import net.minecraft.server.v1_4_R1.Block;
import net.minecraft.server.v1_4_R1.BlockMinecartTrack;
import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityMinecart;
import net.minecraft.server.v1_4_R1.IUpdatePlayerListBox;
import net.minecraft.server.v1_4_R1.MathHelper;
import net.minecraft.server.v1_4_R1.MinecraftServer;
import net.minecraft.server.v1_4_R1.Vec3D;
//CraftBukkit end
import net.minecraft.server.v1_4_R1.World;
import net.minecraft.server.v1_4_R1.WorldServer;

public class MMEntityMinecart extends net.minecraft.server.v1_4_R1.EntityMinecart {
	private int e;
	private boolean f;
	private final IUpdatePlayerListBox g;
	private static final int[][][] matrix = new int[][][] { { { 0, 0, -1}, { 0, 0, 1}}, { { -1, 0, 0}, { 1, 0, 0}}, { { -1, -1, 0}, { 1, 0, 0}}, { { -1, 0, 0}, { 1, -1, 0}}, { { 0, 0, -1}, { 0, -1, 1}}, { { 0, -1, -1}, { 0, 0, 1}}, { { 0, 0, 1}, { 1, 0, 0}}, { { 0, 0, 1}, { -1, 0, 0}}, { { 0, 0, -1}, { -1, 0, 0}}, { { 0, 0, -1}, { 1, 0, 0}}};
	private int j;
	private double at;
	private double au;
	private double av;
	private double aw;
	private double ax;

	private final double defaultpassengerFriction =  0.996999979019165D;
	private final double defaultemptyFriction  = 0.9599999785423279D;
	private final double defaultgravity = 0.03999999910593033D;
	private final double DefaultslopeSpeed = 0.0078125D;
	private final double defaultDerailedFriction  = 0.8;
	private final double defaultPassengerPushSpeed = 0.1D;

	public double derailedFrictioPercent = 100;
	public double passengerFrictionPercent = 100;
	public double emptyFrictionPercent = 100;
	public double slopeSpeedPercent = 100;
	public double MaxPushSpeedPercent = 100;
	public double GravityPercent = 100;

	public boolean onPoweredPoweredRail;
	public boolean onUnpoweredPoweredRail;

	public int blockBeneathtype;
	public int blockBeneathData;

	public boolean frozen;

	public boolean onRails;

	public boolean moving;
	public boolean onSlope;
	public boolean uphill;
	public boolean downhill;

	public boolean magnetic = false;
	
	public boolean onNormalRail(){
		return onRails && !onPoweredPoweredRail && !onUnpoweredPoweredRail;
	}


	private boolean isNew = true;

	public MMEntityMinecart(World world) {
		super(world);
		this.g = world != null ? world.a(this) : null;
		this.m = true;
	}


	private int findRailsYOffset(){

		int xBlock = MathHelper.floor(this.locX);
		int yBlock = MathHelper.floor(this.locY);
		int zBlock = MathHelper.floor(this.locZ);

		for(int i = -1; i <=4 ;i++){
			if (BlockMinecartTrack.e(this.world.getTypeId(xBlock, yBlock + i, zBlock))) {
				return i ;
			}
		}

		return 0;

	}

	private int slopedata = 0;

	private void setPArams(){
		int xBlock = MathHelper.floor(this.locX);
		int yBlock = MathHelper.floor(this.locY);
		int zBlock = MathHelper.floor(this.locZ);
		blockBeneathtype = this.world.getTypeId(xBlock, yBlock, zBlock);
		blockBeneathData = this.world.getData(xBlock, yBlock, zBlock);
		onRails = 	BlockMinecartTrack.e(this.world.getTypeId(xBlock, yBlock, zBlock)) && this.motY <=0;
		onPoweredPoweredRail = false;
		onUnpoweredPoweredRail = false;	
		if (onRails && (blockBeneathtype == Block.GOLDEN_RAIL.id)) {
			onPoweredPoweredRail = (blockBeneathData & 8) != 0;
			onUnpoweredPoweredRail = !onPoweredPoweredRail;
		}
		downhill = false;
		uphill = false;
		onSlope = false;
		if(onRails){

			slopedata = blockBeneathData;
			if (((BlockMinecartTrack) Block.byId[blockBeneathtype]).p()) {
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
	public void j_() {

		move();		

	}

	public void move() {

		//		com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info(" j start " + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);

		// CraftBukkit start
		double prevX = this.locX;
		double prevY = this.locY;
		double prevZ = this.locZ;
		float prevYaw = this.yaw;
		float prevPitch = this.pitch;
		// CraftBukkit end

		if (this.g != null) {
			this.g.a();
		}

		if (this.j() > 0) {
			this.h(this.j() - 1);
		}

		if (this.getDamage() > 0) {
			this.setDamage(this.getDamage() - 1);
		}

		if (this.locY < -64.0D) {
			this.C();
		}

		if (this.h() && this.random.nextInt(4) == 0) {
			this.world.addParticle("largesmoke", this.locX, this.locY + 0.8D, this.locZ, 0.0D, 0.0D, 0.0D);
		}

		int i;

		if (!this.world.isStatic && this.world instanceof WorldServer) {
			this.world.methodProfiler.a("portal");
			MinecraftServer minecraftserver = ((WorldServer) this.world).getMinecraftServer();

			i = this.z();
			if (this.ao) {
				if (true || minecraftserver.getAllowNether()) { // CraftBukkit - multi-world should still allow teleport even if default vanilla nether disabled
					if (this.vehicle == null && this.ap++ >= i) {
						this.ap = i;
						this.portalCooldown = this.ab();
						byte b0;

						if (this.world.worldProvider.dimension == -1) {
							b0 = 0;
						} else {
							b0 = -1;
						}

						this.b(b0);
					}

					this.ao = false;
				}
			} else {
				if (this.ap > 0) {
					this.ap -= 4;
				}

				if (this.ap < 0) {
					this.ap = 0;
				}
			}

			if (this.portalCooldown > 0) {
				--this.portalCooldown;
			}

			this.world.methodProfiler.b();
		}

		if (frozen) return;

		if (this.world.isStatic) {
			//	com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info(" j static " + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);

			if (this.j > 0) {
				double d0 = this.locX + (this.at - this.locX) / (double) this.j;
				double d1 = this.locY + (this.au - this.locY) / (double) this.j;
				double d2 = this.locZ + (this.av - this.locZ) / (double) this.j;
				double d3 = MathHelper.g(this.aw - (double) this.yaw);

				this.yaw = (float) ((double) this.yaw + d3 / (double) this.j);
				this.pitch = (float) ((double) this.pitch + (this.ax - (double) this.pitch) / (double) this.j);
				--this.j;
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
			if (motX < -maxSpeed) {
				motX = -maxSpeed;
			}

			if (motX > maxSpeed) {
				motX = maxSpeed;
			}

			if (motZ < -maxSpeed) {
				motZ = -maxSpeed;
			}

			if (motZ > maxSpeed) {
				motZ = maxSpeed;
			}

			// move in .5m increments.
			double spd= Math.sqrt(motX*motX + motZ*motZ);
			double incspd= spd;
			double speeddelta = 0;

	//		com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info(" incomming speed x:" + motX + " z:" + motZ);

			int mod = 1;
			while (spd > .4) {		

				if (Math.abs(motX) > Math.abs(motZ)){
					if (motX < 0) mod = -1;
					motX = .4 * mod;
					motZ = 0;
				}
				else if (Math.abs(motZ) > Math.abs(motX)){
					if (motZ < 0) mod = -1;
					motZ = .4 * mod;		
					motX = 0;
				}
				else {
					motZ = Math.sqrt(.4*.4/2) *  (motZ < 0 ? -1 :1);	
					motX = Math.sqrt(.4*.4/2)  * (motX < 0 ? -1 :1);
				}	

				nonstaticmove();

				double ts = Math.sqrt(motX*motX + motZ*motZ);
				speeddelta += (.4- ts); //4.8
				spd -=  .4; 
			} 

			
			if(spd > 0.0001D || incspd <= 0.0001D){
				if (Math.abs(motX) > Math.abs(motZ)){
					if (motX < 0) mod = -1;
					motX = spd * mod;
					motZ = 0;
				}
				else if (Math.abs(motZ) > Math.abs(motX)){
					if (motZ < 0) mod = -1;
					motZ = spd * mod;		
					motX = 0;
				}
				else {
					motZ = Math.sqrt(spd*spd/2) *  (motZ < 0 ? -1 :1);	
					motX = Math.sqrt(spd*spd/2)  * (motX < 0 ? -1 :1);
				}		

				// remainder speed sent to last call    .0024

				nonstaticmove();
			}

			//have to repopulate the vars with the original speed minus the total change, so the real speed is available to external code.

			double ts = Math.sqrt(motX*motX + motZ*motZ);    //.0023

			speeddelta += (spd- ts); //4.8		

			spd = incspd - speeddelta;

			if (Math.abs(motX) > Math.abs(motZ)){
				motX = spd * (motX < 0 ? -1 :1);	
				motZ = 0;
			}
			else if (Math.abs(motZ) > Math.abs(motX)){
				motZ = spd *  (motZ < 0 ? -1 :1);
				motX = 0;
			}
			else{		
				motZ = Math.sqrt(spd*spd/2) *  (motZ < 0 ? -1 :1);	
				motX = Math.sqrt(spd*spd/2)  * (motX < 0 ? -1 :1);		
			}

	//		com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info(" outgoing speed x:" + motX + " z:" + motZ + "spd: " + spd + " delta: " + speeddelta);
//
		}

		//stop motion if very slow.
		double d12 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ + this.motY * this.motY);
		if (d12 < 0.0001D) {
			this.motX *= 0.0D;
			//	this.motY *= 0.0D;
			this.motZ *= 0.0D;
		} 


		this.D();
		this.pitch = 0.0F; //I think minecart tilting  is handled on the client only.


		//turn
		double d23 = this.lastX - this.locX;
		double d24 = this.lastZ - this.locZ;
		if (d23 * d23 + d24 * d24 > 0.001D) {
			this.yaw = (float) (Math.atan2(d24, d23) * 180.0D / 3.141592653589793D);
			if (this.f) {
				this.yaw += 180.0F;
			}
		}

		double d25 = (double) MathHelper.g(this.yaw - this.lastYaw);

		if (d25 < -170.0D || d25 >= 170.0D) {
			this.yaw += 180.0F;
			this.f = !this.f;
		}

		this.b(this.yaw, this.pitch);

		// CraftBukkit start
		org.bukkit.World bworld = this.world.getWorld();
		Location from = new Location(bworld, prevX, prevY, prevZ, prevYaw, prevPitch);
		Location to = new Location(bworld, this.locX, this.locY, this.locZ, this.yaw, this.pitch);
		Vehicle vehicle = (Vehicle) this.getBukkitEntity();

		if (!isNew) {

			this.moving = false;
			if (!from.equals(to)) {
				this.moving = true;
				this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleMoveEvent(vehicle, from, to));
			}	
		}
		else isNew = false;



		// CraftBukkit end


		List list = this.world.getEntities(this, this.boundingBox.grow(0.20000000298023224D, 0.0D, 0.20000000298023224D));

		if (list != null && !list.isEmpty()) {
			for (int l1 = 0; l1 < list.size(); ++l1) {
				Entity entity = (Entity) list.get(l1);

				if (entity != this.passenger && entity.M() && entity instanceof EntityMinecart) {
					entity.collide(this);
				}
			}
		}


		if (this.passenger != null && this.passenger.dead) {
			if (this.passenger.vehicle == this) {
				this.passenger.vehicle = null;
			}

			this.passenger = null;
		}

		if (this.e > 0) {
			--this.e;
		}

		if (this.e <= 0) {
			this.b = this.c = 0.0D;
		}

		this.e(this.e > 0);


	}


	private void nonstaticmove(){

		//	com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info(" j notstatic " + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);



		//establish location

		this.lastX = this.locX;
		this.lastY = this.locY;
		this.lastZ = this.locZ;


		int offset = findRailsYOffset();

		if((motY <=0 && offset >=0) || magnetic){
			this.setPosition(this.locX, this.locY + offset , this.locZ);	
		}

		int xBlock = MathHelper.floor(this.locX);
		int yBlock = MathHelper.floor(this.locY);
		int zBlock = MathHelper.floor(this.locZ);


		setPArams(); //populate the fields, cause im lazy.

		this.motY -= defaultgravity * GravityPercent / 100;

		if ((onRails && offset >=0) || (onSlope && offset ==-1)) { //only count as on rails when above when its a slope.
			//on rails
		//	com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info(" j onrails " + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);

			this.fallDistance = 0.0F;
			//	Vec3D vec3d = this.a(this.locX, this.locY, this.locZ);

			//modify speed

			//slopes

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


			//Curves
			int[][] aint = matrix[slopedata];
			double dX = (double) (aint[1][0] - aint[0][0]);
			double dZ = (double) (aint[1][2] - aint[0][2]);

			double d8 = Math.sqrt(dX * dX + dZ * dZ);
			double d9 = this.motX * dX + this.motZ * dZ;

			if (d9 < 0.0D) {
				dX = -dX;
				dZ = -dZ;
			}

			double totalSpeed = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);

			this.motX = totalSpeed * dX / d8;
			this.motZ = totalSpeed * dZ / d8;


			if (this.passenger != null) {
				// there is a passenger
				double	passengerSpeed = this.passenger.motX * this.passenger.motX + this.passenger.motZ * this.passenger.motZ;
				double	cartSpeed = this.motX * this.motX + this.motZ * this.motZ;
				if (passengerSpeed > .0001D && cartSpeed < MaxPushSpeedPercent / 100 * defaultPassengerPushSpeed) {
					this.motX += this.passenger.motX * 0.2D;
					this.motZ += this.passenger.motZ * 0.2D;
				}
				//I think this bumps the cart along? or maybe when the passenger gets in?
			}


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


			//powered cart	
			if (this.type == 2) {
				double d18 = this.b * this.b + this.c * this.c;
				if (d18 > 1.0E-4D) {
					d18 = (double) MathHelper.sqrt(d18);
					this.b /= d18;
					this.c /= d18;
					double d19 = 0.04D;

					this.motX *= 0.800000011920929D;
					//	this.motY *= 0.0D;
					this.motZ *= 0.800000011920929D;
					this.motX += this.b * d19;
					this.motZ += this.c * d19;
				} else {
					//powered minecart friction with no fuel?		
					this.motX *= 0.8999999761581421D;
					//	this.motY *= 0.0D;
					this.motZ *= 0.8999999761581421D;
				}
			}

			//frictions
			if(slowWhenEmpty && this.passenger == null){
				this.motX *= this.emptyFrictionPercent / 100 * this.defaultemptyFriction;
				this.motZ *=  this.emptyFrictionPercent / 100 * this.defaultemptyFriction;
			}
			else{
				this.motX *= this.passengerFrictionPercent / 100 * this.defaultpassengerFriction;
				this.motZ *=  this.passengerFrictionPercent / 100 * this.defaultpassengerFriction;
			}


			//constrain.
			if (motX < -maxSpeed) {
				motX = -maxSpeed;
			}

			if (motX > maxSpeed) {
				motX = maxSpeed;
			}

			if (motZ < -maxSpeed) {
				motZ = -maxSpeed;
			}

			if (motZ > maxSpeed) {
				motZ = maxSpeed;
			}


			if (this.uphill) {
				this.setPosition(this.locX, yBlock+1.5, this.locZ); //avoid collision
			}
			else if((motY <=0 && offset >=0) || this.magnetic) {
				this.setPosition(this.locX, yBlock+.5, this.locZ); //avoid collision
			}

			if (motY < 0 ) motY = 0;

		//	com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info(" j beforemove " + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);

			//move the cart.
			this.move(motX, motY, motZ);

		//	com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info(" j aftermove " + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);


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
				totalSpeed = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
				this.motX = totalSpeed * (double) (newXBlock - xBlock);
				this.motZ = totalSpeed * (double) (newZBlock - zBlock);
				
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

		//	com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info(" j corrected " + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);


			double d21;

			if (this.type == 2) {
				//something do with fuel?
				d21 = this.b * this.b + this.c * this.c;
				if (d21 > 1.0E-4D && this.motX * this.motX + this.motZ * this.motZ > 0.001D) {
					d21 = (double) MathHelper.sqrt(d21);
					this.b /= d21;
					this.c /= d21;
					if (this.b * this.motX + this.c * this.motZ < 0.0D) {
						this.b = 0.0D;
						this.c = 0.0D;
					} else {
						this.b = this.motX;
						this.c = this.motZ;
					}
				}
			}

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

	//		com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info(" j off1 " + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);

			if (this.motX < -maxSpeed) {
				this.motX = -maxSpeed;
			}

			if (this.motX > maxSpeed) {
				this.motX = maxSpeed;
			}

			if (this.motZ < -maxSpeed) {
				this.motZ = -maxSpeed;
			}

			if (this.motZ > maxSpeed) {
				this.motZ = maxSpeed;
			}

			//Don't apply friction if in the block above a rail, cause i think onground retruns true.
			if (this.onGround && offset !=-1) {
				this.motX *= this.derailedFrictioPercent / 100 * this.defaultDerailedFriction;
				this.motZ *=  this.derailedFrictioPercent / 100 * this.defaultDerailedFriction;
			}

			this.setPosition(this.locX, this.locY, this.locZ); //necessary when first created
			this.move(this.motX, this.motY, this.motZ);		


	//		com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info(" j off2 " + locX + " " + locY + " " + locZ + ":" + motX + " " + motY + " " + motZ);

		}

		//this is a horrible idea.
		this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleUpdateEvent((Vehicle) this.getBukkitEntity()));

		//TODO: process what might have changed.

	}
}