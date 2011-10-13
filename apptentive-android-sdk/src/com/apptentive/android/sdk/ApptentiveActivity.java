/*
 * ApptentiveActivity.java
 *
 * Created by Sky Kelsey on 2011-09-18.
 * Copyright 2011 Apptentive, Inc. All rights reserved.
 */

package com.apptentive.android.sdk;

import android.os.Bundle;
import android.view.Window;
import com.apptentive.android.sdk.activity.BaseActivity;
import com.apptentive.android.sdk.model.ApptentiveModel;
import com.apptentive.android.sdk.model.FeedbackController;
import com.apptentive.android.sdk.survey.SurveyController;

public class ApptentiveActivity  extends BaseActivity {

	private Module activeModule;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		activeModule = Module.valueOf(getIntent().getStringExtra("module"));

		// Inflate the wrapper view, and then inflate the content view into it
		switch(activeModule){
			case FEEDBACK:
				setContentView(R.layout.apptentive_feedback);
				new FeedbackController(this, getIntent().getBooleanExtra("forced", false));
				break;
			case SURVEY:
				ApptentiveModel model = ApptentiveModel.getInstance();
				if(model.getSurveys() == null || model.getSurveys().size() == 0){
					finish();
					return;
				}
				setContentView(R.layout.apptentive_survey);
				new SurveyController(this);
				break;
			default:
				new FeedbackController(this, getIntent().getBooleanExtra("forced", false));
				break;
		}
	}

	public static enum Module{
		FEEDBACK,
		SURVEY
	}
}