package com.mckenney.zac.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;

    //private FetchWeatherTask refreshMe = new FetchWeatherTask();

    public ForecastFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Added this line for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("44720");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //ArrayAdapter<String> mForecastAdapter;

        String[] data = {
                "Today - Sunny - 88/63",
                "Tomorrow - Memorial Day! - 70/40",
                "Tuesday - Fuck Ohio - Cold",
                "Wednesday - Seriously - Colder",
                "Thursday - Still in OH - Warming up",
                "Friday - Blizzard - Because Ohio",
                "Saturday - West Coast Best Coast - Worse than Cali",
                "Sunday - Another Week in OH - Hopefully Warm",
                "Monday - I should make the TBR wine App - yep",
                "Tuesday - maybe I should be done by today - probably",
                "Wednesday - Yep yep yep yep yep - definitely",
                "Thursday - Noooooo douuuuubt - whatevs"
        };

        ArrayList<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

        mForecastAdapter = new ArrayAdapter<String>(
                //Context (This frags parent Activity
                getActivity(),
                //ID of list item layout
                R.layout.list_item_forecast,
                //ID of textview to populate
                R.id.list_item_forecast_textview,
                //Forecast Data
                weekForecast);


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);


        return rootView;
    }


        public class FetchWeatherTask extends AsyncTask<String, Void, Void> {


            private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

            //  "http://api.openweathermap.org/data/2.5/forecast/daily?q=44720&mode=json&units=metric&cnt=7"
            //  "http://api.openweathermap.org/ data/   2.5/    forecast/   daily?  q=44720&    mode=json&  units=metric&   cnt=7"


            @Override
            protected Void doInBackground(String... params) {

                //If there arent any params then theres nothing to do so null
                if (params.length == 0) {
                    return null;
                }


                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String forecastJsonStr = null;

                String format = "json";
                String units = "metric";
                int numDays = 7;


                try {
                    // Construct the URL for the OpenWeatherMap query
                    // Possible parameters are avaiable at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast

                    //              STATIC CODE BELOW - NEED TO MAKE IT AN INPUT
                    //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=44720&mode=json&units=metric&cnt=7");

                    final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                    final String QUERY_PARAM = "q";
                    final String FORMAT_PARAM = "mode";
                    final String UNITS_PARAM = "units";
                    final String DAYS_PARAM = "cnt";

                    Uri.Builder builtURI = Uri.parse(FORECAST_BASE_URL).buildUpon()
                            .appendQueryParameter(QUERY_PARAM, params[0])
                            .appendQueryParameter(FORMAT_PARAM, format)
                            .appendQueryParameter(UNITS_PARAM, units)
                            .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays));



                    URL url = new URL(builtURI.toString());

                    Log.v(LOG_TAG, "Built URI: " + builtURI.toString());


                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }

                    forecastJsonStr = buffer.toString();

                    Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);


                } catch (IOException e) {
                    Log.e("PlaceholderFragment", "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally{
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e("PlaceholderFragment", "Error closing stream", e);
                        }
                    }
                }




                return null;
            }
        }
}
