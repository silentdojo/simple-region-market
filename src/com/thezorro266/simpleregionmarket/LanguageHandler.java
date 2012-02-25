package com.thezorro266.simpleregionmarket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LanguageHandler {
	private ConfigHandler configurationHandler;
	private FileConfiguration languageFile = null;

	public LanguageHandler(SimpleRegionMarket plugin,
			ConfigHandler configurationHandler) {
		this.configurationHandler = configurationHandler;
		plugin.saveResource("en.yml", false);
	}

	public void langOutputConsole(String id, Level level,
			ArrayList<String> args) {
		outputConsole(level, parseLanguageString(id, args));
	}

	public void outputConsole(Level level, String string) {
		Bukkit.getLogger().log(level, "[SimpleRegionMarket] " + string);
	}

	public void outputDebug(Player p, String id, ArrayList<String> args) {
		p.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_BLUE + "SRM"
				+ ChatColor.WHITE + "] " + ChatColor.YELLOW
				+ parseLanguageString(id, args));
	}

	public void outputError(Player p, String id, ArrayList<String> args) {
		p.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_BLUE + "SRM"
				+ ChatColor.WHITE + "] " + ChatColor.RED
				+ parseLanguageString(id, args));
	}

	public void outputString(Player p, String string) {
		p.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_BLUE + "SRM"
				+ ChatColor.WHITE + "] " + ChatColor.YELLOW + string);
	}

	private String parseLanguageString(String id, ArrayList<String> args) {
		String string = id;
		
		String lang = configurationHandler.getConfig().getString("language");
		if(!new File(SimpleRegionMarket.plugin_dir
				+ lang
				+ ".yml").exists()) {
			ArrayList<String> list = new ArrayList<String>();
			list.add(lang);
			this.langOutputConsole("ERROR_LANG_NOT_FOUND", Level.WARNING, list);
			lang = "en";
			configurationHandler.getConfig().set("language", lang);
		}
		
		try {
			languageFile.load(SimpleRegionMarket.plugin_dir
					+ lang
					+ ".yml");
		} catch (FileNotFoundException e1) {
			outputConsole(Level.SEVERE, "No write permissions on '" + SimpleRegionMarket.plugin_dir + "'.");
			e1.printStackTrace();
		} catch (IOException e1) {
			outputConsole(Level.SEVERE, "IO Exception in language system.");
			e1.printStackTrace();
		} catch (InvalidConfigurationException e1) {
			outputConsole(Level.SEVERE, "Language file corrupt (Invalid YAML).");
			e1.printStackTrace();
		}

		for (int i = string.length() - 1; i >= 0; i--) {
			if (string.charAt(i) == '$') {
				if (string.charAt(i - 1) == '$') {
					string = string.substring(0, i)
							+ string.substring(i + 1, string.length());
				} else if (Character.isDigit(string.charAt(i + 1))) {
					int argi;
					try {
						argi = Integer.parseInt(Character.toString(string
								.charAt(i + 1)));
					} catch (final Exception e) {
						string = string.substring(0, i) + "ERROR ARGUMENT"
								+ string.substring(i + 2, string.length());
						continue;
					}

					try {
						string = string.substring(0, i) + args.get(argi)
								+ string.substring(i + 2, string.length());
					} catch (final Exception e) {
						string = string.substring(0, i) + "ERROR ARGUMENT"
								+ string.substring(i + 2, string.length());
						continue;
					}
				}
			}
		}
		return string;
	}
}
