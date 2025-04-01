package com.mehmettekin.gunkurasiapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mehmettekin.gunkurasiapp.domain.model.DrawResult
import com.mehmettekin.gunkurasiapp.domain.model.DrawSettings
import com.mehmettekin.gunkurasiapp.domain.model.Participant
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


private val Context.drawResultsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "draw_results_datastore"
)

@Singleton
class DrawResultsDataStore @Inject constructor(
    private val context: Context,
    moshi: Moshi
) {
    private val drawResultsKey = stringPreferencesKey("draw_results")
    private val participantsKey = stringPreferencesKey("participants")
    private val drawSettingsKey = stringPreferencesKey("draw_settings")

    private val drawResultsType = Types.newParameterizedType(
        List::class.java,
        DrawResult::class.java
    )

    private val participantsType = Types.newParameterizedType(
        List::class.java,
        Participant::class.java
    )

    private val drawResultsAdapter: JsonAdapter<List<DrawResult>> = moshi.adapter(drawResultsType)
    private val participantsAdapter: JsonAdapter<List<Participant>> = moshi.adapter(participantsType)
    private val drawSettingsAdapter: JsonAdapter<DrawSettings> = moshi.adapter(DrawSettings::class.java)

    // Draw Results operations
    suspend fun saveDrawResults(results: List<DrawResult>) {
        context.drawResultsDataStore.edit { preferences ->
            preferences[drawResultsKey] = drawResultsAdapter.toJson(results)
        }
    }

    fun getDrawResults(): Flow<List<DrawResult>> {
        return context.drawResultsDataStore.data.map { preferences ->
            val json = preferences[drawResultsKey] ?: "[]"
            drawResultsAdapter.fromJson(json) ?: emptyList()
        }
    }


    suspend fun clearDrawResults() {
        context.drawResultsDataStore.edit { preferences ->
            preferences[drawResultsKey] = "[]"
        }
    }

    // Participants operations
    suspend fun saveParticipants(participants: List<Participant>) {
        context.drawResultsDataStore.edit { preferences ->
            preferences[participantsKey] = participantsAdapter.toJson(participants)
        }
    }

    suspend fun getParticipants(): List<Participant> {
        return context.drawResultsDataStore.data.map { preferences ->
            val json = preferences[participantsKey] ?: "[]"
            participantsAdapter.fromJson(json) ?: emptyList()
        }.first()
    }

    // Draw Settings operations
    suspend fun saveDrawSettings(settings: DrawSettings) {
        context.drawResultsDataStore.edit { preferences ->
            preferences[drawSettingsKey] = drawSettingsAdapter.toJson(settings)
        }
    }

    suspend fun getDrawSettings(): DrawSettings? {
        return context.drawResultsDataStore.data.map { preferences ->
            val json = preferences[drawSettingsKey] ?: return@map null
            drawSettingsAdapter.fromJson(json)
        }.first()
    }
}