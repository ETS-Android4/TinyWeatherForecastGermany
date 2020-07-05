/*
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020 Pawel Dube
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.net.URL;

public class WeatherUpdateBroadcastReceiver extends BroadcastReceiver {

    public final static String UPDATE_ACTION = "de.kaffeemitkoffein.broadcast.REQUEST_UPDATE";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(UPDATE_ACTION)) {
                // check if enough time passed since last update
                if (UpdateChecker.eligibleForForecastUpdate(context)){
                    StationsArrayList stationsArrayList = new StationsArrayList(context);
                    int position = stationsArrayList.getSetStationPositionByName(context);
                    // gets station instance
                    Station station = stationsArrayList.stations.get(position);
                    // determines web urls of api
                    URL stationURLs[] = station.getAbsoluteWebURLArray();
                    final WeatherCard weatherCardArray[] = {new WeatherCard()};
                    WeatherForecastReader weatherForecastReader = new WeatherForecastReader(context){
                        @Override
                        public void onPositiveResult(WeatherCard wc){
                            // weather data was already saved; check if data needs to be sent
                            // to dadgetbridge.
                            GadgetbridgeAPI gadgetbridgeAPI = new GadgetbridgeAPI(context);
                            gadgetbridgeAPI.sendWeatherBroadcastIfEnabled();
                        }

                        @Override
                        public void onNegativeResult(){
                            // do nothing
                        }

                    };
                    weatherForecastReader.execute(stationURLs);
                }
            }
        }
    }
}