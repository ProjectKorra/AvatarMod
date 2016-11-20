package com.crowsofwar.avatar.common.network;

import static com.crowsofwar.avatar.common.bending.BendingManager.getBending;

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingType;

import io.netty.buffer.ByteBuf;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class Transmitters {
	
	public static final DataTransmitter<List<BendingController>> CONTROLLER_LIST = new DataTransmitter<List<BendingController>>() {
		
		@Override
		public void write(ByteBuf buf, List<BendingController> t) {
			buf.writeInt(t.size());
			for (BendingController controller : t)
				buf.writeInt(controller.getType().id());
		}
		
		@Override
		public List<BendingController> read(ByteBuf buf) {
			int size = buf.readInt();
			List<BendingController> out = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				out.add(getBending(BendingType.find(buf.readInt())));
			}
			return out;
		}
	};
	
}
