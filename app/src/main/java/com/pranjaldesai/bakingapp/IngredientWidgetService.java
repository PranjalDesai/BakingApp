package com.pranjaldesai.bakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.pranjaldesai.bakingapp.ApiData.BakingResult;

public class IngredientWidgetService extends IntentService {

    public static final String ACTION_UPDATE_INGREDIENT_WIDGETS = "com.pranjaldesai.bakingapp.action.update_ingredient_widgets";

    public IngredientWidgetService() {
        super("IngredientWidgetService");
    }

    /**
     * Starts this service to perform UpdatePlantWidgets action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateIngredientWidgets(Context context, BakingResult bakingResult) {
        Intent intent = new Intent(context, IngredientWidgetService.class);
        intent.setAction(ACTION_UPDATE_INGREDIENT_WIDGETS);
        intent.putExtra("bakingIngredientList",bakingResult);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            BakingResult bakingResult= (BakingResult) intent.getExtras().get("bakingIngredientList");
            if (ACTION_UPDATE_INGREDIENT_WIDGETS.equals(action)) {
                handleActionUpdateIngredientWidgets(bakingResult);
            }
        }
    }

    /**
     * Handle action UpdateIngredientWidgets in the provided background thread
     */
    private void handleActionUpdateIngredientWidgets(BakingResult bakingResult) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeIngredientsWidget.class));
        //Now update all widgets
        RecipeIngredientsWidget.updateIngredientWidgets(this, appWidgetManager, bakingResult, appWidgetIds);
    }
}
