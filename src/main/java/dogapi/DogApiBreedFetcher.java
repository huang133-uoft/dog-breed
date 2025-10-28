package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * BreedFetcher implementation that relies on the dog API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private static final String BASE = "https://dog.ceo/api/breed/";
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog API.
     *
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException{
        if (breed == null) throw new IllegalArgumentException("breed is null");
        final String normalizedBreed = breed.trim().toLowerCase();
        if (normalizedBreed.isEmpty()) throw new BreedNotFoundException("breed is empty");

        final String safe = URLEncoder.encode(normalizedBreed, UTF_8);
        final String url = BASE + safe + "/list";

        final Request request = new Request.Builder().url(url).get().build();

        try (Response resp = client.newCall(request).execute()) {
            if (resp.body() == null) {
                throw new BreedNotFoundException("Empty response body for " + normalizedBreed, true);
            }
            final String body = resp.body().string();

            JSONObject jsonObject = new JSONObject(body);
            String status = jsonObject.optString("status", "");

            if ("success".equalsIgnoreCase(status)) {
                JSONArray arr = jsonObject.getJSONArray("message");
                List<String> outp = new ArrayList<>(arr.length());
                for (int i = 0; i < arr.length(); i++) outp.add(arr.getString(i));
                return Collections.unmodifiableList(outp);
            }

            if ("error".equalsIgnoreCase(status) && jsonObject.optInt("code", 0) == 404) {
                throw new BreedNotFoundException(normalizedBreed);
            }
            throw new BreedNotFoundException("Unexpected API response for " + normalizedBreed, true);
        } catch (IOException ioe) {
            throw new BreedNotFoundException("Network/API error while fetching " + normalizedBreed + ": " + ioe.getMessage(), true);
        } catch (Exception e) {
            throw new BreedNotFoundException("Failed to parse API response for " + normalizedBreed + ": " + e.getMessage(), true);
        }


    }

}