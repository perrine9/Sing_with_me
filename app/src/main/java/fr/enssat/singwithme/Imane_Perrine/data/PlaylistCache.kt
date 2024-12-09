package fr.enssat.singwithme.Imane_Perrine.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistCache(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("PlaylistCache", Context.MODE_PRIVATE)

    // Sauvegarde la playlist sous forme de JSON
    fun savePlaylist(tracks: List<Track>) {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(tracks)
        editor.putString("cached_playlist", json)
        editor.apply()
    }

    // Récupère la playlist depuis le cache
    fun getPlaylist(): List<Track>? {
        val json = sharedPreferences.getString("cached_playlist", null) ?: return null
        val type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    // Vide le cache
    fun clearCache() {
        sharedPreferences.edit().clear().apply()
    }
}
