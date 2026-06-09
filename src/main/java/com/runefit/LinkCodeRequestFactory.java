package com.runefit;

import net.runelite.api.GameState;

final class LinkCodeRequestFactory
{
	private LinkCodeRequestFactory()
	{
	}

	static LinkCodeRequest create(GameState gameState, String characterName, long accountHash, int accountType)
	{
		if (gameState != GameState.LOGGED_IN || characterName == null || characterName.trim().isEmpty())
		{
			throw new IllegalStateException("Log into the OSRS character you want to link first.");
		}

		if (accountHash == -1L)
		{
			throw new IllegalStateException("RuneLite has not loaded a valid account hash yet. Try again in a moment.");
		}

		return new LinkCodeRequest(characterName, Long.toString(accountHash), AccountTypeMapper.fromVarbit(accountType));
	}
}
