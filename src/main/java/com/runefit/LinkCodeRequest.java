package com.runefit;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
class LinkCodeRequest
{
	@SerializedName("character_name")
	String characterName;

	@SerializedName("account_hash")
	String accountHash;

	@SerializedName("account_type")
	String accountType;
}
