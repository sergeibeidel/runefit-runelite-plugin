package com.runefit;

final class AccountTypeMapper
{
	private AccountTypeMapper()
	{
	}

	static String fromVarbit(int accountType)
	{
		switch (accountType)
		{
			case 1:
				return "ironman";
			case 2:
				return "ultimate_ironman";
			case 3:
				return "hardcore_ironman";
			default:
				return "regular";
		}
	}
}
