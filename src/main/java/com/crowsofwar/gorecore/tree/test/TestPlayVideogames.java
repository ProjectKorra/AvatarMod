package com.crowsofwar.gorecore.tree.test;

import java.util.List;

import com.crowsofwar.gorecore.chat.ChatMessage;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.NodeFunctional;

import net.minecraft.util.text.TextComponentTranslation;

public class TestPlayVideogames extends NodeFunctional {
	
	public TestPlayVideogames() {
		super("play", false);
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		String videogame = options.isEmpty() ? "" : options.get(0);
		String send = options.isEmpty() ? "test.videogames.none" : "test.videogames";
		call.getFrom().addChatMessage(new TextComponentTranslation(send, videogame));
		return null;
	}
	
	@Override
	public ChatMessage getInfoMessage() {
		return TestMessages.MSG_PLAYVIDEOGAMES_HELP;
	}
	
}
