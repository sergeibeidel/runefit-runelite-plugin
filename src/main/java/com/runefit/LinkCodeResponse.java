package com.runefit;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
class LinkCodeResponse
{
	@SerializedName("link_code")
	String linkCode;

	@SerializedName("character_name")
	String characterName;

	@SerializedName("expires_at")
	String expiresAt;
}
