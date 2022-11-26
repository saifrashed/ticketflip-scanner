package com.ticketflip.scanner.ui.app.event

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ticketflip.scanner.data.api.util.Resource
import com.ticketflip.scanner.data.model.response.EventResponse
import com.ticketflip.scanner.data.repository.EventRepository
import kotlinx.coroutines.launch

/**
 * View model for Events
 */
class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val eventRepository = EventRepository(application)


    /**
     * Live data for events
     */
    val eventResource: LiveData<Resource<List<EventResponse>>>
        get() = _eventResource

    private val _eventResource: MutableLiveData<Resource<List<EventResponse>>> =
        MutableLiveData(Resource.Empty())


    init {
        viewModelScope.launch { getEvents() }
    }

    fun getEvents() {
        _eventResource.value = Resource.Loading()

        viewModelScope.launch {
            val result = eventRepository.getEvents()

            _eventResource.value = result
        }
    }


}