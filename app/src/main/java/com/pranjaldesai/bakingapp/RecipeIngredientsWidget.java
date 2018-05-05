package com.pranjaldesai.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.pranjaldesai.bakingapp.ApiData.BakingResult;
import com.pranjaldesai.bakingapp.ApiData.Ingredients;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeIngredientsWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                BakingResult result, int appWidgetId) {

        Intent intent = new Intent(context, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_ingredients_widget);

        String widgetTitleText = result.getName();
        ArrayList<Ingredients> ingredients = new ArrayList<>();
        ingredients= result.getIngredients();

        if(ingredients!= null && widgetTitleText!= null && pendingIntent!=null) {
            String widgetBodyText="";
            for(Ingredients ingredient: ingredients){
                String ingredientBuilder= "- "+ ingredient.getQuantity() + " "+ ingredient.getMeasure() + " "+ ingredient.getIngredient()+"\n";
                widgetBodyText= widgetBodyText.concat(ingredientBuilder);
            }
            views.setTextViewText(R.id.appwidget_title_text, widgetTitleText);
            views.setTextViewText(R.id.appwidget_body_text, widgetBodyText);
            views.setOnClickPendingIntent(R.id.appwidget_title_button, pendingIntent);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        IngredientWidgetService.startActionUpdateIngredientWidgets(context, new BakingResult());
    }

    public static void updateIngredientWidgets(Context context, AppWidgetManager appWidgetManager,
                                               BakingResult result, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, result, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}




