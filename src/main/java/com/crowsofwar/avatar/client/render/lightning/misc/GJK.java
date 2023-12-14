package com.crowsofwar.avatar.client.render.lightning.misc;

import com.crowsofwar.avatar.client.render.lightning.math.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GJK {

	public static final int gjkMaxIterations = 64;
	public static final int epaMaxIterations = 128;
	public static float margin = 0;
	
	public static Simplex csoSimplex = new Simplex();
	
	//https://www.youtube.com/watch?v=Qupqu1xe7Io
	public static GJKInfo colliding(RigidBody bodyA, RigidBody bodyB, Collider a, Collider b){
		return colliding(bodyA, bodyB, a, b, true);
	}
	public static boolean collidesAny(RigidBody bodyA, RigidBody bodyB, Collider a, Collider b){
		return colliding(bodyA, bodyB, a, b, false) != null;
	}
	public static GJKInfo colliding(@Nullable RigidBody bodyA, @Nullable RigidBody bodyB, Collider a, Collider b, boolean epa){
		GJKInfo returnInfo = new GJKInfo();
		csoSimplex.reset();
		Vec3 direction = new Vec3(0, 0, 1);
		Vec3 supportCSO = doCSOSupport(bodyA, bodyB, a, b, direction).v;
		direction = supportCSO.negate();
		for(int iter = 0; iter < gjkMaxIterations; iter ++){
			supportCSO = doCSOSupport(bodyA, bodyB, a, b, direction).v;
			if(supportCSO.dotProduct(direction) < 0){
				//We didn't find a closer point
				returnInfo.result = Result.SEPARATED;
				if(!epa)
					return null;
				return returnInfo;
			}
			switch(csoSimplex.size){
			case 0:
			case 1:
				//Should never happen since we already added 2 points.
				break;
			case 2:
				Vec3 ab = csoSimplex.points[1].v.subtract(csoSimplex.points[0].v);
				Vec3 ao = csoSimplex.points[0].v.negate();
				if(ab.dotProduct(ao) > 0){
					direction = ab.crossProduct(ao).crossProduct(ab);
				} else {
					csoSimplex.points[1] = null;
					csoSimplex.size--;
					direction = csoSimplex.points[0].v.mult(-1);
				}
				break;
			case 3:
				ab = csoSimplex.points[1].v.subtract(csoSimplex.points[0].v);
				Vec3 ac = csoSimplex.points[2].v.subtract(csoSimplex.points[0].v);
				Vec3 abc = ab.crossProduct(ac);
				ao = csoSimplex.points[0].v.negate();
				direction = triangleCase(ab, ac, abc, ao);
				break;
			case 4:
				ab = csoSimplex.points[1].v.subtract(csoSimplex.points[0].v);
				ac = csoSimplex.points[2].v.subtract(csoSimplex.points[0].v);
				Vec3 ad = csoSimplex.points[3].v.subtract(csoSimplex.points[0].v);
				ao = csoSimplex.points[0].v.negate();
				Vec3 dir = tetraCase(ab, ac, ad, ao);
				if(dir == null){
					if(epa)
						EPA(bodyA, bodyB, a, b, returnInfo);
					return returnInfo;
				} else {
					direction = dir;
				}
				break;
			}
		}
		//Fail, most likely because the origin was exactly touching a simplex.
		//I'm not sure how much of a performance impact adding checks for these would be.
		//But it's worth checking out to see if it's needed or not
		//TODO check
		returnInfo.result = Result.GJK_FAILED;
		return returnInfo;
	}
	
	public static Vec3 triangleCase(Vec3 ab, Vec3 ac, Vec3 abc, Vec3 ao){
		if(abc.crossProduct(ac).dotProduct(ao) > 0){
			if(ac.dotProduct(ao) > 0){
				csoSimplex.points[1] = csoSimplex.points[2];
				csoSimplex.points[2] = null;
				csoSimplex.size--;
				return ac.crossProduct(ao).crossProduct(ac);
			} else {
				if(ab.dotProduct(ao) > 0){
					csoSimplex.points[2] = null;
					csoSimplex.size--;
					return ab.crossProduct(ao).crossProduct(ab);
				} else {
					csoSimplex.points[1] = null;
					csoSimplex.points[2] = null;
					csoSimplex.size -= 2;
					return ao;
				}
			}
		} else {
			if(ab.crossProduct(abc).dotProduct(ao) > 0){
				if(ab.dotProduct(ao) > 0){
					csoSimplex.points[2] = null;
					csoSimplex.size--;
					return ab.crossProduct(ao).crossProduct(ab);
				} else {
					csoSimplex.points[1] = null;
					csoSimplex.points[2] = null;
					csoSimplex.size -= 2;
					return ao;
				}
			} else {
				if(abc.dotProduct(ao) > 0){
					return abc;
				} else {
					Mkv tmp = csoSimplex.points[2];
					csoSimplex.points[2] = csoSimplex.points[1];
					csoSimplex.points[1] = tmp;
					return abc.negate();
				}
			}
		}
	}
	
	public static Vec3 tetraCase(Vec3 ab, Vec3 ac, Vec3 ad, Vec3 ao){
		if(ab.crossProduct(ac).dotProduct(ao) > 0){
			csoSimplex.points[3] = null;
			csoSimplex.size--;
			return triangleCase(ab, ac, ab.crossProduct(ac), ao);
		} else if(ac.crossProduct(ad).dotProduct(ao) > 0){
			csoSimplex.points[1] = csoSimplex.points[2];
			csoSimplex.points[2] = csoSimplex.points[3];
			csoSimplex.points[3] = null;
			csoSimplex.size--;
			return triangleCase(ac, ad, ac.crossProduct(ad), ao);
		} else if(ad.crossProduct(ab).dotProduct(ao) > 0){
			csoSimplex.points[2] = csoSimplex.points[1];
			csoSimplex.points[1] = csoSimplex.points[3];
			csoSimplex.points[3] = null;
			csoSimplex.size--;
			return triangleCase(ad, ab, ad.crossProduct(ab), ao);
		} else {
			//Origin is contained by simplex, we're done
			return null;
		}
	}
	
	//Calls csoSupport, possibly will be useful if I need to keep the support points found on a and b as well.
	public static Mkv doCSOSupport(RigidBody bodyA, RigidBody bodyB, Collider a, Collider b, Vec3 direction){
		Vec3 supportCSO = new Vec3(0, 0, 0);
		csoSupport(bodyA, bodyB, a, b, direction, supportCSO);
		Mkv vert = new Mkv(supportCSO, direction);
		csoSimplex.push_back(vert);
		return vert;
	}
	
	public static void csoSupport(RigidBody bodyA, RigidBody bodyB, Collider a, Collider b, Vec3 dir, Vec3 supportCSO){
		/*if(a.body != null){
			Vec3 vecA = a.body.globalToLocalVec(dir);
			supportA.set(a.body.localToGlobalPos(a.support(vecA)));
		} else {
			supportA.set(a.support(dir));
		}
		if(b.body != null){
			Vec3 vecB = b.body.globalToLocalVec(dir.negate());
			supportB.set(b.body.localToGlobalPos(b.support(vecB)));
		} else {
			supportB.set(b.support(dir.negate()));
		}
		supportCSO.set(supportA.subtract(supportB));*/
		supportCSO.set(localSupport(bodyA, a, dir).subtract(localSupport(bodyB, b, dir.negate())));
	}
	
	public static Vec3 localSupport(RigidBody body, Collider c, Vec3 worldDir){
		if(body != null){
			Vec3 localDir = body.globalToLocalVec(worldDir);
			if(margin != 0){
				localDir = localDir.normalize();
				return body.localToGlobalPos(c.support(localDir).add(localDir.mult(margin)));
			}
			return body.localToGlobalPos(c.support(localDir));
		} else {
			if(margin != 0){
				worldDir = worldDir.normalize();
				return c.support(worldDir).add(worldDir.mult(margin));
			}
			return c.support(worldDir);
		}
	}
	
	/// EPA START ///
	
	private static List<Mkv[]> faces = new ArrayList<>();
	private static List<Mkv[]> edges = new ArrayList<>();
	private static Vec3[][] features = new Vec3[2][3];
	
	public static void EPA(RigidBody bodyA, RigidBody bodyB, Collider a, Collider b, GJKInfo info){
		//Create the faces for the first tetrahedron
		faces.add(buildFace(csoSimplex.points[0], csoSimplex.points[1], csoSimplex.points[2]));
		faces.add(buildFace(csoSimplex.points[0], csoSimplex.points[2], csoSimplex.points[3]));
		faces.add(buildFace(csoSimplex.points[0], csoSimplex.points[3], csoSimplex.points[1]));
		faces.add(buildFace(csoSimplex.points[1], csoSimplex.points[2], csoSimplex.points[3]));
		for(int iter = 0; iter < epaMaxIterations; iter ++){
			Mkv[] closestFace = null;
			double smallestDist = Double.MAX_VALUE;
			for(Mkv[] face : faces){
				double lenSq = originDistToPlaneSq(face);
				if(lenSq < smallestDist){
					smallestDist = lenSq;
					closestFace = face;
				}
			}
			Mkv support = doCSOSupport(bodyA, bodyB, a, b, closestFace[3].v);
			final float epsilon = 0.00001F;
			if(distToPlaneSq(closestFace, support.v) < epsilon){
				info.result = Result.COLLIDING;
				Vec3 separation = planeProjectOrigin(closestFace);
				info.normal = separation.normalize();
				info.depth = (float) separation.lengthVector();
				for(int i = 0; i < 3; i ++){
					features[0][i] = localSupport(bodyA, a, closestFace[i].r);
					features[1][i] = localSupport(bodyB, b, closestFace[i].r.negate());
				}
				Vec3 bCoords = barycentricCoords(closestFace, separation);
				info.contactPointA = new Vec3(
						features[0][0].xCoord*bCoords.xCoord+features[0][1].xCoord*bCoords.yCoord+features[0][2].xCoord*bCoords.zCoord,
						features[0][0].yCoord*bCoords.xCoord+features[0][1].yCoord*bCoords.yCoord+features[0][2].yCoord*bCoords.zCoord,
						features[0][0].zCoord*bCoords.xCoord+features[0][1].zCoord*bCoords.yCoord+features[0][2].zCoord*bCoords.zCoord);
				//Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleTauHit(Minecraft.getMinecraft().world, features[0][0].xCoord, features[0][0].yCoord, features[0][0].zCoord, 1F, new Vec3d(0, 0, 1)));
				//Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleTauHit(Minecraft.getMinecraft().world, features[0][1].xCoord, features[0][1].yCoord, features[0][1].zCoord, 1F, new Vec3d(0, 0, 1)));
				//Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleTauHit(Minecraft.getMinecraft().world, features[0][2].xCoord, features[0][2].yCoord, features[0][2].zCoord, 1F, new Vec3d(0, 0, 1)));
				//Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleTauHit(Minecraft.getMinecraft().world, info.contactPointA.xCoord, info.contactPointA.yCoord, info.contactPointA.zCoord, 2F, new Vec3d(0, 0, 1)));
				info.contactPointB = new Vec3(
						features[1][0].xCoord*bCoords.xCoord+features[1][1].xCoord*bCoords.yCoord+features[1][2].xCoord*bCoords.zCoord,
						features[1][0].yCoord*bCoords.xCoord+features[1][1].yCoord*bCoords.yCoord+features[1][2].yCoord*bCoords.zCoord,
						features[1][0].zCoord*bCoords.xCoord+features[1][1].zCoord*bCoords.yCoord+features[1][2].zCoord*bCoords.zCoord);
				
				faces.clear();
				return;
			}
			//E x p a n d  the polytope
			Iterator<Mkv[]> itr = faces.iterator();
			while(itr.hasNext()){
				Mkv[] face = itr.next();
				if(face[3].v.dotProduct(support.v.subtract(face[0].v)) > 0){
					itr.remove();
					Mkv[] edge = new Mkv[]{face[1], face[0]};
					if(!removeEdge(edge)){
						edge[0] = face[0];
						edge[1] = face[1];
						edges.add(edge);
					}
					edge = new Mkv[]{face[2], face[1]};
					if(!removeEdge(edge)){
						edge[0] = face[1];
						edge[1] = face[2];
						edges.add(edge);
					}
					edge = new Mkv[]{face[0], face[2]};
					if(!removeEdge(edge)){
						edge[0] = face[2];
						edge[1] = face[0];
						edges.add(edge);
					}
				}
			}
			for(Mkv[] edge : edges){
				faces.add(buildFace(edge[0], edge[1], support));
			}
			edges.clear();
		}
		faces.clear();
		info.result = Result.EPA_FAILED;
	}
	
	//I don't trust ArrayList's default remove to work with arrays, and I don't want to spend the time to check it.
	public static boolean removeEdge(Mkv[] edge){
		Iterator<Mkv[]> itr = edges.iterator();
		while(itr.hasNext()){
			Mkv[] edge2 = itr.next();
			if(edge[0] == edge2[0] && edge[1] == edge2[1]){
				itr.remove();
				return true;
			}
		}
		return false;
	}
	
	public static Vec3 planeProjectOrigin(Mkv[] face){
		Vec3 point = face[0].v.negate();
		double dot = face[3].v.dotProduct(point);
		return face[3].v.mult((float) dot).negate();
	}
	
	public static double distToPlaneSq(Mkv[] face, Vec3 point){
		double dot = face[3].v.dotProduct(point.subtract(face[0].v));
		Vec3 proj = face[3].v.mult((float) dot);
		return proj.lengthSquared();
	}
	
	public static double originDistToPlaneSq(Mkv[] face){
		double dot = face[0].v.dotProduct(face[3].v);
		Vec3 proj = face[3].v.mult((float) dot);
		return proj.lengthSquared();
	}
	
	public static Mkv[] buildFace(Mkv a, Mkv b, Mkv c){
		Vec3 ab = b.v.subtract(a.v);
		Vec3 ac = c.v.subtract(a.v);
		Vec3 ao = a.v.negate();
		Vec3 normal = ab.crossProduct(ac).normalize();
		if(normal.dotProduct(ao) < 0){
			return new Mkv[]{a, b, c, new Mkv(normal, null)};
		} else {
			return new Mkv[]{a, c, b, new Mkv(normal.negate(), null)};
		}
	}
	
	public static Vec3 barycentricCoords(Mkv[] face, Vec3 point){
		//Idea is that the barycentric coordinate is the area of the opposite triangle to the vertex, so we compute that with the cross product
		//and make that the weight. You also have to divide by the sum of the weights to normalize them.
		//I was under the impression that the area of the triangle would be the cross product over 2, but apparently the barycentric coords don't need that.
		//I'm thinking this is because the normalization deals with that for me.
		double u = face[1].v.subtract(point).crossProduct(face[2].v.subtract(point)).lengthVector();
		double v = face[0].v.subtract(point).crossProduct(face[2].v.subtract(point)).lengthVector();
		double w = face[0].v.subtract(point).crossProduct(face[1].v.subtract(point)).lengthVector();
		//Normalize
		double uvw = u+v+w;
		return new Vec3(u, v, w).multd(1/uvw);
	}
	
	public static class Simplex {
		public int size = 0;
		public Mkv[] points = new Mkv[4];
		
		public void push_back(Mkv vec){
			for(int i = Math.min(size, 2); i >= 0; i --){
				points[i+1] = points[i];
			}
			points[0] = vec;
			size ++;
			if(size > 4)
				size = 4;
		}
		
		public void reset(){
			size = 0;
			for(int i = 0; i < 4; i ++){
				points[i] = null;
			}
		}
		
		public Simplex copy(){
			Simplex simp = new Simplex();
			simp.size = size;
			for(int i = 0; i < 4; i ++){
				simp.points[i] = points[i].copy();
			}
			return simp;
		}
	}
	
	//Minkowski vertex, a struct for both the vertex on the minkowski difference and the ray that got there for extracting the contact.
	//Idea from the bullet physics engine.
	public static class Mkv {
		public Vec3 v;
		public Vec3 r;
		
		public Mkv(Vec3 point, Vec3 direction) {
			this.v = point;
			this.r = direction;
		}
		
		public Mkv copy(){
			Mkv vert = new Mkv(v.copy(), r.copy());
			return vert;
		}
	}
	
	public static class GJKInfo {
		public Result result;
		public Vec3 normal;
		public float depth;
		public Vec3 contactPointA;
		public Vec3 contactPointB;
	}
	
	public static enum Result {
		COLLIDING,
		SEPARATED,
		GJK_FAILED,
		EPA_FAILED;
	}
}
