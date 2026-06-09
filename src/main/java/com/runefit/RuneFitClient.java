package com.runefit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

final class RuneFitClient
{
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	interface ResultCallback
	{
		void onSuccess(LinkCodeResponse response);

		void onFailure(String message);
	}

	private final OkHttpClient httpClient;
	private final Gson gson;
	private final String endpoint;

	RuneFitClient(OkHttpClient httpClient, Gson gson, String endpoint)
	{
		this.httpClient = httpClient;
		this.gson = gson;
		this.endpoint = endpoint;
	}

	void createLinkCode(LinkCodeRequest payload, ResultCallback callback)
	{
		HttpUrl url = HttpUrl.parse(endpoint);

		if (url == null || (!url.isHttps() && !isLocalDevelopment(url)))
		{
			callback.onFailure("RuneFit API endpoint must use HTTPS.");
			return;
		}

		Request request = new Request.Builder()
			.url(url)
			.header("User-Agent", "RuneFitRuneLitePlugin/1.0")
			.post(RequestBody.create(JSON, gson.toJson(payload)))
			.build();

		httpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException exception)
			{
				callback.onFailure("RuneFit could not be reached. Try again shortly.");
			}

			@Override
			public void onResponse(Call call, Response response)
			{
				try (Response closeableResponse = response)
				{
					ResponseBody body = closeableResponse.body();
					String responseBody = body == null ? "" : body.string();

					if (!closeableResponse.isSuccessful())
					{
						callback.onFailure(parseError(responseBody));
						return;
					}

					LinkCodeResponse linkCode = gson.fromJson(responseBody, LinkCodeResponse.class);

					if (linkCode == null || linkCode.getLinkCode() == null)
					{
						callback.onFailure("RuneFit returned an invalid response.");
						return;
					}

					callback.onSuccess(linkCode);
				}
				catch (Exception exception)
				{
					callback.onFailure("RuneFit returned an invalid response.");
				}
			}
		});
	}

	private String parseError(String responseBody)
	{
		try
		{
			JsonObject error = gson.fromJson(responseBody, JsonObject.class);

			if (error != null && error.has("message"))
			{
				return error.get("message").getAsString();
			}
		}
		catch (Exception ignored)
		{
			// Fall through to a generic message.
		}

		return "RuneFit rejected the link request.";
	}

	private boolean isLocalDevelopment(HttpUrl url)
	{
		return "localhost".equals(url.host()) || "127.0.0.1".equals(url.host());
	}
}
