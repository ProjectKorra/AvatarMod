package com.crowsofwar.avatar.common.network;

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.data.BendingState;

import io.netty.buffer.ByteBuf;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class Transmitters {
	
	public static final DataTransmitter<List<BendingController>, PlayerDataContext> CONTROLLER_LIST = new DataTransmitter<List<BendingController>, PlayerDataContext>() {
		
		@Override
		public void write(ByteBuf buf, List<BendingController> t) {
			buf.writeInt(t.size());
			for (BendingController controller : t)
				buf.writeInt(controller.getType().id());
		}
		
		@Override
		public List<BendingController> read(ByteBuf buf, PlayerDataContext ctx) {
			int size = buf.readInt();
			List<BendingController> out = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				out.add(BendingManager.getBending(BendingType.find(buf.readInt())));
			}
			return out;
		}
	};
	
	public static final DataTransmitter<BendingState, PlayerDataContext> STATE_SINGLE = new DataTransmitter<BendingState, PlayerDataContext>() {
		
		@Override
		public void write(ByteBuf buf, BendingState t) {
			buf.writeInt(t.getType().id());
			t.toBytes(buf);
		}
		
		@Override
		public BendingState read(ByteBuf buf, PlayerDataContext ctx) {
			BendingState state = BendingManager.getBending(BendingType.find(buf.readInt()))
					.createState(ctx.getData());
			state.fromBytes(buf);
			return state;
		}
	};
	
	public static final DataTransmitter<List<BendingState>, PlayerDataContext> STATE_LIST = new DataTransmitter<List<BendingState>, PlayerDataContext>() {
		
		@Override
		public void write(ByteBuf buf, List<BendingState> t) {
			buf.writeInt(t.size());
			for (BendingState state : t) {
				buf.writeInt(state.getType().id());
				state.toBytes(buf);
			}
		}
		
		@Override
		public List<BendingState> read(ByteBuf buf, PlayerDataContext ctx) {
			int size = buf.readInt();
			ArrayList out = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				BendingState state = BendingManager.getBending(BendingType.find(buf.readInt()))
						.createState(ctx.getData());
				state.fromBytes(buf);
				out.add(state);
			}
			return out;
		}
	};
	
}
