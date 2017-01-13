package com.thoughtbot.tropos.main

import com.thoughtbot.tropos.commons.Presenter
import com.thoughtbot.tropos.data.LocationDataSource
import com.thoughtbot.tropos.data.WeatherDataSource
import com.thoughtbot.tropos.data.remote.LocationService
import com.thoughtbot.tropos.data.remote.WeatherDataService
import io.reactivex.disposables.Disposable

class MainPresenter(override val view: MainView,
    val locationDataSource: LocationDataSource = LocationService(view.context),
    val weatherDataSource: WeatherDataSource = WeatherDataService()) : Presenter {

  lateinit var disposable: Disposable

  fun init() {
    //TODO confirm observable is completing
    view.viewState = ViewState.Loading(ToolbarViewModel(view.context, null))
    disposable = locationDataSource.fetchLocation()
        .flatMap { weatherDataSource.fetchForecast(it, 3) }
        .doOnError { view.viewState = ViewState.Error(it.message ?: "") }
        .subscribe {
          view.viewState = ViewState.Weather(ToolbarViewModel(view.context, it[0]), it)
        }
  }

  fun onStop() {
    disposable.dispose()
  }
}