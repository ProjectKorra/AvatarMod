/* 
  This file is part of AvatarMod.
  
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.gorecore.config;

import static com.crowsofwar.gorecore.config.Animal.ANIMAL;

import java.io.IOException;
import java.util.List;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ConfigTest {
	
	private boolean showLoadingBar;
	private List<String> list;
	
	private boolean tabCompletion, remoteFsAccess;
	
	private Animal george;
	
	public ConfigTest() throws IOException {
		
		Configuration config = Configuration.from("avatar/test.cfg").withDefaults("def/test_defaults.cfg");
		
		george = config.load("george").as(ANIMAL);
		showLoadingBar = config.load("showLoadingBar").asBoolean();
		list = config.load("allowedBlocks").asStringList();
		tabCompletion = config.fromMapping("commandOptions").load("tabCompletion").asBoolean();
		remoteFsAccess = config.fromMapping("commandOptions").load("enableRemoteFilesystem").asBoolean();
		System.out.println("Default is: " + config.load("default").asString());
		
		System.out.println("George is: " + george);
		System.out.println("List is: " + list);
		System.out.println("Show loading bar: " + showLoadingBar);
		System.out.println("Remote FS access: " + remoteFsAccess);
		
		config.save();
		
	}
	
	public static void main(String[] args) throws IOException {
		new ConfigTest();
	}
	
}
