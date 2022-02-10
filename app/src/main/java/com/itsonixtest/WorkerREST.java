package com.itsonixtest;

import android.content.Context;
import android.util.Base64;
import android.util.JsonReader;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class WorkerREST extends Worker {
    public WorkerREST(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        URL URL;

        try {
            URL = new URL(getInputData().getString(Constants.URL));
        } catch (MalformedURLException e) {
            return Result.failure();
        }
        HttpsURLConnection connection;
        try {
            connection = (HttpsURLConnection) URL.openConnection();
        } catch (IOException e) {
            return Result.failure();
        }
/**
 * Authentication Work
 */
        if (getInputData().getString(Constants.Operation).contains("Authentication")) {
            connection.addRequestProperty("Authorization", getInputData().getString(Constants.authHeader));
            int responseCode;
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e) {
                return Result.failure();
            }
            Data outputData = new Data.Builder()
                    .putString(Constants.httpResponseCode, String.valueOf(responseCode))
                    .build();
            return Result.success(outputData);
/**
 * Status codes Work
  */
        } else if (getInputData().getString(Constants.Operation).contains("StatusCodes")) {
            int responseCode;

            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e) {
                return Result.failure();
            }
            Data outputData = new Data.Builder()
                    .putString(Constants.httpResponseCode, String.valueOf(responseCode))
                    .build();
            return Result.success(outputData);
/**
 * Internal Counter Work
  */
        } else if (getInputData().getString(Constants.Operation).contains("InternalCounter")) {
            try {
                connection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                return Result.failure();
            }

            String postParams = getInputData().getString(Constants.postInternalCounter);

            connection.setDoOutput(true);

            InputStream responseBody;

            try {
                connection.getOutputStream().write(postParams.getBytes());
                int responseCode;

                responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    responseBody = connection.getInputStream();
                } else {
                    return Result.failure();

                }
            } catch (IOException e) {
                return Result.failure();
            }

            InputStreamReader responseBodyReader;

            try {
                responseBodyReader =
                        new InputStreamReader(responseBody, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return Result.failure();
            }

            BufferedReader reader;
            reader = new BufferedReader(responseBodyReader);

            String line = "";
            String jsonString = "";

            do {
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    return Result.failure();
                }
                jsonString += line;
            } while (line != null);


            JSONObject obj;
            String counter;
            try {
                obj = new JSONObject(jsonString);
                counter = obj.getJSONObject("form").getString("counter");
            } catch (JSONException e) {
                return Result.failure();
            }

            Data outputData = new Data.Builder()
                    .putString(Constants.outputInternalCounter, counter)
                    .build();

            return Result.success(outputData);
        }

        return Result.success();
    }
}
