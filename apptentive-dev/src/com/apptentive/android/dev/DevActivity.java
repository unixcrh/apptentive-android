/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.dev;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.apptentive.android.sdk.*;
import com.apptentive.android.sdk.module.messagecenter.UnreadMessagesListener;
import com.apptentive.android.sdk.module.survey.OnSurveyFinishedListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sky Kelsey
 */
public class DevActivity extends ApptentiveActivity {

	private static final String LOG_TAG = "Apptentive Dev App";

	private String selectedTag;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// *** BEGIN APPTENTIVE INITIALIZATION

		// OPTIONAL: To specify a different user email than what the device was setup with.
		//Apptentive.setUserEmail("user_email@example.com");

		// OPTIONAL: To send extra about the app to the server.
		Map<String, String> customData = new HashMap<String, String>();
		customData.put("user-id", "1234567890");
		Apptentive.setCustomDeviceData(customData);

		// OPTIONAL: Specify a different rating provider if your app is not served from Google Play.
		//Apptentive.setRatingProvider(new AmazonAppstoreRatingProvider());

		// *** END APPTENTIVE INITIALIZATION

		// If you would like to be notified when there are unread messages available, set a listener like this.
		Apptentive.setUnreadMessagesListener(new UnreadMessagesListener() {
			public void onUnreadMessageCountChanged(final int unreadMessages) {
				Log.e(LOG_TAG, "There are " + unreadMessages + " unread messages.");
				DevActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						Button messageCenterButton = (Button) findViewById(R.id.button_message_center);
						messageCenterButton.setText("Message Center, unread = " + unreadMessages);
					}
				});
			}
		});

		// Set up a spinner to choose which tag we will use to show a survey.
		Spinner surveySpinner = (Spinner) findViewById(R.id.survey_spinner);
		surveySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if (i == 0) {
					selectedTag = null;
				} else {
					String[] tagsArray = getResources().getStringArray(R.array.survey_tags);
					selectedTag = tagsArray[i];
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});
	}

	public void runTests(View view) {
		Intent testsIntent = new Intent();
		testsIntent.setClass(this, TestsActivity.class);
		startActivity(testsIntent);
	}

	public void reinstall(View view) {
		DevDebugHelper.resetRatingFlow(this);
	}

	public void logSignificantEvent(View view) {
		Apptentive.logSignificantEvent(this);
	}

	public void logDay(View view) {
		DevDebugHelper.logDay(this);
	}

	public void showChoice(View view) {
		DevDebugHelper.forceShowEnjoymentDialog(this);
	}

	public void showRating(View view) {
		DevDebugHelper.showRatingDialog(this);
	}

	public void showFeedback(View view) {
		DevDebugHelper.forceShowIntroDialog(this);
	}

	public void showMessageCenter(View view) {
		Apptentive.showMessageCenter(this);
	}

	public void showSurvey(View view) {
		OnSurveyFinishedListener listener = new OnSurveyFinishedListener() {
			public void onSurveyFinished(boolean completed) {
				Log.e(LOG_TAG, "A survey finished, and was " + (completed ? "completed" : "skipped"));
			}
		};

		boolean ret;
		if (selectedTag != null) {
			ret = Apptentive.showSurvey(this, listener, selectedTag);
		} else {
			ret = Apptentive.showSurvey(this, listener);
		}
		if (!ret) {
			Toast.makeText(this, "No matching survey found.", Toast.LENGTH_SHORT).show();
		}
	}

	// Call the ratings flow. This is one way to do it: Show the ratings flow if conditions are met when the window
	// gains focus.
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			Apptentive.showRatingFlowIfConditionsAreMet(DevActivity.this);
		}
	}
}
