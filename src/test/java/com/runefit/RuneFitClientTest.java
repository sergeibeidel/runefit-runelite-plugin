package com.runefit;

import com.google.gson.Gson;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RuneFitClientTest
{
	@Test
	public void returnsGeneratedCodeFromSuccessfulApiResponse() throws Exception
	{
		OkHttpClient httpClient = clientReturning(201,
			"{\"link_code\":\"RF-ABCD-EFGH-JK\",\"character_name\":\"Example\",\"expires_at\":\"2026-06-09T15:30:00Z\"}");
		CompletableFuture<LinkCodeResponse> result = new CompletableFuture<>();

		new RuneFitClient(httpClient, new Gson(), "https://runefit.app/api/runelite/link-codes")
			.createLinkCode(new LinkCodeRequest("Example", "123", "regular"), new RuneFitClient.ResultCallback()
			{
				@Override
				public void onSuccess(LinkCodeResponse response)
				{
					result.complete(response);
				}

				@Override
				public void onFailure(String message)
				{
					result.completeExceptionally(new IllegalStateException(message));
				}
			});

		assertEquals("RF-ABCD-EFGH-JK", result.get(2, TimeUnit.SECONDS).getLinkCode());
	}

	@Test
	public void doesNotIncludeIpAddressInRequestBody() throws Exception
	{
		AtomicReference<String> requestBody = new AtomicReference<>();
		OkHttpClient httpClient = new OkHttpClient.Builder()
			.addInterceptor(chain ->
			{
				okio.Buffer buffer = new okio.Buffer();
				chain.request().body().writeTo(buffer);
				requestBody.set(buffer.readUtf8());

				return new Response.Builder()
					.request(chain.request())
					.protocol(Protocol.HTTP_1_1)
					.code(201)
					.message("Test response")
					.body(ResponseBody.create(MediaType.parse("application/json"),
						"{\"link_code\":\"RF-ABCD-EFGH-JK\",\"character_name\":\"Example\",\"expires_at\":\"2026-06-09T15:30:00Z\"}"))
					.build();
			})
			.build();
		CompletableFuture<LinkCodeResponse> result = new CompletableFuture<>();

		new RuneFitClient(httpClient, new Gson(), "https://runefit.app/api/runelite/link-codes")
			.createLinkCode(new LinkCodeRequest("Example", "123", "regular"), callbackFor(result));

		result.get(2, TimeUnit.SECONDS);
		org.junit.Assert.assertFalse(requestBody.get().contains("\"ip\""));
		org.junit.Assert.assertFalse(requestBody.get().contains("ip_address"));
	}

	@Test
	public void returnsApiFailureMessage() throws Exception
	{
		OkHttpClient httpClient = clientReturning(422, "{\"message\":\"RuneLite did not provide a valid OSRS character name.\"}");
		CompletableFuture<String> result = new CompletableFuture<>();

		new RuneFitClient(httpClient, new Gson(), "https://runefit.app/api/runelite/link-codes")
			.createLinkCode(new LinkCodeRequest("Example", "123", "regular"), new RuneFitClient.ResultCallback()
			{
				@Override
				public void onSuccess(LinkCodeResponse response)
				{
					result.completeExceptionally(new IllegalStateException("Expected failure"));
				}

				@Override
				public void onFailure(String message)
				{
					result.complete(message);
				}
			});

		assertEquals("RuneLite did not provide a valid OSRS character name.", result.get(2, TimeUnit.SECONDS));
	}

	@Test
	public void rejectsNonHttpsRemoteEndpoint() throws Exception
	{
		CompletableFuture<String> result = new CompletableFuture<>();

		new RuneFitClient(new OkHttpClient(), new Gson(), "http://runefit.app/api/runelite/link-codes")
			.createLinkCode(new LinkCodeRequest("Example", "123", "regular"), new RuneFitClient.ResultCallback()
			{
				@Override
				public void onSuccess(LinkCodeResponse response)
				{
					result.completeExceptionally(new IllegalStateException("Expected failure"));
				}

				@Override
				public void onFailure(String message)
				{
					result.complete(message);
				}
			});

		assertEquals("RuneFit API endpoint must use HTTPS.", result.get(2, TimeUnit.SECONDS));
	}

	private OkHttpClient clientReturning(int status, String body)
	{
		return new OkHttpClient.Builder()
			.addInterceptor(chain -> new Response.Builder()
				.request(chain.request())
				.protocol(Protocol.HTTP_1_1)
				.code(status)
				.message("Test response")
				.body(ResponseBody.create(MediaType.parse("application/json"), body))
				.build())
			.build();
	}

	private RuneFitClient.ResultCallback callbackFor(CompletableFuture<LinkCodeResponse> result)
	{
		return new RuneFitClient.ResultCallback()
		{
			@Override
			public void onSuccess(LinkCodeResponse response)
			{
				result.complete(response);
			}

			@Override
			public void onFailure(String message)
			{
				result.completeExceptionally(new IllegalStateException(message));
			}
		};
	}
}
