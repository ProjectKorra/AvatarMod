package com.crowsofwar.avatar.client.render.lightning.misc;

import com.crowsofwar.avatar.client.render.lightning.math.Vec3;
import net.minecraft.util.math.MathHelper;

public class Contact {

	public RigidBody bodyA;
	public RigidBody bodyB;
	public Collider a;
	public Collider b;
	public Vec3 localA;
	public Vec3 localB;
	public Vec3 globalA;
	public Vec3 globalB;
	public Vec3 normal;
	public float depth;
	public Vec3 tangent;
	public Vec3 bitangent;
	
	public Vec3 rA;
	public Vec3 rB;
	
	public Jacobian normalContact;
	public Jacobian tangentContact;
	public Jacobian bitangentContact;
	
	public Contact(RigidBody bodyA, RigidBody bodyB, Collider a, Collider b, GJK.GJKInfo info) {
		this.a = a;
		this.b = b;
		if(bodyA == null){
			bodyA = RigidBody.DUMMY;
		}
		if(bodyB == null){
			bodyB = RigidBody.DUMMY;
		}
		this.bodyA = bodyA;
		this.bodyB = bodyB;
		localA = bodyA.globalToLocalPos(info.contactPointA);
		localB = bodyB.globalToLocalPos(info.contactPointB);
		globalA = info.contactPointA;
		globalB = info.contactPointB;
		normal = info.normal;
		depth = info.depth;
		//https://box2d.org/posts/2014/02/computing-a-basis/
		if(Math.abs(normal.xCoord) >= 0.57735){
			tangent = new Vec3(normal.yCoord, -normal.xCoord, 0).normalize();
		} else {
			tangent = new Vec3(0, normal.zCoord, -normal.yCoord).normalize();
		}
		bitangent = normal.crossProduct(tangent);
		
		normalContact = new Jacobian(false);
		tangentContact = new Jacobian(true);
		bitangentContact = new Jacobian(true);
	}
	
	public void init(float dt){
		rA = globalA.subtract(bodyA == RigidBody.DUMMY ? a.localCentroid : bodyA.globalCentroid);
		rB = globalB.subtract(bodyB == RigidBody.DUMMY ? b.localCentroid : bodyB.globalCentroid);
		
		normalContact.init(this, normal, dt);
		tangentContact.init(this, tangent, dt);
		bitangentContact.init(this, bitangent, dt);
	}
	
	public void solve(float dt){
		normalContact.solve(this, dt);
		tangentContact.solve(this, dt);
		bitangentContact.solve(this, dt);
	}
	
	public static class Jacobian {
		
		boolean tangent;
		
		Vec3 j_va;
		Vec3 j_wa;
		Vec3 j_vb;
		Vec3 j_wb;
		
		float bias;
		double effectiveMass;
		double totalLambda;
		
		public Jacobian(boolean tangent) {
			this.tangent = tangent;
		}
		
		public void init(Contact c, Vec3 dir, float dt){
			j_va = dir.negate();
			j_wa = c.rA.crossProduct(dir).negate();
			j_vb = dir;
			j_wb = c.rB.crossProduct(dir);
			
			if(!tangent){
				float closingVel = (float)c.bodyA.linearVelocity.negate()
						.subtract(c.bodyA.angularVelocity.crossProduct(c.rA))
						.add(c.bodyB.linearVelocity)
						.add(c.bodyB.angularVelocity.crossProduct(c.rB))
						.dotProduct(c.normal);
				float restitution = c.bodyA.restitution*c.bodyB.restitution;
				
				float beta = 0.2F;
				float dslop = 0.0005F;
				float rslop = 0.5F;
				bias = -(beta/dt)*Math.max(c.depth-dslop, 0)+Math.max(restitution*closingVel-rslop, 0);
			}
			
			effectiveMass = 
					  c.bodyA.inv_mass
					+ j_wa.dotProduct(j_wa.matTransform(c.bodyA.inv_globalInertiaTensor))
					+ c.bodyB.inv_mass
					+ j_wb.dotProduct(j_wb.matTransform(c.bodyB.inv_globalInertiaTensor));
			effectiveMass = 1D/effectiveMass;
			
			totalLambda = 0;
		}
		
		public void solve(Contact c, float dt){
			double jv = 
					  j_va.dotProduct(c.bodyA.linearVelocity)
					+ j_wa.dotProduct(c.bodyA.angularVelocity)
					+ j_vb.dotProduct(c.bodyB.linearVelocity)
					+ j_wb.dotProduct(c.bodyB.angularVelocity);
			double lambda = effectiveMass * (-(jv + bias));
			double oldTotalLambda = totalLambda;
			if(tangent){
				float friction = c.bodyA.friction*c.bodyB.friction;
				double maxFriction = friction*c.normalContact.totalLambda;
				totalLambda = MathHelper.clamp(totalLambda + lambda, -maxFriction, maxFriction);
			} else {
				totalLambda = Math.max(0, oldTotalLambda + lambda);
			}
			lambda = totalLambda - oldTotalLambda;
			
			c.bodyA.addLinearVelocity(j_va.multd(c.bodyA.inv_mass * lambda));
			c.bodyA.addAngularVelocity(j_wa.matTransform(c.bodyA.inv_globalInertiaTensor).multd(lambda));
			c.bodyB.addLinearVelocity(j_vb.multd(c.bodyB.inv_mass * lambda));
			c.bodyB.addAngularVelocity(j_wb.matTransform(c.bodyB.inv_globalInertiaTensor).multd(lambda));
		}
	}
}
