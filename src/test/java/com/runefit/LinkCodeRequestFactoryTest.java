package com.runefit;

import net.runelite.api.GameState;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LinkCodeRequestFactoryTest
{
	@Test
	public void createsRequestForLoggedInCharacter()
	{
		LinkCodeRequest request = LinkCodeRequestFactory.create(GameState.LOGGED_IN, "Example", -123456789L, 1);

		assertEquals("Example", request.getCharacterName());
		assertEquals("-123456789", request.getAccountHash());
		assertEquals("ironman", request.getAccountType());
	}

	@Test(expected = IllegalStateException.class)
	public void rejectsLoggedOutState()
	{
		LinkCodeRequestFactory.create(GameState.LOGIN_SCREEN, null, -1L, 0);
	}

	@Test(expected = IllegalStateException.class)
	public void rejectsInvalidAccountHash()
	{
		LinkCodeRequestFactory.create(GameState.LOGGED_IN, "Example", -1L, 0);
	}

	@Test
	public void mapsGroupIronmanVariantsToRegular()
	{
		assertEquals("regular", AccountTypeMapper.fromVarbit(4));
		assertEquals("regular", AccountTypeMapper.fromVarbit(5));
	}
}
