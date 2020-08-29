package com.crowsofwar.avatar.client.model_loaders.wmb;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class WmbDataStructures {

	// 128 bytes
	public static class WmbHeader {
		public String name;
		public int vertexHeaderIndex;
		public int vertexHeaderCount;
		public int meshArrayIndex;
		public int meshCount;
		public int meshMetaHeaderIndex;
		public int boneArrayIndex;
		public int boneCount;
		public int boneMapIndex;
		public int boneMapCount;
		public int materialArrayIndex;
		public int materialCount;
		public int textureArrayIndex;
		public int textureCount;
		public int meshNameHeaderArrayIndex;
		public int meshNameHeaderCount;

		public WmbHeader(ByteBuffer buf) throws UnsupportedEncodingException {
			byte[] chars = new byte[4];
			buf.get(chars);
			name = new String(chars, "UTF-8");
			System.out.println("AAAAA " + name);
			// 40 bytes of unknown data
			buf.position(buf.position() + 40);
			vertexHeaderIndex = buf.getInt();
			vertexHeaderCount = buf.getInt();
			meshArrayIndex = buf.getInt();
			meshCount = buf.getInt();
			meshMetaHeaderIndex = buf.getInt();
			boneArrayIndex = buf.getInt();
			boneCount = buf.getInt();
			// Skip another 8 bytes of unknown information
			buf.position(buf.position() + 8);
			boneMapIndex = buf.getInt();
			boneMapCount = buf.getInt();
			materialArrayIndex = buf.getInt();
			materialCount = buf.getInt();
			textureArrayIndex = buf.getInt();
			textureCount = buf.getInt();
			meshNameHeaderArrayIndex = buf.getInt();
			meshNameHeaderCount = buf.getInt();
		}

	}

	public static class VertexHeader {
		public int vertArrayIndex;
		public int boneWeightArrayIndex;
		public int vertStride;
		public int boneWeightStride;
		public int vertCount;
		public int faceArrayIndex;
		public int faceCount;

		public VertexHeader(ByteBuffer buf) {
			vertArrayIndex = buf.getInt();
			boneWeightArrayIndex = buf.getInt();
			// Two unknown integers that seem to always be 0
			buf.position(buf.position() + 8);
			vertStride = buf.getInt();
			boneWeightStride = buf.getInt();
			// Same thing here
			buf.position(buf.position() + 8);
			vertCount = buf.getInt();
			faceArrayIndex = buf.getInt();
			faceCount = buf.getInt();

		}
	}

	public static class VertexData {
		public float x;
		public float y;
		public float z;
		public float u;
		public float v;
		public int normal; // Not sure how to parse this into a regular normal
							// value

		public VertexData(ByteBuffer buf) {
			x = buf.getFloat();
			y = buf.getFloat();
			z = buf.getFloat();
			u = getHalf(buf);
			v = getHalf(buf);
			normal = buf.getInt();
		}
	}

	public static class BoneWeight {
		public byte boneIndex0;
		public byte boneIndex1;
		public byte boneIndex2;
		public byte boneIndex3;
		public byte boneWeight0;
		public byte boneWeight1;
		public byte boneWeight2;
		public byte boneWeight3;

		public BoneWeight(ByteBuffer buf, int stride) {
			buf.position(buf.position() + 8);
			if(stride > 8) {
				boneIndex0 = buf.get();
				boneIndex1 = buf.get();
				boneIndex2 = buf.get();
				boneIndex3 = buf.get();
				boneWeight0 = buf.get();
				boneWeight1 = buf.get();
				boneWeight2 = buf.get();
				boneWeight3 = buf.get();
			}
			if(stride == 20) {
				buf.position(buf.position() + 4);
			} else if(stride == 24) {
				buf.position(buf.position() + 8);
			}
		}
	}
	
	public static class BoneMap {
		public int boneMapOffset;
		public int boneMapCount;
		
		public BoneMap(ByteBuffer buf) {
			boneMapOffset = buf.getInt();
			boneMapCount = buf.getInt();
		}
	}
	
	public static class Bone {
		public short boneNumber;
		public short parentIndex;
		public float posRelativeX;
		public float posRelativeY;
		public float posRelativeZ;
		public float posAbsoluteX;
		public float posAbsoluteY;
		public float posAbsoluteZ;
		
		public Bone(ByteBuffer buf) {
			buf.position(buf.position() + 2);
			boneNumber = buf.getShort();
			parentIndex = buf.getShort();
			buf.position(buf.position() + 2);
			posRelativeX = buf.getFloat();
			posRelativeY = buf.getFloat();
			posRelativeZ = buf.getFloat();
			posAbsoluteX = buf.getFloat();
			posAbsoluteY = buf.getFloat();
			posAbsoluteZ = buf.getFloat();
		}
	}

	public static class Mesh {
		public int vertexStart;
		public int faceStart;
		public int vertexCount;
		public int faceCount;
		public MeshMetadata meta;
		public String name;

		public Mesh(ByteBuffer buf) {
			// Skip first unknown number
			buf.position(buf.position() + 4);
			vertexStart = buf.getInt();
			faceStart = buf.getInt();
			vertexCount = buf.getInt();
			faceCount = buf.getInt();
		}
	}

	public static class MeshMetadataHeader {
		public int meshMetaIndex;
		public int meshMetaCount;
		public int hiddenMetaIndex;
		public int hiddenMetaCount;

		public MeshMetadataHeader(ByteBuffer buf) {
			meshMetaIndex = buf.getInt();
			meshMetaCount = buf.getInt();
			buf.position(buf.position() + 16);
			hiddenMetaIndex = buf.getInt();
			hiddenMetaCount = buf.getInt();
		}
	}

	public static class MeshMetadata {
		public int meshIndex;
		public int meshNameIndex;
		public short materialIndex;
		public short boneMapIndex;

		public MeshMetadata(ByteBuffer buf) {
			meshIndex = buf.getInt();
			meshNameIndex = buf.getInt();
			materialIndex = buf.getShort();
			boneMapIndex = buf.getShort();
			buf.position(buf.position() + 4);
		}
	}

	public static class NameHeader {
		public int nameIndex;
		public int meshIdArrayIndex;
		public int meshIdCount;

		public NameHeader(ByteBuffer buf) {
			nameIndex = buf.getInt();
			buf.position(buf.position() + 24);
			meshIdArrayIndex = buf.getInt();
			meshIdCount = buf.getInt();
			buf.position(buf.position() + 40);
		}
	}

	public static float getHalf(ByteBuffer buf) {
		/*
		 * byte[] bytes = new byte[2]; buf.get(bytes);
		 * 
		 * int hbits = (bytes[1] << 8) | bytes[0];
		 */
		return toFloat(buf.getShort());
	}

	// Half decoder,
	// https://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java
	// ignores the higher 16 bits
	public static float toFloat(int hbits) {
		int mant = hbits & 0x03ff; // 10 bits mantissa
		int exp = hbits & 0x7c00; // 5 bits exponent
		if(exp == 0x7c00) // NaN/Inf
			exp = 0x3fc00; // -> NaN/Inf
		else if(exp != 0) // normalized value
		{
			exp += 0x1c000; // exp - 15 + 127
			if(mant == 0 && exp > 0x1c400) // smooth transition
				return Float.intBitsToFloat((hbits & 0x8000) << 16 | exp << 13 | 0x3ff);
		} else if(mant != 0) // && exp==0 -> subnormal
		{
			exp = 0x1c400; // make it normal
			do {
				mant <<= 1; // mantissa * 2
				exp -= 0x400; // decrease exp by 1
			} while((mant & 0x400) == 0); // while not normal
			mant &= 0x3ff; // discard subnormal bit
		} // else +/-0 -> +/-0
		return Float.intBitsToFloat( // combine all parts
				(hbits & 0x8000) << 16 // sign << ( 31 - 15 )
						| (exp | mant) << 13); // value << ( 23 - 10 )
	}
}
