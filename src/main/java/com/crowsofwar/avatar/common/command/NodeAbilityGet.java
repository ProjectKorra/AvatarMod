package com.crowsofwar.avatar.common.command;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarChatMessages;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.gorecore.tree.ArgumentList;
import com.crowsofwar.gorecore.tree.ArgumentOptions;
import com.crowsofwar.gorecore.tree.ArgumentPlayerName;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.IArgument;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.ITypeConverter;
import com.crowsofwar.gorecore.tree.NodeFunctional;
import com.crowsofwar.gorecore.tree.TreeCommandException;
import com.crowsofwar.gorecore.tree.TreeCommandException.Reason;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class NodeAbilityGet extends NodeFunctional {
	
	static final ITypeConverter<BendingAbility<?>> convertAbility = new ITypeConverter<BendingAbility<?>>() {
		@Override
		public BendingAbility<?> convert(String str) {
			List<BendingAbility> allAbilities = BendingManager.allAbilities();
			for (BendingAbility<?> ability : allAbilities) {
				if (ability.getClass().getSimpleName().equals(str)) return ability;
			}
			
			throw new TreeCommandException(Reason.NOT_OPTION);
		}
		
		@Override
		public String toString(BendingAbility<?> obj) {
			return obj.getClass().getSimpleName();
		}
		
		@Override
		public String getTypeName() {
			return "Ability";
		}
	};
	
	static final BendingAbility[] allAbilities;
	static {
		List<BendingAbility> list = BendingManager.allAbilities();
		allAbilities = new BendingAbility[list.size()];
		for (int i = 0; i < list.size(); i++)
			allAbilities[i] = list.get(i);
	}
	
	private final IArgument<String> argPlayer;
	private final IArgument<BendingAbility<?>> argAbility;
	
	/**
	 * @param name
	 * @param op
	 */
	public NodeAbilityGet() {
		super("get", true);
		argPlayer = addArgument(new ArgumentPlayerName("player"));
		argAbility = addArgument(new ArgumentOptions<>(convertAbility, "ability", allAbilities));
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		
		ArgumentList args = call.popArguments(this);
		String player = args.get(argPlayer);
		BendingAbility ability = args.get(argAbility);
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(call.getFrom().getEntityWorld(),
				player);
		if (data != null) {
			
			int xp = data.getAbilityData(ability).getXp();
			AvatarChatMessages.MSG_ABILITY_GET.send(call.getFrom(), player,
					ability.getClass().getSimpleName(), xp);
			
		}
		
		return null;
	}
	
}
